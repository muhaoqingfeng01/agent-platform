package com.example.agent.domain.task.entity;

import com.example.agent.domain.task.valueobject.StepStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务步骤执行实体 — 每个 DAG 节点的执行追踪.
 *
 * <p>设计模式: <b>Builder</b> + <b>Memento</b>（inputJson/outputJson 保存快照）
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class TaskStepExecution {

    /** 数据库主键 */
    private Long id;

    /** 关联执行记录 */
    private String executionId;

    /** 步骤 ID（对应 plan_json 中的 id） */
    private String stepId;

    /** 动作类型 */
    private String action;

    /** ActionHandler 实现类全限定名 */
    private String handlerClass;

    /** 输入参数 JSON */
    private String inputJson;

    /** 输出结果 JSON */
    private String outputJson;

    /** 步骤状态 */
    private StepStatus status;

    /** 已重试次数 */
    private int retryCount;

    /** 最大重试次数 */
    private int maxRetries;

    /** 执行耗时（毫秒） */
    private Long durationMs;

    /** 错误信息 */
    private String errorMessage;

    /** 开始时间 */
    private LocalDateTime startedAt;

    /** 完成时间 */
    private LocalDateTime finishedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    // ==================== 状态机方法 ====================

    /** 开始执行 — STATE: PENDING → RUNNING */
    public void start() {
        if (this.status != StepStatus.PENDING) {
            throw new IllegalStateException("只有 PENDING 状态的步骤才能开始执行，当前: " + this.status);
        }
        this.status = StepStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    /** 标记成功 — STATE: RUNNING → SUCCESS */
    public void succeed(String outputJson, long durationMs) {
        if (this.status != StepStatus.RUNNING) {
            throw new IllegalStateException("只有 RUNNING 状态的步骤才能标记成功，当前: " + this.status);
        }
        this.status = StepStatus.SUCCESS;
        this.outputJson = outputJson;
        this.durationMs = durationMs;
        this.finishedAt = LocalDateTime.now();
    }

    /** 标记失败 — STATE: RUNNING → FAILED */
    public void fail(String errorMessage, long durationMs) {
        if (this.status != StepStatus.RUNNING) {
            throw new IllegalStateException("只有 RUNNING 状态的步骤才能标记失败，当前: " + this.status);
        }
        this.status = StepStatus.FAILED;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
        this.finishedAt = LocalDateTime.now();
    }

    /** 标记跳过 — STATE: PENDING → SKIPPED */
    public void skip() {
        if (this.status != StepStatus.PENDING) {
            throw new IllegalStateException("只有 PENDING 状态的步骤才能跳过，当前: " + this.status);
        }
        this.status = StepStatus.SKIPPED;
    }

    /** 重置为待执行（重试前） */
    public void resetForRetry() {
        this.status = StepStatus.PENDING;
        this.retryCount++;
        this.startedAt = null;
        this.finishedAt = null;
        this.durationMs = null;
        this.errorMessage = null;
    }

    /** 是否可重试 */
    public boolean canRetry() {
        return this.retryCount < this.maxRetries;
    }

    /** 是否已完结 */
    public boolean isFinished() {
        return this.status == StepStatus.SUCCESS
                || this.status == StepStatus.FAILED
                || this.status == StepStatus.SKIPPED;
    }
}
