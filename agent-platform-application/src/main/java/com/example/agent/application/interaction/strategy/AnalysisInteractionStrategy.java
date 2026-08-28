package com.example.agent.application.interaction.strategy;

import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.analytics.AnalyticsQueryPort;
import com.example.agent.domain.analytics.AnalyticsQueryPort.DailyHitStat;
import com.example.agent.domain.analytics.AnalyticsQueryPort.EvalOverallResult;
import com.example.agent.domain.analytics.AnalyticsQueryPort.FeedbackBucket;
import com.example.agent.domain.analytics.AnalyticsQueryPort.FeedbackDistResult;
import com.example.agent.domain.analytics.AnalyticsQueryPort.HitRateResult;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 分析推理交互策略 — 只读查询本平台指标后流式生成简报.
 * <p>
 * 流程：解析时间范围（一期仅最近 7/30 天）→ AnalyticsQueryPort 查询 →
 * SSE {@code analysis_table} → 约束 Prompt（不得编造表中没有的数字）→ ChatClient 流式输出。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisInteractionStrategy implements InteractionStrategy {

    private static final Pattern DAYS_7 = Pattern.compile(
            "(最近\\s*7\\s*天|近\\s*7\\s*天|过去\\s*7\\s*天|本周|7\\s*天)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DAYS_30 = Pattern.compile(
            "(最近\\s*30\\s*天|近\\s*30\\s*天|过去\\s*30\\s*天|本月|30\\s*天)", Pattern.CASE_INSENSITIVE);

    private static final String ASK_RANGE_MSG =
            "请明确分析时间范围。当前仅支持「最近 7 天」或「最近 30 天」，例如：本周知识库命中率趋势（最近7天）。";

    private static final String ANALYSIS_SYSTEM_PROMPT = """
            你是企业 Agent 平台的数据分析助手。请严格根据下方「分析数据表」归纳简报，回答用户问题。

            ## 分析区间
            %s

            ## 分析数据表
            %s

            ## 硬性规则（必须遵守）
            1. **不得编造** — 只能使用表中出现的数字与标签，禁止估算、补全或引用训练知识中的百分比
            2. **无数据时明确说明** — 若表标注「区间内无记录」或全部为空，必须明确回答「区间内无记录」，不要捏造趋势
            3. **简洁有条理** — 先给结论，再引用关键数字；可用条目列出
            4. **不越权发挥** — 不要建议查询外部数仓或本表未覆盖的指标

            ## 用户问题
            %s
            """;

    private final AnalyticsQueryPort analyticsQueryPort;
    private final MessageApplicationService messageService;
    private final ChatClient chatClient;
    private final SessionConfig sessionConfig;

    @Override
    public InteractionMode getMode() {
        return InteractionMode.ANALYSIS;
    }

    @Override
    public Object execute(InteractionContext context) {
        throw new BusinessException(400, "分析推理模式请使用流式端点");
    }

    @Override
    public void executeStream(InteractionContext context) {
        Long tenantId = context.getTenantId();
        String userId = context.getUserId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        SseEmitter emitter = (SseEmitter) context.getEmitter();
        if (emitter == null) {
            throw new IllegalStateException("分析推理流式模式需要 SseEmitter，但上下文中未提供");
        }
        String conversationId = context.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalStateException("分析推理流式模式需要 conversationId，但上下文中未提供");
        }
        String userInput = context.getUserInput();

        log.info("[AnalysisStrategy] 开始: convId={}, userId={}, inputLength={}",
                conversationId, userId, userInput != null ? userInput.length() : 0);

        ScheduledExecutorService heartbeatExecutor = null;
        try {
            messageService.saveUserMessage(conversationId, userInput);
            heartbeatExecutor = startHeartbeat(emitter);

            Integer days = parseDays(userInput);
            if (days == null) {
                sendEvent(emitter, SseEventFactory.thinking("未能识别时间范围"));
                streamPlainAndDone(emitter, conversationId, ASK_RANGE_MSG);
                return;
            }

            LocalDateTime to = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).withNano(0);
            LocalDateTime from = LocalDateTime.of(LocalDate.now().minusDays(days - 1L), LocalTime.MIN);
            String rangeLabel = "最近 " + days + " 天（" + from.toLocalDate() + " ~ " + to.toLocalDate() + "）";

            sendEvent(emitter, SseEventFactory.thinking("正在查询平台指标（" + rangeLabel + "）..."));

            boolean canObs = flag(context, "canObservability");
            boolean canEval = flag(context, "canEvaluation");
            StringBuilder tableText = new StringBuilder();
            boolean anyData = false;

            if (canObs && shouldQueryHit(userInput)) {
                HitRateResult hit = analyticsQueryPort.hitRate(tenantId, from, to);
                anyData = anyData || !hit.isEmpty();
                pushHitTable(emitter, hit, tableText);
            }
            if (canObs && shouldQueryFeedback(userInput)) {
                FeedbackDistResult fb = analyticsQueryPort.feedbackDist(tenantId, from, to);
                anyData = anyData || !fb.isEmpty();
                pushFeedbackTable(emitter, fb, tableText);
            }
            if (canEval && shouldQueryEval(userInput)) {
                EvalOverallResult eval = analyticsQueryPort.evalOverall(tenantId, from, to);
                anyData = anyData || !eval.isEmpty();
                pushEvalTable(emitter, eval, tableText);
            }

            // 若关键词未命中任何查询类型，按权限拉全量指标
            if (tableText.isEmpty()) {
                if (canObs) {
                    HitRateResult hit = analyticsQueryPort.hitRate(tenantId, from, to);
                    FeedbackDistResult fb = analyticsQueryPort.feedbackDist(tenantId, from, to);
                    anyData = !hit.isEmpty() || !fb.isEmpty();
                    pushHitTable(emitter, hit, tableText);
                    pushFeedbackTable(emitter, fb, tableText);
                }
                if (canEval) {
                    EvalOverallResult eval = analyticsQueryPort.evalOverall(tenantId, from, to);
                    anyData = anyData || !eval.isEmpty();
                    pushEvalTable(emitter, eval, tableText);
                }
            }

            if (!anyData) {
                String noData = "区间内无记录（" + rangeLabel + "）。请换一段时间范围或确认平台是否已有命中/反馈/评估数据。";
                sendEvent(emitter, SseEventFactory.thinking("查询完成，区间内无记录"));
                // 仍推空表，便于前端展示
                if (tableText.isEmpty()) {
                    tableText.append("（全部指标：区间内无记录）\n");
                }
                streamPlainAndDone(emitter, conversationId, noData);
                return;
            }

            sendEvent(emitter, SseEventFactory.thinking("数据已就绪，正在生成分析简报..."));
            String prompt = String.format(ANALYSIS_SYSTEM_PROMPT, rangeLabel, tableText, userInput);
            streamLlm(emitter, conversationId, tenantId, prompt);

        } catch (BusinessException e) {
            log.warn("[AnalysisStrategy] 业务失败: convId={}, msg={}", conversationId, e.getMessage());
            sendErrorAndComplete(emitter, conversationId, e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.error("[AnalysisStrategy] 流式执行异常: convId={}", conversationId, e);
            String msg = e.getMessage() != null ? e.getMessage() : "分析推理失败";
            sendErrorAndComplete(emitter, conversationId, msg, 500);
        } finally {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
            }
        }
    }

    @Override
    public int getPriority() {
        return 7;
    }

    // ==================== 时间与意图解析 ====================

    /**
     * 一期仅支持最近 7/30 天；无法识别时返回 null，由调用方引导用户重试.
     */
    Integer parseDays(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        if (DAYS_30.matcher(input).find()) {
            return 30;
        }
        if (DAYS_7.matcher(input).find()) {
            return 7;
        }
        return null;
    }

    private boolean shouldQueryHit(String input) {
        return containsAny(input, "命中", "hit", "检索量", "知识库");
    }

    private boolean shouldQueryFeedback(String input) {
        return containsAny(input, "反馈", "点赞", "点踩", "badcase", "BadCase", "差评", "好评");
    }

    private boolean shouldQueryEval(String input) {
        return containsAny(input, "评估", "评测", "分数", "evaluation", "overall");
    }

    private static boolean containsAny(String input, String... keys) {
        if (input == null) {
            return false;
        }
        String lower = input.toLowerCase();
        for (String k : keys) {
            if (lower.contains(k.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static boolean flag(InteractionContext context, String key) {
        Map<String, Object> cfg = context.getSearchConfig();
        if (cfg == null) {
            return false;
        }
        Object v = cfg.get(key);
        return Boolean.TRUE.equals(v) || "true".equalsIgnoreCase(String.valueOf(v));
    }

    // ==================== 表格推送 ====================

    private void pushHitTable(SseEmitter emitter, HitRateResult hit, StringBuilder tableText) {
        List<String> columns = List.of("日期", "命中次数", "写入Prompt次数");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (hit.daily() != null) {
            for (DailyHitStat d : hit.daily()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("日期", d.day());
                row.put("命中次数", d.hitCount());
                row.put("写入Prompt次数", d.usedInPromptCount());
                rows.add(row);
            }
        }
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("日期", "合计");
        summary.put("命中次数", hit.totalHits());
        summary.put("写入Prompt次数", hit.usedInPromptCount());
        if (hit.usedRate() != null) {
            summary.put("写入Prompt占比(%)", hit.usedRate());
        }
        rows.add(summary);

        boolean empty = hit.isEmpty();
        sendEvent(emitter, SseEventFactory.analysisTable("知识库命中趋势", columns, rows, empty));

        tableText.append("### 知识库命中趋势\n");
        if (empty) {
            tableText.append("区间内无记录\n\n");
            return;
        }
        tableText.append("合计命中次数=").append(hit.totalHits())
                .append("，写入Prompt次数=").append(hit.usedInPromptCount());
        if (hit.usedRate() != null) {
            tableText.append("，写入Prompt占比=").append(hit.usedRate()).append("%");
        }
        tableText.append("\n按日明细:\n");
        for (DailyHitStat d : hit.daily()) {
            tableText.append("- ").append(d.day()).append(": 命中=").append(d.hitCount())
                    .append(", 写入Prompt=").append(d.usedInPromptCount()).append("\n");
        }
        tableText.append("\n");
    }

    private void pushFeedbackTable(SseEmitter emitter, FeedbackDistResult fb, StringBuilder tableText) {
        List<String> columns = List.of("反馈类型", "数量");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (fb.buckets() != null) {
            for (FeedbackBucket b : fb.buckets()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("反馈类型", b.feedback());
                row.put("数量", b.count());
                rows.add(row);
            }
        }
        boolean empty = fb.isEmpty();
        sendEvent(emitter, SseEventFactory.analysisTable("消息反馈分布", columns, rows, empty));

        tableText.append("### 消息反馈分布\n");
        if (empty) {
            tableText.append("区间内无记录\n\n");
            return;
        }
        tableText.append("反馈总条数=").append(fb.total()).append("\n");
        for (FeedbackBucket b : fb.buckets()) {
            tableText.append("- ").append(b.feedback()).append(": ").append(b.count()).append("\n");
        }
        tableText.append("\n");
    }

    private void pushEvalTable(SseEmitter emitter, EvalOverallResult eval, StringBuilder tableText) {
        List<String> columns = List.of("指标", "值");
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(Map.of("指标", "评估次数", "值", eval.runCount()));
        rows.add(Map.of("指标", "成功次数", "值", eval.completedCount()));
        rows.add(Map.of("指标", "失败次数", "值", eval.failedCount()));
        rows.add(Map.of("指标", "平均 overallScore",
                "值", eval.avgOverallScore() != null ? eval.avgOverallScore() : "无"));

        boolean empty = eval.isEmpty();
        sendEvent(emitter, SseEventFactory.analysisTable("评估总体表现", columns, rows, empty));

        tableText.append("### 评估总体表现\n");
        if (empty) {
            tableText.append("区间内无记录\n\n");
            return;
        }
        tableText.append("评估次数=").append(eval.runCount())
                .append("，成功=").append(eval.completedCount())
                .append("，失败=").append(eval.failedCount());
        if (eval.avgOverallScore() != null) {
            tableText.append("，平均 overallScore=").append(eval.avgOverallScore());
        } else {
            tableText.append("，平均 overallScore=无");
        }
        tableText.append("\n\n");
    }

    // ==================== SSE / LLM ====================

    private void streamLlm(SseEmitter emitter, String conversationId, Long tenantId, String prompt) {
        StringBuilder fullResponse = new StringBuilder();
        AtomicInteger tokenCount = new AtomicInteger(0);

        chatClient.prompt()
                .user(prompt)
                .stream()
                .chatResponse()
                .doOnNext(resp -> {
                    String token = resp.getResult().getOutput().getText();
                    if (token != null) {
                        fullResponse.append(token);
                        tokenCount.incrementAndGet();
                        sendEvent(emitter, SseEventFactory.token(token));
                    }
                })
                .doOnComplete(() -> {
                    Message assistantMsg = messageService.saveAssistantMessage(
                            conversationId, fullResponse.toString(), tokenCount.get());
                    sendEvent(emitter, SseEventFactory.done(tokenCount.get(), assistantMsg.getMessageId()));
                    emitter.complete();
                    log.info("[AnalysisStrategy] 流式完成: convId={}, tokens={}, tenantId={}",
                            conversationId, tokenCount.get(), tenantId);
                })
                .doOnError(error -> {
                    log.error("[AnalysisStrategy] LLM 调用失败: convId={}", conversationId, error);
                    sendEvent(emitter, SseEventFactory.error("LLM 调用失败，请稍后重试", 500));
                    emitter.completeWithError(error);
                })
                .blockLast();
    }

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
            String content = "分析推理失败: " + message;
            messageService.saveAssistantMessage(conversationId, content, estimateTokenCount(content));
        } catch (Exception e) {
            log.warn("[AnalysisStrategy] 保存失败消息异常", e);
        }
        try {
            emitter.complete();
        } catch (Exception ignored) {
            // 连接已关闭
        }
    }

    private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "sse-analysis-heartbeat"));
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
            log.warn("[AnalysisStrategy] SSE 发送失败", e);
        }
    }

    private int estimateTokenCount(String content) {
        if (content == null) {
            return 0;
        }
        return (int) Math.ceil(content.length() * 0.5);
    }
}
