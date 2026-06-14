package com.example.agent.infrastructure.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流式响应专用线程池 — ThreadPoolExecutor，遵循阿里规范.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Configuration
public class StreamThreadPoolConfig {

    @Bean("streamExecutor")
    public ThreadPoolExecutor streamExecutor() {
        return new ThreadPoolExecutor(
                4,
                8,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                r -> {
                    Thread t = new Thread(r, "stream-pool-" + threadNum.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private final AtomicInteger threadNum = new AtomicInteger(0);
}
