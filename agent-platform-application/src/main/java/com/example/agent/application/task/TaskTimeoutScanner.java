package com.example.agent.application.task;

import com.example.agent.domain.task.entity.AsyncTask;
import com.example.agent.domain.task.repository.AsyncTaskRepository;
import com.example.agent.domain.task.service.TaskHandler;
import com.example.agent.domain.task.valueobject.AsyncTaskStatus;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务超时扫描器 — 定期扫描超时和僵尸任务.
 * <p>
 * 与 worker 线程通过 CAS（WHERE status='RUNNING'）竞争，避免重复处理.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskTimeoutScanner {

    private final AsyncTaskRepository taskRepository;
    private final TaskHandlerRegistry handlerRegistry;
    private final Gson gson = new Gson();

    /** 每次扫描最大处理数 */
    private static final int BATCH_SIZE = 100;
    /** 超时扫描间隔（毫秒） */
    private static final int TIMEOUT_SCAN_INTERVAL_MS = 15_000;
    /** 僵尸任务扫描间隔（毫秒） */
    private static final int STALE_SCAN_INTERVAL_MS = 60_000;
    /** SUBMITTED 任务超时未启动的阈值（分钟） */
    private static final int STALE_SUBMITTED_MINUTES = 5;

    /**
     * 每 15 秒扫描超时的 RUNNING 任务.
     */
    @Scheduled(fixedDelay = TIMEOUT_SCAN_INTERVAL_MS)
    public void scanTimeoutTasks() {
        try {
            List<AsyncTask> timeoutTasks = taskRepository.findTimeoutRunning(LocalDateTime.now(), BATCH_SIZE);
            if (timeoutTasks.isEmpty()) return;

            log.info("[TaskTimeoutScanner] 发现 {} 个超时任务", timeoutTasks.size());
            for (AsyncTask task : timeoutTasks) {
                handleTimeout(task);
            }
        } catch (Exception e) {
            log.error("[TaskTimeoutScanner] 扫描超时任务异常", e);
        }
    }

    /**
     * 每 60 秒扫描僵尸 SUBMITTED 任务（提交后长时间未启动）.
     */
    @Scheduled(fixedDelay = STALE_SCAN_INTERVAL_MS)
    public void scanStaleSubmittedTasks() {
        try {
            LocalDateTime before = LocalDateTime.now().minusMinutes(STALE_SUBMITTED_MINUTES);
            List<AsyncTask> staleTasks = taskRepository.findStaleSubmitted(before, BATCH_SIZE);
            if (staleTasks.isEmpty()) return;

            log.warn("[TaskTimeoutScanner] 发现 {} 个僵尸 SUBMITTED 任务（>{}分钟未启动）",
                    staleTasks.size(), STALE_SUBMITTED_MINUTES);
            for (AsyncTask task : staleTasks) {
                handleStaleSubmitted(task);
            }
        } catch (Exception e) {
            log.error("[TaskTimeoutScanner] 扫描僵尸任务异常", e);
        }
    }

    // ==================== 内部处理 ====================

    @SuppressWarnings("unchecked")
    private void handleTimeout(AsyncTask task) {
        try {
            // CAS: RUNNING → TIMEOUT
            int rows = taskRepository.updateStatusIfExpected(task.getTaskId(),
                    AsyncTaskStatus.RUNNING, AsyncTaskStatus.TIMEOUT,
                    "任务执行超时", null);

            if (rows > 0) {
                log.warn("[TaskTimeoutScanner] 任务已超时: taskId={}, taskType={}, bizId={}",
                        task.getTaskId(), task.getTaskType(), task.getBizId());

                // 回调 Handler.onTimeout
                try {
                    TaskHandler<Object> handler = (TaskHandler<Object>) handlerRegistry
                            .getHandler(task.getTaskType());
                    Object payload = gson.fromJson(task.getPayloadJson(), handler.getPayloadType());
                    handler.onTimeout(payload);
                } catch (Exception callbackEx) {
                    log.error("[TaskTimeoutScanner] onTimeout 回调异常: taskId={}",
                            task.getTaskId(), callbackEx);
                }
            }
        } catch (Exception e) {
            log.error("[TaskTimeoutScanner] 处理超时任务失败: taskId={}", task.getTaskId(), e);
        }
    }

    private void handleStaleSubmitted(AsyncTask task) {
        try {
            // CAS: SUBMITTED → FAILED
            int rows = taskRepository.updateStatusIfExpected(task.getTaskId(),
                    AsyncTaskStatus.SUBMITTED, AsyncTaskStatus.FAILED,
                    "任务提交后超过" + STALE_SUBMITTED_MINUTES + "分钟未启动（可能应用重启导致丢失）", null);

            if (rows > 0) {
                log.warn("[TaskTimeoutScanner] 僵尸任务已标记失败: taskId={}, taskType={}, bizId={}",
                        task.getTaskId(), task.getTaskType(), task.getBizId());
            }
        } catch (Exception e) {
            log.error("[TaskTimeoutScanner] 处理僵尸任务失败: taskId={}", task.getTaskId(), e);
        }
    }
}
