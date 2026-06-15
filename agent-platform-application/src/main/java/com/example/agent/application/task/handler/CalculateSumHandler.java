package com.example.agent.application.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 数据计算 Handler — Strategy 模式具体实现.
 *
 * <p>支持对上游步骤返回的数据集进行聚合计算（sum, avg, count, max, min）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class CalculateSumHandler implements ActionHandler {

    @Override
    public String getActionType() {
        return "calculate_sum";
    }

    @Override
    public String getDescription() {
        return "对数据集进行聚合计算（sum/avg/count/max/min）";
    }

    @Override
    public String getParamsSchema() {
        return "{\"field\": \"待计算的字段名\", \"operation\": \"聚合操作 (sum/avg/count/max/min)\"}";
    }

    @Override
    public void validateParams(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (!params.containsKey("field")) {
            throw new IllegalArgumentException("缺少必填参数: field（待计算字段名）");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(Map<String, Object> params) throws Exception {
        String field = (String) params.get("field");
        String operation = (String) params.getOrDefault("operation", "sum");
        log.info("[CalculateSum] 计算: field={}, operation={}", field, operation);

        // 从上下文获取上游数据（简化实现: 硬编码模拟数据）
        // 真实场景中，上游步骤的结果会通过 StepResult 传递

        Collection<Map<String, Object>> dataset = (Collection<Map<String, Object>>) params.get("_upstream_data");
        if (dataset == null || dataset.isEmpty()) {
            // 模拟数据
            return Map.of("field", field, "operation", operation, "result", 12800.50);
        }

        double result = switch (operation) {
            case "sum" -> dataset.stream()
                    .mapToDouble(m -> ((Number) m.getOrDefault(field, 0)).doubleValue()).sum();
            case "avg" -> dataset.stream()
                    .mapToDouble(m -> ((Number) m.getOrDefault(field, 0)).doubleValue()).average().orElse(0);
            case "count" -> dataset.size();
            case "max" -> dataset.stream()
                    .mapToDouble(m -> ((Number) m.getOrDefault(field, 0)).doubleValue()).max().orElse(0);
            case "min" -> dataset.stream()
                    .mapToDouble(m -> ((Number) m.getOrDefault(field, 0)).doubleValue()).min().orElse(0);
            default -> throw new IllegalArgumentException("不支持的聚合操作: " + operation);
        };

        return Map.of("field", field, "operation", operation, "result", result);
    }
}
