package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tenant.TenantModeQuota;
import com.example.agent.domain.tenant.TenantModeQuotaRepository;
import com.example.agent.infrastructure.persistence.mapper.TenantModeQuotaMapper;
import com.example.agent.infrastructure.persistence.po.TenantModeQuotaPO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 租户模式配额仓储实现.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Repository
@Primary
@RequiredArgsConstructor
public class TenantModeQuotaRepositoryImpl implements TenantModeQuotaRepository {

    private final TenantModeQuotaMapper mapper;

    @Override
    public Optional<TenantModeQuota> findByTenantIdAndModeCode(Long tenantId, String modeCode) {
        return mapper.findByTenantIdAndModeCode(tenantId, modeCode)
                .filter(po -> !Boolean.TRUE.equals(po.getDeleted()))
                .map(this::toDomain);
    }

    @Override
    public List<TenantModeQuota> findByTenantId(Long tenantId) {
        return mapper.findByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    @Override
    public void save(TenantModeQuota quota) {
        mapper.insert(toPO(quota));
    }

    @Override
    public void update(TenantModeQuota quota) {
        mapper.update(toPO(quota));
    }

    @Override
    public Optional<TenantModeQuota> findByTenantIdAndModeCodeIncludingDeleted(Long tenantId, String modeCode) {
        return mapper.findByTenantIdAndModeCode(tenantId, modeCode).map(this::toDomain);
    }

    private TenantModeQuota toDomain(TenantModeQuotaPO po) {
        return TenantModeQuota.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .modeCode(po.getModeCode())
                .enabled(po.getEnabled())
                .dailyLlmLimit(po.getDailyLlmLimit())
                .dailyUploadBytes(po.getDailyUploadBytes())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .deleted(po.getDeleted())
                .build();
    }

    private TenantModeQuotaPO toPO(TenantModeQuota quota) {
        return TenantModeQuotaPO.builder()
                .id(quota.getId())
                .tenantId(quota.getTenantId())
                .modeCode(quota.getModeCode())
                .enabled(quota.getEnabled())
                .dailyLlmLimit(quota.getDailyLlmLimit())
                .dailyUploadBytes(quota.getDailyUploadBytes())
                .createdAt(quota.getCreatedAt())
                .updatedAt(quota.getUpdatedAt())
                .deleted(quota.getDeleted() != null ? quota.getDeleted() : Boolean.FALSE)
                .build();
    }
}
