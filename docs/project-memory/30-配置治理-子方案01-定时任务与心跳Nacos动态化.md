# 配置治理 — 子方案 01：定时任务 & 心跳参数 Nacos 动态化

> **日期**: 2026-07-14
> **分支**: nacos/nacos-config
> **触发**: 代码生成（子方案01实施）

---

## 做了什么

将项目中全部 6 处 `@Scheduled` 定时任务从硬编码 `static final` 常量 + `@Scheduled` 注解，改造为 Nacos 动态配置驱动。

### 新建文件（2 个）

- `infrastructure/config/nacos/SchedulerConfig.java` — 继承 `NacosConfig` 模板，管理 9 个调度参数，DataId=`agent-platform-scheduler.json`
- `infrastructure/config/scheduler/DynamicScheduledTaskManager.java` — 动态调度管理器，使用 `ThreadPoolTaskScheduler` + `LongSupplier` 驱动，每次触发前从 `SchedulerConfig` 读取最新间隔

### 改造文件（5 个）

| 文件 | 改动 |
|------|------|
| `TaskTimeoutScanner.java` | 移除 4 个 `static final` + 2 个 `@Scheduled` → `@PostConstruct` 注册 2 个动态任务 |
| `ApprovalTimeoutJob.java` | 移除 `@Scheduled(fixedDelay=30000)` → `@PostConstruct` 动态注册 |
| `McpHeartbeatDetector.java` | 移除 2 个 `static final` + `@Scheduled`，`MAX_FAILURES` → `schedulerConfig.getMcpMaxFailures()` |
| `McpClientManager.java` | 移除 `@Scheduled(fixedDelay=300000)`，在 `afterPropertiesSet()` 中注册动态刷新 |
| `SensitiveWordFilter.java` | 移除 `@Scheduled(fixedDelay=300000)`，在 `@PostConstruct` 中注册动态刷新 |

### 配置文件（1 个）

- `application.yml` — 新增 `app.scheduler.*` 兜底配置（9 项默认值，Nacos 不可用时使用）

---

## 关键决策

1. **`DynamicScheduledTaskManager` 放 infrastructure 层** — application 模块编译期依赖 infrastructure（`pom.xml:47`），无循环依赖风险
2. **使用 `ThreadPoolTaskScheduler` + `Trigger` 动态计算** — 因为 `@Scheduled` 的 `fixedDelay` 在容器启动时一次性解析，之后不可变；动态 `Trigger` 每次触发前读取 `LongSupplier` 获取最新间隔
3. **兼容 Spring 6.x 的 `TriggerContext` API** — 使用 `Object` 类型接收 `lastScheduledExecutionTime()` 返回值，避免 `Date` vs `Instant` 版本不匹配
4. **`McpClientManager` 的特殊处理** — `@Scheduled` 移除后，在 `afterPropertiesSet()` 中注册动态任务（而非 `@PostConstruct`），因为 `InitializingBean` 语义更清晰

---

## 踩坑记录

1. **Spring `Trigger.nextExecution()` 返回 `Instant`（非 `Date`）** — 初次实现使用 `new Date(epoch)` 导致编译错误，修正为 `Instant.ofEpochMilli()`
2. **`application.yml` 中 `app:` 重复 key** — 新增 `app.scheduler` 时未合并到已有 `app.logging`，导致 YAML 解析异常，修复为同一 `app:` 下嵌套两个子节点

---

## 下一步

- 子方案 02：RAG 检索参数 Nacos 动态化（24 项）
- 子方案 03：AI 模型 + 安全 + 会话参数 Nacos 动态化（17 项）
- 子方案 04：静态常量类统一管理（12 项）
- 子方案 05：Sentinel 规则 Nacos 持久化（6 项）
