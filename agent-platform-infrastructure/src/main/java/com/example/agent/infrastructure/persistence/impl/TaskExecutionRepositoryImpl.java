package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.task.entity.TaskExecution;
import com.example.agent.domain.task.repository.TaskExecutionRepository;
import com.example.agent.domain.task.valueobject.ExecutionStatus;
import com.example.agent.infrastructure.persistence.mapper.TaskExecutionMapper;
import com.example.agent.infrastructure.persistence.po.TaskExecutionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 任务执行仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class TaskExecutionRepositoryImpl implements TaskExecutionRepository {

    private final TaskExecutionMapper mapper;

    @Override
    public void save(TaskExecution execution) {
        mapper.insert(toPO(execution));
    }

    @Override
    public void update(TaskExecution execution) {
        mapper.update(toPO(execution));
    }

    @Override
    public void updateStatus(String executionId, ExecutionStatus status) {
        mapper.updateStatus(executionId, status.getCode());
    }

    @Override
    public void updateProgress(String executionId, int completedSteps) {
        mapper.updateProgress(executionId, completedSteps);
    }

    @Override
    public void markFailed(String executionId, String failedStepId, String errorMessage) {
        mapper.markFailed(executionId, failedStepId, errorMessage);
    }

    @Override
    public Optional<TaskExecution> findByExecutionId(String executionId) {
        return mapper.selectByExecutionId(executionId).map(this::toDomain);
    }

    @Override
    public List<TaskExecution> findByConversationId(String conversationId) {
        return mapper.selectByConversationId(conversationId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<TaskExecution> findByAgentId(String agentId, int offset, int size) {
        return mapper.selectByAgentId(agentId, offset, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByAgentId(String agentId) {
        return mapper.countByAgentId(agentId);
    }

    @Override
    public List<TaskExecution> findActiveByTenant(Long tenantId) {
        return mapper.selectActiveByTenant(tenantId)
                .stream().map(this::toDomain).toList();
    }

    // ==================== 映射方法 ====================

    private TaskExecution toDomain(TaskExecutionPO po) {
        return TaskExecution.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .executionId(po.getExecutionId())
                .conversationId(po.getConversationId())
                .agentId(po.getAgentId())
                .planJson(po.getPlanJson())
                .status(ExecutionStatus.fromCode(po.getStatus()))
                .totalSteps(po.getTotalSteps() != null ? po.getTotalSteps() : 0)
                .completedSteps(po.getCompletedSteps() != null ? po.getCompletedSteps() : 0)
                .failedStepId(po.getFailedStepId())
                .errorMessage(po.getErrorMessage())
                .startedAt(po.getStartedAt())
                .finishedAt(po.getFinishedAt())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private TaskExecutionPO toPO(TaskExecution execution) {
        return TaskExecutionPO.builder()
                .id(execution.getId())
                .tenantId(execution.getTenantId())
                .executionId(execution.getExecutionId())
                .conversationId(execution.getConversationId())
                .agentId(execution.getAgentId())
                .planJson(execution.getPlanJson())
                .status(execution.getStatus().getCode())
                .totalSteps(execution.getTotalSteps())
                .completedSteps(execution.getCompletedSteps())
                .failedStepId(execution.getFailedStepId())
                .errorMessage(execution.getErrorMessage())
                .startedAt(execution.getStartedAt())
                .finishedAt(execution.getFinishedAt())
                .createdAt(execution.getCreatedAt() != null ? execution.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
