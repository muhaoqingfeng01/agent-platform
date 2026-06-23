package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 严重程度 — 决定触发后的处理策略.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SeverityLevel {
    /** 低 — 仅记录日志 */
    LOW("LOW", "低"),
    /** 中 — 记录并告警 */
    MEDIUM("MEDIUM", "中"),
    /** 高 — 阻断请求并告警 */
    HIGH("HIGH", "高"),
    /** 直接阻断（最高级别） */
    BLOCK("BLOCK", "阻断");

    private final String code;
    private final String desc;

    public static SeverityLevel fromCode(String code) {
        if (code == null || code.isBlank()) return MEDIUM;
        for (SeverityLevel e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return MEDIUM;
    }
}
