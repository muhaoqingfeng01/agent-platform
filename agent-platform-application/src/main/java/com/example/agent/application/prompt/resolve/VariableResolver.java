package com.example.agent.application.prompt.resolve;

import java.util.Map;

/**
 * 变量解析器接口 — Strategy + Chain of Responsibility 模式.
 * <p>
 * 不同的变量来源（系统变量、上下文变量、自定义变量）由不同的解析器处理。
 * 解析器链按顺序尝试解析，第一个能处理的解析器返回结果。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface VariableResolver {

    /**
     * 尝试解析变量值.
     *
     * @param variableName 变量名
     * @param context      上下文（用户提供的变量值集合）
     * @return 解析后的值，如果此解析器无法处理则返回 null
     */
    String resolve(String variableName, Map<String, Object> context);

    /**
     * 此解析器是否能处理指定变量.
     */
    default boolean supports(String variableName) {
        return true;
    }

    /**
     * 解析器优先级（越小越先执行）.
     */
    default int priority() {
        return 100;
    }
}
