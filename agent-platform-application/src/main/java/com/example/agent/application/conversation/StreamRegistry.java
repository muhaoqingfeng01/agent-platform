package com.example.agent.application.conversation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSE 流会话注册表 — 支持按 streamId 取消进行中的流式输出.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
public class StreamRegistry {

    private final ConcurrentHashMap<String, StreamHandle> byStreamId = new ConcurrentHashMap<>();
    /** 已取消的 streamId 集合 — emitter 注销后仍可供管线检查 */
    private final ConcurrentHashMap.KeySetView<String, Boolean> cancelledIds = ConcurrentHashMap.newKeySet();

    /**
     * 注册 emitter，返回 streamId；emitter 完成/超时/出错时自动注销活跃句柄.
     */
    public String register(String conversationId, SseEmitter emitter) {
        String streamId = UUID.randomUUID().toString().replace("-", "");
        AtomicBoolean cancelled = new AtomicBoolean(false);
        StreamHandle handle = new StreamHandle(streamId, conversationId, emitter, cancelled);
        byStreamId.put(streamId, handle);

        Runnable cleanup = () -> {
            byStreamId.remove(streamId, handle);
            log.debug("[StreamRegistry] 注销活跃句柄: streamId={}", streamId);
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        log.debug("[StreamRegistry] 注册: streamId={}, convId={}", streamId, conversationId);
        return streamId;
    }

    /**
     * 取消指定流. conversationId 非空时须与注册时一致.
     *
     * @return true 表示找到并取消；false 表示流已结束或不存在
     */
    public boolean cancel(String streamId, String conversationId) {
        StreamHandle handle = byStreamId.get(streamId);
        if (handle == null) {
            return cancelledIds.contains(streamId);
        }
        if (conversationId != null && !conversationId.isBlank()
                && !conversationId.equals(handle.conversationId())) {
            throw new IllegalArgumentException("streamId 与 conversationId 不匹配");
        }
        handle.cancelled().set(true);
        cancelledIds.add(streamId);
        byStreamId.remove(streamId, handle);
        try {
            handle.emitter().complete();
        } catch (Exception e) {
            log.debug("[StreamRegistry] complete 时忽略: streamId={}, msg={}", streamId, e.getMessage());
        }
        log.info("[StreamRegistry] 已取消: streamId={}, convId={}", streamId, handle.conversationId());
        return true;
    }

    public boolean isCancelled(String streamId) {
        if (streamId == null || streamId.isBlank()) {
            return false;
        }
        if (cancelledIds.contains(streamId)) {
            return true;
        }
        StreamHandle handle = byStreamId.get(streamId);
        return handle != null && handle.cancelled().get();
    }

    /** 测试辅助：清除取消标记 */
    public void clearCancelled(String streamId) {
        cancelledIds.remove(streamId);
    }

    /** 测试/运维：当前活跃流数量 */
    public int size() {
        return byStreamId.size();
    }

    public record StreamHandle(
            String streamId,
            String conversationId,
            SseEmitter emitter,
            AtomicBoolean cancelled
    ) {}
}
