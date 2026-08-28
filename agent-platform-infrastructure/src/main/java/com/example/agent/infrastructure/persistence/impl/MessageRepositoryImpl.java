package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.repository.MessageRepository;
import com.example.agent.domain.conversation.valueobject.FeedbackType;
import com.example.agent.domain.conversation.valueobject.MessageRole;
import com.example.agent.infrastructure.persistence.mapper.MessageMapper;
import com.example.agent.infrastructure.persistence.po.MessagePO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
    private final ObjectMapper objectMapper;

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
    public void updateFeedback(String messageId, FeedbackType feedback, String reason) {
        boolean clear = feedback == null || feedback == FeedbackType.NONE;
        String code = clear ? null : feedback.getCode();
        String storedReason = clear ? null : reason;
        messageMapper.updateFeedback(messageId, code, storedReason, clear);
    }

    // ==================== 映射方法 ====================

    private Message toDomain(MessagePO po) {
        return Message.builder()
                .messageId(po.getMessageId())
                .conversationId(po.getConversationId())
                .role(MessageRole.fromCode(po.getRole()))
                .content(po.getContent())
                .tokenCount(po.getTokenCount())
                .metadata(parseMetadata(po.getMetadataJson()))
                .feedback(parseFeedback(po.getFeedback()))
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

    private Map<String, Object> parseMetadata(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("[Message] 解析 metadata_json 失败: {}", e.getMessage());
            return Map.of();
        }
    }

    private FeedbackType parseFeedback(String code) {
        if (code == null || code.isBlank() || "NONE".equalsIgnoreCase(code)) {
            return null;
        }
        try {
            return FeedbackType.fromCode(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
