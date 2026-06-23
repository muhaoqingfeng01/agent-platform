package com.example.agent.domain.audit.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作者类型枚举 — 区分用户操作与系统操作.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum ActorTypeEnums {

    USER("USER", "用户操作"),
    SYSTEM("SYSTEM", "系统操作");

    private final String code;
    private final String desc;

    public static ActorTypeEnums fromCode(String code) {
        if (code == null || code.isBlank()) return SYSTEM;
        for (ActorTypeEnums e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知操作者类型: " + code);
    }
}
