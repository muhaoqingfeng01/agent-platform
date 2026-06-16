package com.example.agent.domain.security.valueobject;

/**
 * 严重程度 — 决定触发后的处理策略.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum SeverityLevel {
    /** 低 — 仅记录日志 */
    LOW,
    /** 中 — 记录并告警 */
    MEDIUM,
    /** 高 — 阻断请求并告警 */
    HIGH,
    /** 直接阻断（最高级别） */
    BLOCK;

    public static SeverityLevel fromCode(String code) {
        if (code == null) return MEDIUM;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
