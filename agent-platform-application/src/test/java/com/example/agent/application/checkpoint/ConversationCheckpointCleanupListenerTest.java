package com.example.agent.application.checkpoint;

import com.example.agent.application.conversation.event.ConversationStatusChangedEvent;
import com.example.agent.domain.conversation.entity.Conversation;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * 会话关闭/归档清理检查点.
 */
@ExtendWith(MockitoExtension.class)
class ConversationCheckpointCleanupListenerTest {

    @Mock
    private CheckpointService checkpointService;

    private ConversationCheckpointCleanupListener listener;

    @BeforeEach
    void setUp() {
        listener = new ConversationCheckpointCleanupListener(checkpointService);
    }

    @Test
    void closed_clears() {
        listener.onConversationStatusChanged(event(ConversationStatus.CLOSED));
        verify(checkpointService).clear("conv_1");
    }

    @Test
    void archived_clears() {
        listener.onConversationStatusChanged(event(ConversationStatus.ARCHIVED));
        verify(checkpointService).clear("conv_1");
    }

    @Test
    void active_doesNotClear() {
        listener.onConversationStatusChanged(event(ConversationStatus.ACTIVE));
        verify(checkpointService, never()).clear("conv_1");
    }

    private ConversationStatusChangedEvent event(ConversationStatus status) {
        Conversation conv = Conversation.builder()
                .conversationId("conv_1")
                .status(status)
                .build();
        return new ConversationStatusChangedEvent(this, conv, ConversationStatus.ACTIVE);
    }
}
