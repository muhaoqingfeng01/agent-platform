# 配置治理 — 子方案 03：AI 模型 + 安全 + 会话参数 Nacos 动态化

> **版本**: V1.0.0
> **日期**: 2026-07-14
> **批次**: 第三批（P1，补齐覆盖面）
> **父方案**: [配置治理-总体设计方案](配置治理-总体设计方案.md)
> **涉及项数**: 17 项 / 7 个文件

---

## 一、范围与目标

将 AI 模型调用参数、安全围栏参数、会话记忆参数统一迁移到 Nacos 动态管理。本方案覆盖三个相对独立的配置域，可并行开发。

### 1.1 涉及配置项

#### AI 模型参数（4 项）— 复用现有 `AiModelConfig`

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 1 | `chat.model` | `application.yml:94` | deepseek-v4-pro |
| 2 | `chat.temperature` | `application.yml:95` | 0.7 |
| 3 | `chat.max-tokens` | `application.yml:96` | 4096 |
| 4 | `embedding.model` | `application.yml:105` | text-embedding-v3 |

#### 安全参数（5 项）

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 5 | `security.filter.max-input-length` | `LengthFilter.java:21` | 10000 |
| 6 | 审批超时分钟 | `ApprovalWorkflowApplicationService.java:50` | 5 |
| 7 | Presidio 脱敏超时 | `application.yml:280` | 5000ms |
| 8 | CORS allowedOriginPatterns | `CorsConfig.java:30-32` | 3 个域名 |
| 9 | CORS maxAge | `CorsConfig.java:48` | 3600s |

#### 会话 & 记忆参数（6 项）

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 10 | 短期记忆最大轮数 | `SessionMemoryService.java:32` | 20 |
| 11 | 短期记忆 TTL | `SessionMemoryService.java:34` | 30min |
| 12 | 长期记忆最大轮数 | `LongTermMemoryService.java:39` | 20 |
| 13 | SSE 心跳间隔 | `StreamOrchestrationService.java:44` | 15s |
| 14 | SSE 心跳间隔（重复） | `KnowledgeSearchStreamService.java:80` | 15s |
| 15 | 上下文引用轮数 | `KnowledgeSearchStreamService.java:83` | 5 |

#### 缓存 TTL（2 项）

| # | 配置项 | 当前文件 | 当前值 |
|:--:|--------|----------|:------:|
| 16 | 意图缓存 TTL | `CacheRecognizer.java:27` | 30min |
| 17 | 工具缓存 TTL | `ToolCacheManager.java:34` | 1h |

---

## 二、当前状态分析

### 2.1 AiModelConfig 现状

`AiModelConfig` 已在 `test` 包下实现并继承 `NacosConfig`，但：

- ⚠️ 在 **test 包**（`config/nacos/test/AiModelConfig.java`）
- ⚠️ 只覆盖了 6 个 DAG 任务参数（`maxParallelism`/`stepTimeoutMinutes` 等）
- ❌ 未覆盖 LLM 调用参数（`model`/`temperature`/`max-tokens`）
- ❌ 未覆盖 Embedding 模型参数

### 2.2 AiConfig.java 现状

```
AiConfig.java（infrastructure/config/）
├── ChatClient Bean         ← 使用 @Value 注入 model/temperature/maxTokens
├── ChatModel Bean
└── EmbeddingModel Bean
```

当前 `@Value` 注入无 `@RefreshScope`，修改 YAML 后需重启。

### 2.3 重复硬编码问题

| 硬编码值 | 出现位置 | 
|----------|----------|
| `HEARTBEAT_INTERVAL_MS = 15_000L` | `StreamOrchestrationService.java:44` |
| `HEARTBEAT_INTERVAL_MS = 15_000L` | `KnowledgeSearchStreamService.java:80` |

两处完全相同但独立定义，改一处漏一处。

---

## 三、技术方案设计

### 3.1 AI 模型参数：升级现有 AiModelConfig

#### 步骤 1：从 test 包迁出

