package com.example.agent.domain.tenant.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户套餐等级枚举.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum TenantTierEnums {

    STANDARD("STANDARD", "标准版"),
    PREMIUM("PREMIUM", "高级版"),
    ENTERPRISE("ENTERPRISE", "企业版");

    private final String code;
    private final String desc;

    public static TenantTierEnums fromCode(String code) {
        if (code == null || code.isBlank()) return STANDARD;
        for (TenantTierEnums e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return STANDARD;
    }
}
