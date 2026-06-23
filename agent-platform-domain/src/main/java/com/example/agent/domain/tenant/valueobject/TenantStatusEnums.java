package com.example.agent.domain.tenant.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户状态枚举.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum TenantStatusEnums {

    ACTIVE("ACTIVE", "活跃"),
    SUSPENDED("SUSPENDED", "已暂停"),
    DELETED("DELETED", "已删除");

    private final String code;
    private final String desc;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static TenantStatusEnums fromCode(String code) {
        if (code == null || code.isBlank()) return ACTIVE;
        for (TenantStatusEnums e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return SUSPENDED;
    }
}
