package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感词规则状态.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SensitiveWordStatus {
    /** 启用 — 参与过滤 */
    ACTIVE("ACTIVE", "启用"),
    /** 禁用 — 不参与过滤 */
    DISABLED("DISABLED", "禁用");

    private final String code;
    private final String desc;

    public static SensitiveWordStatus fromCode(String code) {
        if (code == null || code.isBlank()) return ACTIVE;
        for (SensitiveWordStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return ACTIVE;
    }
}
