package com.example.agent.infrastructure.config.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 审计日志异步写入线程池配置.
 *
 * <p>此线程池专用于审计日志的异步持久化，与业务线程池隔离，
 * 确保审计写入的延迟和失败不影响主业务流程。
 *
 * <h3>配置说明</h3>
 * <ul>
 *   <li><b>corePoolSize = 2</b> — 日常仅维持 2 个线程</li>
 *   <li><b>maxPoolSize = 4</b> — 峰值扩容上限</li>
 *   <li><b>queueCapacity = 1000</b> — 缓冲区（满时直接丢弃，返回 WARN 日志）</li>
 *   <li><b>DiscardPolicy</b> — 丢弃策略（fail-open，不阻断业务）</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class AuditLogThreadPoolConfig {

    public static final String AUDIT_LOG_EXECUTOR = "auditLogExecutor";

    @Bean(name = AUDIT_LOG_EXECUTOR)
    public ThreadPoolTaskExecutor auditLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("audit-log-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                log.warn("[AuditLog] 线程池队列已满，审计日志写入被丢弃（不影响业务）");
            }
        });
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
        log.info("[AuditLog] 审计日志线程池已初始化: core=2, max=4, queue=1000");
        return executor;
    }
}
