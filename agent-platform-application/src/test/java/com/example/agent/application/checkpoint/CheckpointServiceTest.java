package com.example.agent.application.checkpoint;

import com.example.agent.domain.conversation.ConversationCheckpointStore;
import com.example.agent.domain.conversation.valueobject.AgentPhase;
import com.example.agent.domain.conversation.valueobject.ConversationCheckpoint;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 检查点应用服务 — 仅续传可恢复相位，INIT/DONE 不注入.
 */
@ExtendWith(MockitoExtension.class)
class CheckpointServiceTest {

    @Mock
    private ConversationCheckpointStore checkpointStore;

    private CheckpointService service;
    private SlotCollectionCodec codec;

    @BeforeEach
    void setUp() {
        codec = new SlotCollectionCodec(new ObjectMapper());
        service = new CheckpointService(checkpointStore, codec);
        TenantContext.setTenantId(8L);
        TenantContext.setUserId("u1");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void recordTransition_savesSnapshot() {
        SlotCollection slots = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("name", "orderId", "description", "订单号")));
        ConversationCheckpoint saved = service.recordTransition(
                "conv_1", AgentPhase.AWAIT_PARAM, "QUERY_ORDER", slots, null, null);

        ArgumentCaptor<ConversationCheckpoint> captor = ArgumentCaptor.forClass(ConversationCheckpoint.class);
        verify(checkpointStore).save(eq(8L), eq("conv_1"), captor.capture());
        assertEquals(AgentPhase.AWAIT_PARAM, captor.getValue().getState());
        assertEquals("QUERY_ORDER", captor.getValue().getIntentCode());
        assertTrue(captor.getValue().getCheckpointId().startsWith("ckpt_"));
        assertEquals(saved.getCheckpointId(), captor.getValue().getCheckpointId());
    }

    @Test
    void restoreForNewStream_skipsInitAndDone() {
        when(checkpointStore.loadLatest(8L, "conv_1")).thenReturn(Optional.of(checkpoint(AgentPhase.INIT)));
        assertTrue(service.restoreForNewStream("conv_1").isEmpty());

        when(checkpointStore.loadLatest(8L, "conv_1")).thenReturn(Optional.of(checkpoint(AgentPhase.DONE)));
        assertTrue(service.restoreForNewStream("conv_1").isEmpty());
    }

    @Test
    void restoreForNewStream_returnsAwaitParam() {
        ConversationCheckpoint ckpt = checkpoint(AgentPhase.AWAIT_PARAM);
        when(checkpointStore.loadLatest(8L, "conv_1")).thenReturn(Optional.of(ckpt));
        Optional<ConversationCheckpoint> restored = service.restoreForNewStream("conv_1");
        assertTrue(restored.isPresent());
        assertEquals(AgentPhase.AWAIT_PARAM, restored.get().getState());
    }

    @Test
    void attachLatest_injectsAttributes() {
        ConversationCheckpoint ckpt = checkpoint(AgentPhase.AWAIT_PARAM);
        when(checkpointStore.loadLatest(8L, "conv_1")).thenReturn(Optional.of(ckpt));
        InteractionContext context = InteractionContext.forConversation(
                "补充一下", "conv_1", 8L, "u1", null);
        service.attachLatest(context);
        assertTrue(service.restoredOf(context).isPresent());
        assertFalse(service.slotsOf(context).isComplete());
    }

    @Test
    void attachLatest_blankConversation_skips() {
        InteractionContext context = InteractionContext.forConversation("hi", "  ", 8L, "u1", null);
        service.attachLatest(context);
        verify(checkpointStore, never()).loadLatest(any(), any());
        assertTrue(service.restoredOf(context).isEmpty());
    }

    private ConversationCheckpoint checkpoint(AgentPhase phase) {
        SlotCollection slots = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("name", "orderId", "description", "订单号")));
        return ConversationCheckpoint.builder()
                .checkpointId("ckpt_1")
                .conversationId("conv_1")
                .state(phase)
                .intentCode("QUERY_ORDER")
                .slotsJson(codec.encode(slots))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
