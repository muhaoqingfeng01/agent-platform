package com.example.agent.domain.task.entity;

import com.example.agent.domain.task.valueobject.ExecutionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务执行聚合根 — 每次 DAG 复杂任务的执行记录.
 *
 * <p>设计模式: <b>Builder</b> + <b>State</b>（状态机驱动生命周期）
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class TaskExecution {

    /** 数据库主键 */
    private Long id;

    /** 租户 ID */
    private String tenantId;

    /** 执行唯一标识 */
    private String executionId;

    /** 关联会话 */
    private String conversationId;

    /** 关联 Agent */
    private String agentId;

    /** DAG 任务计划 JSON */
    private String planJson;

    /** 执行状态 */
    private ExecutionStatus status;

    /** 总步骤数 */
    private int totalSteps;

    /** 已完成步骤数 */
    private int completedSteps;

    /** 失败步骤 ID */
    private String failedStepId;

    /** 错误信息 */
    private String errorMessage;

    /** 开始时间 */
    private LocalDateTime startedAt;

    /** 完成时间 */
    private LocalDateTime finishedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    // ==================== 状态机方法 ====================

    /** 开始执行 — STATE: PENDING → RUNNING */
    public void start() {
        if (this.status != ExecutionStatus.PENDING) {
            throw new IllegalStateException("只有 PENDING 状态的任务才能开始执行，当前: " + this.status);
        }
        this.status = ExecutionStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    /** 标记完成 — STATE: RUNNING → COMPLETED */
    public void complete() {
        if (this.status != ExecutionStatus.RUNNING) {
            throw new IllegalStateException("只有 RUNNING 状态的任务才能完成，当前: " + this.status);
        }
        this.status = ExecutionStatus.COMPLETED;
        this.finishedAt = LocalDateTime.now();
    }

    /** 标记失败 — STATE: RUNNING → FAILED */
    public void fail(String stepId, String errorMessage) {
        if (this.status != ExecutionStatus.RUNNING) {
            throw new IllegalStateException("只有 RUNNING 状态的任务才能标记失败，当前: " + this.status);
        }
        this.status = ExecutionStatus.FAILED;
        this.failedStepId = stepId;
        this.errorMessage = errorMessage;
        this.finishedAt = LocalDateTime.now();
    }

    /** 取消任务 — STATE: PENDING|RUNNING → CANCELLED */
    public void cancel() {
        if (this.status != ExecutionStatus.PENDING && this.status != ExecutionStatus.RUNNING) {
            throw new IllegalStateException("只有 PENDING 或 RUNNING 状态的任务才能取消，当前: " + this.status);
        }
        this.status = ExecutionStatus.CANCELLED;
        this.finishedAt = LocalDateTime.now();
    }

    /** 增加已完成步骤计数 */
    public void incrementCompletedSteps() {
        this.completedSteps++;
    }

    /** 是否已完结 */
    public boolean isFinished() {
        return this.status == ExecutionStatus.COMPLETED
                || this.status == ExecutionStatus.FAILED
                || this.status == ExecutionStatus.CANCELLED;
    }
}
