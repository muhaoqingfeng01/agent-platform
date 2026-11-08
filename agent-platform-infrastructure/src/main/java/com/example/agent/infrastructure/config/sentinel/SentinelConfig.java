package com.example.agent.infrastructure.config.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 限流熔断 — 硬编码 Fallback 规则.
 *
 * <p><b>正常模式（推荐）：</b>
 * 当 {@code spring.cloud.sentinel.datasource.ds-flow.nacos.*} 配置正确且 Nacos 可达时，
 * Sentinel 自动通过 {@code NacosDataSource} 从 Nacos 拉取规则并注册到
 * {@link FlowRuleManager} / {@link DegradeRuleManager}。
 * 此时本类不加载任何规则，所有规则由 Nacos 统一管理。
 *
 * <p><b>Fallback 模式：</b>
 * 当 Nacos 不可达或 DataSource 配置缺失时，Sentinel 规则管理器为空。
 * 设置 {@code app.sentinel.fallback.enabled=true} 启用硬编码兜底规则，
 * 确保应用在 Nacos 故障时仍有基础限流熔断保护。
 *
 * <p><b>生效优先级：</b>
 * <pre>
 * Nacos 规则（最高优先级）
 *   ↓ Nacos 不可达时
 * 硬编码 Fallback 规则（需显式启用 app.sentinel.fallback.enabled=true）
 *   ↓ 未启用 fallback 时
 * 无限流（应用正常运行但无保护）
 * </pre>
 *
 * <p><b>规则同步说明：</b>
 * Sentinel Dashboard 修改规则 → 推送至 Nacos → 所有实例自动热更新 → 持久化存储。
 * 应用重启后自动从 Nacos 拉取上次保存的规则，不再丢失。
 *
 * <p>Nacos 配置位置：
 * <ul>
 *   <li>DataId: {@code agent-platform-flow-rules.json} — Group: {@code SENTINEL_GROUP}</li>
 *   <li>DataId: {@code agent-platform-degrade-rules.json} — Group: {@code SENTINEL_GROUP}</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.sentinel.fallback.enabled", havingValue = "true")
public class SentinelConfig {

    // ========== Fallback 硬编码默认值（仅 Nacos 不可用时生效） ==========

    /** 全局聊天接口 QPS（fallback） */
    private static final double FALLBACK_GLOBAL_QPS = 200;
    /** 租户级聊天接口 QPS（fallback） */
    private static final double FALLBACK_TENANT_QPS = 50;
    /** 慢调用比例熔断阈值（fallback） */
    private static final double FALLBACK_SLOW_RATIO = 0.5;
    /** 异常比例熔断阈值（fallback） */
    private static final double FALLBACK_ERROR_RATIO = 0.5;
    /** 熔断恢复窗口 秒（fallback） */
    private static final int FALLBACK_TIME_WINDOW = 30;
    /** 最小请求数（fallback） */
    private static final int FALLBACK_MIN_REQUEST_AMOUNT = 10;
    /** 统计间隔 ms（fallback） */
    private static final int FALLBACK_STAT_INTERVAL_MS = 1000;
    /** 慢调用 RT 阈值 秒（fallback，200ms = 0.2s） */
    private static final double FALLBACK_SLOW_RT_THRESHOLD = 0.2;

    /** 流式聊天资源名（与 @RateLimit resource 对齐） */
    private static final String RESOURCE_CHAT_STREAM = "chat:stream";
    /** 非流式发送资源名 */
    private static final String RESOURCE_CHAT_SEND = "chat:send";
    /** 登录资源名 */
    private static final String RESOURCE_AUTH_LOGIN = "auth:login";
    /** 知识检索资源名 */
    private static final String RESOURCE_KB_SEARCH = "knowledge:search";
    /** LLM 调用资源名（熔断目标） */
    private static final String RESOURCE_LLM = "com.example.agent.llm.call";

    /**
     * 仅在 Nacos 数据源不可用时加载硬编码兜底规则.
     *
     * <p>加载前检查规则管理器是否已有规则（来自 Nacos DataSource），
     * 已有规则则跳过，避免覆盖 Nacos 动态规则。</p>
     */
    @PostConstruct
    public void initFallbackRules() {
        if (!FlowRuleManager.getRules().isEmpty() || !DegradeRuleManager.getRules().isEmpty()) {
            log.info("[Sentinel] 规则已由 Nacos DataSource 加载，跳过 Fallback 硬编码规则");
            return;
        }

        log.warn("[Sentinel] ⚠️ Nacos 规则未加载，启用硬编码 Fallback 规则（app.sentinel.fallback.enabled=true）");
        loadFlowRules();
        loadDegradeRules();
        log.info("[Sentinel] Fallback 硬编码规则加载完成");
    }

    /**
     * 加载限流规则（Fallback）
     */
    private void loadFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        rules.add(flowRule(RESOURCE_CHAT_STREAM, FALLBACK_GLOBAL_QPS));
        rules.add(flowRule(RESOURCE_CHAT_SEND, FALLBACK_TENANT_QPS));
        rules.add(flowRule(RESOURCE_AUTH_LOGIN, 20));
        rules.add(flowRule(RESOURCE_KB_SEARCH, 40));
        log.info("[Sentinel-Fallback] 限流规则: chat:stream={}, chat:send={}, auth:login=20, knowledge:search=40",
                FALLBACK_GLOBAL_QPS, FALLBACK_TENANT_QPS);

        FlowRuleManager.loadRules(rules);
    }

    private static FlowRule flowRule(String resource, double qps) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(qps);
        rule.setLimitApp("default");
        return rule;
    }

    /**
     * 加载熔断降级规则（Fallback）
     */
    private void loadDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // — LLM 调用慢调用比例熔断 —
        DegradeRule slowCallRule = new DegradeRule(RESOURCE_LLM)
                .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                .setCount(FALLBACK_SLOW_RATIO)
                .setTimeWindow(FALLBACK_TIME_WINDOW)
                .setMinRequestAmount(FALLBACK_MIN_REQUEST_AMOUNT)
                .setSlowRatioThreshold(FALLBACK_SLOW_RT_THRESHOLD)
                .setStatIntervalMs(FALLBACK_STAT_INTERVAL_MS);
        rules.add(slowCallRule);
        log.info("[Sentinel-Fallback] 熔断规则: LLM慢调用比例 > {}% → 熔断 {} 秒",
                (int) (FALLBACK_SLOW_RATIO * 100), slowCallRule.getTimeWindow());

        // — LLM 调用异常比例熔断 —
        DegradeRule errorRule = new DegradeRule(RESOURCE_LLM)
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(FALLBACK_ERROR_RATIO)
                .setTimeWindow(FALLBACK_TIME_WINDOW)
                .setMinRequestAmount(FALLBACK_MIN_REQUEST_AMOUNT)
                .setStatIntervalMs(FALLBACK_STAT_INTERVAL_MS);
        rules.add(errorRule);
        log.info("[Sentinel-Fallback] 熔断规则: LLM异常比例 > {}% → 熔断 {} 秒",
                (int) (FALLBACK_ERROR_RATIO * 100), errorRule.getTimeWindow());

        DegradeRuleManager.loadRules(rules);
    }
}
