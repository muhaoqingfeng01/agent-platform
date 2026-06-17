package com.example.agent.domain.optimization.valueobject;

/**
 * 优化工单状态 — 含状态流转规则.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum TicketStatus {

    OPEN("待处理"),
    ANALYZING("分析中"),
    IN_PROGRESS("处理中"),
    RESOLVED("已解决"),
    CLOSED("已关闭");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

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
        if (code == null) return OPEN;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OPEN;
        }
    }
}
