package com.example.agent.domain.task.valueobject;

/**
 * 步骤执行状态枚举 — State 模式.
 *
 * <p>生命周期: PENDING → RUNNING → SUCCESS | FAILED | SKIPPED
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public enum StepStatus {

    /** 等待执行 */
    PENDING,

    /** 执行中 */
    RUNNING,

    /** 执行成功 */
    SUCCESS,

    /** 执行失败 */
    FAILED,

    /** 已跳过（前置步骤失败导致） */
    SKIPPED;

    /** 是否为终态 */
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == SKIPPED;
    }
}
