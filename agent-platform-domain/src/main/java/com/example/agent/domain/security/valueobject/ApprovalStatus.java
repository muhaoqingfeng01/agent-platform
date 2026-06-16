package com.example.agent.domain.security.valueobject;

/**
 * 审批工单状态.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum ApprovalStatus {
    /** 待审批 */
    PENDING,
    /** 已同意 */
    APPROVED,
    /** 已拒绝 */
    REJECTED,
    /** 已超时（自动拒绝） */
    TIMEOUT,
    /** 已取消 */
    CANCELLED;

    public static ApprovalStatus fromCode(String code) {
        if (code == null) return PENDING;
        try {
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
