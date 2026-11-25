# Prometheus 指标与 Grafana 监控

> **状态**: ✅ 已实现 | **日期**: 2026-06-18 | **所属**: P4-T9

---

## 1. 设计目标

建立三个层级的可观测性：

| 层级 | 工具 | 内容 |
|------|------|------|
| **基础设施层** | Spring Boot Actuator | JVM 内存、GC、线程池、DB 连接池 |
| **业务指标层** | Micrometer → Prometheus | 对话/LLM/Token/工具/RAG/安全 10 个自定义指标 |
| **LLM 调用层** | Langfuse Trace | 每次 LLM 调用的 prompt/completion/token/duration |

---

## 2. 指标体系设计

### 2.1 指标分类

```
                        Agent Platform 业务指标
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
    请求/对话域             LLM 调用域            资源/安全域
          │                    │                    │
  ┌───────┴────────┐   ┌──────┴───────┐   ┌───────┴────────┐
  │ conversations  │   │ llm.call     │   │ tool.invocations│
  │ (Counter)      │   │ (Timer)      │   │ (Counter)       │
  │                │   │              │   │                 │
  │ t_message.      │   │ llm.errors   │   │ tool.failures   │
  │ processing     │   │ (Counter)    │   │ (Counter)       │
  │ (Timer)        │   │              │   │                 │
  │                │   │ tokens.      │   │ rag.retrieval   │
  │                │   │ consumed     │   │ (Timer)         │
  │                │   │ (Counter)    │   │                 │
  │                │   │              │   │ rag.hits        │
  │                │   │              │   │ (Histogram)     │
  │                │   │              │   │                 │
  │                │   │              │   │ security.blocks │
  │                │   │              │   │ (Counter)       │
  └────────────────┘   └──────────────┘   └─────────────────┘
```

### 2.2 指标选择原则

| 指标类型 | 何时使用 | 示例 |
|----------|---------|------|
| **Counter** | 只增不减的累计值 | 对话数、Token消耗、错误数、拦截数 |
| **Timer** | 耗时分布 + 自动记录调用次数 | LLM调用延迟、消息处理延迟 |
| **DistributionSummary** | 任意数值的分布（非耗时） | RAG检索命中数 |

### 2.3 publishPercentiles 选择

| 百分位 | 含义 | 用途 |
|:--:|------|------|
| P50 | 中位数 | 典型用户体验 |
| P95 | 95% 用户的体验 | SLA 基准线 |
| P99 | 99% 用户的体验 | 长尾优化目标 |

> ⚠️ publishPercentiles 会产生额外的内存开销（每个 percentiles 维护一个直方图桶），仅在 Timer 和 DistributionSummary 上启用，Counter 不需要。

---

## 3. 数据流架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     Agent Platform (JVM)                        │
│                                                                 │
│  ┌───────────────────┐    ┌───────────────────┐                 │
│  │ StreamOrchestration│    │ ToolApplication   │                 │
│  │ Service            │    │ Service           │                 │
│  │                    │    │                   │                 │
│  │ Timer.Sample.start │    │ metrics.record    │                 │
│  │   → LLM call       │    │   ToolInvocation  │                 │
│  │   → .stop(timer)   │    │   (...)           │                 │
│  └────────┬───────────┘    └────────┬──────────┘                 │
│           │                         │                            │
│           ▼                         ▼                            │
│  ┌───────────────────────────────────────────────────┐          │
│  │        AgentMetrics (Micrometer Registry)         │          │
│  │                                                   │          │
│  │  agent.conversations      (Counter)               │          │
│  │  agent.t_message.processing (Timer)  [P50,P95,P99]│          │
│  │  agent.tokens.consumed    (Counter)               │          │
│  │  agent.llm.call           (Timer)   [P50,P95,P99] │          │
│  │  agent.llm.errors         (Counter)               │          │
│  │  agent.tool.invocations   (Counter)               │          │
│  │  agent.tool.failures      (Counter)               │          │
│  │  agent.rag.retrieval      (Timer)   [P50,P95]     │          │
│  │  agent.rag.hits           (Histogram) [P50,P95]   │          │
│  │  agent.security.blocks    (Counter)               │          │
│  └───────────────────────┬───────────────────────────┘          │
│                          │                                       │
└──────────────────────────┼───────────────────────────────────────┘
                           │ HTTP GET /actuator/prometheus
                           ▼
              ┌─────────────────────────┐
              │  Prometheus Server       │
              │  (定期拉取，15s 间隔)     │
              └────────────┬────────────┘
                           │
                           ▼
              ┌─────────────────────────┐
              │  Grafana Dashboard       │
              │  • 对话 QPS 折线图        │
              │  • LLM P99 热力图         │
              │  • Token 消耗速率         │
              │  • 安全拦截告警           │
              └─────────────────────────┘
