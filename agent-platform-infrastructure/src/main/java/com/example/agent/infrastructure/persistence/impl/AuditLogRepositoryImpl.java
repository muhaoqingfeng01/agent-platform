package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.audit.entity.AuditLog;
import com.example.agent.domain.audit.repository.AuditLogRepository;
import com.example.agent.infrastructure.persistence.mapper.AuditLogMapper;
import com.example.agent.infrastructure.persistence.po.AuditLogPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志仓储实现 — MyBatis 适配.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        AuditLogPO po = toPO(auditLog);
        mapper.insert(po);
        log.trace("[AuditLog] 审计日志已写入: traceId={}, action={}, status={}",
                auditLog.getTraceId(), auditLog.getAction(), auditLog.getStatus());
    }

    @Override
    public List<AuditLog> findByTraceId(String traceId) {
        return mapper.selectByTraceId(traceId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByTenant(Long tenantId, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenant(tenantId, offset, size).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByConversation(String conversationId, int page, int size) {
        int offset = page * size;
        return mapper.selectByConversation(conversationId, offset, size).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByTenant(Long tenantId) {
        return mapper.countByTenant(tenantId);
    }

    // ==================== 转换方法 ====================

    private AuditLog toDomain(AuditLogPO po) {
        return AuditLog.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .traceId(po.getTraceId())
                .conversationId(po.getConversationId())
                .actorType(po.getActorType())
                .actorId(po.getActorId())
                .action(po.getAction())
                .resourceType(po.getResourceType())
                .resourceId(po.getResourceId())
                .requestJson(po.getRequestJson())
                .responseJson(po.getResponseJson())
                .durationMs(po.getDurationMs())
                .status(po.getStatus())
                .ipAddress(po.getIpAddress())
                .userAgent(po.getUserAgent())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private AuditLogPO toPO(AuditLog log) {
        return AuditLogPO.builder()
                .id(log.getId())
                .tenantId(log.getTenantId())
                .traceId(log.getTraceId())
                .conversationId(log.getConversationId())
                .actorType(log.getActorType())
                .actorId(log.getActorId())
                .action(log.getAction())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .requestJson(log.getRequestJson())
                .responseJson(log.getResponseJson())
                .durationMs(log.getDurationMs())
                .status(log.getStatus())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
