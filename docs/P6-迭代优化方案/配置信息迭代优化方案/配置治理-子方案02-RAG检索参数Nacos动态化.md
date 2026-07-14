# 配置治理 — 子方案 02：RAG 检索参数 Nacos 动态化

> **版本**: V1.0.0
> **日期**: 2026-07-14
> **批次**: 第二批（P1，涉及面最广）
> **父方案**: [配置治理-总体设计方案](配置治理-总体设计方案.md)
> **涉及项数**: 24 项 / 3 个文件

---

## 一、范围与目标

将 RAG 检索全链路参数（召回 → 重排序 → 融合 → 精度监控）从 `@ConfigurationProperties`（无热刷新）迁移到 Nacos 动态配置，支持 **A/B 测试**和**渐进调优**。

### 1.1 涉及配置项

#### 召回参数（7 项）

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 1 | `search.topK` | `RagPrecisionProperties.SearchProps` | 20 |
| 2 | `search.similarityThreshold` | 同上 | 0.50 |
| 3 | `search.nprobe` | 同上 | 16 |
| 4 | `search.ef` | 同上 | 64 |
| 5 | `search.searchListSize` | 同上 | 100 |
| 6 | `search.consistencyLevel` | 同上 | BOUNDED |
| 7 | `search.timeoutMs` | 同上 | 5000 |

#### 多阶段融合参数（10 项）

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 8 | `multiStage.enableReranker` | `RagPrecisionProperties.MultiStageProps` | false |
| 9 | `multiStage.rerankerType` | 同上 | NONE |
| 10 | `multiStage.rerankerTopK` | 同上 | 10 |
| 11 | `multiStage.coarseTopK` | 同上 | 50 |
| 12 | `multiStage.enableRrfFusion` | 同上 | true |
| 13 | `multiStage.rrfK` | 同上 | 60 |
| 14 | `multiStage.fusionTopN` | 同上 | 5 |
| 15 | `multiStage.vectorWeight` | 同上 | 0.5 |
| 16 | `multiStage.keywordWeight` | 同上 | 0.5 |
| 17 | `multiStage.finalTopK` | `HybridSearchApplicationService.java:48` | 5 |

#### 精度监控参数（7 项）

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 18 | `monitoring.enableAutoTuning` | `RagPrecisionProperties.MonitoringProps` | false |
| 19 | `monitoring.evaluationDatasetSize` | 同上 | 50 |
| 20 | `monitoring.recallTarget` | 同上 | 0.90 |
| 21 | `monitoring.precisionTarget` | 同上 | 0.80 |
| 22 | `monitoring.tuningIntervalDays` | 同上 | 7 |
| 23 | `monitoring.maxLatencyMsTarget` | 同上 | 200 |
| 24 | `monitoring.regressionAlertThreshold` | 同上 | 0.05 |

### 1.2 不纳入本方案的配置

| 配置项 | 原因 | 归属 |
|--------|------|------|
| `index.defaultType/MetricType` | Collection 创建后不可变 | [子方案 04](配置治理-子方案04-静态常量类统一管理.md) |
| `index.nlist/hnswM/efConstruction` | 同上 | 同上 |
| Milvus 连接参数（host/port 等） | 启动时必需，环境差异 | YAML 保持 |

---

## 二、当前状态分析

### 2.1 核心问题：配置源不一致

```
RagPrecisionProperties (YAML)
        ↓ getTopK() 等 getter
    HybridSearchApplicationService
        ↓ 调用 search(topK, threshold, ...)
    MilvusCollectionManager.search()
        ↓ 内部硬编码 "nprobe": 16  ← 🔴 与 RagPrecisionProperties.getNprobe() 不一致！
```

**存在 Bug**：`MilvusCollectionManager.java:272` 硬编码 `"{\"nprobe\": 16}"`，完全忽略了 `RagPrecisionProperties.search.nprobe` 的配置值。修改 YAML 中的 nprobe 无法影响实际检索行为。

### 2.2 涉及文件现状

```
agent-platform-infrastructure/
├── config/rag/RagPrecisionProperties.java    ← @ConfigurationProperties，28 字段，无热刷新
├── rag/MilvusCollectionManager.java           ← @Value(milvus.*) + 硬编码 nprobe/M/efConstruction
agent-platform-application/
├── knowledge/HybridSearchApplicationService.java ← static final FINAL_TOP_K=5, RRF_K=60
```

