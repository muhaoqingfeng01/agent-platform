package com.example.agent.domain.tenant;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 租户模式配额 — 官方交互模式开关 + 日限额.
 * <p>
 * 无记录时由领域服务按「开通且不限额」补默认值，避免存量租户被一刀切关闭。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Getter
@Builder
public class TenantModeQuota {

    private Long id;
    private Long tenantId;
    private String modeCode;
    private Boolean enabled;
    private Integer dailyLlmLimit;
    private Long dailyUploadBytes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    public boolean isEnabled() {
        return enabled == null || Boolean.TRUE.equals(enabled);
    }

    /**
     * 未配置限额视为不限制.
     */
    public boolean hasLlmLimit() {
        return dailyLlmLimit != null;
    }

    public boolean hasUploadLimit() {
        return dailyUploadBytes != null;
    }

    public static TenantModeQuota defaultEnabled(Long tenantId, String modeCode) {
        return TenantModeQuota.builder()
                .tenantId(tenantId)
                .modeCode(modeCode)
                .enabled(true)
                .dailyLlmLimit(null)
                .dailyUploadBytes(null)
                .deleted(false)
                .build();
    }
}
