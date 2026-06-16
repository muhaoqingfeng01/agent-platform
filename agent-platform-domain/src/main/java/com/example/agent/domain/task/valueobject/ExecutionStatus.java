package com.example.agent.domain.task.valueobject;

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
public enum ExecutionStatus {

    /** 等待执行 */
    PENDING,

    /** 执行中 */
    RUNNING,

    /** 已完成 */
    COMPLETED,

    /** 执行失败 */
    FAILED,

    /** 已取消 */
    CANCELLED,

    /** 等待审批（T11 人机协同 — 高风险工具调用暂停） */
    WAITING_APPROVAL;

    /** 是否为终态 */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /** 是否为活跃状态 */
    public boolean isActive() {
        return this == PENDING || this == RUNNING || this == WAITING_APPROVAL;
    }
}
