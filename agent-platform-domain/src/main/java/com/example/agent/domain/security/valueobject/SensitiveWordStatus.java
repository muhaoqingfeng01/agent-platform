package com.example.agent.domain.security.valueobject;

/**
 * 敏感词规则状态.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum SensitiveWordStatus {
    /** 启用 — 参与过滤 */
    ACTIVE,
    /** 禁用 — 不参与过滤 */
    DISABLED;

    public static SensitiveWordStatus fromCode(String code) {
        if (code == null) return ACTIVE;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
