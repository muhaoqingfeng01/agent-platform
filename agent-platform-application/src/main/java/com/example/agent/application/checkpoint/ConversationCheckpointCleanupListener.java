package com.example.agent.application.checkpoint;

import com.example.agent.application.conversation.event.ConversationStatusChangedEvent;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 会话关闭/归档时清理检查点，避免过期快照被误恢复.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConversationCheckpointCleanupListener {

    private final CheckpointService checkpointService;

    @EventListener
    public void onConversationStatusChanged(ConversationStatusChangedEvent event) {
        if (event == null || event.getConversation() == null) {
            return;
        }
        ConversationStatus status = event.getConversation().getStatus();
        if (status == ConversationStatus.CLOSED || status == ConversationStatus.ARCHIVED) {
            String conversationId = event.getConversation().getConversationId();
            checkpointService.clear(conversationId);
            log.info("[Checkpoint] 会话结束已清理检查点: convId={}, status={}", conversationId, status);
        }
    }
}
