package com.example.agent.application.task.retry;

import com.example.agent.application.task.handler.ActionHandler;
import com.example.agent.domain.task.valueobject.StepResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 指数退避重试策略 — Template Method 模式.
 *
 * <p>重试间隔: 第1次 1s → 第2次 2s → 第3次 4s → ... → 第N次 2^(N-1) s
 * <p>可被子类覆盖 {@code beforeRetry} / {@code afterRetry} 钩子方法实现自定义逻辑。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
public class RetryPolicy {

    /** 最大退避时间上限（秒） */
    private static final long MAX_BACKOFF_SECONDS = 60;

    /**
     * 使用指数退避策略重试执行.
     *
     * @param handler   动作处理器
     * @param params    执行参数
     * @param maxRetries 最大重试次数
     * @param stepId    步骤 ID（用于日志追踪）
     * @return 最终执行结果
     */
    public static StepResult executeWithRetry(ActionHandler handler, Map<String, Object> params,
                                               int maxRetries, String stepId) {
        Exception lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            boolean isRetry = attempt > 0;
            if (isRetry) {
                long backoffMs = calculateBackoff(attempt);
                log.info("[RetryPolicy] 步骤 {} 第 {}/{} 次重试，等待 {}ms", stepId, attempt, maxRetries, backoffMs);
                sleepQuietly(backoffMs);
            }

            try {
                long start = System.currentTimeMillis();
                Object result = handler.execute(params);
                long duration = System.currentTimeMillis() - start;

                if (isRetry) {
                    log.info("[RetryPolicy] 步骤 {} 第 {} 次重试成功 (耗时{}ms)", stepId, attempt, duration);
                }

                StepResult stepResult = StepResult.success(stepId, result, duration);
                stepResult.setRetried(isRetry);
                stepResult.setRetryCount(attempt);
                return stepResult;

            } catch (Exception e) {
                lastException = e;
                log.warn("[RetryPolicy] 步骤 {} 执行失败 (attempt={}/{}): {}",
                        stepId, attempt, maxRetries, e.getMessage());

                if (attempt >= maxRetries) {
                    log.error("[RetryPolicy] 步骤 {} 已达最大重试次数({})，放弃重试", stepId, maxRetries);
                }
            }
        }

        // 所有重试均失败
        String errorMsg = "重试 " + maxRetries + " 次后仍失败: " + lastException.getMessage();
        StepResult failResult = StepResult.failed(stepId, errorMsg, 0);
        failResult.setRetried(true);
        failResult.setRetryCount(maxRetries);
        return failResult;
    }

    /**
     * 计算指数退避等待时间.
     *
     * @param attempt 当前重试次数 (1-based)
     * @return 等待毫秒数
     */
    public static long calculateBackoff(int attempt) {
        long seconds = (long) Math.pow(2, attempt - 1);  // 1, 2, 4, 8, 16, 32, ...
        return Math.min(seconds, MAX_BACKOFF_SECONDS) * 1000;
    }

    /** 线程安全地睡眠指定毫秒 */
    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[RetryPolicy] 重试等待被中断");
        }
    }
}
