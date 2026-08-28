package com.example.agent.domain.conversation.repository;

import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.FeedbackType;

import java.util.List;

/**
 * 消息仓储接口 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface MessageRepository {

    void save(Message message);

    List<Message> findByConversationId(String conversationId, int offset, int size);

    List<Message> findBefore(String conversationId, String beforeMessageId, int size);

    /**
     * 更新消息反馈。
     *
     * @param feedback  LIKE / DISLIKE；null 或 NONE 表示清除
     * @param reason    点踩原因，可空；清除反馈时忽略
     */
    void updateFeedback(String messageId, FeedbackType feedback, String reason);
}
