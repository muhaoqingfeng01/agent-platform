package com.example.agent.common.exception;

import lombok.Getter;

/**
 * 安全阻断异常 — 由输入过滤器链抛出，GlobalExceptionHandler 捕获后返回 HTTP 403.
 *
 * <p>携带事件类型和匹配模式，供 {@code SecurityEventRecorder} 记录安全事件.
 * <p>继承自 {@link BusinessException}，HTTP 状态码固定为 403.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
public class SecurityBlockedException extends BusinessException {

    /** 安全事件类型 — 如 INJECTION / JAILBREAK / SENSITIVE_WORD */
    private final String eventType;

    /** 匹配到的模式 — 供审计追溯 */
    private final String matchedPattern;

    /**
     * @param eventType      事件类型（如 "INJECTION", "JAILBREAK"）
     * @param message        阻断原因（面向用户）
     * @param matchedPattern 匹配到的模式（面向审计）
     */
    public SecurityBlockedException(String eventType, String message, String matchedPattern) {
        super(403, message);
        this.eventType = eventType;
        this.matchedPattern = matchedPattern;
    }

    /**
     * 不携带匹配模式的简洁构造.
     *
     * @param eventType 事件类型
     * @param message   阻断原因
     */
    public SecurityBlockedException(String eventType, String message) {
        this(eventType, message, null);
    }
}
