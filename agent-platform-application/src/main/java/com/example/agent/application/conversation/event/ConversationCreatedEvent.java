package com.example.agent.application.conversation.event;

import com.example.agent.domain.conversation.entity.Conversation;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 会话创建事件 — Observer 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
public class ConversationCreatedEvent extends ApplicationEvent {
    private final Conversation conversation;

    public ConversationCreatedEvent(Object source, Conversation conversation) {
        super(source);
        this.conversation = conversation;
    }
}
