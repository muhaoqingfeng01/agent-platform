package com.example.agent.application.conversation;

import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.tool.ToolApplicationService;
import com.example.agent.application.tool.dto.CallableToolSummary;
import com.example.agent.application.tool.dto.ToolTestResponse;
import com.example.agent.infrastructure.config.ai.DeepSeekUtilityClient;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话内工具桥接 — 显式两轮：非流式决策 → 执行 MCP/HTTP → 结果注入最终 Prompt.
 *
 * <p>fail-open：无工具 / 决策失败 / 解析失败时返回空上下文，不阻断主对话.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationToolBridge {

    private static final int MAX_CALLS = 3;
    private static final int SCHEMA_MAX_LEN = 800;

    private final ToolApplicationService toolApplicationService;
    private final DeepSeekUtilityClient deepSeekUtilityClient;
    private final ObjectMapper objectMapper;

    /**
     * 规划并执行本轮工具调用.
     *
     * @param tenantId       租户
     * @param conversationId 会话
     * @param userContent    用户输入
     * @param emitter        SSE
     * @return 工具轮次结果（可能含审批中断）
     */
    public ToolRoundResult planAndExecute(Long tenantId, String conversationId,
                                          String userContent, SseEmitter emitter) {
        try {
            List<CallableToolSummary> tools = toolApplicationService.listActiveCallableTools(tenantId);
            if (tools == null || tools.isEmpty()) {
                return ToolRoundResult.empty();
            }

            sendSafe(emitter, SseEventFactory.thinking("正在判断是否需要调用工具..."));

            String decisionJson = decide(tools, userContent);
            List<PlannedCall> calls = parseCalls(decisionJson, tools);
            if (calls.isEmpty()) {
                return ToolRoundResult.empty();
            }

            StringBuilder context = new StringBuilder();
            context.append("\n\n## 本轮工具调用结果\n");
            boolean executed = false;

            for (PlannedCall call : calls) {
                Map<String, Object> params = call.params() != null ? call.params() : Map.of();
                sendSafe(emitter, SseEventFactory.toolCall(call.toolId(), call.name(), params));

                ToolTestResponse resp = toolApplicationService.invokeForConversation(
                        call.toolId(), params, conversationId);

                if (resp.isPendingApproval()) {
                    ApprovalWorkflowResponse approval = ApprovalWorkflowResponse.builder()
                            .approvalId(resp.getApprovalId())
                            .conversationId(conversationId)
                            .toolId(call.toolId())
                            .title("工具调用待审批: " + call.name())
                            .status("PENDING")
                            .build();
                    return ToolRoundResult.builder()
                            .executed(executed)
                            .toolContext(context.toString())
                            .approval(approval)
                            .build();
                }

                executed = true;
                sendSafe(emitter, SseEventFactory.toolResult(
                        call.toolId(),
                        call.name(),
                        resp.isSuccess(),
                        resp.getResult(),
                        resp.getErrorMessage()));

                context.append("- 工具 ").append(call.name()).append(" (").append(call.toolId()).append(")\n");
                if (resp.isSuccess()) {
                    context.append("  结果: ").append(truncate(String.valueOf(resp.getResult()), 1500)).append("\n");
                } else {
                    context.append("  失败: ").append(resp.getErrorMessage()).append("\n");
                }
            }

            return ToolRoundResult.builder()
                    .executed(executed)
                    .toolContext(executed ? context.toString() : "")
                    .build();
        } catch (Exception e) {
            log.warn("[ToolBridge] 工具轮次失败，降级为普通对话: convId={}, err={}",
                    conversationId, e.getMessage());
            return ToolRoundResult.empty();
        }
    }

    private String decide(List<CallableToolSummary> tools, String userContent) {
        StringBuilder catalog = new StringBuilder();
        for (CallableToolSummary t : tools) {
            catalog.append("- toolId=").append(t.getToolId())
                    .append(", name=").append(t.getName())
                    .append(", type=").append(t.getToolType())
                    .append(", desc=").append(nullToEmpty(t.getDescription()))
                    .append(", requireApproval=").append(t.isRequireApproval());
            String schema = t.getInputSchema();
            if (schema != null && !schema.isBlank()) {
                catalog.append(", inputSchema=").append(truncate(schema, SCHEMA_MAX_LEN));
            }
            catalog.append("\n");
        }

        String prompt = """
                你是工具路由助手。根据用户问题决定是否需要调用下列工具。
                只返回 JSON，不要其它文字。格式二选一：
                {"action":"call_tools","calls":[{"toolId":"tool_xxx","params":{}}]}
                {"action":"none"}
                规则：最多 3 个 calls；toolId 必须来自列表；不需要工具时返回 none。

                ## 可用工具
                %s

                ## 用户问题
                %s
                """.formatted(catalog, userContent == null ? "" : userContent);

        return deepSeekUtilityClient.completeText(prompt, 512);
    }

    private List<PlannedCall> parseCalls(String raw, List<CallableToolSummary> tools) {
        List<PlannedCall> result = new ArrayList<>();
        try {
            String json = extractJson(raw);
            JsonNode root = objectMapper.readTree(json);
            String action = root.path("action").asText("none");
            if (!"call_tools".equalsIgnoreCase(action)) {
                return result;
            }
            Map<String, CallableToolSummary> byId = new HashMap<>();
            for (CallableToolSummary t : tools) {
                byId.put(t.getToolId(), t);
            }
            JsonNode calls = root.path("calls");
            if (!calls.isArray()) {
                return result;
            }
            for (JsonNode node : calls) {
                if (result.size() >= MAX_CALLS) {
                    break;
                }
                String toolId = node.path("toolId").asText(null);
                if (toolId == null || !byId.containsKey(toolId)) {
                    continue;
                }
                CallableToolSummary meta = byId.get(toolId);
                Map<String, Object> params = new HashMap<>();
                JsonNode paramsNode = node.path("params");
                if (paramsNode.isObject()) {
                    params = objectMapper.convertValue(paramsNode, new TypeReference<Map<String, Object>>() {});
                }
                result.add(new PlannedCall(toolId, meta.getName(), params));
            }
        } catch (Exception e) {
            log.debug("[ToolBridge] 决策 JSON 解析失败: {}", e.getMessage());
        }
        return result;
    }

    private String extractJson(String text) {
        if (text == null) {
            return "{}";
        }
        String trimmed = text.trim();
        if (trimmed.contains("```json")) {
            int s = trimmed.indexOf("```json") + 7;
            int e = trimmed.indexOf("```", s);
            return e > s ? trimmed.substring(s, e).trim() : trimmed;
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    private void sendSafe(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (Exception e) {
            log.debug("[ToolBridge] SSE 推送失败: {}", e.getMessage());
        }
    }

    private static String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private record PlannedCall(String toolId, String name, Map<String, Object> params) {}

    @Data
    @Builder
    public static class ToolRoundResult {
        private boolean executed;
        private String toolContext;
        private ApprovalWorkflowResponse approval;

        public static ToolRoundResult empty() {
            return ToolRoundResult.builder().executed(false).toolContext("").build();
        }

        public boolean hasApproval() {
            return approval != null && approval.getApprovalId() != null;
        }

        public boolean hasToolContext() {
            return toolContext != null && !toolContext.isBlank();
        }
    }
}
