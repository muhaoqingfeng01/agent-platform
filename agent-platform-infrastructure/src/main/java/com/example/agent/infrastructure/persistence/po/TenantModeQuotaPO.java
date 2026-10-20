package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 租户模式配额 PO — 映射 t_tenant_mode_quota.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantModeQuotaPO {

    private Long id;
    private Long tenantId;
    private String modeCode;
    private Boolean enabled;
    private Integer dailyLlmLimit;
    private Long dailyUploadBytes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
