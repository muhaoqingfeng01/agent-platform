package com.example.agent.domain.tenant.service;

import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;

/**
 * 租户领域服务 — 封装租户核心业务规则.
 *
 * <p>Domain service for tenant invariants:
 * <ul>
 *   <li>租户必须处于 ACTIVE 状态才能承载用户操作</li>
 *   <li>租户标识全局唯一</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public class TenantDomainService {

    private final TenantRepository tenantRepository;

    public TenantDomainService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * 确保租户存在且处于可用状态.
     *
     * @param tenantId 租户标识
     * @return 可用的租户实体
     * @throws IllegalStateException 如果租户不存在或已停用
     */
    public Tenant ensureActive(Long tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalStateException("租户不存在: " + tenantId));
        if (!tenant.isActive()) {
            throw new IllegalStateException("租户已停用: " + tenantId);
        }
        return tenant;
    }

    /**
     * 验证租户标识唯一性.
     */
    public boolean isTenantIdUnique(Long tenantId) {
        return tenantRepository.findByTenantId(tenantId).isEmpty();
    }
}
