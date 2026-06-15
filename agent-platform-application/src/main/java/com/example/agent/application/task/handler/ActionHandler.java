package com.example.agent.application.task.handler;

import java.util.Map;

/**
 * 动作处理器接口 — Strategy 模式.
 *
 * <p>每个实现类对应一种 LLM 可规划的动作类型（如 retrieve_orders、send_email）。
 * <p>实现类标注 {@code @Component} 即可由 {@link ActionHandlerRegistry} 自动发现注册。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface ActionHandler {

    /** 动作类型标识（如 "retrieve_orders"、"send_email"） */
    String getActionType();

    /** 执行动作 */
    Object execute(Map<String, Object> params) throws Exception;

    /** 参数校验（执行前调用） */
    void validateParams(Map<String, Object> params);

    /** 是否支持并行执行（默认 true） */
    default boolean supportsParallel() {
        return true;
    }

    /** 最大重试次数（默认 3） */
    default int maxRetries() {
        return 3;
    }

    /** 超时时间毫秒（默认 30s） */
    default long timeoutMs() {
        return 30_000;
    }

    /** 是否高风险操作（高风险需审批确认，默认 false） */
    default boolean isHighRisk() {
        return false;
    }

    /** Handler 描述（用于 LLM 规划提示词） */
    default String getDescription() {
        return getActionType();
    }

    /** 参数 Schema 描述（用于 LLM 规划提示词，JSON Schema 格式） */
    default String getParamsSchema() {
        return "{}";
    }
}
