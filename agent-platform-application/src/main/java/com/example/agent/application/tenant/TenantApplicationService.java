package com.example.agent.application.tenant;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;
import com.example.agent.domain.tenant.valueobject.TenantStatusEnums;
import com.example.agent.domain.tenant.valueobject.TenantTierEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户应用服务 — 编排租户 CRUD 业务逻辑.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantApplicationService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantResponse createTenant(TenantCreateCommand request) {
        log.info("[Tenant] 创建: tenantId={}, name={}", request.getTenantId(), request.getName());
        if (tenantRepository.findByTenantId(request.getTenantId()).isPresent()) {
            throw new BusinessException(409, "租户标识已存在: " + request.getTenantId());
        }
        Tenant tenant = Tenant.builder()
                .tenantId(request.getTenantId()).name(request.getName())
                .tier(TenantTierEnums.fromCode(request.getTier()))
                .status(TenantStatusEnums.ACTIVE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deleted(false)
                .build();
        tenantRepository.save(tenant);
        return TenantResponse.from(tenant);
    }

    public List<TenantResponse> listTenants(int page, int size) {
        log.debug("[Tenant] 列表: page={}, size={}", page, size);
        return tenantRepository.findAll(page, size).stream()
                .map(TenantResponse::from).toList();
    }

    public TenantResponse getTenant(Long id) {
        log.debug("[Tenant] 详情: id={}", id);
        return tenantRepository.findById(id)
                .map(TenantResponse::from)
                .orElseThrow(() -> new BusinessException(404, "租户不存在: " + id));
    }

    @Transactional
    public TenantResponse updateTenant(Long id, TenantUpdateCommand request) {
        log.info("[Tenant] 更新: id={}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "租户不存在: " + id));
        tenant = Tenant.builder()
                .id(tenant.getId()).tenantId(tenant.getTenantId())
                .name(request.getName())
                .tier(TenantTierEnums.fromCode(request.getTier()) )
                .configJson(request.getConfigJson() != null ? request.getConfigJson() : tenant.getConfigJson())
                .status(tenant.getStatus()).createdAt(tenant.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(tenant.getDeleted())
                .build();
        tenantRepository.update(tenant);
        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse toggleStatus(Long id, TenantUpdateStatusCommand request) {
        log.info("[Tenant] 状态变更: id={}, status={}", id, request.getStatus());
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "租户不存在: " + id));
        tenant = Tenant.builder()
                .id(tenant.getId()).tenantId(tenant.getTenantId()).name(tenant.getName())
                .tier(tenant.getTier())
                .status(TenantStatusEnums.fromCode(request.getStatus()))
                .configJson(tenant.getConfigJson()).createdAt(tenant.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(tenant.getDeleted())
                .build();
        tenantRepository.update(tenant);
        return TenantResponse.from(tenant);
    }

    @Transactional
    public void deleteTenant(Long id) {
        log.info("[Tenant] 删除: id={}", id);
        if (tenantRepository.findById(id).isEmpty()) {
            throw new BusinessException(404, "租户不存在: " + id);
        }
        tenantRepository.delete(id);
    }
}
