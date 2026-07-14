# 配置治理 — 子方案 05：Sentinel 规则 Nacos 持久化

> **版本**: V1.0.0
> **日期**: 2026-07-14
> **批次**: 第五批（P2，Sentinel 增强）
> **父方案**: [配置治理-总体设计方案](配置治理-总体设计方案.md)
> **涉及项数**: 6 项规则 / 1 个文件

---

## 一、背景

### 1.1 Sentinel 规则生命周期问题

当前 `SentinelConfig.java` 在代码中硬编码了流控/熔断规则。虽然 Sentinel Dashboard 可以在**运行时**动态修改规则，但存在一个关键缺陷：

```
Sentinel Dashboard 修改规则 → 写入 Dashboard 内存
                              ↓
                        应用重启 / Dashboard 重启
                              ↓
                        规则丢失！回退到代码中的硬编码值
```

### 1.2 本方案目标

将 Sentinel 规则持久化到 Nacos，实现：

```
Sentinel Dashboard 修改规则 → 推送至 Nacos → 所有应用实例同步 → 持久化存储
                                  ↓
                          应用重启 / Dashboard 重启
                                  ↓
                          从 Nacos 拉取上次保存的规则（不丢失）
```

### 1.3 涉及规则（当前硬编码在 `SentinelConfig.java`）

| # | 规则类型 | 参数 | 当前硬编码值 |
|:--:|---------|------|:-----------:|
| 1 | 流控 — 全局 | 全局聊天接口 QPS | 200 |
| 2 | 流控 — 租户 | 单租户聊天接口 QPS | 50 |
| 3 | 熔断 — 慢调用 | 慢调用比例阈值 | 0.5 (50%) |
| 4 | 熔断 — 异常 | 异常比例阈值 | 0.5 (50%) |
| 5 | 熔断 — 窗口 | 熔断恢复窗口 | 30s |
| 6 | 熔断 — 统计 | 慢调用 RT 阈值 / 最小请求 / 统计间隔 | 200ms / 10 / 1000ms |

---

## 二、当前状态分析

### 2.1 已有基础设施

`application.yml` 中已预留 Nacos 数据源配置：

```yaml
spring:
  sentinel:
    enabled: ${SENTINEL_ENABLED:false}
    transport:
      dashboard: ${SENTINEL_DASHBOARD:localhost:8080}
      port: ${SENTINEL_PORT:8719}
    datasource:
      nacos:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group-id: DEFAULT_GROUP
        data-type: json
```

✅ Nacos 连接信息已配置
⚠️ 但缺少具体的 `data-id` 和 `rule-type` 绑定

### 2.2 当前 SentinelConfig 代码模式

```java
// 当前 — 伪代码示意
@Configuration
public class SentinelConfig {
    @PostConstruct
    public void initRules() {
        // 硬编码规则
        List<FlowRule> flowRules = List.of(
            new FlowRule("global_chat").setGrade(FlowGrade.QPS).setCount(200),
            new FlowRule("tenant_chat").setGrade(FlowGrade.QPS).setCount(50)
        );
        FlowRuleManager.loadRules(flowRules);
        // 熔断规则同理...
    }
}
```

---

## 三、技术方案

### 3.1 Sentinel Nacos 数据源机制

Sentinel 官方提供了 `sentinel-datasource-nacos` 模块，支持通过 Nacos 持久化规则。工作机制：

```
┌──────────────┐   rule change    ┌──────────┐   push    ┌─────────┐
│  Dashboard   │ ───────────────→ │  Nacos   │ ────────→ │  应用1   │
│              │                  │ Config   │           │  应用2   │
└──────────────┘                  └──────────┘           │  应用N   │
                                                         └─────────┘
应用重启 → ReadableDataSource 从 Nacos 拉取 → 恢复上次规则
```

### 3.2 方案选型

| 方案 | 描述 | 结论 |
|------|------|:----:|
| A. YAML 声明式配置 | 在 `application.yml` 中声明 `datasource.nacos` 的 data-id | ✅ **选用**（官方推荐） |
| B. Java 编程式配置 | 在 `SentinelConfig` 中手动注册 `NacosDataSource` | ❌ 与已有 YAML 配置重复 |
| C. 仅靠 Dashboard | 不做持久化，规则丢失后回退 | ❌ 不解决问题 |

### 3.3 YAML 配置设计（完整版）

```yaml
spring:
  sentinel:
    enabled: true
    transport:
      dashboard: ${SENTINEL_DASHBOARD:localhost:8080}
      port: ${SENTINEL_PORT:8719}
    datasource:
      # 流控规则数据源
      ds-flow:
        nacos:
          server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
          namespace: ${NACOS_NAMESPACE:}
          group-id: SENTINEL_GROUP
          data-id: ${spring.application.name}-flow-rules.json
          data-type: json
          rule-type: flow
      # 熔断规则数据源
      ds-degrade:
        nacos:
          server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
          namespace: ${NACOS_NAMESPACE:}
          group-id: SENTINEL_GROUP
          data-id: ${spring.application.name}-degrade-rules.json
          data-type: json
          rule-type: degrade
      # 系统规则数据源（可选）
      ds-system:
        nacos:
          server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
          namespace: ${NACOS_NAMESPACE:}
          group-id: SENTINEL_GROUP
          data-id: ${spring.application.name}-system-rules.json
          data-type: json
          rule-type: system
```