---

## 三、技术方案设计

### 3.1 方案选型

| 方案 | 描述 | 结论 |
|------|------|:----:|
| A. 给 `RagPrecisionProperties` 加 `@RefreshScope` | 最简改动，标准 Spring 方式 | ✅ **选用** |
| B. 新建 `RagConfig extends NacosConfig` | 复用项目框架，但需改所有引用方 | ❌ 成本高 |
| C. 保持现状 + 文档化 | 零改动 | ❌ 不解决问题 |

**选择方案 A 的理由**：
- `RagPrecisionProperties` 已经是 `@ConfigurationProperties`，全项目通过 getter 引用
- 只需加 `@RefreshScope` 即可获得热更新能力
- 不需要改任何引用方代码
- 但是：**`@RefreshScope` 要求配置在 Spring Environment 中**，目前 RAG 参数在 YAML 中，需要确认 Nacos 配置是否通过 `spring.cloud.nacos.config` 注入到了 Environment

**最终确定：方案 A（`@RefreshScope`）+ YAML fallback**

> ⚠️ `@RefreshScope` 代理机制会在下次方法调用时触发刷新，对无状态 Bean 完美适用。
> `RagPrecisionProperties` 本身只有 getter，符合条件。

### 3.2 同步修复 MilvusCollectionManager 硬编码

这是本方案的**核心修复**：让 `MilvusCollectionManager.search()` 的 `nprobe` 参数从 `RagPrecisionProperties` 读取。

**改造前**（`MilvusCollectionManager.java:272`）：
```java
.withParams("{\"nprobe\": 16}")  // 硬编码
```

**改造后**：
```java
// 注入 RagPrecisionProperties
.withParams("{\"nprobe\": " + ragPrecisionProperties.getSearch().getNprobe() + "}")
```

同理 `buildIndexExtraParam()` 中的 `nlist`/`M`/`efConstruction`：
```java
// 改造前 — 硬编码
case IVF_FLAT -> "{\"nlist\":128}";
case HNSW -> "{\"M\":16,\"efConstruction\":200}";

// 改造后 — 从配置读取（兜底保留原值）
case IVF_FLAT -> "{\"nlist\":" + ragProps.getIndex().getNlist() + "}";
case HNSW -> "{\"M\":" + ragProps.getIndex().getHnswM() +
             ",\"efConstruction\":" + ragProps.getIndex().getHnswEfConstruction() + "}";
```

### 3.3 HybridSearchApplicationService 常量消除

**改造前**：
```java
private static final int FINAL_TOP_K = 5;
private static final int RRF_K = 60;
```

**改造后**：直接从 `RagPrecisionProperties` 读取，删除常量。

### 3.4 文件改动清单

| 文件 | 改动类型 | 改动内容 |
|------|:------:|----------|
| `RagPrecisionProperties.java` | **修改** | 加 `@RefreshScope` 注解；`@ConfigurationProperties` 保持不变 |
| `MilvusCollectionManager.java` | **修改** | 注入 `RagPrecisionProperties`；消除 `nprobe`/索引参数硬编码 |
| `HybridSearchApplicationService.java` | **修改** | 删除 `FINAL_TOP_K`/`RRF_K` 常量；改用属性注入 |

---

## 四、Nacos 配置规划

### 4.1 两种管理方式

由于 RAG 参数已通过 `@ConfigurationProperties` 绑定到 `agent.rag.*`，有两种 Nacos 接入方式：

| 方式 | 机制 | 操作 |
|------|------|------|
| **方式 1**：Nacos 全覆盖 | 在 Nacos 创建 `agent-platform-rag.json`，发布后 Spring 自动合并到 Environment | Nacos 控制台创建 DataId |
| **方式 2**：YAML + Nacos 覆盖 | YAML 保留默认值，Nacos 中同名属性覆盖 | 同上，但 Nacos 中只写需覆盖的字段 |

**建议方式 1**：将 24 个 RAG 参数全量写入 Nacos，YAML 中保留默认值作为 fallback。

### 4.2 Nacos 配置内容（`agent-platform-rag.json`）

