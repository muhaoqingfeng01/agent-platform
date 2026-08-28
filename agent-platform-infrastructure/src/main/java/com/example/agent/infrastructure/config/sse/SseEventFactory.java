package com.example.agent.infrastructure.config.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SSE 事件工厂 — Factory Method 模式统一事件构建.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public final class SseEventFactory {

    public static final String EVENT_TOKEN = "token";
    public static final String EVENT_TOOL_CALL = "tool_call";
    public static final String EVENT_TOOL_RESULT = "tool_result";
    public static final String EVENT_THINKING = "thinking";
    public static final String EVENT_REFERENCES = "references";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_DONE = "done";
    public static final String EVENT_TASK_PLAN = "task_plan";
    public static final String EVENT_TASK_STEP = "task_step";
    public static final String EVENT_APPROVAL_REQUIRED = "approval_required";
    public static final String EVENT_ANALYSIS_TABLE = "analysis_table";

    private SseEventFactory() {}

    public static SseEmitter.SseEventBuilder token(String token) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TOKEN)
                .data(token);
    }

    public static SseEmitter.SseEventBuilder toolCall(String toolName, Map<String, Object> params) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TOOL_CALL)
                .data(Map.of("tool", toolName, "status", "calling", "params", params));
    }

    public static SseEmitter.SseEventBuilder toolResult(String toolName, Object result) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TOOL_RESULT)
                .data(Map.of("tool", toolName, "status", "done", "result", result));
    }

    public static SseEmitter.SseEventBuilder thinking(String message) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_THINKING)
                .data(message);
    }

    /**
     * 知识库检索命中文件的引用信息事件.
     * <p>
     * 在 LLM 流式输出之前推送，前端可据此渲染"参考文档"侧栏，
     * 提供文件预览和下载入口。
     *
     * @param references 文件引用列表（来源: {@code SearchResultDTO.getDocuments()}）
     */
    public static SseEmitter.SseEventBuilder references(Object references) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_REFERENCES)
                .data(references);
    }

    public static SseEmitter.SseEventBuilder error(String message, int code) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_ERROR)
                .data(Map.of("code", code, "message", message));
    }

    public static SseEmitter.SseEventBuilder done(int totalTokens, String messageId) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_DONE)
                .data(Map.of("status", "completed", "tokens", totalTokens, "messageId", messageId));
    }

    /**
     * 任务规划完成事件 — 推送 DAG 节点列表，供前端渲染步骤清单.
     *
     * @param executionId 执行 ID
     * @param totalSteps  总步骤数
     * @param nodes       节点列表（id / action / description / dep）
     */
    public static SseEmitter.SseEventBuilder taskPlan(String executionId, int totalSteps, Object nodes) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("executionId", executionId);
        payload.put("totalSteps", totalSteps);
        payload.put("nodes", nodes != null ? nodes : java.util.List.of());
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TASK_PLAN)
                .data(payload);
    }

    /**
     * 任务步骤进度事件 — 步骤开始/成功/失败/等待审批时推送.
     */
    public static SseEmitter.SseEventBuilder taskStep(String executionId, String stepId,
                                                       String action, String status, Object result) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("executionId", executionId);
        payload.put("stepId", stepId);
        payload.put("action", action != null ? action : "");
        payload.put("status", status);
        if (result != null) {
            payload.put("result", result);
        }
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TASK_STEP)
                .data(payload);
    }

    /**
     * 需要审批事件 — 本轮 SSE 结束前通知前端，审批卡片走 WebSocket.
     */
    public static SseEmitter.SseEventBuilder approvalRequired(String approvalId, String executionId, String title) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("approvalId", approvalId);
        payload.put("executionId", executionId);
        payload.put("title", title != null ? title : "需要审批");
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_APPROVAL_REQUIRED)
                .data(payload);
    }

    /**
     * 分析表格事件 — 在 LLM 归纳前推送只读查询结果，前端可渲染表格.
     *
     * @param title   表格标题（如「知识库命中趋势」）
     * @param columns 列名列表
     * @param rows    行数据（每行为 Map 或 List）
     * @param empty   是否无数据（区间内无记录）
     */
    public static SseEmitter.SseEventBuilder analysisTable(String title, Object columns,
                                                            Object rows, boolean empty) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", title != null ? title : "分析数据");
        payload.put("columns", columns != null ? columns : java.util.List.of());
        payload.put("rows", rows != null ? rows : java.util.List.of());
        payload.put("empty", empty);
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_ANALYSIS_TABLE)
                .data(payload);
    }
}
