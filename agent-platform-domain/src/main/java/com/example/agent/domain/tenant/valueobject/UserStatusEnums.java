package com.example.agent.domain.tenant.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnums {

    ACTIVE("ACTIVE", "活跃"),
    DISABLED("DISABLED", "禁用");

    private final String code;
    private final String desc;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static UserStatusEnums fromCode(String code) {
        if (code == null || code.isBlank()) return ACTIVE;
        for (UserStatusEnums e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return DISABLED;
    }
}
