package com.example.agent.application.optimization.event;

import com.example.agent.domain.conversation.valueobject.FeedbackType;
import org.springframework.context.ApplicationEvent;

/**
 * 消息反馈事件 — 用户点赞/点踩时发布.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public class MessageFeedbackEvent extends ApplicationEvent {

    private final String messageId;
    private final String conversationId;
    private final Long tenantId;
    private final FeedbackType feedback;

    public MessageFeedbackEvent(Object source, String messageId, String conversationId,
                                 Long tenantId, FeedbackType feedback) {
        super(source);
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.tenantId = tenantId;
        this.feedback = feedback;
    }

    public String getMessageId() { return messageId; }
    public String getConversationId() { return conversationId; }
    public Long getTenantId() { return tenantId; }
    public FeedbackType getFeedback() { return feedback; }
}
