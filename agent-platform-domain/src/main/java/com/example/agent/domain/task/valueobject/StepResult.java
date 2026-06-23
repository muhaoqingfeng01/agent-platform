package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 步骤执行结果值对象.
 *
 * <p>封装单个步骤的执行结果，供 DAG 执行器层间传递.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepResult {

    /** 步骤 ID */
    private String stepId;

    /** 执行状态 */
    private StepStatus status;

    /** 执行结果数据 */
    private Object result;

    /** 执行耗时（毫秒） */
    private long durationMs;

    /** 错误信息 */
    private String errorMessage;

    /** 是否已重试 */
    private boolean retried;

    /** 实际重试次数 */
    private int retryCount;

    // ==================== 工厂方法 ====================

    public static StepResult success(String stepId, Object result, long durationMs) {
        return StepResult.builder()
                .stepId(stepId)
                .status(StepStatus.SUCCESS)
                .result(result)
                .durationMs(durationMs)
                .build();
    }

    public static StepResult failed(String stepId, String errorMessage, long durationMs) {
        return StepResult.builder()
                .stepId(stepId)
                .status(StepStatus.FAILED)
                .errorMessage(errorMessage)
                .durationMs(durationMs)
                .build();
    }

    public static StepResult timeout(String stepId, long durationMs) {
        return StepResult.builder()
                .stepId(stepId)
                .status(StepStatus.TIMEOUT)
                .durationMs(durationMs)
                .errorMessage("执行超时")
                .build();
    }

    public static StepResult skipped(String stepId, String reason) {
        return StepResult.builder()
                .stepId(stepId)
                .status(StepStatus.SKIPPED)
                .errorMessage(reason)
                .build();
    }

    public boolean isSuccess() {
        return this.status == StepStatus.SUCCESS;
    }

    public boolean isFailed() {
        return this.status == StepStatus.FAILED || this.status == StepStatus.TIMEOUT;
    }
}
