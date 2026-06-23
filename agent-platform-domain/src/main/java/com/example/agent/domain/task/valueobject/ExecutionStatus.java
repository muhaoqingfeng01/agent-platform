package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务执行状态枚举 — State 模式.
 *
 * <p>生命周期: PENDING → RUNNING → COMPLETED | FAILED | CANCELLED
 * <p>含审批: PENDING → RUNNING → WAITING_APPROVAL → RUNNING → COMPLETED | FAILED
 * <p>审批不通过: WAITING_APPROVAL → CANCELLED
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ExecutionStatus {

    /** 等待执行 */
    PENDING("PENDING", "等待执行"),

    /** 执行中 */
    RUNNING("RUNNING", "执行中"),

    /** 已完成 */
    COMPLETED("COMPLETED", "已完成"),

    /** 执行失败 */
    FAILED("FAILED", "执行失败"),

    /** 已取消 */
    CANCELLED("CANCELLED", "已取消"),

    /** 等待审批（T11 人机协同 — 高风险工具调用暂停） */
    WAITING_APPROVAL("WAITING_APPROVAL", "等待审批");

    private final String code;
    private final String desc;

    /** 是否为终态 */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /** 是否为活跃状态 */
    public boolean isActive() {
        return this == PENDING || this == RUNNING || this == WAITING_APPROVAL;
    }

    public static ExecutionStatus fromCode(String code) {
        if (code == null || code.isBlank()) return PENDING;
        for (ExecutionStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知: " + code);
    }
}
