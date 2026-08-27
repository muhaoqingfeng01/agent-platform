package com.example.agent.infrastructure.config.sse;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SseEmitter 工厂 — Factory Method 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public final class SseEmitterFactory {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterFactory.class);

    private SseEmitterFactory() {}

    /**
     * 创建带标准配置的 SseEmitter.
     *
     * @param timeoutMs 超时毫秒数
     */
    public static SseEmitter create(long timeoutMs) {
        SseEmitter emitter = new SseEmitter(timeoutMs);
        emitter.onTimeout(() -> log.warn("[SSE] 连接超时"));
        emitter.onCompletion(() -> log.debug("[SSE] 流式完成"));
        emitter.onError(ex -> log.error("[SSE] 连接错误", ex));
        return emitter;
    }

    /**
     * 创建 SSE 连接并写入禁止代理缓冲的响应头，避免 Nginx 把 token 攒成一整段。
     */
    public static SseEmitter create(long timeoutMs, HttpServletResponse response) {
        if (response != null) {
            response.setHeader("X-Accel-Buffering", "no");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        return create(timeoutMs);
    }
}
