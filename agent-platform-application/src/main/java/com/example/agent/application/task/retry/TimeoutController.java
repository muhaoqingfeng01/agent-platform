package com.example.agent.application.task.retry;

import com.example.agent.application.task.handler.ActionHandler;
import com.example.agent.domain.task.valueobject.StepResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 超时控制工具 — Command 模式.
 *
 * <p>在独立线程中执行 Handler，超时后强制中断并返回 TIMEOUT 结果。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
public final class TimeoutController {

    private TimeoutController() { /* 工具类禁止实例化 */ }

    /**
     * 带超时控制的执行.
     *
     * @param handler  动作处理器
     * @param params   执行参数
     * @param stepId   步骤 ID
     * @return 执行结果（可能为 TIMEOUT）
     */
    public static StepResult executeWithTimeout(ActionHandler handler, Map<String, Object> params, String stepId) {
        long timeoutMs = handler.timeoutMs();
        ExecutorService timeoutExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "timeout-" + stepId);
            t.setDaemon(true);
            return t;
        });

        Future<Object> future = timeoutExecutor.submit(() -> handler.execute(params));

        try {
            long start = System.currentTimeMillis();
            Object result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            long duration = System.currentTimeMillis() - start;
            return StepResult.success(stepId, result, duration);
        } catch (TimeoutException e) {
            future.cancel(true);
            log.warn("[TimeoutCtrl] 步骤 {} 执行超时 ({}ms)", stepId, timeoutMs);
            return StepResult.timeout(stepId, timeoutMs);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("[TimeoutCtrl] 步骤 {} 执行异常: {}", stepId, cause.getMessage());
            return StepResult.failed(stepId, cause.getMessage(), 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return StepResult.failed(stepId, "执行被中断", 0);
        } finally {
            timeoutExecutor.shutdownNow();
        }
    }
}
