package com.example.agent.domain.task.repository;

import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.valueobject.StepStatus;

import java.util.List;
import java.util.Optional;

/**
 * 任务步骤执行仓储接口 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface TaskStepExecutionRepository {

    /** 批量保存步骤执行记录 */
    void batchSave(List<TaskStepExecution> steps);

    /** 更新步骤状态 */
    void updateStatus(String executionId, String stepId, StepStatus status);

    /** 更新步骤结果 */
    void updateResult(String executionId, String stepId, StepStatus status,
                      String outputJson, String errorMessage, long durationMs);

    /** 更新重试信息 */
    void updateRetry(String executionId, String stepId, int retryCount, StepStatus status);

    /** 根据 executionId + stepId 查询 */
    Optional<TaskStepExecution> findByExecutionIdAndStepId(String executionId, String stepId);

    /** 根据 executionId 查询所有步骤 */
    List<TaskStepExecution> findByExecutionId(String executionId);

    /** 批量更新步骤状态（用于失败时跳过剩余步骤） */
    void batchUpdateStatusByExecutionId(String executionId, StepStatus status);
}
