package com.example.agent.common.exception;

/**
 * 任务执行超时异常 — 由 TaskHandler 在 deadline 检查时抛出.
 * <p>
 * 任务中心捕获此异常后不调用 onFailure，而是交由超时扫描器处理.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
public class TaskTimeoutException extends RuntimeException {

    public TaskTimeoutException(String message) {
        super(message);
    }

    public TaskTimeoutException(String taskId, long deadlineMs, long currentMs) {
        super(String.format("任务 %s 执行超时: deadline=%d, current=%d, overtime=%dms",
                taskId, deadlineMs, currentMs, currentMs - deadlineMs));
    }
}
