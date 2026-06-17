package com.example.agent.domain.security.repository;

import com.example.agent.domain.security.entity.SecurityEvent;

import java.util.List;

/**
 * 安全事件仓储接口 — 只追加，不更新不删除.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface SecurityEventRepository {

    /** 保存安全事件（仅追加） */
    void save(SecurityEvent event);

    /** 按租户分页查询 */
    List<SecurityEvent> findByTenant(Long tenantId, int page, int size);

    /** 统计租户下的事件数 */
    long countByTenant(Long tenantId);

    /** 按会话查询 */
    List<SecurityEvent> findByConversation(String conversationId, int page, int size);

    /** 按事件类型查询 */
    List<SecurityEvent> findByEventType(Long tenantId, String eventType, int page, int size);
}