### 3.4 Nacos 中需创建的配置

#### DataId: `agent-platform-flow-rules.json`（Group: SENTINEL_GROUP）

```jsonc
[
  {
    "resource": "global_chat",
    "grade": 1,           // 1=QPS, 0=线程数
    "count": 200,
    "limitApp": "default",
    "strategy": 0,        // 0=直接, 1=关联, 2=链路
    "controlBehavior": 0  // 0=快速失败, 1=Warm Up, 2=匀速排队
  },
  {
    "resource": "tenant_chat",
    "grade": 1,
    "count": 50,
    "limitApp": "default",
    "strategy": 0,
    "controlBehavior": 0
  }
]
```

#### DataId: `agent-platform-degrade-rules.json`（Group: SENTINEL_GROUP）

```jsonc
[
  {
    "resource": "global_chat",
    "grade": 0,            // 0=慢调用比例, 1=异常比例, 2=异常数
    "count": 200,          // 慢调用 RT 阈值 (ms)
    "timeWindow": 30,      // 熔断窗口 (秒)
    "minRequestAmount": 10,// 最小请求数
    "statIntervalMs": 1000,// 统计间隔 (ms)
    "slowRatioThreshold": 0.5  // 慢调用比例阈值
  },
  {
    "resource": "global_chat",
    "grade": 1,            // 异常比例
    "count": 0.5,          // 异常比例阈值
    "timeWindow": 30,
    "minRequestAmount": 10,
    "statIntervalMs": 1000
  }
]
```

### 3.5 SentinelConfig.java 改造

保留代码中的硬编码值作为 **fallback 默认值**（Nacos 不可用 / 数据源拉取失败时使用），但优先级低于 Nacos：

```java
// 改造后 — 伪代码示意
@Configuration
public class SentinelConfig {

    private static final double FALLBACK_GLOBAL_QPS = 200;
    private static final double FALLBACK_TENANT_QPS = 50;
    // ... (保留原硬编码值作为 fallback)

    @PostConstruct
    public void initFallbackRules() {
        // 仅在 Sentinel 禁用的退化场景下加载 fallback 规则
        // 正常运行时由 Nacos DataSource 自动加载，不执行此方法
    }
}
```

> ℹ️ Sentinel 的 `ReadableDataSource` 机制内置了 Nacos 不可用时的容错：首次拉取失败不阻塞启动，后续通过定时轮询自动恢复。

---

## 四、实施前提

| 前提 | 状态 |
|------|:----:|
| `spring-cloud-starter-alibaba-sentinel` 依赖 | ✅ 已有（infrastructure/pom.xml:163-167） |
| `sentinel-datasource-nacos` 依赖 | ⚠️ 需确认是否已引入（通常由 starter 传递引入） |
| Sentinel Dashboard 已部署 | ⚠️ 运维前提 |
| Nacos 配置中心可用 | ✅ 已配置 |

---

## 五、文件改动清单

| 文件 | 改动类型 | 改动内容 |
|------|:------:|----------|
| `bootstrap/src/main/resources/application.yml` | **修改** | 补全 `sentinel.datasource` 中的 `ds-flow`/`ds-degrade` 配置 |
| `SentinelConfig.java` | **修改** | 调整注释，标识代码中规则为 fallback；避免与 Nacos DataSource 规则冲突 |
| `infrastructure/pom.xml` | **可能修改** | 若缺少 `sentinel-datasource-nacos` 依赖则添加（scope=compile） |
| Nacos 控制台 | **新建** | 创建 `agent-platform-flow-rules.json` + `agent-platform-degrade-rules.json` |

---

## 六、验证方式

| 验证项 | 方法 | 预期 |
|--------|------|------|
| 规则从 Nacos 加载 | 启动后查看 `FlowRuleManager.getRules()` | 包含 Nacos 中配置的规则 |
| Dashboard 修改持久化 | Dashboard 改 QPS → 应用重启 → 查看规则 | 规则为修改后的值（非代码硬编码） |
| 规则实时生效 | Dashboard 改 QPS 为 1 → 快速调用 2 次 | 第 2 次被限流 |
| Nacos 不可用 | 停 Nacos → 重启应用 | 应用正常启动，规则为空或 fallback |
| Nacos 恢复 | 启动 Nacos → 等待轮询 | 应用自动拉取规则恢复 |

---

## 七、参考

- [Sentinel Nacos DataSource 官方文档](https://sentinelguard.io/zh-cn/docs/dynamic-rule-configuration.html)
- 已有 YAML 预留配置: `application.yml:43-48`
- 已有 Sentinel 依赖: `infrastructure/pom.xml:163-167`

---

> 📋 **上一步**: [子方案 04](配置治理-子方案04-静态常量类统一管理.md)
> 📋 **返回总方案**: [配置治理-总体设计方案](配置治理-总体设计方案.md)
