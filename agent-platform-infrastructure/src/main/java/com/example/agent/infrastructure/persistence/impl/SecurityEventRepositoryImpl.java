package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.security.entity.SecurityEvent;
import com.example.agent.domain.security.repository.SecurityEventRepository;
import com.example.agent.infrastructure.persistence.mapper.SecurityEventMapper;
import com.example.agent.infrastructure.persistence.po.SecurityEventPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全事件仓储 MyBatis 实现 — 只追加不修改.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class SecurityEventRepositoryImpl implements SecurityEventRepository {

    private final SecurityEventMapper mapper;

    @Override
    public void save(SecurityEvent event) {
        mapper.insert(toPO(event));
    }

    @Override
    public List<SecurityEvent> findByTenant(Long tenantId, int page, int size) {
        return mapper.selectByTenant(tenantId, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(Long tenantId) {
        return mapper.countByTenant(tenantId);
    }

    @Override
    public List<SecurityEvent> findByConversation(String conversationId, int page, int size) {
        return mapper.selectByConversation(conversationId, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<SecurityEvent> findByEventType(Long tenantId, String eventType, int page, int size) {
        return mapper.selectByEventType(tenantId, eventType, page * size, size).stream()
                .map(this::toDomain).toList();
    }

    // ==================== 映射方法 ====================

    private SecurityEvent toDomain(SecurityEventPO po) {
        return SecurityEvent.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .eventType(po.getEventType())
                .ruleId(po.getRuleId())
                .conversationId(po.getConversationId())
                .messageId(po.getMessageId())
                .originalContent(po.getOriginalContent())
                .processedContent(po.getProcessedContent())
                .matchedPattern(po.getMatchedPattern())
                .actionTaken(po.getActionTaken())
                .operator(po.getOperator())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private SecurityEventPO toPO(SecurityEvent event) {
        return SecurityEventPO.builder()
                .tenantId(event.getTenantId())
                .eventType(event.getEventType())
                .ruleId(event.getRuleId())
                .conversationId(event.getConversationId())
                .messageId(event.getMessageId())
                .originalContent(event.getOriginalContent())
                .processedContent(event.getProcessedContent())
                .matchedPattern(event.getMatchedPattern())
                .actionTaken(event.getActionTaken())
                .operator(event.getOperator())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
