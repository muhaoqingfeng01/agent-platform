package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 命中后的动作类型.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ActionType {
    /** 仅记录日志，不干预 */
    LOG("LOG", "仅记录"),
    /** 记录并告警 */
    WARN("WARN", "告警"),
    /** 阻断请求 */
    BLOCK("BLOCK", "阻断");

    private final String code;
    private final String desc;

    public static ActionType fromCode(String code) {
        if (code == null || code.isBlank()) return LOG;
        for (ActionType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
