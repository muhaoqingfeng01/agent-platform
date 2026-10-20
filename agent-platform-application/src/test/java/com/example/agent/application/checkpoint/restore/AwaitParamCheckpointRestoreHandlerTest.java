package com.example.agent.application.checkpoint.restore;

import com.example.agent.application.checkpoint.CheckpointService;
import com.example.agent.application.checkpoint.SlotAskSupport;
import com.example.agent.application.checkpoint.SlotCollectionCodec;
import com.example.agent.application.conversation.MessageApplicationService;
import com.example.agent.domain.conversation.ConversationCheckpointStore;
import com.example.agent.domain.conversation.entity.Message;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 缺槽断点续传 — 未齐则追问「请继续补充 xx」，齐则进入 EXECUTING.
 */
@ExtendWith(MockitoExtension.class)
class AwaitParamCheckpointRestoreHandlerTest {

    @Mock
    private ConversationCheckpointStore checkpointStore;
    @Mock
    private MessageApplicationService messageService;

    private CheckpointService checkpointService;
    private AwaitParamCheckpointRestoreHandler handler;
    private SlotCollectionCodec codec;

    @BeforeEach
    void setUp() {
        codec = new SlotCollectionCodec(new ObjectMapper());
        checkpointService = new CheckpointService(checkpointStore, codec);
        SlotAskSupport slotAskSupport = new SlotAskSupport(checkpointService, messageService);
        handler = new AwaitParamCheckpointRestoreHandler(checkpointService, slotAskSupport);
        TenantContext.setTenantId(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void handle_stillMissing_asksContinuePrompt() {
        when(messageService.saveAssistantMessage(anyString(), anyString(), anyInt()))
                .thenReturn(Message.builder().messageId("msg_ask").build());
        SlotCollection slots = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("name", "orderId", "description", "订单号"),
                Map.of("name", "date", "description", "日期")
        ));
        ConversationCheckpoint ckpt = checkpoint(slots);
        InteractionContext context = InteractionContext.forConversation(
                "A1001", "conv_1", 1L, "u1", new SseEmitter(1_000L));
        checkpointService.putSlots(context, slots);

        RestoreDecision decision = handler.handle(context, ckpt);

        assertEquals(RestoreDecision.HANDLED, decision);
        ArgumentCaptor<ConversationCheckpoint> captor = ArgumentCaptor.forClass(ConversationCheckpoint.class);
        verify(checkpointStore).save(eq(1L), eq("conv_1"), captor.capture());
        assertEquals(AgentPhase.AWAIT_PARAM, captor.getValue().getState());
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageService).saveAssistantMessage(eq("conv_1"), promptCaptor.capture(), eq(0));
        assertEquals("请继续补充日期", promptCaptor.getValue());
    }

    @Test
    void handle_allFilled_continuesExecuting() {
        SlotCollection slots = SlotCollection.fromRequiredParamMaps(List.of(
                Map.of("name", "orderId", "description", "订单号")
        ));
        ConversationCheckpoint ckpt = checkpoint(slots);
        InteractionContext context = InteractionContext.forConversation(
                "A1001", "conv_1", 1L, "u1", new SseEmitter(1_000L));
        checkpointService.putSlots(context, slots);

        RestoreDecision decision = handler.handle(context, ckpt);

        assertEquals(RestoreDecision.CONTINUE, decision);
        ArgumentCaptor<ConversationCheckpoint> captor = ArgumentCaptor.forClass(ConversationCheckpoint.class);
        verify(checkpointStore).save(eq(1L), eq("conv_1"), captor.capture());
        assertEquals(AgentPhase.EXECUTING, captor.getValue().getState());
        assertTrue(checkpointService.slotsOf(context).isComplete());
        assertEquals("A1001", checkpointService.slotsOf(context).getFilled().get("orderId"));
    }

    private ConversationCheckpoint checkpoint(SlotCollection slots) {
        return ConversationCheckpoint.builder()
                .checkpointId("ckpt_1")
                .conversationId("conv_1")
                .state(AgentPhase.AWAIT_PARAM)
                .intentCode("QUERY_ORDER")
                .slotsJson(codec.encode(slots))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
