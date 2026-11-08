package com.example.agent.application.conversation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StreamRegistry 单元测试.
 */
class StreamRegistryTest {

    private StreamRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new StreamRegistry();
    }

    @Test
    void register_thenCancel_marksCancelledAndRemovesActive() {
        SseEmitter emitter = new SseEmitter(10_000L);
        String streamId = registry.register("conv-1", emitter);

        assertEquals(1, registry.size());
        assertFalse(registry.isCancelled(streamId));

        assertTrue(registry.cancel(streamId, "conv-1"));
        assertTrue(registry.isCancelled(streamId));
        assertEquals(0, registry.size());
    }

    @Test
    void cancel_wrongConversation_throws() {
        SseEmitter emitter = new SseEmitter(10_000L);
        String streamId = registry.register("conv-1", emitter);

        assertThrows(IllegalArgumentException.class, () -> registry.cancel(streamId, "conv-other"));
    }

    @Test
    void cancel_unknownStream_returnsFalse() {
        assertFalse(registry.cancel("missing", "conv-1"));
    }

    @Test
    void cancel_keepsCancelledFlagAfterUnregister() {
        SseEmitter emitter = new SseEmitter(10_000L);
        String streamId = registry.register("conv-1", emitter);
        registry.cancel(streamId, "conv-1");
        assertTrue(registry.isCancelled(streamId));
        assertEquals(0, registry.size());
    }
}
