# 配置治理 — 子方案 01：定时任务 & 心跳参数 Nacos 动态化

> **版本**: V1.0.0
> **日期**: 2026-07-14
> **批次**: 第一批（P0，ROI 最高）
> **父方案**: [配置治理-总体设计方案](配置治理-总体设计方案.md)
> **涉及项数**: 9 项 / 5 个文件

---

## 一、范围与目标

将项目中所有 `@Scheduled` 定时任务的间隔周期和静态阈值常量，从 Java `static final` 硬编码迁移到 Nacos 动态配置，实现**免重启调优**。

### 1.1 涉及配置项

| # | 配置项 | 当前文件 | 当前值 | 类型 |
|:--:|--------|----------|:------:|:----:|
| 1 | 超时任务扫描间隔 | `TaskTimeoutScanner.java:36` | 15s | `@Scheduled` 周期 |
| 2 | 僵尸任务扫描间隔 | `TaskTimeoutScanner.java:38` | 60s | `@Scheduled` 周期 |
| 3 | 僵尸任务判定阈值 | `TaskTimeoutScanner.java:40` | 5min | 业务阈值 |
| 4 | 批处理大小 | `TaskTimeoutScanner.java:34` | 100 | 业务阈值 |
| 5 | 审批超时扫描间隔 | `ApprovalTimeoutJob.java:38` | 30s | `@Scheduled` 周期 |
| 6 | MCP 心跳间隔 | `McpHeartbeatDetector.java:49` | 30s | `@Scheduled` 周期 |
| 7 | MCP 最大失败次数 | `McpHeartbeatDetector.java:48` | 3 | 业务阈值 |
| 8 | MCP Client 刷新间隔 | `McpClientManager.java:72` | 5min | `@Scheduled` 周期 |
| 9 | 敏感词缓存刷新间隔 | `SensitiveWordFilter.java:56` | 5min | `@Scheduled` 周期 |

### 1.2 不改动的配置

| 配置项 | 原因 |
|--------|------|
| 任务超时扫描的 CAS 更新逻辑 | 业务逻辑，非配置 |
| 审批超时处理流程 | 同上 |
| 敏感词匹配算法（Aho-Corasick） | 算法选择，非配置 |

---

## 二、当前状态分析

### 2.1 硬编码模式

当前所有定时任务都使用 `@Scheduled(fixedDelay = CONSTANT)` 硬编码，存在两个问题：

1. **变更需重启**：调整扫描间隔必须改代码 → 编译 → 部署
2. **`@Scheduled` 不支持动态修改**：Spring 的 `@Scheduled` 注解值在容器启动时解析，无法运行时更改

### 2.2 涉及文件现状

```
agent-platform-application/
├── task/TaskTimeoutScanner.java          ← 4 个 static final 常量 + 2 个 @Scheduled
├── approval/ApprovalTimeoutJob.java      ← 1 个 @Scheduled(硬编码 30_000)
├── security/filter/SensitiveWordFilter.java ← 1 个 @Scheduled(硬编码 300_000)
agent-platform-infrastructure/
├── mcp/McpHeartbeatDetector.java         ← 2 个 static final 常量 + 1 个 @Scheduled
├── mcp/McpClientManager.java             ← 1 个 @Scheduled(硬编码 300_000)
```

---

## 三、技术方案设计

### 3.1 核心挑战：`@Scheduled` 无法动态修改

Spring `@Scheduled` 的 `fixedDelay` / `fixedRate` 在容器初始化时一次性解析，之后不可变。

**方案选型**：

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|:----:|
| A. 自定义 `TaskScheduler` + 动态 `Trigger` | 标准 Spring 方式 | "下次触发时间"动态计算复杂 | ✅ **选用** |
| B. `ScheduledTaskRegistrar` 动态注册/取消 | API 清晰 | 需管理任务生命周期 | ❌ 过度设计 |
| C. 保持 `@Scheduled` + 内部判断开关 | 最简单 | 间隔仍然固定，仅能做开关 | ❌ 治标不治本 |

### 3.2 选定方案：`DynamicScheduledTaskManager` + `Trigger` 动态计算

**核心思路**：

```java
// 伪代码 — 仅示意设计思路
@Component
public class DynamicScheduledTaskManager {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    private final SchedulerConfig schedulerConfig; // 从 Nacos 拉取配置

    // 注册一个动态定时任务
    public void registerTask(String taskName, Runnable task, String configKey) {
        ScheduledFuture<?> future = taskScheduler.schedule(task, triggerContext -> {
            // 每次执行完，从 schedulerConfig 读取下一次的间隔
            long intervalMs = schedulerConfig.getInterval(configKey);
            return new Date(System.currentTimeMillis() + intervalMs);
        });
        scheduledTasks.put(taskName, future);
    }
}
```

**设计要点**：

| 要点 | 说明 |
|------|------|
| 使用 `ThreadPoolTaskScheduler` | Spring 内置，无需额外依赖 |
| Trigger 每次动态计算 nextExecutionTime | 运行时读取最新 Nacos 配置值 |
| 保留 `@PostConstruct` 初始化注册 | 首次用 Nacos 值，Nacos 不可用时用 YAML fallback |
| 每个任务独立 `ScheduledFuture` | 可单独取消/重启 |
| 配置变更无需重启任务 | Trigger 下次触发时自然读取新值 |

### 3.3 新增 Nacos 配置类

#### SchedulerConfig.java（新建）

```
路径: infrastructure/config/nacos/SchedulerConfig.java
继承: NacosConfig<SchedulerConfig.SchedulerProps>
```

#### Nacos DataId: `agent-platform-scheduler.json`

