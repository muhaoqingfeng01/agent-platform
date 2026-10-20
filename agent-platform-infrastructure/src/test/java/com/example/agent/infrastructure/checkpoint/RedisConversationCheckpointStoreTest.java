package com.example.agent.infrastructure.checkpoint;

import com.example.agent.domain.conversation.valueobject.AgentPhase;
import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Redis 检查点 — RPUSH + LTRIM 最近 5 条，坏 JSON 跳过.
 */
@ExtendWith(MockitoExtension.class)
class RedisConversationCheckpointStoreTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ListOperations<String, String> listOperations;
    @Mock
    private SessionConfig sessionConfig;

    private RedisConversationCheckpointStore store;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(sessionConfig.getCheckpointMaxCount()).thenReturn(5);
        lenient().when(sessionConfig.getCheckpointTtlMinutes()).thenReturn(30);
        store = new RedisConversationCheckpointStore(stringRedisTemplate, objectMapper, sessionConfig);
    }

    @Test
    void save_rightPushAndTrimLastFive() {
        ConversationCheckpoint checkpoint = ConversationCheckpoint.builder()
                .checkpointId("ckpt_1")
                .conversationId("conv_1")
                .state(AgentPhase.AWAIT_PARAM)
                .intentCode("QUERY_ORDER")
                .slotsJson("{\"required\":[],\"filled\":{}}")
                .createdAt(LocalDateTime.of(2026, 9, 4, 16, 0))
                .build();

        store.save(3L, "conv_1", checkpoint);

        String key = "ckpt:3:conv_1";
        verify(listOperations).rightPush(eq(key), anyString());
        verify(listOperations).trim(key, -5L, -1L);
        verify(stringRedisTemplate).expire(key, Duration.ofMinutes(30));
    }

    @Test
    void loadLatest_skipsCorruptThenReturnsNewestValid() throws Exception {
        ConversationCheckpoint older = ConversationCheckpoint.builder()
                .checkpointId("ckpt_old")
                .conversationId("conv_1")
                .state(AgentPhase.AWAIT_PARAM)
                .intentCode("QUERY_ORDER")
                .createdAt(LocalDateTime.of(2026, 9, 4, 15, 0))
                .build();
        ConversationCheckpoint newer = ConversationCheckpoint.builder()
                .checkpointId("ckpt_new")
                .conversationId("conv_1")
                .state(AgentPhase.EXECUTING)
                .intentCode("QUERY_ORDER")
                .stepIndex(2)
                .createdAt(LocalDateTime.of(2026, 9, 4, 16, 0))
                .build();
        String olderJson = objectMapper.writeValueAsString(
                RedisConversationCheckpointStore.CheckpointRedisEntry.from(older));
        String newerJson = objectMapper.writeValueAsString(
                RedisConversationCheckpointStore.CheckpointRedisEntry.from(newer));
        when(listOperations.range("ckpt:3:conv_1", 0, -1))
                .thenReturn(List.of(olderJson, "{not-json", newerJson));

        Optional<ConversationCheckpoint> latest = store.loadLatest(3L, "conv_1");
        assertTrue(latest.isPresent());
        assertEquals("ckpt_new", latest.get().getCheckpointId());
        assertEquals(AgentPhase.EXECUTING, latest.get().getState());
        assertEquals(2, latest.get().getStepIndex());
    }

    @Test
    void loadLatest_allCorrupt_returnsEmpty() {
        when(listOperations.range("ckpt:3:conv_1", 0, -1))
                .thenReturn(List.of("{bad", "also-bad"));
        assertTrue(store.loadLatest(3L, "conv_1").isEmpty());
    }

    @Test
    void clear_deletesKey() {
        store.clear(3L, "conv_1");
        verify(stringRedisTemplate).delete("ckpt:3:conv_1");
    }

    @Test
    void save_capturesStateCodeInJson() {
        ConversationCheckpoint checkpoint = ConversationCheckpoint.builder()
                .checkpointId("ckpt_1")
                .conversationId("conv_1")
                .state(AgentPhase.CONFIRMATION)
                .executionId("exec_1")
                .stepIndex(1)
                .createdAt(LocalDateTime.of(2026, 9, 4, 16, 0))
                .build();
        store.save(3L, "conv_1", checkpoint);

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(listOperations).rightPush(eq("ckpt:3:conv_1"), jsonCaptor.capture());
        assertTrue(jsonCaptor.getValue().contains("CONFIRMATION"));
        assertTrue(jsonCaptor.getValue().contains("exec_1"));
    }
}
