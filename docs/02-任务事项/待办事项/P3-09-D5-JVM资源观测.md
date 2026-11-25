# D5 JVM 资源观测实现方案

> **类别**: D | **优先级**: P3  
> **现有**: Actuator metrics 含 JVM；无 Grafana JVM 看板

## 1. 场景

看堆、GC、线程、Hikari 连接池，排查 Full GC 导致 LLM 超时。

## 2. 设计要点

使用官方 JVM dashboard 或 Micrometer JVM 面板，datasource 同 D1。关注 `jvm.memory.used`、`jvm.gc.pause`、`hikaricp.connections.active`、Tomcat 线程。

## 3. 实现步骤

导入 Grafana ID 常见 JVM 模板，job 匹配 `agent-platform`。文档注明容器内存与 `-Xmx` 关系。

## 4. 验收

制造堆占用可见曲线。

## 5. 风险

无。
