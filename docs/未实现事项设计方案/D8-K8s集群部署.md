# D8 K8s 集群部署实现方案

> **类别**: D | **优先级**: P3  
> **依赖**: D7 镜像

## 1. 场景

无状态 App 多副本；有状态组件用云 RDS/Redis 或 Operator。

## 2. 设计要点

- Deployment + HPA（CPU/自定义 LLM 队列）。  
- Probe：`/actuator/health/liveness` `/readiness`（需拆分：readiness 含 DB）。  
- Secret 引用。  
- Ingress SSE：**关缓冲、长超时**。  
- 不在 K8s 内自建 Milvus 一期，可用托管或独立 namespace Helm。

## 3. 清单

`deploy/k8s/deployment.yaml` `service.yaml` `ingress.yaml` `hpa.yaml` `configmap.yaml`。

## 4. 验收

两副本滚动更新不断 SSE（允许短暂错误）。

## 5. 风险

会话在 Redis 才能水平扩展（Sa-Token Redis 已具备）。
