package com.example.agent.application.task;

import com.example.agent.domain.task.entity.AsyncTask;
import com.example.agent.domain.task.repository.AsyncTaskRepository;
import com.example.agent.domain.task.service.TaskHandler;
import com.example.agent.domain.task.valueobject.AsyncTaskStatus;
import com.example.agent.infrastructure.config.nacos.SchedulerConfig;
import com.example.agent.infrastructure.config.scheduler.DynamicScheduledTaskManager;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务超时扫描器 — 定期扫描超时和僵尸任务.
 *
 * <p>使用 {@link DynamicScheduledTaskManager} 替代 {@code @Scheduled} 注解，
 * 扫描间隔从 {@link SchedulerConfig}（Nacos 动态配置）读取，支持运行时免重启调优.
 * <p>与 worker 线程通过 CAS（WHERE status='RUNNING'）竞争，避免重复处理.
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
    private final DynamicScheduledTaskManager dynamicScheduler;
    private final SchedulerConfig schedulerConfig;
    private final Gson gson = new Gson();

    @PostConstruct
    public void registerTasks() {
        // 注册超时 RUNNING 任务扫描
        dynamicScheduler.register(
                "timeoutTaskScan",
                this::scanTimeoutTasks,
                schedulerConfig::getTimeoutScanIntervalMs);

        // 注册僵尸 SUBMITTED 任务扫描
        dynamicScheduler.register(
                "staleSubmittedScan",
                this::scanStaleSubmittedTasks,
                schedulerConfig::getStaleScanIntervalMs);

        log.info("[TaskTimeoutScanner] 动态定时任务已注册: timeoutTaskScan, staleSubmittedScan");
    }

    /**
     * 扫描超时的 RUNNING 任务.
     */
    public void scanTimeoutTasks() {
        try {
            int batchSize = schedulerConfig.getTimeoutScanBatchSize();
            List<AsyncTask> timeoutTasks = taskRepository.findTimeoutRunning(LocalDateTime.now(), batchSize);
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
     * 扫描僵尸 SUBMITTED 任务（提交后长时间未启动）.
     */
    public void scanStaleSubmittedTasks() {
        try {
            int staleMinutes = schedulerConfig.getStaleSubmittedMinutes();
            int batchSize = schedulerConfig.getTimeoutScanBatchSize();
            LocalDateTime before = LocalDateTime.now().minusMinutes(staleMinutes);
            List<AsyncTask> staleTasks = taskRepository.findStaleSubmitted(before, batchSize);
            if (staleTasks.isEmpty()) return;

            log.warn("[TaskTimeoutScanner] 发现 {} 个僵尸 SUBMITTED 任务（>{}分钟未启动）",
                    staleTasks.size(), staleMinutes);
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
            int staleMinutes = schedulerConfig.getStaleSubmittedMinutes();
            // CAS: SUBMITTED → FAILED
            int rows = taskRepository.updateStatusIfExpected(task.getTaskId(),
                    AsyncTaskStatus.SUBMITTED, AsyncTaskStatus.FAILED,
                    "任务提交后超过" + staleMinutes + "分钟未启动（可能应用重启导致丢失）", null);

            if (rows > 0) {
                log.warn("[TaskTimeoutScanner] 僵尸任务已标记失败: taskId={}, taskType={}, bizId={}",
                        task.getTaskId(), task.getTaskType(), task.getBizId());
            }
        } catch (Exception e) {
            log.error("[TaskTimeoutScanner] 处理僵尸任务失败: taskId={}", task.getTaskId(), e);
        }
    }
}
