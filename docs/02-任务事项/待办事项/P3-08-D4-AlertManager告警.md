# D4 AlertManager 告警实现方案

> **类别**: D | **优先级**: P3  
> **现有**: T9 文档有阈值示例，无 Alertmanager

## 1. 场景

LLM 错误率、接口 P95、磁盘、心跳失败 MCP 自动 DISABLED 后通知人。

## 2. 设计要点

Prometheus rule 文件进 Git。通知：企业微信 Webhook 即可，不自建邮件。

告警：`agent_llm_errors` 5m 突增；`chat.stream` 429 过多；进程 down；Milvus scrape fail。

## 3. 实现步骤

1. `deploy/prometheus/alerts.yml`。  
2. Alertmanager 路由。  
3. 抑制与值班时间。  
4. 与 MCP 心跳：应用已 DISABLED，告警基于日志 metrics 或自定义 gauge `mcp.disabled`。

## 4. 验收

故意停 Redis 后 5 分钟内收到通知（health 失败）。

## 5. 风险

告警风暴，必须分组。
