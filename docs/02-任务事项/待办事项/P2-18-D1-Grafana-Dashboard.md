# D1 Grafana Dashboard 实现方案

> **类别**: D 运维 | **优先级**: P2  
> **现有**: `/actuator/prometheus`，`AgentMetrics` 前缀 `agent.*`

## 1. 现状

指标已暴露，无看板，无法看对话/LLM/RAG 黄金信号。

## 2. 场景

值班看 QPS、LLM 耗时、Token、安全阻断、RAG 延迟。

## 3. 设计要点

Prometheus 拉 `/actuator/prometheus`（已白名单）。Grafana 用文件配面板，纳入 Git：`deploy/grafana/dashboards/agent-platform.json`。

面板：conversations 计数、message processing P95、llm.call P95、tokens、tool 失败、rag.retrieval、security.blocks、JVM 另见 D5。

## 4. 实现步骤

1. Compose 加 prometheus.yml scrape `app:8080`。  
2. Grafana datasource Prometheus。  
3. 导入 JSON。  
4. 文档：匿名只读仅内网。

## 5. 验收

造几次对话后面板非空。

## 6. 风险

指标名改动会空图，面板用变量 job。