```
移动前: infrastructure/.../config/nacos/test/AiModelConfig.java
移动后: infrastructure/.../config/nacos/AiModelConfig.java
```

#### 步骤 2：补全字段

```java
// 原有字段（保留）
public Integer maxParallelism;
public Integer stepTimeoutMinutes;
public String retryStrategy;
public Integer dagMaxDepth;
public Integer dagMaxNodes;
public Integer planningTimeoutSeconds;

// 新增字段 — LLM 调用参数
public String chatModel;           // 原 application.yml 中的 spring.ai.deepseek.chat.options.model
public Double chatTemperature;     // 原 temperature
public Integer chatMaxTokens;      // 原 max-tokens
public String embeddingModel;      // 原 spring.ai.openai.embedding.options.model

// 新增字段 — AI 连接参数
public Integer chatTimeoutSeconds;  // 调用超时（默认 60）
public Integer chatRetryMaxAttempts; // 最大重试次数（默认 3）
```

#### 步骤 3：修改 AiConfig.java

`AiConfig.java` 注入 `AiModelConfig`，在创建 `ChatModel` / `ChatClient` 时使用 Nacos 配置值而非 `@Value`：

```java
// 改造后 — 伪代码示意
@Configuration
public class AiConfig {
    private final AiModelConfig aiModelConfig;

    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        // 从 Nacos 配置读取 model/temperature/maxTokens
        return ChatClient.builder(chatModel)
            .defaultOptions(OpenAiChatOptions.builder()
                .withModel(aiModelConfig.chatModelOrDefault("deepseek-v4-pro"))
                .withTemperature(aiModelConfig.chatTemperatureOrDefault(0.7))
                .withMaxTokens(aiModelConfig.chatMaxTokensOrDefault(4096))
                .build());
    }
}
```

> ⚠️ 注意：`AiModelConfig` 字段是 `public` 且直接访问（通过 `NacosConfig.getConfig()` 返回内部 POJO），需要在便捷方法中处理 null（Nacos 不可用时）。

#### Nacos DataId: `agent-platform-ai-model.json`

```jsonc
{
  "maxParallelism": 5,
  "stepTimeoutMinutes": 10,
  "retryStrategy": "EXPONENTIAL_BACKOFF",
  "dagMaxDepth": 10,
  "dagMaxNodes": 50,
  "planningTimeoutSeconds": 120,
  "chatModel": "deepseek-v4-pro",
  "chatTemperature": 0.7,
  "chatMaxTokens": 4096,
  "embeddingModel": "text-embedding-v3",
  "chatTimeoutSeconds": 60,
  "chatRetryMaxAttempts": 3
}
```

### 3.2 安全参数：新建 SecurityConfig

#### SecurityConfig.java（新建）

```
路径: infrastructure/config/nacos/SecurityConfig.java
继承: NacosConfig<SecurityConfig.SecurityProps>
```

#### Nacos DataId: `agent-platform-security.json`

```jsonc
{
  "maxInputLength": 10000,
  "approvalTimeoutMinutes": 5,
  "presidioTimeoutMs": 5000,
  "corsAllowedOrigins": [
    "http://localhost:*",
    "https://*.agent-platform.local",
    "https://*.agent-platform.com"
  ],
  "corsMaxAgeSeconds": 3600,
  "presidioFallbackToRegex": true
}
```

#### 文件改动

| 文件 | 改动 |
|------|------|
| **新建** `SecurityConfig.java` | 继承 `NacosConfig` |
| `LengthFilter.java` | 注入 `SecurityConfig`，替换 `@Value` |
| `ApprovalWorkflowApplicationService.java` | 注入 `SecurityConfig`，替换 `TIMEOUT_MINUTES` 常量 |
| `CorsConfig.java` | 注入 `SecurityConfig`，域名列表从配置读取 |

#### CorsConfig 改造要点

```java
// 改造后 — 伪代码示意
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final SecurityConfig securityConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = securityConfig.getCorsAllowedOriginsOrDefault();
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origins)
                .maxAge(securityConfig.getCorsMaxAgeOrDefault(3600));
    }
}
```

