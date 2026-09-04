package com.example.agent.application.interaction.strategy;

import cn.dev33.satoken.exception.NotPermissionException;
import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.application.interaction.dto.InteractionResponse;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 安全审批交互策略 — 自然语言 → list / approve / reject，写操作落到现有审批工作流.
 * <p>
 * 不复制 ApprovalWorkflow 状态机：规则意图优先，避免 LLM 误点同意。
 * 无审批 ID 时禁止默认同意，仅 SSE/文案二次确认引导用户带上 ID。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalInteractionStrategy implements InteractionStrategy {

    private static final Pattern APPROVAL_ID = Pattern.compile(
            "(appr_[A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern APPROVE_INTENT = Pattern.compile(
            "(同意|批准|通过|approve|approval\\s*ok)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REJECT_INTENT = Pattern.compile(
            "(拒绝|驳回|不同意|reject|deny)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIST_INTENT = Pattern.compile(
            "(待办|待审批|审批列表|查看审批|有哪些审批|list|pending)", Pattern.CASE_INSENSITIVE);

    private static final String HELP_MSG = """
            安全审批模式用法：
            · 查看待办：待办 / 查看审批
            · 同意：同意 appr_xxx（必须带审批 ID，禁止默认同意）
            · 拒绝：拒绝 appr_xxx [原因]
            """;

    private final ApprovalWorkflowApplicationService approvalService;
    private final MessageApplicationService messageService;
    private final SessionConfig sessionConfig;

    @Override
    public InteractionMode getMode() {
        return InteractionMode.APPROVAL;
    }

    @Override
    public Object execute(InteractionContext context) {
        Long tenantId = context.getTenantId();
        String userId = context.getUserId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        ParsedIntent intent = parseIntent(context.getUserInput());
        Map<String, Object> result = dispatch(intent, context, null);
        return InteractionResponse.success(InteractionMode.APPROVAL.getCode(), result);
    }

    @Override
    public void executeStream(InteractionContext context) {
        Long tenantId = context.getTenantId();
        String userId = context.getUserId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        SseEmitter emitter = (SseEmitter) context.getEmitter();
        if (emitter == null) {
            throw new IllegalStateException("安全审批流式模式需要 SseEmitter，但上下文中未提供");
        }
        String conversationId = context.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalStateException("安全审批流式模式需要 conversationId，但上下文中未提供");
        }
        String userInput = context.getUserInput();

        log.info("[ApprovalStrategy] 开始: convId={}, userId={}, inputLength={}",
                conversationId, userId, userInput != null ? userInput.length() : 0);

        ScheduledExecutorService heartbeatExecutor = null;
        try {
            messageService.saveUserMessage(conversationId, userInput);
            heartbeatExecutor = startHeartbeat(emitter);

            ParsedIntent intent = parseIntent(userInput);
            sendEvent(emitter, SseEventFactory.thinking("正在解析审批意图: " + intent.action().name()));

            Map<String, Object> result = dispatch(intent, context, emitter);
            String reply = String.valueOf(result.getOrDefault("message", "处理完成"));
            streamPlainAndDone(emitter, conversationId, reply);

        } catch (NotPermissionException e) {
            log.warn("[ApprovalStrategy] 无权限: convId={}, perm={}", conversationId, e.getPermission());
            sendErrorAndComplete(emitter, conversationId, "无审批权限", 403);
        } catch (BusinessException e) {
            log.warn("[ApprovalStrategy] 业务失败: convId={}, msg={}", conversationId, e.getMessage());
            sendErrorAndComplete(emitter, conversationId, e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.error("[ApprovalStrategy] 流式执行异常: convId={}", conversationId, e);
            String msg = e.getMessage() != null ? e.getMessage() : "安全审批处理失败";
            sendErrorAndComplete(emitter, conversationId, msg, 500);
        } finally {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
            }
        }
    }

    @Override
    public int getPriority() {
        return 8;
    }

    // ==================== 意图解析 ====================

    /**
     * 规则解析动作 + 审批 ID（包可见便于单测）.
     */
    ParsedIntent parseIntent(String input) {
        if (input == null || input.isBlank()) {
            return new ParsedIntent(Action.LIST, null, null);
        }
        String approvalId = extractApprovalId(input);
        String reason = extractRejectReason(input, approvalId);

        boolean reject = REJECT_INTENT.matcher(input).find();
        boolean approve = APPROVE_INTENT.matcher(input).find();
        boolean list = LIST_INTENT.matcher(input).find();

        if (reject) {
            return new ParsedIntent(Action.REJECT, approvalId, reason);
        }
        if (approve) {
            return new ParsedIntent(Action.APPROVE, approvalId, null);
        }
        if (list || approvalId == null) {
            // 无明确同意/拒绝 → 查看待办（进入模式默认）
            return new ParsedIntent(Action.LIST, approvalId, null);
        }
        return new ParsedIntent(Action.HELP, null, null);
    }

    String extractApprovalId(String input) {
        if (input == null) {
            return null;
        }
        Matcher m = APPROVAL_ID.matcher(input);
        return m.find() ? m.group(1) : null;
    }

    private String extractRejectReason(String input, String approvalId) {
        if (input == null) {
            return null;
        }
        String cleaned = input;
        if (approvalId != null) {
            cleaned = cleaned.replace(approvalId, " ");
        }
        cleaned = REJECT_INTENT.matcher(cleaned).replaceAll(" ");
        cleaned = cleaned.replaceAll("[：:，,。.\\s]+", " ").trim();
        return cleaned.isBlank() ? null : cleaned;
    }

    // ==================== 调度 ====================

    private Map<String, Object> dispatch(ParsedIntent intent, InteractionContext context,
                                          SseEmitter emitter) {
        return switch (intent.action()) {
            case LIST -> doList(context, emitter);
            case APPROVE -> doApprove(intent, context, emitter);
            case REJECT -> doReject(intent, context, emitter);
            case HELP -> helpResult();
        };
    }

    private Map<String, Object> doList(InteractionContext context, SseEmitter emitter) {
        requireRead(context);
        String conversationId = blankToNull(context.getConversationId());
        List<ApprovalWorkflowResponse> pending = approvalService.listPending(conversationId);

        if (emitter != null) {
            sendEvent(emitter, SseEventFactory.approvalList(pending, pending.size()));
            for (ApprovalWorkflowResponse item : pending) {
                sendEvent(emitter, SseEventFactory.approvalRequired(
                        item.getApprovalId(), item.getExecutionId(), item.getTitle()));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", "list");
        result.put("count", pending.size());
        result.put("items", pending);
        if (pending.isEmpty()) {
            result.put("message", "当前没有待审批工单。");
        } else {
            StringBuilder sb = new StringBuilder("待审批工单共 ").append(pending.size()).append(" 条：\n");
            for (int i = 0; i < pending.size(); i++) {
                ApprovalWorkflowResponse item = pending.get(i);
                sb.append(i + 1).append(". ").append(item.getTitle())
                        .append("（").append(item.getApprovalId()).append("）\n");
            }
            sb.append("同意请回复「同意 appr_xxx」，拒绝请回复「拒绝 appr_xxx」。");
            result.put("message", sb.toString().trim());
        }
        return result;
    }

    private Map<String, Object> doApprove(ParsedIntent intent, InteractionContext context,
                                           SseEmitter emitter) {
        requireApprove(context);

        if (intent.approvalId() == null || intent.approvalId().isBlank()) {
            // 验收：无 ID 不得改库 — 仅二次确认提问
            List<ApprovalWorkflowResponse> pending = approvalService.listPending(
                    blankToNull(context.getConversationId()));
            if (emitter != null && !pending.isEmpty()) {
                sendEvent(emitter, SseEventFactory.approvalList(pending, pending.size()));
            }
            String message = buildConfirmAsk("同意", pending);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("action", "approve");
            result.put("confirmed", false);
            result.put("message", message);
            result.put("items", pending);
            return result;
        }

        if (emitter != null) {
            sendEvent(emitter, SseEventFactory.thinking("正在同意审批 " + intent.approvalId() + "..."));
        }
        ApprovalWorkflowResponse approved = approvalService.approve(intent.approvalId(), "对话内同意");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", "approve");
        result.put("confirmed", true);
        result.put("approvalId", approved.getApprovalId());
        result.put("status", approved.getStatus());
        result.put("item", approved);
        result.put("message", "已同意审批 " + approved.getApprovalId()
                + "：" + nullToEmpty(approved.getTitle()));
        return result;
    }

    private Map<String, Object> doReject(ParsedIntent intent, InteractionContext context,
                                          SseEmitter emitter) {
        requireApprove(context);

        if (intent.approvalId() == null || intent.approvalId().isBlank()) {
            List<ApprovalWorkflowResponse> pending = approvalService.listPending(
                    blankToNull(context.getConversationId()));
            if (emitter != null && !pending.isEmpty()) {
                sendEvent(emitter, SseEventFactory.approvalList(pending, pending.size()));
            }
            String message = buildConfirmAsk("拒绝", pending);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("action", "reject");
            result.put("confirmed", false);
            result.put("message", message);
            result.put("items", pending);
            return result;
        }

        String reason = intent.reason() != null ? intent.reason() : "对话内拒绝";
        if (emitter != null) {
            sendEvent(emitter, SseEventFactory.thinking("正在拒绝审批 " + intent.approvalId() + "..."));
        }
        ApprovalWorkflowResponse rejected = approvalService.reject(intent.approvalId(), reason);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", "reject");
        result.put("confirmed", true);
        result.put("approvalId", rejected.getApprovalId());
        result.put("status", rejected.getStatus());
        result.put("item", rejected);
        result.put("message", "已拒绝审批 " + rejected.getApprovalId()
                + "：" + nullToEmpty(rejected.getTitle())
                + "（原因：" + reason + "）");
        return result;
    }

    private Map<String, Object> helpResult() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", "help");
        result.put("message", HELP_MSG.trim());
        return result;
    }

    private String buildConfirmAsk(String verb, List<ApprovalWorkflowResponse> pending) {
        if (pending == null || pending.isEmpty()) {
            return "您说了「" + verb + "」，但当前没有待审批工单，也未提供审批 ID。请先查看待办或带上 appr_xxx。";
        }
        ApprovalWorkflowResponse first = pending.get(0);
        return "检测到「" + verb + "」但未提供审批 ID，禁止默认同意。"
                + "当前待办共 " + pending.size() + " 条，第一条是「"
                + nullToEmpty(first.getTitle()) + "」（" + first.getApprovalId() + "）。"
                + "请回复「" + verb + " " + first.getApprovalId() + "」以确认；"
                + "或「查看审批」浏览全部待办。";
    }

    // ==================== 权限 ====================

    /**
     * 读列表：需 approval:read 或 approval:approve（与 REST 对齐，设计文档中的 write 对应 approve）.
     */
    private void requireRead(InteractionContext context) {
        if (!flag(context, "canRead") && !flag(context, "canApprove")) {
            throw new NotPermissionException("approval:read");
        }
    }

    /** 同意/拒绝：需 approval:approve */
    private void requireApprove(InteractionContext context) {
        if (!flag(context, "canApprove")) {
            throw new NotPermissionException("approval:approve");
        }
    }

    private static boolean flag(InteractionContext context, String key) {
        Map<String, Object> cfg = context.getSearchConfig();
        if (cfg == null) {
            return false;
        }
        Object v = cfg.get(key);
        return Boolean.TRUE.equals(v) || "true".equalsIgnoreCase(String.valueOf(v));
    }

    // ==================== SSE 辅助 ====================

    private void streamPlainAndDone(SseEmitter emitter, String conversationId, String text) {
        sendEvent(emitter, SseEventFactory.token(text));
        Message assistantMsg = messageService.saveAssistantMessage(
                conversationId, text, estimateTokenCount(text));
        sendEvent(emitter, SseEventFactory.done(estimateTokenCount(text), assistantMsg.getMessageId()));
        emitter.complete();
    }

    private void sendErrorAndComplete(SseEmitter emitter, String conversationId, String message, int code) {
        sendEvent(emitter, SseEventFactory.error(message, code));
        try {
            String content = "安全审批失败: " + message;
            messageService.saveAssistantMessage(conversationId, content, estimateTokenCount(content));
        } catch (Exception e) {
            log.warn("[ApprovalStrategy] 保存失败消息异常", e);
        }
        try {
            emitter.complete();
        } catch (Exception ignored) {
            // 连接已关闭
        }
    }

    private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "sse-approval-heartbeat"));
        long intervalMs = sessionConfig != null
                ? sessionConfig.getSseHeartbeatIntervalMs() : 15_000L;
        executor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data(""));
            } catch (Exception ignored) {
                // 连接已关闭
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        return executor;
    }

    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (Exception e) {
            log.warn("[ApprovalStrategy] SSE 发送失败", e);
        }
    }

    private int estimateTokenCount(String content) {
        if (content == null) {
            return 0;
        }
        return (int) Math.ceil(content.length() * 0.5);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    // ==================== 内部类型 ====================

    enum Action {
        LIST, APPROVE, REJECT, HELP
    }

    record ParsedIntent(Action action, String approvalId, String reason) {}
}
