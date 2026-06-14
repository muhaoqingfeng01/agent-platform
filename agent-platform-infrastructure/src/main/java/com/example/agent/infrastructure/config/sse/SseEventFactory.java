package com.example.agent.infrastructure.config.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_DONE = "done";

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
}
