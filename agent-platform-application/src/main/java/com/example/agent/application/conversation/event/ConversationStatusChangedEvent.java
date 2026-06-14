package com.example.agent.application.conversation.event;

import com.example.agent.domain.conversation.entity.Conversation;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 会话状态变更事件 — Observer 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
public class ConversationStatusChangedEvent extends ApplicationEvent {
    private final Conversation conversation;
    private final ConversationStatus oldStatus;

    public ConversationStatusChangedEvent(Object source, Conversation conversation, ConversationStatus oldStatus) {
        super(source);
        this.conversation = conversation;
        this.oldStatus = oldStatus;
    }
}
