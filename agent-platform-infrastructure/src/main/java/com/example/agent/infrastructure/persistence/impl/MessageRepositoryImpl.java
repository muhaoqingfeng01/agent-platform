package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.MessageRepository;
import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.domain.conversation.valueobject.MessageRole;
import com.example.agent.infrastructure.persistence.mapper.MessageMapper;
import com.example.agent.infrastructure.persistence.po.MessagePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageMapper messageMapper;

    @Override
    public void save(Message message) {
        messageMapper.insert(toPO(message));
    }

    @Override
    public List<Message> findByConversationId(String conversationId, int offset, int size) {
        return messageMapper.selectByConversationId(conversationId, offset, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Message> findBefore(String conversationId, String beforeMessageId, int size) {
        return messageMapper.selectBefore(conversationId, beforeMessageId, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void updateFeedback(String messageId, FeedbackType feedback) {
        messageMapper.updateFeedback(messageId, feedback.getCode());
    }

    // ==================== 映射方法 ====================

    private Message toDomain(MessagePO po) {
        return Message.builder()
                .messageId(po.getMessageId())
                .conversationId(po.getConversationId())
                .role(MessageRole.fromCode(po.getRole()))
                .content(po.getContent())
                .tokenCount(po.getTokenCount())
                .feedback(po.getFeedback() != null ? FeedbackType.fromCode(po.getFeedback()) : null)
                .createdAt(po.getCreatedAt())
                .build();
    }

    private MessagePO toPO(Message message) {
        return MessagePO.builder()
                .conversationId(message.getConversationId())
                .messageId(message.getMessageId())
                .role(message.getRole().getCode())
                .content(message.getContent())
                .tokenCount(message.getTokenCount())
                .feedback(message.getFeedback() != null ? message.getFeedback().getCode() : null)
                .createdAt(message.getCreatedAt())
                .build();
    }
}
