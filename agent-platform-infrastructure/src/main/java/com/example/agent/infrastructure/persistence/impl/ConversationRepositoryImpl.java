package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.conversation.entity.Conversation;
import com.example.agent.domain.conversation.repository.ConversationRepository;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import com.example.agent.infrastructure.persistence.mapper.ConversationMapper;
import com.example.agent.infrastructure.persistence.po.ConversationPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 会话仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepository {

    private final ConversationMapper conversationMapper;

    @Override
    public void save(Conversation conversation) {
        conversationMapper.insert(toPO(conversation));
    }

    @Override
    public Optional<Conversation> findByConversationId(String conversationId) {
        return conversationMapper.selectByConversationId(conversationId).map(this::toDomain);
    }

    @Override
    public List<Conversation> findByUserId(String tenantId, String userId, int page, int size) {
        int offset = page * size;
        return conversationMapper.selectByUserId(tenantId, userId, offset, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByUserId(String tenantId, String userId) {
        return conversationMapper.countByUserId(tenantId, userId);
    }

    @Override
    public void updateTitle(String conversationId, String title) {
        conversationMapper.updateTitle(conversationId, title);
    }

    @Override
    public void updateStatus(String conversationId, ConversationStatus status) {
        conversationMapper.updateStatus(conversationId, status.name());
    }

    @Override
    public void incrementMessageCount(String conversationId, int delta) {
        conversationMapper.incrementMessageCount(conversationId, delta);
    }

    @Override
    public void addTokens(String conversationId, long tokens) {
        conversationMapper.addTokens(conversationId, tokens);
    }

    @Override
    public void softDelete(String conversationId) {
        conversationMapper.softDelete(conversationId);
    }

    // ==================== 映射方法 ====================

    private Conversation toDomain(ConversationPO po) {
        return Conversation.builder()
                .conversationId(po.getConversationId())
                .tenantId(po.getTenantId())
                .agentId(po.getAgentId())
                .userId(po.getUserId())
                .title(po.getTitle())
                .status(ConversationStatus.valueOf(po.getStatus()))
                .messageCount(po.getMessageCount() != null ? po.getMessageCount() : 0)
                .totalTokens(po.getTotalTokens() != null ? po.getTotalTokens() : 0L)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private ConversationPO toPO(Conversation conversation) {
        return ConversationPO.builder()
                .tenantId(conversation.getTenantId())
                .conversationId(conversation.getConversationId())
                .agentId(conversation.getAgentId())
                .userId(conversation.getUserId())
                .title(conversation.getTitle())
                .status(conversation.getStatus().name())
                .messageCount(conversation.getMessageCount())
                .totalTokens(conversation.getTotalTokens())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
