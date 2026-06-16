package com.example.agent.domain.security.valueobject;

/**
 * 命中后的动作类型.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum ActionType {
    /** 仅记录日志，不干预 */
    LOG,
    /** 记录并告警 */
    WARN,
    /** 阻断请求 */
    BLOCK;

    public static ActionType fromCode(String code) {
        if (code == null) return LOG;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LOG;
        }
    }
}
