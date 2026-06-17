package com.example.agent.domain.tenant;

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
    private String status;   // ACTIVE / SUSPENDED / DELETED
    private String tier;     // STANDARD / PREMIUM / ENTERPRISE
    private String configJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isSuspended() {
        return "SUSPENDED".equals(status);
    }
}
