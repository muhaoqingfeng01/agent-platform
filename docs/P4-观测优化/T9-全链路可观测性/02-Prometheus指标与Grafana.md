# Prometheus 指标与 Grafana 监控

## 所属阶段
**P4 观测优化 → T9 全链路可观测性**

## 使用技术
- Micrometer + Prometheus
- Spring Boot Actuator
- Grafana Dashboard

## 实现方案

### 1. 业务指标定义

```java
@Component
public class AgentMetrics {

    private final MeterRegistry meterRegistry;

    // 对话相关
    private final Counter conversationCounter;
    private final Timer messageProcessingTimer;
    private final Counter tokenConsumptionCounter;

    // LLM 调用
    private final Timer llmCallTimer;
    private final Counter llmErrorCounter;

    // 工具调用
    private final Counter toolInvocationCounter;
    private final Counter toolFailureCounter;

    // RAG 检索
    private final Timer ragRetrievalTimer;
    private final Histogram ragHitCount;

    // 安全
    private final Counter securityBlockCounter;

    public AgentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.conversationCounter = Counter.builder("agent.conversations")
                .description("对话创建总数")
                .tag("version", "1.0")
                .register(meterRegistry);

        this.tokenConsumptionCounter = Counter.builder("agent.tokens.consumed")
                .description("Token 消耗总数")
                .register(meterRegistry);

        this.messageProcessingTimer = Timer.builder("agent.t_message.processing")
                .description("消息处理耗时")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.llmCallTimer = Timer.builder("agent.llm.call")
                .description("LLM 调用耗时")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.llmErrorCounter = Counter.builder("agent.llm.errors")
                .description("LLM 调用错误数")
                .register(meterRegistry);

        this.toolInvocationCounter = Counter.builder("agent.tool.invocations")
                .description("工具调用总数")
                .register(meterRegistry);

        this.securityBlockCounter = Counter.builder("agent.security.blocks")
                .description("安全拦截次数")
                .register(meterRegistry);

        this.ragRetrievalTimer = Timer.builder("agent.rag.retrieval")
                .description("RAG 检索耗时")
                .register(meterRegistry);

        this.ragHitCount = Histogram.builder("agent.rag.hits")
                .description("RAG 检索命中数分布")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);
    }
}
```

### 2. 使用示例

```java
@Service
public class OrchestrationService {

    @Autowired
    private AgentMetrics metrics;

    public String processMessage(String conversationId, String t_message) {
        // 记录消息处理耗时
        return metrics.messageProcessingTimer.record(() -> {
            // ... 编排逻辑 ...
            // 记录 token 消耗
            metrics.tokenConsumptionCounter.increment(tokensUsed);
            return response;
        });
    }
}
```

### 3. Actuator 端点暴露

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics,env,loggers
  endpoint:
    health:
      show-details: when_authorized
      probes:
        enabled: true   # K8s liveness/readiness
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      tenant: ${TENANT_ID:none}
```

### 4. Grafana 仪表盘推荐面板

| 面板 | PromQL | 说明 |
|------|--------|------|
| 对话 QPS | `rate(agent_conversations_total[5m])` | 每秒创建对话数 |
| LLM 调用 P99 | `histogram_quantile(0.99, rate(agent_llm_call_seconds_bucket[5m]))` | LLM P99 延迟 |
| LLM 错误率 | `rate(agent_llm_errors_total[5m]) / rate(agent_llm_call_seconds_count[5m])` | LLM 调用错误占比 |
| Token 消耗 | `rate(agent_tokens_consumed_total[1h]) * 3600` | 每小时 Token 消耗预测 |
| 安全拦截 | `rate(agent_security_blocks_total[5m])` | 每分钟安全拦截次数 |
| RAG 命中数 | `histogram_quantile(0.5, rate(agent_rag_hits_bucket[5m]))` | 检索命中中位数 |
