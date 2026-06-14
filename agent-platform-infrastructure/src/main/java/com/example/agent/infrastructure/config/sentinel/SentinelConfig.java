package com.example.agent.infrastructure.config.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 限流熔断配置
 * <p>
 * 在应用启动时通过 {@link PostConstruct} 加载默认限流和熔断规则。
 * 生产环境建议通过 Sentinel Dashboard 动态管理规则。
 * <p>
 * 限流维度：
 * <ul>
 *   <li>全局聊天接口：200 QPS（控制 LLM 调用成本）</li>
 *   <li>租户级：50 QPS（防止单租户打爆系统）</li>
 * </ul>
 * <p>
 * 熔断策略：
 * <ul>
 *   <li>慢调用比例 > 50% → 熔断 30 秒</li>
 *   <li>异常比例 > 50% → 熔断 30 秒</li>
 * </ul>
 */
@Slf4j
@Configuration
public class SentinelConfig {

    /**
     * 应用启动时加载默认流控和熔断规则
     */
    @PostConstruct
    public void initRules() {
        log.info("[Sentinel] 开始加载默认限流熔断规则...");
        loadFlowRules();
        loadDegradeRules();
        log.info("[Sentinel] 默认限流熔断规则加载完成");
    }

    /**
     * 加载限流规则（Flow Rules）
     */
    private void loadFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // — 全局聊天接口限流（200 QPS）—
        FlowRule chatRule = new FlowRule();
        chatRule.setResource("com.example.agent.interfaces.rest.ConversationController.streamChat");
        chatRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        chatRule.setCount(200);
        chatRule.setLimitApp("default");
        rules.add(chatRule);
        log.info("[Sentinel] 限流规则: 全局聊天接口 → {} QPS", chatRule.getCount());

        // — 租户级限流（50 QPS，资源名通过 RateLimitAspect 动态拼接）—
        FlowRule tenantRule = new FlowRule();
        tenantRule.setResource("com.example.agent.interfaces.rest.ConversationController.sendMessage:tenant:*");
        tenantRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        tenantRule.setCount(50);
        tenantRule.setLimitApp("default");
        rules.add(tenantRule);
        log.info("[Sentinel] 限流规则: 租户级 → {} QPS", tenantRule.getCount());

        FlowRuleManager.loadRules(rules);
    }

    /**
     * 加载熔断降级规则（Degrade Rules）
     */
    private void loadDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // — LLM 调用慢调用比例熔断 —
        DegradeRule slowCallRule = new DegradeRule("com.example.agent.llm.call")
                .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                .setCount(0.5)                          // 慢调用比例阈值（50%）
                .setTimeWindow(30)                      // 熔断时长（秒）
                .setMinRequestAmount(10)                // 最小请求数（统计窗口内至少 10 个请求）
                .setSlowRatioThreshold(0.2)             // 慢调用临界值：响应时间 > 200ms
                .setStatIntervalMs(1000);               // 统计窗口 1 秒
        rules.add(slowCallRule);
        log.info("[Sentinel] 熔断规则: LLM调用慢调用比例 > 50% → 熔断 {} 秒", slowCallRule.getTimeWindow());

        // — LLM 调用异常比例熔断 —
        DegradeRule errorRule = new DegradeRule("com.example.agent.llm.call")
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(0.5)                          // 异常比例阈值（50%）
                .setTimeWindow(30)                      // 熔断时长（秒）
                .setMinRequestAmount(10)                // 最小请求数
                .setStatIntervalMs(1000);               // 统计窗口 1 秒
        rules.add(errorRule);
        log.info("[Sentinel] 熔断规则: LLM调用异常比例 > 50% → 熔断 {} 秒", errorRule.getTimeWindow());

        DegradeRuleManager.loadRules(rules);
    }
}
