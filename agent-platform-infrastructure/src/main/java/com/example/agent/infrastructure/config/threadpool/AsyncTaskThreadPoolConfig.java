package com.example.agent.infrastructure.config.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 通用异步任务线程池配置.
 * <p>
 * 所有 TaskCenter 提交的任务共享此线程池，不按任务类型拆分。
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Configuration
public class AsyncTaskThreadPoolConfig {

    /** 核心线程数 */
    private static final int CORE_POOL_SIZE = 4;
    /** 最大线程数 */
    private static final int MAX_POOL_SIZE = 8;
    /** 队列容量 */
    private static final int QUEUE_CAPACITY = 100;
    /** 空闲线程存活时间（秒） */
    private static final int KEEP_ALIVE_SECONDS = 120;
    /** 线程名前缀 */
    private static final String THREAD_NAME_PREFIX = "async-task-";
    /** 优雅关闭等待时间（秒） */
    private static final int AWAIT_TERMINATION_SECONDS = 30;

    @Bean("asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        executor.initialize();
        log.info("[AsyncTaskThreadPool] 异步任务线程池已初始化: core={}, max={}, queue={}, rejected=CallerRuns",
                CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY);
        return executor;
    }
}