```jsonc
{
  "timeoutScanIntervalMs": 15000,
  "staleScanIntervalMs": 60000,
  "staleSubmittedMinutes": 5,
  "timeoutScanBatchSize": 100,
  "approvalTimeoutScanMs": 30000,
  "mcpHeartbeatIntervalMs": 30000,
  "mcpMaxFailures": 3,
  "mcpClientRefreshMs": 300000,
  "sensitiveWordRefreshMs": 300000
}
```

### 3.4 文件改动清单

| 文件 | 改动类型 | 改动内容 |
|------|:------:|----------|
| **新建** `infrastructure/config/nacos/SchedulerConfig.java` | 新增 | 继承 `NacosConfig`，定义 9 个字段 |
| **新建** `infrastructure/config/scheduler/DynamicScheduledTaskManager.java` | 新增 | 动态调度管理器，封装 `ThreadPoolTaskScheduler` |
| `TaskTimeoutScanner.java` | **修改** | 移除 `static final` 常量 + `@Scheduled`；改为 `@PostConstruct` 向 Manager 注册 |
| `ApprovalTimeoutJob.java` | **修改** | 同上 |
| `SensitiveWordFilter.java` | **修改** | 同上 |
| `McpHeartbeatDetector.java` | **修改** | 同上 |
| `McpClientManager.java` | **修改** | 同上 |
| `bootstrap/src/main/resources/application.yml` | **修改** | 增加 scheduler fallback 默认值（兜底） |

### 3.5 兜底机制

```
启动时:
  1. 尝试从 Nacos 读取 agent-platform-scheduler.json
  2. 若读取成功 → 使用 Nacos 配置
  3. 若读取失败 → 使用 application.yml 中的 scheduler.* 默认值
  4. 若 YAML 也无 → 使用 SchedulerConfig 内部类中的硬编码兜底值

运行时:
  Nacos 配置变更 → Listener 回调 → 更新 volatile 字段
  → 下次 Trigger 计算时自动使用新间隔（无需重启任务）
```

---

## 四、详细设计

### 4.1 SchedulerConfig 类结构

```
SchedulerConfig extends NacosConfig<SchedulerProps>
├── getDataId()       → "agent-platform-scheduler.json"
├── getGroup()        → "AGENT-PLATFORM-CONFIG_ENTITY"
├── getConfigName()   → "SchedulerConfig"
├── getPropsClass()   → SchedulerProps.class
│
├── SchedulerProps (内部静态类)
│   ├── timeoutScanIntervalMs: long
│   ├── staleScanIntervalMs: long
│   ├── staleSubmittedMinutes: int
│   ├── timeoutScanBatchSize: int
│   ├── approvalTimeoutScanMs: long
│   ├── mcpHeartbeatIntervalMs: long
│   ├── mcpMaxFailures: int
│   ├── mcpClientRefreshMs: long
│   └── sensitiveWordRefreshMs: long
│
└── 便捷方法 (防止 Nacos 不可用时返回 null)
    ├── getTimeoutScanIntervalMs()    → 返回 long (兜底 15_000)
    ├── getStaleScanIntervalMs()      → 返回 long (兜底 60_000)
    └── ... (每个字段一个 getter，带兜底值)
```

### 4.2 DynamicScheduledTaskManager 接口设计

```
DynamicScheduledTaskManager
├── registerTask(taskName, Runnable, intervalSupplier)
│   注册一个任务，intervalSupplier 每次触发时调用以获取最新间隔
│
├── cancelTask(taskName)
│   取消指定任务
│
├── updateAllIntervals()
│   配置变更后手动触发（可选，Trigger 模式下自动生效）
│
└── getActiveTaskNames()
│   返回当前运行中的任务名称（运维调试用）
```

### 4.3 代码改造模式（以 TaskTimeoutScanner 为例）

**改造前**：
```java
@Component
public class TaskTimeoutScanner {
    private static final int TIMEOUT_SCAN_INTERVAL_MS = 15_000;
    // ...
    @Scheduled(fixedDelay = TIMEOUT_SCAN_INTERVAL_MS)
    public void scanTimeoutTasks() { ... }
}
```

**改造后**：
```java
@Component
public class TaskTimeoutScanner {
    private final SchedulerConfig schedulerConfig;

    @PostConstruct
    public void registerTasks() {
        // 不再用 @Scheduled，改为动态注册
        dynamicScheduler.registerTask(
            "timeoutTaskScan",
            this::scanTimeoutTasks,
            () -> schedulerConfig.getTimeoutScanIntervalMs()
        );
    }

    // 方法签名不变，仅去掉 @Scheduled
    public void scanTimeoutTasks() { ... }
}
```

---

## 五、验证方式

| 验证项 | 方法 | 预期 |
|--------|------|------|
| Nacos 配置加载 | 启动后检查日志 `[SchedulerConfig] 配置加载成功` | 日志输出实际值 |
| 配置热更新 | Nacos 控制台修改间隔为 10s → 等待一个周期 | 日志显示新间隔生效 |
| Nacos 不可用兜底 | 停 Nacos → 重启应用 → 检查任务是否正常运行 | 使用 YAML 兜底值 |
| 扫描逻辑不变 | 观察各定时任务日志输出 | 业务行为无变化 |

---

## 六、回滚方案

若上线后出现问题：

1. **代码回滚**：恢复 `@Scheduled` 注解版本（保留原 `static final` 常量的 commit）
2. **配置回滚**：Nacos 控制台 → 配置管理 → 历史版本 → 回滚到初始版本
3. **紧急降级**：在 Nacos 中删除 `agent-platform-scheduler.json` → 应用自动使用 YAML 兜底值

---

> 📋 **下一步**: [子方案 02 — RAG 检索参数 Nacos 动态化](配置治理-子方案02-RAG检索参数Nacos动态化.md)
