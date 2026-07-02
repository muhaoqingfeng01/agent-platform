package com.example.agent.domain.task.entity;

import com.example.agent.domain.task.valueobject.AsyncTaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 异步任务实体 — 通用任务中心聚合根.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Getter
@Builder(toBuilder = true)
public class AsyncTask {

    private Long id;
    private String taskId;
    private String taskType;
    private String bizId;
    private Long tenantId;
    private AsyncTaskStatus status;
    private String payloadJson;
    private String resultJson;
    private LocalDateTime timeoutAt;
    private int retryCount;
    private int maxRetries;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ========== 领域行为（状态机） ==========

    public void start() {
        if (this.status != AsyncTaskStatus.SUBMITTED) {
            throw new IllegalStateException("仅 SUBMITTED 状态可启动，当前: " + this.status.getDesc());
        }
        this.status = AsyncTaskStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(String resultJson) {
        assertRunning();
        this.status = AsyncTaskStatus.COMPLETED;
        this.resultJson = resultJson;
        this.finishedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMsg) {
        assertRunning();
        this.status = AsyncTaskStatus.FAILED;
        this.errorMessage = errorMsg;
        this.finishedAt = LocalDateTime.now();
    }

    public void markTimeout() {
        assertRunning();
        this.status = AsyncTaskStatus.TIMEOUT;
        this.finishedAt = LocalDateTime.now();
    }

    public void prepareRetry() {
        if (!this.status.isTerminal()) {
            throw new IllegalStateException("仅终态可重试，当前: " + this.status.getDesc());
        }
        if (this.retryCount >= this.maxRetries) {
            throw new IllegalStateException("重试次数已达上限: " + this.retryCount + "/" + this.maxRetries);
        }
        this.status = AsyncTaskStatus.SUBMITTED;
        this.retryCount++;
        this.errorMessage = null;
        this.resultJson = null;
        this.startedAt = null;
        this.finishedAt = null;
    }

    // ========== 查询方法 ==========

    public boolean isActive() {
        return this.status != null && this.status.isActive();
    }

    public boolean isTerminal() {
        return this.status != null && this.status.isTerminal();
    }

    public boolean canRetry() {
        return isTerminal() && this.retryCount < this.maxRetries;
    }

    private void assertRunning() {
        if (this.status != AsyncTaskStatus.RUNNING) {
            throw new IllegalStateException("仅 RUNNING 状态可完成，当前: " + this.status.getDesc());
        }
    }
}
