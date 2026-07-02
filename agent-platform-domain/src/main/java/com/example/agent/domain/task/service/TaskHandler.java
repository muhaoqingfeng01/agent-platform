package com.example.agent.domain.task.service;

import com.example.agent.domain.task.valueobject.TaskResult;

/**
 * 通用任务处理器接口 — 任务中心的核心抽象.
 * <p>
 * 所有需要接入任务中心的业务都必须实现此接口。
 * 新业务接入只需 3 步：
 * <ol>
 *   <li>定义 Payload（业务参数 POJO）</li>
 *   <li>实现 TaskHandler&lt;T&gt;</li>
 *   <li>添加 @Component 自动注册</li>
 * </ol>
 *
 * @param <T> 业务参数类型（Payload），用于 JSON 反序列化
 * @author Agent Platform Team
 * @since 1.6.0
 */
public interface TaskHandler<T> {

    /** 任务类型标识（对应 t_async_task.task_type） */
    String getTaskType();

    /**
     * 执行任务的核心逻辑.
     * <p>
     * 由任务中心在工作线程中调用。实现者应在关键步骤边界检查 deadlineMs，
     * 若超过则抛出 TaskTimeoutException 主动退出.
     *
     * @param payload    业务参数
     * @param deadlineMs 截止时间毫秒值（System.currentTimeMillis() + timeoutSeconds*1000）
     * @return 执行结果
     */
    TaskResult execute(T payload, Long deadlineMs) throws Exception;

    /** 超时回调：扫描器发现任务超时后调用（如更新业务实体状态为失败） */
    void onTimeout(T payload);

    /** 成功回调：任务执行成功后调用 */
    void onSuccess(T payload, TaskResult result);

    /** 失败回调：任务执行异常后调用 */
    void onFailure(T payload, Throwable error);

    /** 超时时间（秒），每个 Handler 可自定义，默认 120 秒 */
    default int getTimeoutSeconds() { return 120; }

    /** Payload 类型，用于 JSON 反序列化 */
    Class<T> getPayloadType();
}
