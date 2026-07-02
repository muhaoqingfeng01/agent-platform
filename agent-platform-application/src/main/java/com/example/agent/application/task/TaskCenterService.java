package com.example.agent.application.task;

import com.example.agent.common.exception.TaskTimeoutException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.task.entity.AsyncTask;
import com.example.agent.domain.task.repository.AsyncTaskRepository;
import com.example.agent.domain.task.service.TaskHandler;
import com.example.agent.domain.task.valueobject.AsyncTaskStatus;
import com.example.agent.domain.task.valueobject.TaskResult;
import com.example.agent.infrastructure.context.TenantContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

/**
 * 通用异步任务中心 — 管理任务生命周期（提交、执行、超时、重试）.
 * <p>
 * 不关心任何业务逻辑，所有业务操作通过 TaskHandler 接口解耦.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Service
public class TaskCenterService {

    /** 任务 ID 前缀 */
    public static final String TASK_ID_PREFIX = "task";
    /** 错误信息最大长度（超过截断） */
    private static final int ERROR_MSG_MAX_LENGTH = 2000;

    private final AsyncTaskRepository taskRepository;
    private final TaskHandlerRegistry handlerRegistry;
    private final TransactionTemplate transactionTemplate;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final Gson gson = new Gson();

    public TaskCenterService(AsyncTaskRepository taskRepository,
                              TaskHandlerRegistry handlerRegistry,
                              TransactionTemplate transactionTemplate,
                              @Qualifier("asyncTaskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.taskRepository = taskRepository;
        this.handlerRegistry = handlerRegistry;
        this.transactionTemplate = transactionTemplate;
        this.taskExecutor = taskExecutor;
    }

    /**
     * 提交任务 — 创建任务记录并在事务提交后入队执行.
     *
     * @param taskType 任务类型
     * @param bizId    业务主体 ID（防重用）
     * @param payload  业务参数
     * @param tenantId 租户 ID
     * @return 任务 ID
     */
    public String submit(String taskType, String bizId, Object payload, Long tenantId) {
        // 1. 防重：同类型 + 同业务主体不允许重复提交
        int activeCount = taskRepository.countActiveByTypeAndBiz(taskType, bizId);
        if (activeCount > 0) {
            throw new IllegalStateException(
                    String.format("该业务已有活跃任务: taskType=%s, bizId=%s", taskType, bizId));
        }

        // 2. 查找 Handler
        TaskHandler<?> rawHandler = handlerRegistry.getHandler(taskType);

        // 3. 构造任务记录
        String taskId = IdGenerator.generate(TASK_ID_PREFIX);
        String payloadJson = gson.toJson(payload);
        int timeoutSeconds = rawHandler.getTimeoutSeconds();

        AsyncTask task = AsyncTask.builder()
                .taskId(taskId)
                .taskType(taskType)
                .bizId(bizId)
                .tenantId(tenantId)
                .status(AsyncTaskStatus.SUBMITTED)
                .payloadJson(payloadJson)
                .timeoutAt(LocalDateTime.now().plusSeconds(timeoutSeconds))
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskRepository.save(task);

        // 4. 事务提交后入队执行
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            taskExecutor.submit(() -> executeTask(taskId));
                        }
                    });
        } else {
            taskExecutor.submit(() -> executeTask(taskId));
        }

        log.info("[TaskCenter] 任务已提交: taskId={}, taskType={}, bizId={}, timeoutSeconds={}",
                taskId, taskType, bizId, timeoutSeconds);
        return taskId;
    }

    /**
     * 重试失败/超时的任务.
     */
    public void retryTask(String taskId) {
        AsyncTask task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在: " + taskId));

        if (!task.canRetry()) {
            throw new IllegalStateException(
                    String.format("任务不可重试: taskId=%s, status=%s, retryCount=%d/%d",
                            taskId, task.getStatus().getDesc(), task.getRetryCount(), task.getMaxRetries()));
        }

        taskRepository.incrementRetryAndReset(taskId);

        // 获取更新后的 timeout
        AsyncTask updated = taskRepository.findByTaskId(taskId).orElseThrow();
        TaskHandler<?> handler = handlerRegistry.getHandler(updated.getTaskType());
        int timeoutSeconds = handler.getTimeoutSeconds();

        // 更新 timeout_at
        AsyncTask withTimeout = updated.toBuilder()
                .timeoutAt(LocalDateTime.now().plusSeconds(timeoutSeconds))
                .build();
        taskRepository.update(withTimeout);

        taskExecutor.submit(() -> executeTask(taskId));
        log.info("[TaskCenter] 任务重试已提交: taskId={}, retryCount={}", taskId, updated.getRetryCount());
    }

    // ==================== 内部执行逻辑 ====================

    @SuppressWarnings("unchecked")
    private <T> void executeTask(String taskId) {
        AsyncTask task = taskRepository.findByTaskId(taskId).orElse(null);
        if (task == null) {
            log.warn("[TaskCenter] 任务不存在，跳过: taskId={}", taskId);
            return;
        }

        // CAS: SUBMITTED → RUNNING
        int cas = taskRepository.updateStatusIfExpected(taskId,
                AsyncTaskStatus.SUBMITTED, AsyncTaskStatus.RUNNING, null, null);
        if (cas <= 0) {
            log.info("[TaskCenter] 任务状态不是 SUBMITTED，跳过: taskId={}, status={}",
                    taskId, task.getStatus().getDesc());
            return;
        }

        TaskHandler<T> handler = (TaskHandler<T>) handlerRegistry.getHandler(task.getTaskType());
        T payload = gson.fromJson(task.getPayloadJson(), handler.getPayloadType());
        long deadlineMs = System.currentTimeMillis() + handler.getTimeoutSeconds() * 1000L;

        log.info("[TaskCenter] 开始执行任务: taskId={}, taskType={}, deadlineMs={}",
                taskId, task.getTaskType(), deadlineMs);

        try {
            TaskResult result = handler.execute(payload, deadlineMs);

            // CAS: RUNNING → COMPLETED
            int updateRows = taskRepository.updateStatusIfExpected(taskId,
                    AsyncTaskStatus.RUNNING, AsyncTaskStatus.COMPLETED,
                    null, result.getResultJson());

            if (updateRows > 0) {
                handler.onSuccess(payload, result);
                log.info("[TaskCenter] 任务执行成功: taskId={}", taskId);
            } else {
                // 被扫描器标记为 TIMEOUT 了
                log.warn("[TaskCenter] 任务已被扫描器标记为超时，放弃更新: taskId={}", taskId);
            }

        } catch (TaskTimeoutException e) {
            // 协作式超时：不更新任务状态，等扫描器处理
            log.warn("[TaskCenter] 任务协作式超时退出: taskId={}, msg={}", taskId, e.getMessage());

        } catch (Exception e) {
            log.error("[TaskCenter] 任务执行异常: taskId={}", taskId, e);

            // CAS: RUNNING → FAILED
            int updateRows = taskRepository.updateStatusIfExpected(taskId,
                    AsyncTaskStatus.RUNNING, AsyncTaskStatus.FAILED,
                    truncateError(e.getMessage()), null);

            if (updateRows > 0) {
                try {
                    handler.onFailure(payload, e);
                } catch (Exception callbackEx) {
                    log.error("[TaskCenter] onFailure 回调异常: taskId={}", taskId, callbackEx);
                }
            }
        }
    }

    private String truncateError(String msg) {
        if (msg == null) return null;
        return msg.length() > ERROR_MSG_MAX_LENGTH
                ? msg.substring(0, ERROR_MSG_MAX_LENGTH) + "..."
                : msg;
    }
}
