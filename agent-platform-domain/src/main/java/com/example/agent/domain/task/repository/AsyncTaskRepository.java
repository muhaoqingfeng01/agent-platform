package com.example.agent.domain.task.repository;

import com.example.agent.domain.task.entity.AsyncTask;
import com.example.agent.domain.task.valueobject.AsyncTaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 异步任务仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
public interface AsyncTaskRepository {

    void save(AsyncTask task);

    void update(AsyncTask task);

    /** CAS 更新状态: 仅当前状态为 expected 时才更新 */
    int updateStatusIfExpected(String taskId, AsyncTaskStatus expected, AsyncTaskStatus target,
                                String errorMessage, String resultJson);

    /** 重试计数 +1，重置为 SUBMITTED */
    int incrementRetryAndReset(String taskId);

    Optional<AsyncTask> findByTaskId(String taskId);

    /** 查询超时的 RUNNING 任务 */
    List<AsyncTask> findTimeoutRunning(LocalDateTime now, int limit);

    /** 查询超时未启动的 SUBMITTED 任务 */
    List<AsyncTask> findStaleSubmitted(LocalDateTime before, int limit);

    /** 查询同类型+同业务主体的活跃任务数（防重） */
    int countActiveByTypeAndBiz(String taskType, String bizId);
}
