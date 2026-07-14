# P6 配置治理 — 子方案 03：AI 模型 + 安全 + 会话参数 Nacos 动态化（实现快照）

> **日期**: 2026-07-14 12:02
> **分支**: nacos/nacos-config
> **触发**: 代码生成
> **父方案**: [配置治理-总体设计方案](../../P6-迭代优化方案/配置信息迭代优化方案/配置治理-总体设计方案.md)

---

## 做了什么

3 个 Nacos DataId + 3 个配置类 + 10 个消费者改造：

### 1. 🆕/升迁 AiModelConfig（config/nacos/，12 字段）
- 从 `config/nacos/test/` 迁出到 `config/nacos/`（正式环境）
- 保留原有 6 个 DAG 任务调度字段
- 🆕 新增 6 个 LLM/Embedding 参数字段
- 12 个 Optional 链式 getter + 硬编码兜底
- **AiConfig.java** 注入 AiModelConfig，ChatClient 默认参数从 Nacos 动态读取（model/temperature/maxTokens）

### 2. 🆕 SecurityConfig（config/nacos/，6 字段）
- 输入长度限制、审批超时、Presidio 超时/降级、CORS 域名列表/maxAge
- **LengthFilter.java** — `@Value` → SecurityConfig.getMaxInputLength()
- **ApprovalWorkflowApplicationService.java** — TIMEOUT_MINUTES 常量 → SecurityConfig
- **CorsConfig.java** — 硬编码域名列表/maxAge → SecurityConfig

### 3. 🆕 SessionConfig（config/nacos/，7 字段）
- 短期记忆（轮数+TTL）、长期记忆轮数、SSE 心跳间隔、上下文轮数、意图/工具缓存 TTL
- **SessionMemoryService.java** — MAX_ROUNDS/TTL 常量 → SessionConfig
- **LongTermMemoryService.java** — MAX_ROUNDS 常量 → SessionConfig
- **StreamOrchestrationService.java** — HEARTBEAT_INTERVAL_MS → SessionConfig
- **KnowledgeSearchStreamService.java** — HEARTBEAT_INTERVAL_MS/CONTEXT_ROUNDS → SessionConfig
- **CacheRecognizer.java** — CACHE_TTL 常量 → SessionConfig
- **ToolCacheManager.java** — TTL 常量 → SessionConfig

---

## 关键决策

| 决策 | 选择 | 理由 |
|------|------|------|
| AiModelConfig 迁移 | 从 test 包升迁到正式包 | test 包不适合生产引用 |
| ChatClient 参数传递 | `.defaultOptions(OpenAiChatOptions)` | Spring AI 1.1.7 API（`.model()` / `.temperature()` / `.maxTokens()`，无 `with` 前缀） |
| CORS 域名列表 | `List<String>` 硬编码兜底 | Jackson 自然映射 JSON array |
| SessionMemoryService 常量 | 改为实例方法 `maxRounds()`/`maxMessages()`/`ttl()` | 保持代码简洁，避免 static→instance 不一致 |
| 重复消除 | HEARTBEAT_INTERVAL_MS 两处定义 → SessionConfig 单点 | 改一处漏另一处的 bug 从此根除 |

---

## 踩坑记录

- **Spring AI 1.1.7 API 差异**：`OpenAiChatOptions.Builder` 方法无 `with` 前缀（`.model()` 不是 `.withModel()`，`.temperature()` 不是 `.withTemperature()`）
- **旧 test 包引用**：`InteractionApplicationService` 仍引用 `config.nacos.test.AiModelConfig`，删除旧文件后编译失败，需同步修改 import

---

## 涉及文件

| 类别 | 文件 | 改动 |
|:--:|------|:--:|
| 新建/升迁 | `AiModelConfig.java` | 迁出 + 补全字段（12 getter） |
| 新建 | `SecurityConfig.java` | ~90 行（6 getter + CORS 兜底） |
| 新建 | `SessionConfig.java` | ~100 行（7 getter） |
| 改造 | `AiConfig.java` | ChatClient 默认参数动态化 |
| 改造 | `LengthFilter.java` | @Value → SecurityConfig |
| 改造 | `ApprovalWorkflowApplicationService.java` | TIMEOUT_MINUTES → SecurityConfig |
| 改造 | `CorsConfig.java` | 硬编码 → SecurityConfig |
| 改造 | `SessionMemoryService.java` | 3 常量 → SessionConfig |
| 改造 | `LongTermMemoryService.java` | 1 常量 → SessionConfig |
| 改造 | `StreamOrchestrationService.java` | 1 常量 → SessionConfig（消除重复） |
| 改造 | `KnowledgeSearchStreamService.java` | 2 常量 → SessionConfig（消除重复） |
| 改造 | `CacheRecognizer.java` | 1 常量 → SessionConfig |
| 改造 | `ToolCacheManager.java` | 1 常量 → SessionConfig |
| 删除 | `config/nacos/test/AiModelConfig.java` | 旧 test 包文件 |
| 新增 | 3 个 Nacos JSON 模板 | nacos-configs/ |

---

## Nacos DataId 汇总

```
AGENT-PLATFORM-CONFIG_ENTITY/
├── agent-platform-scheduler.json    ← 子方案01
├── agent-platform-rag.json          ← 子方案02
├── agent-platform-ai-model.json     ← 🆕 子方案03（12 字段）
├── agent-platform-security.json     ← 🆕 子方案03（6 字段）
└── agent-platform-session.json      ← 🆕 子方案03（7 字段）
```

## 下一步

- [子方案 04](配置治理-子方案04-静态常量类统一管理.md) — 静态常量类统一管理（12 项）
- [子方案 05](配置治理-子方案05-Sentinel规则Nacos持久化.md) — Sentinel 规则 Nacos 持久化（6 项）
