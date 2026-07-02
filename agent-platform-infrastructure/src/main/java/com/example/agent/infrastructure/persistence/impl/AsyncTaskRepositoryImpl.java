package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.task.entity.AsyncTask;
import com.example.agent.domain.task.repository.AsyncTaskRepository;
import com.example.agent.domain.task.valueobject.AsyncTaskStatus;
import com.example.agent.infrastructure.persistence.mapper.AsyncTaskMapper;
import com.example.agent.infrastructure.persistence.po.AsyncTaskPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 异步任务仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class AsyncTaskRepositoryImpl implements AsyncTaskRepository {

    private final AsyncTaskMapper mapper;

    /** 活跃状态码列表（SUBMITTED + RUNNING），用于防重查询 */
    private static final List<String> ACTIVE_STATUS_CODES = List.of(
            AsyncTaskStatus.SUBMITTED.getCode(),
            AsyncTaskStatus.RUNNING.getCode()
    );

    @Override
    public void save(AsyncTask task) {
        mapper.insert(toPO(task));
    }

    @Override
    public void update(AsyncTask task) {
        mapper.update(toPO(task));
    }

    @Override
    public int updateStatusIfExpected(String taskId, AsyncTaskStatus expected,
                                       AsyncTaskStatus target, String errorMessage, String resultJson) {
        return mapper.updateStatusIfExpected(taskId,
                expected.getCode(), target.getCode(), errorMessage, resultJson);
    }

    @Override
    public int incrementRetryAndReset(String taskId) {
        return mapper.incrementRetryAndReset(taskId, AsyncTaskStatus.SUBMITTED.getCode());
    }

    @Override
    public Optional<AsyncTask> findByTaskId(String taskId) {
        return Optional.ofNullable(mapper.selectByTaskId(taskId)).map(this::toDomain);
    }

    @Override
    public List<AsyncTask> findTimeoutRunning(LocalDateTime now, int limit) {
        return mapper.selectTimeoutRunning(now, AsyncTaskStatus.RUNNING.getCode(), limit)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<AsyncTask> findStaleSubmitted(LocalDateTime before, int limit) {
        return mapper.selectStaleSubmitted(before, AsyncTaskStatus.SUBMITTED.getCode(), limit)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public int countActiveByTypeAndBiz(String taskType, String bizId) {
        return mapper.countActiveByTypeAndBiz(taskType, bizId, ACTIVE_STATUS_CODES);
    }

    // ==================== 映射方法 ====================

    private AsyncTask toDomain(AsyncTaskPO po) {
        return AsyncTask.builder()
                .id(po.getId())
                .taskId(po.getTaskId())
                .taskType(po.getTaskType())
                .bizId(po.getBizId())
                .tenantId(po.getTenantId())
                .status(po.getStatus() != null ? AsyncTaskStatus.fromCode(po.getStatus()) : null)
                .payloadJson(po.getPayloadJson())
                .resultJson(po.getResultJson())
                .timeoutAt(po.getTimeoutAt())
                .retryCount(po.getRetryCount() != null ? po.getRetryCount() : 0)
                .maxRetries(po.getMaxRetries() != null ? po.getMaxRetries() : 3)
                .errorMessage(po.getErrorMessage())
                .startedAt(po.getStartedAt())
                .finishedAt(po.getFinishedAt())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private AsyncTaskPO toPO(AsyncTask entity) {
        return AsyncTaskPO.builder()
                .taskId(entity.getTaskId())
                .taskType(entity.getTaskType())
                .bizId(entity.getBizId())
                .tenantId(entity.getTenantId())
                .status(entity.getStatus() != null ? entity.getStatus().getCode() : null)
                .payloadJson(entity.getPayloadJson())
                .resultJson(entity.getResultJson())
                .timeoutAt(entity.getTimeoutAt())
                .retryCount(entity.getRetryCount())
                .maxRetries(entity.getMaxRetries())
                .errorMessage(entity.getErrorMessage())
                .startedAt(entity.getStartedAt())
                .finishedAt(entity.getFinishedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
