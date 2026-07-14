package com.example.agent.infrastructure.config.scheduler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.LongSupplier;

/**
 * 动态定时任务管理器 — 替代 {@code @Scheduled} 注解，支持运行时调整任务间隔.
 *
 * <h3>设计动机</h3>
 * Spring {@code @Scheduled(fixedDelay = CONSTANT)} 的间隔在容器启动时解析，之后不可变。
 * 本管理器每次触发前动态读取 {@link com.example.agent.infrastructure.config.nacos.SchedulerConfig}
 * 中 Nacos 配置的最新值，实现免重启调优.
 *
 * <h3>使用方式</h3>
 * <pre>{@code
 * @Component
 * public class MyTask {
 *     private final DynamicScheduledTaskManager scheduler;
 *     private final SchedulerConfig config;
 *
 *     @PostConstruct
 *     public void register() {
 *         scheduler.register("myTask", this::execute, config::getMyTaskIntervalMs);
 *     }
 *
 *     void execute() { ... }  // 无需 @Scheduled
 * }}</pre>
 *
 * <h3>线程模型</h3>
 * 所有定时任务共享一个 {@link ThreadPoolTaskScheduler} 线程池（core=4），
 * 适合轻量级扫描/探测任务。CPU 密集型任务应使用独立的 {@code @Async} 线程池.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class DynamicScheduledTaskManager {

    /** 调度线程池 — core=4，适合轻量级定时扫描 */
    private final ThreadPoolTaskScheduler taskScheduler;

    /** 已注册的任务: taskName → ScheduledFuture */
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DynamicScheduledTaskManager() {
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(4);
        this.taskScheduler.setThreadNamePrefix("dynamic-scheduler-");
        this.taskScheduler.setRemoveOnCancelPolicy(true);
        this.taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        this.taskScheduler.setAwaitTerminationSeconds(10);
        this.taskScheduler.initialize();
        log.info("[DynamicScheduler] 调度线程池已初始化: poolSize=4");
    }

    /**
     * 注册一个动态定时任务 — 首次执行延迟为 initialDelayMs，之后每次执行完等待
     * intervalMsSupplier 返回的最新间隔再触发.
     *
     * @param taskName            任务名称（用于日志和取消）
     * @param task                要执行的任务（Runnable）
     * @param intervalMsSupplier  间隔毫秒提供者 — 每次触发时调用，返回最新间隔
     */
    public void register(String taskName, Runnable task, LongSupplier intervalMsSupplier) {
        registerWithInitialDelay(taskName, task, intervalMsSupplier, intervalMsSupplier.getAsLong());
    }

    /**
     * 注册一个动态定时任务 — 可指定首次延迟与后续间隔不同的 Supplier.
     *
     * @param taskName              任务名称
     * @param task                  要执行的任务
     * @param intervalMsSupplier    后续间隔毫秒提供者
     * @param initialDelayMs        首次执行延迟（毫秒）
     */
    public void registerWithInitialDelay(String taskName, Runnable task,
                                          LongSupplier intervalMsSupplier, long initialDelayMs) {
        cancelTask(taskName);  // 防止重复注册

        ScheduledFuture<?> future = taskScheduler.schedule(
                new DynamicTriggerTask(taskName, task, intervalMsSupplier),
                triggerContext -> {
                    // TriggerContext 在 Spring 5.x 返回 Date，6.x 返回 Instant
                    // 为兼容两个版本，通过 System.currentTimeMillis() 追踪时间
                    Object lastExecution = triggerContext.lastScheduledExecutionTime();
                    if (lastExecution == null) {
                        return Instant.ofEpochMilli(System.currentTimeMillis() + initialDelayMs);
                    }
                    long intervalMs = intervalMsSupplier.getAsLong();
                    return Instant.ofEpochMilli(System.currentTimeMillis() + intervalMs);
                });

        scheduledTasks.put(taskName, future);
        log.info("[DynamicScheduler] 任务已注册: name={}, initialDelayMs={}", taskName, initialDelayMs);
    }

    /**
     * 取消并移除指定任务.
     *
     * @param taskName 任务名称
     */
    public void cancelTask(String taskName) {
        ScheduledFuture<?> existing = scheduledTasks.remove(taskName);
        if (existing != null) {
            existing.cancel(false);
            log.info("[DynamicScheduler] 任务已取消: name={}", taskName);
        }
    }

    /**
     * 获取当前已注册的任务数量（运维调试用）.
     */
    public int getActiveTaskCount() {
        return scheduledTasks.size();
    }

    @PreDestroy
    public void shutdown() {
        log.info("[DynamicScheduler] 正在关闭调度线程池，活跃任务数: {}", scheduledTasks.size());
        scheduledTasks.values().forEach(f -> f.cancel(false));
        scheduledTasks.clear();
        taskScheduler.shutdown();
    }

    // ==================== 内部类 ====================

    /**
     * 包装 Runnable，添加间隔日志和执行异常容错.
     */
    private static class DynamicTriggerTask implements Runnable {
        private final String taskName;
        private final Runnable delegate;
        private final LongSupplier intervalMsSupplier;

        DynamicTriggerTask(String taskName, Runnable delegate, LongSupplier intervalMsSupplier) {
            this.taskName = taskName;
            this.delegate = delegate;
            this.intervalMsSupplier = intervalMsSupplier;
        }

        @Override
        public void run() {
            long intervalMs = intervalMsSupplier.getAsLong();
            log.debug("[DynamicScheduler] 触发任务: name={}, intervalMs={}", taskName, intervalMs);
            try {
                delegate.run();
            } catch (Exception e) {
                log.error("[DynamicScheduler] 任务执行异常: name={}", taskName, e);
            }
            log.debug("[DynamicScheduler] 任务完成: name={}, 下次间隔={}ms", taskName, intervalMs);
        }
    }
}
