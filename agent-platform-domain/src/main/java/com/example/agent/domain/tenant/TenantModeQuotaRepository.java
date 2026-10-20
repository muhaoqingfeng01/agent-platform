package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 租户模式配额仓储.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
public interface TenantModeQuotaRepository {

    Optional<TenantModeQuota> findByTenantIdAndModeCode(Long tenantId, String modeCode);

    /** 含逻辑删除行，用于 upsert 避免唯一键冲突. */
    Optional<TenantModeQuota> findByTenantIdAndModeCodeIncludingDeleted(Long tenantId, String modeCode);

    List<TenantModeQuota> findByTenantId(Long tenantId);

    void save(TenantModeQuota quota);

    void update(TenantModeQuota quota);
}
