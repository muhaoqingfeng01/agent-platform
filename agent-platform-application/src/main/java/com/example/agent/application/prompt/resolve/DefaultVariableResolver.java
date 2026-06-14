package com.example.agent.application.prompt.resolve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 默认变量解析器 — 从 context Map 中直接取值.
 * <p>
 * 作为解析器链的末尾，处理所有未被系统/上下文解析器处理的变量。
 * 直接从用户传入的 context Map 中按名称查找。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class DefaultVariableResolver implements VariableResolver {

    @Override
    public String resolve(String variableName, Map<String, Object> context) {
        Object value = context.get(variableName);
        if (value == null) {
            return null;
        }
        return formatValue(value);
    }

    @Override
    public boolean supports(String variableName) {
        return true; // 兜底解析器，处理所有变量
    }

    @Override
    public int priority() {
        return 100; // 最低优先级
    }

    private String formatValue(Object value) {
        if (value == null) return "";
        if (value instanceof java.util.List<?> list) {
            return list.stream().map(Object::toString)
                    .collect(java.util.stream.Collectors.joining("\n"));
        }
        if (value instanceof java.util.Map<?, ?> map) {
            // 简单 JSON 化
            return map.toString();
        }
        return value.toString();
    }
}
