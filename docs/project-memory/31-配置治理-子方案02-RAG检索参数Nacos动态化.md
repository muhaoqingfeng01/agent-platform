# P6 配置治理 — 子方案 02：RAG 检索参数 Nacos 动态化（实现快照）

> **日期**: 2026-07-14 10:58
> **分支**: nacos/nacos-config
> **触发**: 代码生成（重构版 — NacosConfig 模板 + JSON + 硬编码兜底）
> **父方案**: [配置治理-总体设计方案](../../P6-迭代优化方案/配置信息迭代优化方案/配置治理-总体设计方案.md)

---

## 做了什么

最终采用 **NacosConfig 模板模式**（对齐子方案01的 `SchedulerConfig`），完成 1 个新建 + 3 个改造：

### 1. 🆕 `RagConfig.java` — Nacos JSON 动态配置类（新建，~270 行）
- 继承 `NacosConfig<RagConfig.RagProps>` 模板，风格完全对齐 `SchedulerConfig`
- DataId: `agent-platform-rag.json`，Group: `AGENT-PLATFORM-CONFIG_ENTITY`
- Jackson 反序列化嵌套 JSON → `RagProps`（含 `SearchProps`/`IndexProps`/`MultiStageProps`/`MonitoringProps`）
- **29 个 `getXxx()` 便捷方法**，每个内置硬编码兜底值（Nacos 不可用时自动生效）
- Nacos 监听器自动注册，配置变更即时回调 `parseConfig()`

### 2. `RagPrecisionProperties.java` — 回退原始状态
- 保持 `@ConfigurationProperties(prefix = "agent.rag")` 不变
- RAG 参数动态管理职责移交给 `RagConfig`

### 3. `MilvusCollectionManager.java` — 注入 RagConfig
- `RagPrecisionProperties` → `RagConfig`
- `search()`: `ragConfig.getSearchNprobe()` 动态获取 nprobe
- `buildIndexExtraParam()`: 4 种索引类型参数均从 `ragConfig.getIndexXxx()` 读取

### 4. `HybridSearchApplicationService.java` — 注入 RagConfig
- `RagPrecisionProperties` → `RagConfig`
- 删除 `FINAL_TOP_K`/`RRF_K` 常量
- 3 处引用改为 `ragConfig.getMultiStageRrfK()` / `ragConfig.getFinalTopK()`

### 5. `application.yml` — 清理
- 移除 `agent.rag.*` YAML 兜底块（兜底逻辑已移至 RagConfig.getXxx() 方法内）
- 移除 `extension-configs` 中的 RAG 条目（RagConfig 手动拉取 JSON，不依赖 Environment 注入）

---

## 架构对比

| 维度 | 初版（@RefreshScope） | 🔴 问题 | 终版（NacosConfig 模板） |
|------|----------------------|---------|--------------------------|
| 配置源 | YAML extension-config | JSON→Environment 不解析 | JSON，手动 `getConfig()` 拉取 |
| 解析器 | YamlPropertySourceLoader | — | Jackson `ObjectMapper` |
| 热刷新 | @RefreshScope 代理 | 依赖 Environment 有 key-value | Nacos Listener 回调 → `parseConfig()` |
| 兜底 | YAML fallback 块 | 双源维护 | `getXxx()` 方法内硬编码默认值 |
| 风格 | Spring Cloud 标准 | — | 对齐 SchedulerConfig（子方案01） |

---

## 关键决策

| 决策 | 选择 | 理由 |
|------|------|------|
| 方案选型 | 放弃 @RefreshScope，改用 NacosConfig 模板 | JSON 格式 Nacos 配置不解析为独立 key-value，@ConfigurationProperties 无法绑定 |
| 兜底位置 | Java 硬编码（`getXxx()` 内） | 单源维护，不依赖 YAML，与 SchedulerConfig 风格一致 |
| Props 结构 | 嵌套内部类（SearchProps/IndexProps/...） | Jackson 自然映射，JSON 可读性好 |
| extension-config | 不注册 | RagConfig 手动 `getConfig()` 拉取，无需 Environment 注入 |

---

## 踩坑记录

- **🔴 JSON 格式 Nacos 配置与 @ConfigurationProperties 不兼容**
  - `file-extension: json` → NacosPropertySource 存储原始字符串，不解析为独立 key-value
  - `@ConfigurationProperties` + `@RefreshScope` 方案在此前提下完全无效
  - **正确方案**: NacosConfig 模板 + Jackson 手动解析（与 SchedulerConfig 一致）

---

## 涉及文件

| 文件 | 改动类型 | 说明 |
|------|:------:|------|
| `RagConfig.java` | **新建** | ~270 行，29 个 getter + 5 个内部 Props 类 |
| `RagPrecisionProperties.java` | 回退 | 移除 @RefreshScope + finalTopK |
| `MilvusCollectionManager.java` | 修改 | RagConfig 替代 RagPrecisionProperties |
| `HybridSearchApplicationService.java` | 修改 | RagConfig 替代 RagPrecisionProperties |
| `application.yml` | 修改 | 移除 agent.rag.* 块 + extension-config |

---

## Nacos 启用步骤

1. 将 `docs/P6-迭代优化方案/配置信息迭代优化方案/nacos-configs/agent-platform-rag.json` 导入 Nacos 控制台
2. DataId: `agent-platform-rag.json`，Group: `AGENT-PLATFORM-CONFIG_ENTITY`
3. 发布后 `RagConfig` 自动通过 Listener 回调刷新，无需重启

## 下一步

- [子方案 03](配置治理-子方案03-AI模型与会话参数Nacos动态化.md) — AI 模型参数与会话参数 Nacos 动态化
- [子方案 04](配置治理-子方案04-静态常量类统一管理.md) — 静态常量类统一管理
- [子方案 05](配置治理-子方案05-Sentinel规则Nacos持久化.md) — Sentinel 规则 Nacos 持久化
