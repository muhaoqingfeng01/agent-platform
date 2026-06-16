package com.example.agent.application.security.filter;

import lombok.Builder;
import lombok.Data;

/**
 * 过滤结果 — 由过滤器返回，表示通过或阻断.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
public class FilterResult {

    /** 是否阻断 */
    private boolean blocked;

    /** 事件类型 — 如 INJECTION / JAILBREAK / SENSITIVE_WORD / LENGTH_EXCEEDED */
    private String eventType;

    /** 阻断原因（面向用户） */
    private String reason;

    /** 匹配到的模式（面向审计） */
    private String matchedPattern;

    /** 严重程度 — LOW / MEDIUM / HIGH / BLOCK */
    private String severity;

    // ==================== 静态工厂 ====================

    /** 通过 */
    public static FilterResult pass() {
        return FilterResult.builder().blocked(false).build();
    }

    /** 阻断 */
    public static FilterResult block(String eventType, String reason) {
        return FilterResult.builder()
                .blocked(true)
                .eventType(eventType)
                .reason(reason)
                .severity("HIGH")
                .build();
    }

    /** 阻断（含匹配模式） */
    public static FilterResult block(String eventType, String reason, String matchedPattern) {
        return FilterResult.builder()
                .blocked(true)
                .eventType(eventType)
                .reason(reason)
                .matchedPattern(matchedPattern)
                .severity("HIGH")
                .build();
    }
}
