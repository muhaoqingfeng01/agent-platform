package com.example.agent.domain.conversation.entity;

import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.domain.conversation.valueobject.MessageRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息领域实体 — Builder 模式构建.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class Message {

    private String messageId;
    private String conversationId;
    private MessageRole role;
    private String content;
    private Integer tokenCount;
    private Map<String, Object> metadata;
    private FeedbackType feedback;
    private LocalDateTime createdAt;

    /** 是否为有效内容 */
    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    /** 估算 Token 数 */
    public int estimateTokens() {
        if (content == null) return 0;
        return (int) Math.ceil(content.length() * 0.5);
    }

    /** 更新反馈 */
    public void updateFeedback(FeedbackType type) {
        this.feedback = type;
    }
}
