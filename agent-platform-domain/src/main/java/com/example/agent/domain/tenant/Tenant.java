package com.example.agent.domain.tenant;

import com.example.agent.domain.tenant.valueobject.TenantStatusEnums;
import com.example.agent.domain.tenant.valueobject.TenantTierEnums;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 租户领域实体
 */
@Getter
@Builder
public class Tenant {

    private Long id;
    private Long tenantId;
    private String name;
    private TenantStatusEnums status;
    private TenantTierEnums tier;
    private String configJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public boolean isSuspended() {
        return status == TenantStatusEnums.SUSPENDED;
    }
}
