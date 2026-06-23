package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;
import com.example.agent.domain.tenant.valueobject.TenantStatusEnums;
import com.example.agent.domain.tenant.valueobject.TenantTierEnums;
import com.example.agent.infrastructure.persistence.mapper.TenantMapper;
import com.example.agent.infrastructure.persistence.po.TenantPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 租户仓储 MyBatis 数据库实现
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {

    private final TenantMapper tenantMapper;

    @Override
    public Optional<Tenant> findById(Long id) {
        return tenantMapper.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Tenant> findByTenantId(Long tenantId) {
        return tenantMapper.findByTenantId(tenantId).map(this::toDomain);
    }

    @Override
    public List<Tenant> findAll(int page, int size) {
        return tenantMapper.findAll(page * size, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long count() {
        return tenantMapper.count();
    }

    @Override
    public void save(Tenant tenant) {
        tenantMapper.insert(toPO(tenant));
    }

    @Override
    public void update(Tenant tenant) {
        tenantMapper.update(toPO(tenant));
    }

    @Override
    public void delete(Long id) {
        tenantMapper.deleteById(id);
    }

    // ==================== 映射方法 ====================

    private Tenant toDomain(TenantPO po) {
        return Tenant.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .name(po.getName())
                .status( TenantStatusEnums.fromCode(po.getStatus()))
                .tier(TenantTierEnums.fromCode(po.getTier()))
                .configJson(po.getConfigJson())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .deleted(po.getDeleted())
                .build();
    }

    private TenantPO toPO(Tenant tenant) {
        return TenantPO.builder()
                .id(tenant.getId())
                .tenantId(tenant.getTenantId())
                .name(tenant.getName())
                .status(Optional.ofNullable(tenant.getStatus()).map(TenantStatusEnums::getCode).orElse(null))
                .tier(Optional.ofNullable(tenant.getTier()).map(TenantTierEnums::getCode).orElse(null))
                .configJson(tenant.getConfigJson())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .deleted(tenant.getDeleted())
                .build();
    }
}