```

---

## 4. 计时策略设计

### 4.1 为什么不用 @Timed 注解？

```
@Timed("agent.llm.call")           ← 注解方案
public void executePipeline() {
    chatClient.prompt().stream()    ← 异步 .subscribe()
        .doOnComplete(...);         ← 真正的"完成"在这里
}
// 问题：@Timed 在方法返回时停止计时，但 LLM 还在流式输出中

Timer.Sample sample = Timer.start();  ← 手动方案
chatClient.prompt().stream()
    .doOnComplete(() -> {
        sample.stop(timer);            ← 精确在 LLM 完成时停止
    })
    .doOnError(() -> {
        sample.stop(timer);            ← 错误时也停止
    });
```

**结论**：流式/异步调用使用 `Timer.Sample` 手动控制边界，同步方法可用 `@Timed`。

### 4.2 计时起止点

```
消息处理 Timer:
  ├── start: executeStreamPipeline() 入口
  ├── stop:  doOnComplete / doOnError
  └── 涵盖: 意图识别 + Prompt构建 + LLM流式输出 + 保存消息

LLM 调用 Timer:
  ├── start: .subscribe() 之前
  ├── stop:  doOnComplete / doOnError
  └── 涵盖: 纯 LLM 推理时间（不含前置处理）
```

---

## 5. Grafana 面板设计

### 5.1 Dashboard 布局建议

```
┌─────────────────────────────────────────────────────────────┐
│  Row 1: 核心业务概览                                         │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │
│  │ 对话 QPS      │ │ LLM P99(ms)  │ │ Token/小时   │        │
│  │ (Stat+Spark) │ │ (Stat+Trend) │ │ (Stat+Bar)   │        │
│  └──────────────┘ └──────────────┘ └──────────────┘        │
├─────────────────────────────────────────────────────────────┤
│  Row 2: LLM 调用详情                                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ LLM 调用延迟分布（P50/P95/P99 时间序列）               │   │
│  │ LLM 错误率（比率面板 + 告警阈值线）                     │   │
│  └──────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│  Row 3: 工具 & RAG                                           │
│  ┌─────────────────────┐ ┌─────────────────────────────┐    │
│  │ 工具调用/失败 占比    │ │ RAG 检索耗时 + 命中数分布    │    │
│  │ (Pie/Bar)           │ │ (Heatmap + Histogram)       │    │
│  └─────────────────────┘ └─────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  Row 4: 安全                                                 │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ 安全拦截速率（时间序列 + 告警规则）                     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 核心 PromQL

| 面板 | PromQL | 阈值建议 |
|------|--------|:--:|
| 对话 QPS | `rate(agent_conversations_total[5m])` | — |
| LLM P99 | `histogram_quantile(0.99, rate(agent_llm_call_seconds_bucket[5m]))` | >30s 告警 |
| LLM 错误率 | `rate(agent_llm_errors_total[5m]) / rate(agent_llm_call_seconds_count[5m])` | >5% 告警 |
| Token 速率 | `rate(agent_tokens_consumed_total[1h]) * 3600` | 用于成本预测 |
| 安全拦截 | `rate(agent_security_blocks_total[5m])` | >10/min 告警 |
| RAG P50 | `histogram_quantile(0.5, rate(agent_rag_hits_bucket[5m]))` | — |
| 消息处理 P95 | `histogram_quantile(0.95, rate(agent_t_message_processing_seconds_bucket[5m]))` | >60s 告警 |

---

## 6. 配置说明

### 6.1 Actuator 暴露策略

```
端点                    认证要求          原因
─────────────────────────────────────────────────
/actuator/health        白名单（无认证）    K8s liveness/readiness probe
/actuator/info          白名单（无认证）    版本信息
/actuator/prometheus    白名单（无认证）    Prometheus Server 拉取
/actuator/metrics       需认证             原始指标数据
/actuator/env           需认证             环境变量（敏感）
```

### 6.2 白名单配置

在 `SaTokenWebMvcConfig` 的路由规则中，`/actuator/prometheus` 已加入白名单（`.stop()` 放行），Prometheus Server 可直接拉取无需 Token。

---

## 7. 如何新增指标

```
1. AgentMetrics 中添加字段
   private final Counter newCounter;

2. 构造器中注册
   this.newCounter = Counter.builder("agent.xxx")
       .description("...")
       .register(meterRegistry);

3. 添加 API 方法（可选，封装标签逻辑）
   public void recordXxx(String tenantId, String label) {
       newCounter.increment();
   }

4. 业务代码中注入 AgentMetrics 并调用
   metrics.recordXxx(tenantId, "value");

5. Grafana 添加 PromQL 面板
```

新增指标无需重启 Prometheus 配置（Prometheus 自动发现新指标名），只需更新 Grafana 面板。
