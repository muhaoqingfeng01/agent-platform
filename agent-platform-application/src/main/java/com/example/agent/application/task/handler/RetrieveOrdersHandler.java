package com.example.agent.application.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 查询订单 Handler — Strategy 模式具体实现.
 *
 * <p>示例 Handler，实际项目中替换为真实订单服务调用.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class RetrieveOrdersHandler implements ActionHandler {

    @Override
    public String getActionType() {
        return "retrieve_orders";
    }

    @Override
    public String getDescription() {
        return "查询指定时间范围内的订单数据";
    }

    @Override
    public String getParamsSchema() {
        return "{\"period\": \"时间范围 (today/last_week/last_month/自定义)\", \"limit\": \"返回条数上限(可选)\"}";
    }

    @Override
    public void validateParams(Map<String, Object> params) {
        if (params == null || !params.containsKey("period")) {
            throw new IllegalArgumentException("缺少必填参数: period（如 today, last_week, last_month）");
        }
    }

    @Override
    public Object execute(Map<String, Object> params) throws Exception {
        String period = (String) params.get("period");
        int limit = params.containsKey("limit") ? ((Number) params.get("limit")).intValue() : 100;
        log.info("[RetrieveOrders] 查询订单: period={}, limit={}", period, limit);

        // TODO: 实际调用订单服务 API
        // return orderServiceClient.queryOrders(period, limit);

        // 模拟返回数据
        return Map.of(
                "period", period,
                "count", 42,
                "total_amount", 12800.50,
                "orders", java.util.List.of(
                        Map.of("id", "ORD-001", "amount", 3200.00, "date", "2026-06-10"),
                        Map.of("id", "ORD-002", "amount", 5600.50, "date", "2026-06-11"),
                        Map.of("id", "ORD-003", "amount", 4000.00, "date", "2026-06-12")
                )
        );
    }
}
