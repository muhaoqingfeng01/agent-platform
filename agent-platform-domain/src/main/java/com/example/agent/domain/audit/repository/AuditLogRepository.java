package com.example.agent.domain.audit.repository;

import com.example.agent.domain.audit.entity.AuditLog;

import java.util.List;

/**
 * 审计日志仓储接口（DDD 领域层）.
 *
 * <p>只提供追加和查询能力，不支持修改和删除（审计日志不可变）。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface AuditLogRepository {

    /** 保存审计日志 */
    void save(AuditLog log);

    /** 按 traceId 查询所有关联日志 */
    List<AuditLog> findByTraceId(String traceId);

    /** 按租户分页查询 */
    List<AuditLog> findByTenant(String tenantId, int page, int size);

    /** 按会话分页查询 */
    List<AuditLog> findByConversation(String conversationId, int page, int size);

    /** 按租户统计总数 */
    long countByTenant(String tenantId);
}
