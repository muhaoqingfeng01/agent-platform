package com.example.agent.common.util;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * MDC 上下文传播工具 — 在线程池异步执行时保留 traceId 等诊断上下文.
 *
 * <p><b>为什么不用 Spring 的 TaskDecorator？</b>
 * <p>Spring 6.1 的 {@code ThreadPoolTaskExecutor.execute(Runnable)} 和 {@code submit(Runnable)}
 * 直接透传到底层 {@code ThreadPoolExecutor}，<b>不经过</b> {@code TaskDecorator}。
 * 只有在 {@code execute(Runnable, long)}（AsyncTaskExecutor 接口方法）中才会调用 decorator。
 * 因此 {@code setTaskDecorator(new MdcTaskDecorator())} 对 {@code execute/submit} 调用<b>无效</b>。
 *
 * <p>本工具类在每个线程池提交点<b>显式捕获并恢复</b> MDC 上下文，不依赖框架行为。
 *
 * <p>用法：
 * <pre>{@code
 *   // 提交 Runnable
 *   executor.submit(MdcContext.wrap(() -> doWork()));
 *
 *   // 提交 Callable
 *   Future<String> f = executor.submit(MdcContext.wrap(() -> compute()));
 *
 *   // CompletableFuture
 *   future.thenApplyAsync(MdcContext.wrapFn(result -> transform(result)), executor);
 * }</pre>
 *
 * <p><b>关键约束</b>：{@code wrap()} 必须在<b>拥有 MDC 上下文的线程</b>（通常是 HTTP 请求线程）上调用，
 * 不能在已经丢失 MDC 的 lambda 内部调用。
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
public final class MdcContext {

    private MdcContext() {
        // utility class
    }

    /**
     * 包装 Runnable — 捕获当前线程的 MDC 上下文，执行时恢复，执行完清理.
     *
     * @param task 原始任务
     * @return 包装后的任务（可直接提交到线程池）
     */
    public static Runnable wrap(Runnable task) {
        Map<String, String> ctx = MDC.getCopyOfContextMap();
        return () -> {
            if (ctx != null) {
                MDC.setContextMap(ctx);
            }
            try {
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装 Callable — 捕获当前线程的 MDC 上下文，执行时恢复，执行完清理.
     *
     * @param task 原始任务
     * @param <T>  返回值类型
     * @return 包装后的任务
     */
    public static <T> Callable<T> wrap(Callable<T> task) {
        Map<String, String> ctx = MDC.getCopyOfContextMap();
        return () -> {
            if (ctx != null) {
                MDC.setContextMap(ctx);
            }
            try {
                return task.call();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装 java.util.function.Function — 用于 CompletableFuture.thenApplyAsync 等场景.
     *
     * @param fn  原始函数
     * @param <T> 输入类型
     * @param <R> 返回值类型
     * @return 包装后的函数
     */
    public static <T, R> java.util.function.Function<T, R> wrapFn(java.util.function.Function<T, R> fn) {
        Map<String, String> ctx = MDC.getCopyOfContextMap();
        return (T input) -> {
            if (ctx != null) {
                MDC.setContextMap(ctx);
            }
            try {
                return fn.apply(input);
            } finally {
                MDC.clear();
            }
        };
    }
}
