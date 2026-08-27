# D2 OpenTelemetry 分布式追踪实现方案

> **类别**: D | **优先级**: P3  
> **现有**: 自研 TraceFilter MDC，非 OTel

## 1. 现状

单机日志可串 traceId，无法进 Jaeger/Tempo，跨线程/跨服务靠手工。

## 2. 场景

stream 线程池 + LLM HTTP + Milvus 的父子 span。

## 3. 设计要点

- 引入 Micrometer Tracing + OTel exporter，**与现有 MDC traceId 对齐**（W3C `traceparent`）。  
- 不强制删 TraceFilter：可读入 `traceparent` 生成 MDC。  
- 一期只导出 HTTP client + 自定义 LLM span。

## 4. 实现步骤

1. 依赖 `micrometer-tracing-bridge-otel`、`opentelemetry-exporter-otlp`。  
2. `management.tracing.sampling.probability`。  
3. `ChatClient` 调用包 span `llm.chat`。  
4. Collector → Tempo。  
5. Grafana 链 Tempo（依赖 D1）。

## 5. 验收

Jaeger/Tempo 见到一次 stream 的 span 树含 llm。

## 6. 风险

高采样打满存储，生产 1%–10%。
