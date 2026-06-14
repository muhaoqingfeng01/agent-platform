package com.example.agent.domain.conversation.service;

import com.example.agent.domain.conversation.entity.Conversation;
import org.springframework.stereotype.Component;

/**
 * 会话领域服务 — 封装跨实体业务规则.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
public class ConversationDomainService {

    /**
     * 校验新建会话的业务不变量.
     */
    public void validateNewConversation(Conversation conversation) {
        if (conversation == null) {
            throw new IllegalArgumentException("会话不能为空");
        }
        if (conversation.getAgentId() == null || conversation.getAgentId().isBlank()) {
            throw new IllegalArgumentException("agentId 不能为空");
        }
    }

    /**
     * 校验租户隔离 + 用户所有权.
     */
    public void assertAccess(Conversation conversation, String currentTenantId, String currentUserId) {
        if (!conversation.getTenantId().equals(currentTenantId)) {
            throw new SecurityException("无权访问其他租户的会话");
        }
        if (!conversation.getUserId().equals(currentUserId)) {
            throw new SecurityException("无权访问其他用户的会话");
        }
    }
}
