package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.task.entity.TaskStepExecution;
import com.example.agent.domain.task.repository.TaskStepExecutionRepository;
import com.example.agent.domain.task.valueobject.StepStatus;
import com.example.agent.infrastructure.persistence.mapper.TaskStepExecutionMapper;
import com.example.agent.infrastructure.persistence.po.TaskStepExecutionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 任务步骤执行仓储 MyBatis 实现 — Repository 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class TaskStepExecutionRepositoryImpl implements TaskStepExecutionRepository {

    private final TaskStepExecutionMapper mapper;

    @Override
    public void batchSave(List<TaskStepExecution> steps) {
        if (steps == null || steps.isEmpty()) return;
        List<TaskStepExecutionPO> poList = steps.stream().map(this::toPO).toList();
        mapper.batchInsert(poList);
        log.info("[TaskStepRepo] 批量保存 {} 条步骤记录", poList.size());
    }

    @Override
    public void updateStatus(String executionId, String stepId, StepStatus status) {
        mapper.updateStatus(executionId, stepId, status.getCode());
    }

    @Override
    public void updateResult(String executionId, String stepId, StepStatus status,
                              String outputJson, String errorMessage, long durationMs) {
        mapper.updateResult(executionId, stepId, status.getCode(), outputJson, errorMessage, durationMs);
    }

    @Override
    public void updateRetry(String executionId, String stepId, int retryCount, StepStatus status) {
        mapper.updateRetry(executionId, stepId, retryCount, status.getCode());
    }

    @Override
    public Optional<TaskStepExecution> findByExecutionIdAndStepId(String executionId, String stepId) {
        return mapper.selectByExecIdAndStepId(executionId, stepId).map(this::toDomain);
    }

    @Override
    public List<TaskStepExecution> findByExecutionId(String executionId) {
        return mapper.selectByExecutionId(executionId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void batchUpdateStatusByExecutionId(String executionId, StepStatus status) {
        mapper.batchUpdateStatusByExecutionId(executionId, status.getCode());
    }

    // ==================== 映射方法 ====================

    private TaskStepExecution toDomain(TaskStepExecutionPO po) {
        return TaskStepExecution.builder()
                .id(po.getId())
                .executionId(po.getExecutionId())
                .stepId(po.getStepId())
                .action(po.getAction())
                .handlerClass(po.getHandlerClass())
                .inputJson(po.getInputJson())
                .outputJson(po.getOutputJson())
                .status(StepStatus.fromCode(po.getStatus()))
                .retryCount(po.getRetryCount() != null ? po.getRetryCount() : 0)
                .maxRetries(po.getMaxRetries() != null ? po.getMaxRetries() : 3)
                .durationMs(po.getDurationMs())
                .errorMessage(po.getErrorMessage())
                .startedAt(po.getStartedAt())
                .finishedAt(po.getFinishedAt())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private TaskStepExecutionPO toPO(TaskStepExecution step) {
        return TaskStepExecutionPO.builder()
                .id(step.getId())
                .executionId(step.getExecutionId())
                .stepId(step.getStepId())
                .action(step.getAction())
                .handlerClass(step.getHandlerClass())
                .inputJson(step.getInputJson())
                .outputJson(step.getOutputJson())
                .status(step.getStatus().getCode())
                .retryCount(step.getRetryCount())
                .maxRetries(step.getMaxRetries())
                .durationMs(step.getDurationMs())
                .errorMessage(step.getErrorMessage())
                .startedAt(step.getStartedAt())
                .finishedAt(step.getFinishedAt())
                .createdAt(step.getCreatedAt() != null ? step.getCreatedAt() : LocalDateTime.now())
                .build();
    }
}