> ⚠️ CORS 域名变更后需调用方重新发起请求才能体现效果（浏览器缓存预检结果），这是 CORS 协议本身特性，非配置热更新问题。

### 3.3 会话 & 记忆参数：新建 SessionConfig

#### SessionConfig.java（新建）

```
路径: infrastructure/config/nacos/SessionConfig.java
继承: NacosConfig<SessionConfig.SessionProps>
```

#### Nacos DataId: `agent-platform-session.json`

```jsonc
{
  "shortTermMemoryMaxRounds": 20,
  "shortTermMemoryTtlMinutes": 30,
  "longTermMemoryMaxRounds": 20,
  "sseHeartbeatIntervalMs": 15000,
  "contextRounds": 5,
  "intentCacheTtlMinutes": 30,
  "toolCacheTtlMinutes": 60
}
```

#### 文件改动

| 文件 | 改动 |
|------|------|
| **新建** `SessionConfig.java` | 继承 `NacosConfig` |
| `SessionMemoryService.java` | 注入 `SessionConfig`，替换 `MAX_ROUNDS`/`TTL` 常量 |
| `LongTermMemoryService.java` | 注入 `SessionConfig`，替换 `MAX_ROUNDS` 常量 |
| `StreamOrchestrationService.java` | 注入 `SessionConfig`，替换 `HEARTBEAT_INTERVAL_MS` → **消除重复** |
| `KnowledgeSearchStreamService.java` | 注入 `SessionConfig`，替换 `HEARTBEAT_INTERVAL_MS`/`CONTEXT_ROUNDS` → **消除重复** |
| `CacheRecognizer.java` | 注入 `SessionConfig`，替换 `CACHE_TTL` 常量 |
| `ToolCacheManager.java` | 注入 `SessionConfig`，替换 `TTL` 常量 |

---

## 四、Nacos DataId 汇总

本方案涉及 3 个 DataId：

```
AGENT-PLATFORM-CONFIG_ENTITY/
├── agent-platform-ai-model.json      ← 升迁现有 AiModelConfig + 补全字段
├── agent-platform-security.json      ← 新建
└── agent-platform-session.json       ← 新建
```

### 4.1 application.yml fallback 配置

```yaml
# AI 模型 fallback
agent:
  ai:
    chat-model: deepseek-v4-pro
    chat-temperature: 0.7
    chat-max-tokens: 4096
    embedding-model: text-embedding-v3
    chat-timeout-seconds: 60
    chat-retry-max-attempts: 3
  session:
    short-term-memory-max-rounds: 20
    short-term-memory-ttl-minutes: 30
    long-term-memory-max-rounds: 20
    sse-heartbeat-interval-ms: 15000
    context-rounds: 5
    intent-cache-ttl-minutes: 30
    tool-cache-ttl-minutes: 60
```

---

## 五、验证方式

| 验证项 | 方法 | 预期 |
|--------|------|------|
| AiModelConfig 加载 | 启动后检查日志 `[AiModelConfig] 配置加载成功` | 日志打印 Nacos 实际值 |
| 动态切换模型 | Nacos 修改 `chatModel` → 发起对话 | 日志中 ChatModel 使用新模型名 |
| 安全过滤器生效 | Nacos 修改 `maxInputLength` → 发送超长输入 | 被拦截 |
| CORS 域名热更新 | Nacos 添加新域名 → 新域名发起请求 | 预检通过 |
| SSE 心跳消除重复 | 修改 `sseHeartbeatIntervalMs` → 观察两处 SSE 连接 | **两处同时生效**，证明单点配置 |
| Nacos 不可用 | 停 Nacos → 重启 | 使用 YAML fallback 值 |

---

## 六、回滚方案

与子方案 01/02 相同：代码 revert + Nacos 历史版本回滚 + 删除 Nacos 配置降级到 YAML。

---

> 📋 **上一步**: [子方案 02](配置治理-子方案02-RAG检索参数Nacos动态化.md)
> 📋 **下一步**: [子方案 04](配置治理-子方案04-静态常量类统一管理.md)
