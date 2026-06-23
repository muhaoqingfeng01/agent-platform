package com.example.agent.domain.security.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批工单状态.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ApprovalStatus {
    /** 待审批 */
    PENDING("PENDING", "待审批"),
    /** 已同意 */
    APPROVED("APPROVED", "已同意"),
    /** 已拒绝 */
    REJECTED("REJECTED", "已拒绝"),
    /** 已超时（自动拒绝） */
    TIMEOUT("TIMEOUT", "已超时"),
    /** 已取消 */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    public static ApprovalStatus fromCode(String code) {
        if (code == null || code.isBlank()) return PENDING;
        for (ApprovalStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
