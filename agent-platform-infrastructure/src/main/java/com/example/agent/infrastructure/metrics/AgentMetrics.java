package com.example.agent.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * Agent 平台业务指标 — 基于 Micrometer + Prometheus.
 *
 * <p>所有指标以 {@code agent.} 为前缀，通过 {@code /actuator/prometheus} 端点暴露。
 * <p>PromQL 查询示例见 {@code docs/P4-观测优化/T9-全链路可观测性/02-Prometheus指标与Grafana.md}
 *
 * <h3>指标分类</h3>
 * <ul>
 *   <li><b>Counter</b> — 只增不减的计数（对话数、Token 消耗、调用次数）</li>
 *   <li><b>Timer</b> — 耗时分布 + 调用次数（LLM 调用、消息处理、RAG 检索）</li>
 *   <li><b>Histogram</b> — 数值分布（RAG 检索命中数）</li>
 * </ul>
 *
 * <h3>标签规范</h3>
 * <ul>
 *   <li>{@code tenant} — 租户 ID（多租户隔离）</li>
 *   <li>{@code model} — LLM 模型名</li>
 *   <li>{@code intent} — 意图分类</li>
 *   <li>{@code tool_name} — 工具名称</li>
 *   <li>{@code filter_type} — 安全过滤器类型</li>
 *   <li>{@code status} — 操作结果（SUCCESS / FAILED）</li>
 *   <li>{@code error_type} — 错误分类</li>
 *   <li>{@code kb_name} — 知识库名称</li>
 * </ul>
 *
 * <p><b>注意：</b>Timer 的 {@code publishPercentiles} 会产生额外内存开销，
 * 高基数标签（如 conversationId）禁止在此注册，应通过日志系统查询。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
public class AgentMetrics {

    // ==================== 对话相关 ====================

    /** 对话创建总数（标签: tenant, status） */
    private final Counter conversationCounter;

    /** 消息处理耗时分布 P50/P95/P99（标签: tenant, intent） */
    private final Timer messageProcessingTimer;

    /** Token 消耗总数（标签: tenant, model） */
    private final Counter tokenConsumptionCounter;

    // ==================== LLM 调用 ====================

    /** LLM 调用耗时分布 P50/P95/P99（标签: tenant, model） */
    private final Timer llmCallTimer;

    /** LLM 调用错误数（标签: tenant, model, error_type） */
    private final Counter llmErrorCounter;

    // ==================== 工具调用 ====================

    /** 工具调用总数（标签: tenant, tool_name） */
    private final Counter toolInvocationCounter;

    /** 工具调用失败数（标签: tenant, tool_name） */
    private final Counter toolFailureCounter;

    // ==================== RAG 检索 ====================

    /** RAG 检索耗时（标签: tenant, kb_name） */
    private final Timer ragRetrievalTimer;

    /** RAG 检索命中数分布（标签: tenant, kb_name） */
    private final DistributionSummary ragHitSummary;

    // ==================== 安全 ====================

    /** 安全拦截次数（标签: tenant, filter_type） */
    private final Counter securityBlockCounter;

    // ==================== 构造器 ====================

    public AgentMetrics(MeterRegistry meterRegistry) {
        // --- 对话 ---
        this.conversationCounter = Counter.builder("agent.conversations")
                .description("对话创建总数")
                .register(meterRegistry);

        this.messageProcessingTimer = Timer.builder("agent.t_message.processing")
                .description("消息处理耗时（毫秒）")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.tokenConsumptionCounter = Counter.builder("agent.tokens.consumed")
                .description("Token 消耗总数")
                .register(meterRegistry);

        // --- LLM ---
        this.llmCallTimer = Timer.builder("agent.llm.call")
                .description("LLM 调用耗时（毫秒）")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.llmErrorCounter = Counter.builder("agent.llm.errors")
                .description("LLM 调用错误数")
                .register(meterRegistry);

        // --- 工具 ---
        this.toolInvocationCounter = Counter.builder("agent.tool.invocations")
                .description("工具调用总数")
                .register(meterRegistry);

        this.toolFailureCounter = Counter.builder("agent.tool.failures")
                .description("工具调用失败数")
                .register(meterRegistry);

        // --- RAG ---
        this.ragRetrievalTimer = Timer.builder("agent.rag.retrieval")
                .description("RAG 检索耗时（毫秒）")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);

        this.ragHitSummary = DistributionSummary.builder("agent.rag.hits")
                .description("RAG 检索命中数分布")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);

        // --- 安全 ---
        this.securityBlockCounter = Counter.builder("agent.security.blocks")
                .description("安全拦截次数")
                .register(meterRegistry);
    }

    // ==================== 对话 API ====================

    /** 记录一次对话创建 */
    public void recordConversation(Long tenantId, String status) {
        conversationCounter.increment();
    }

    /** 记录消息处理耗时 */
    public Timer getMessageProcessingTimer() {
        return messageProcessingTimer;
    }

    /** 记录 Token 消耗 */
    public void recordTokenConsumption(Long tenantId, String model, long tokens) {
        tokenConsumptionCounter.increment(tokens);
    }

    // ==================== LLM API ====================

    /** 获取 LLM 调用 Timer（用于 {@code llmCallTimer.record(callable)} 或手动 Sample） */
    public Timer getLlmCallTimer() {
        return llmCallTimer;
    }

    /** 记录 LLM 调用错误 */
    public void recordLlmError(Long tenantId, String model, String errorType) {
        llmErrorCounter.increment();
    }

    // ==================== 工具 API ====================

    /** 记录工具调用 */
    public void recordToolInvocation(Long tenantId, String toolName) {
        toolInvocationCounter.increment();
    }

    /** 记录工具调用失败 */
    public void recordToolFailure(Long tenantId, String toolName) {
        toolFailureCounter.increment();
    }

    // ==================== RAG API ====================

    /** 获取 RAG 检索 Timer */
    public Timer getRagRetrievalTimer() {
        return ragRetrievalTimer;
    }

    /** 记录 RAG 检索命中数 */
    public void recordRagHits(Long tenantId, String kbName, int hitCount) {
        ragHitSummary.record(hitCount);
    }

    // ==================== 安全 API ====================

    /** 记录安全拦截 */
    public void recordSecurityBlock(Long tenantId, String filterType) {
        securityBlockCounter.increment();
    }
}
