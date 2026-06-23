package com.example.agent.domain.optimization.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优化工单状态 — 含状态流转规则.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TicketStatus {

    OPEN("OPEN", "待处理"),
    ANALYZING("ANALYZING", "分析中"),
    IN_PROGRESS("IN_PROGRESS", "处理中"),
    RESOLVED("RESOLVED", "已解决"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    /** 校验状态流转是否合法 */
    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case OPEN        -> target == ANALYZING || target == IN_PROGRESS || target == CLOSED;
            case ANALYZING   -> target == IN_PROGRESS || target == CLOSED;
            case IN_PROGRESS -> target == RESOLVED || target == CLOSED;
            case RESOLVED    -> target == CLOSED;
            case CLOSED      -> false;
        };
    }

    public static TicketStatus fromCode(String code) {
        if (code == null || code.isBlank()) return OPEN;
        for (TicketStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        return OPEN;
    }
}
