package com.example.agent.domain.task.repository;

import com.example.agent.domain.task.entity.TaskExecution;
import com.example.agent.domain.task.valueobject.ExecutionStatus;

import java.util.List;
import java.util.Optional;

/**
 * 任务执行仓储接口 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface TaskExecutionRepository {

    /** 保存任务执行记录 */
    void save(TaskExecution execution);

    /** 更新任务执行记录 */
    void update(TaskExecution execution);

    /** 更新执行状态 */
    void updateStatus(String executionId, ExecutionStatus status);

    /** 更新进度（已完成步骤数） */
    void updateProgress(String executionId, int completedSteps);

    /** 标记失败 */
    void markFailed(String executionId, String failedStepId, String errorMessage);

    /** 根据 executionId 查询 */
    Optional<TaskExecution> findByExecutionId(String executionId);

    /** 根据会话 ID 查询执行列表 */
    List<TaskExecution> findByConversationId(String conversationId);

    /** 根据 Agent ID 分页查询 */
    List<TaskExecution> findByAgentId(String agentId, int offset, int size);

    /** 根据 Agent ID 统计总数 */
    long countByAgentId(String agentId);

    /** 查询租户下活跃（未完结）的执行 */
    List<TaskExecution> findActiveByTenant(String tenantId);
}