```jsonc
{
  "agent.rag.search.topK": 20,
  "agent.rag.search.similarityThreshold": 0.50,
  "agent.rag.search.nprobe": 16,
  "agent.rag.search.ef": 64,
  "agent.rag.search.searchListSize": 100,
  "agent.rag.search.consistencyLevel": "BOUNDED",
  "agent.rag.search.timeoutMs": 5000,

  "agent.rag.multiStage.enableReranker": false,
  "agent.rag.multiStage.rerankerType": "NONE",
  "agent.rag.multiStage.rerankerTopK": 10,
  "agent.rag.multiStage.coarseTopK": 50,
  "agent.rag.multiStage.enableRrfFusion": true,
  "agent.rag.multiStage.rrfK": 60,
  "agent.rag.multiStage.fusionTopN": 5,
  "agent.rag.multiStage.vectorWeight": 0.5,
  "agent.rag.multiStage.keywordWeight": 0.5,

  "agent.rag.monitoring.enableAutoTuning": false,
  "agent.rag.monitoring.evaluationDatasetSize": 50,
  "agent.rag.monitoring.recallTarget": 0.90,
  "agent.rag.monitoring.precisionTarget": 0.80,
  "agent.rag.monitoring.tuningIntervalDays": 7,
  "agent.rag.monitoring.maxLatencyMsTarget": 200,
  "agent.rag.monitoring.regressionAlertThreshold": 0.05,
  "agent.rag.monitoring.gridSearchEnabled": false,
  "agent.rag.finalTopK": 5
}
```

> ⚠️ 注意：`nacos.config.file-extension` 当前设为 `json`，所以 DataId 必须是 `agent-platform-rag.json`（加 `.json` 后缀）。

### 4.3 application.yml 中的 fallback 配置

```yaml
# RAG fallback 配置 — Nacos 不可用时使用
agent:
  rag:
    search:
      topK: 20
      similarityThreshold: 0.50
      nprobe: 16
      # ... (与 Nacos 值一致，作为兜底)
```

---

## 五、RAG 参数调优指南（运维参考）

以下场景是本方案的核心价值 — **免重启调优**：

| 场景 | 调整参数 | 方向 |
|------|----------|------|
| 检索结果太少 | `similarityThreshold` | ↓ 降低阈值（如 0.5 → 0.3） |
| 检索结果不相关 | `similarityThreshold` | ↑ 提高阈值（如 0.5 → 0.7） |
| 检索太慢 | `search.nprobe` | ↓ 减小探测数（如 16 → 8） |
| 检索不准确 | `search.nprobe` | ↑ 增大探测数（如 16 → 32） |
| Reranker 效果验证 | `multiStage.enableReranker` | false → true（A/B 对比） |
| 关键词权重不够 | `multiStage.keywordWeight` | ↑ 提高（如 0.5 → 0.7） |
| 向量匹配过强 | `multiStage.vectorWeight` | ↓ 降低（如 0.5 → 0.3） |
| 自动调优开关 | `monitoring.enableAutoTuning` | false → true |
| 召回率不达标 | `monitoring.recallTarget` | 按业务调整 |

---

## 六、验证方式

| 验证项 | 方法 | 预期 |
|--------|------|------|
| `@RefreshScope` 生效 | Nacos 修改 `similarityThreshold` → 调用检索 API | 返回结果数量变化 |
| Milvus nprobe 联动 | Nacos 修改 `nprobe` → 观察 Milvus 查询日志 | 日志中的 nprobe 参数变化 |
| YAML fallback | 停 Nacos → 重启 → 调用检索 | 使用 YAML 默认值 |
| 索引参数一致性 | 新创建 Collection → 检查索引参数 | 与 Nacos 配置一致 |

---

## 七、回滚方案

1. **代码回滚**：去掉 `@RefreshScope`，恢复 `MilvusCollectionManager` 硬编码
2. **配置回滚**：Nacos 历史版本一键回滚
3. **紧急降级**：删除 Nacos 中的 `agent-platform-rag.json`，应用使用 YAML fallback

---

> 📋 **上一步**: [子方案 01](配置治理-子方案01-定时任务与心跳参数Nacos动态化.md)
> 📋 **下一步**: [子方案 03](配置治理-子方案03-AI模型与会话参数Nacos动态化.md)
