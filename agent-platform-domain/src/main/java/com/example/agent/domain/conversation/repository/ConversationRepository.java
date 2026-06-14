package com.example.agent.domain.conversation.repository;

import com.example.agent.domain.conversation.entity.Conversation;
import com.example.agent.domain.conversation.valueobject.ConversationStatus;

import java.util.List;
import java.util.Optional;

/**
 * 会话仓储接口 — Repository 模式.
 * <p>
 * 实现在 infrastructure 层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface ConversationRepository {

    void save(Conversation conversation);

    Optional<Conversation> findByConversationId(String conversationId);

    List<Conversation> findByUserId(String tenantId, String userId, int page, int size);

    long countByUserId(String tenantId, String userId);

    void updateTitle(String conversationId, String title);

    void updateStatus(String conversationId, ConversationStatus status);

    void incrementMessageCount(String conversationId, int delta);

    void addTokens(String conversationId, long tokens);

    void softDelete(String conversationId);
}
