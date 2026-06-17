package com.example.agent.domain.conversation.repository;

import com.example.agent.domain.conversation.entity.Intent;
import com.example.agent.domain.conversation.valueobject.IntentStatus;

import java.util.List;
import java.util.Optional;

/**
 * 意图仓储接口 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface IntentRepository {

    void save(Intent intent);

    void update(Intent intent);

    void updateStatus(String intentId, IntentStatus status);

    void softDelete(String intentId);

    Optional<Intent> findByIntentId(String intentId);

    List<Intent> findActiveByTenant(Long tenantId);

    List<Intent> findByTenant(Long tenantId, int page, int size);

    long countByTenant(Long tenantId);

    boolean existsByTenantAndCode(Long tenantId, String intentCode);
}
