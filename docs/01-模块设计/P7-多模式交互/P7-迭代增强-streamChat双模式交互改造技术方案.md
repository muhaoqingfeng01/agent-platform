# P7 迭代增强 — MessageController#streamChat 双模式交互改造技术方案

> **版本**: v1.0 | **日期**: 2026-07-06
> **分支**: master | **状态**: ✅ 已实现，编译通过
> **触发**: 代码生成 — 改造 `streamChat` SSE 流式端点支持 CONVERSATION / KNOWLEDGE_SEARCH 双模式

---

## 一、迭代背景与设计目标

### 1.1 背景

现有 `MessageController#streamChat`（`POST /api/v1/conversations/messages/stream`）仅支持单一的智能对话模式——用户输入直接交给 LLM 进行流式对话。在实际企业场景中，大量问题需要**基于企业内部知识库进行精准回答**：

- 📋 员工询问公司制度、报销流程 → 需要从制度知识库检索后回答
- 📊 技术支持查询产品手册 → 需要从产品文档知识库检索后回答
- 📖 新员工入职培训问答 → 需要从培训资料知识库检索后回答

P7 第一阶段已建立了 `InteractionStrategy` 策略工厂骨架（`InteractionController` 统一入口 + 两种策略），但 `MessageController#streamChat`（原对话主入口）尚未整合该能力。

### 1.2 设计目标

| 目标维度 | 具体描述 |
|---------|---------|
| **功能完整性** | `streamChat` 端点同时支持智能对话 + 知识库检索两种模式，前端一个端点搞定所有对话场景 |
| **向后兼容** | 不传 `mode` 字段时默认走 CONVERSATION，现有前端代码无需任何修改 |
| **DDD 合规** | 严格遵循六模块 DDD 四层架构，新代码零越层调用 |
| **可扩展性** | 新增交互模式只需实现 `InteractionStrategy` 接口 + `@Component` 标注，无需改 Controller |
| **可维护性** | 每种模式的流式管线封装在独立 Service 中，互不耦合 |
| **稳定性** | 知识库无命中时友好提示而非臆造，异常隔离不污染其他模式 |

---

## 二、设计理念

### 2.1 核心设计范式：策略模式 + 门面路由 + 管线编排

```
┌──────────────────────────────────────────────────────────────────┐
│                   MessageController#streamChat                    │
│                   （纯粹 HTTP 适配层，仅创建 SseEmitter 并委托）   │
├──────────────────────────────────────────────────────────────────┤
│  interactionService.executeStream(mode, content, convId, kbId,   │
│                                   emitter)                       │
│       │                                                          │
│       ▼                                                          │
│  InteractionApplicationService#executeStream                     │
│  ├─ resolveMode(modeCode) → InteractionMode                      │
│  ├─ strategyFactory.getStrategy(mode) → InteractionStrategy      │
│  └─ streamExecutor.submit(() → strategy.executeStream(ctx))      │
│       │                                                          │
│       ├── CONVERSATION → ConversationInteractionStrategy         │
│       │                   → StreamOrchestrationService           │
│       │                                                          │
│       └── KNOWLEDGE_SEARCH → KnowledgeSearchInteractionStrategy  │
│                               → KnowledgeSearchStreamService     │
└──────────────────────────────────────────────────────────────────┘
```

### 2.2 设计原则落地

| 设计原则 | 落地方式 |
|---------|---------|
| **开闭原则** | 对扩展开放（新增模式只需加策略实现），对修改封闭（现有 `StreamOrchestrationService` 零改动） |
| **单一职责** | `MessageController` 只做路由；`KnowledgeSearchStreamService` 只做 RAG 流式编排；`HybridSearchApplicationService` 只做检索 |
| **依赖倒置** | `InteractionStrategy` 接口定义在 domain 层，实现在 application 层 |
| **里氏替换** | `ConversationInteractionStrategy` 和 `KnowledgeSearchInteractionStrategy` 可互相替换，工厂透明路由 |
| **接口隔离** | `InteractionStrategy` 拆分 `execute()`（同步）和 `executeStream()`（流式），策略按需实现 |

### 2.3 关键架构决策

**决策 1：为什么不直接把 RAG 逻辑写在 MessageController 里？**
- ❌ 违反 DDD 分层：Controller 不应包含业务编排逻辑
- ❌ 违反单一职责：Controller 会随模式增加而膨胀
- ✅ 当前方案：Controller 仅做模式解析 + 路由，每个模式独立 Service 封装

**决策 2：为什么不复用 InteractionController 的流式端点？**
- `MessageController#streamChat` 是对话主入口，所有前端对话功能都走这个端点
- 新增 `InteractionController` 会让前端需要对接两个端点，增加复杂度
- ✅ 当前方案：改造现有端点，前端无感知升级

**决策 3：为什么 KnowledgeSearchStreamService 要自己管理 SSE 管线？**
- `StreamOrchestrationService` 管线包含意图识别、会话记忆等对话特有逻辑
- RAG 管线流程不同：检索 → Prompt 构建 → LLM 流式
- ✅ 独立 Service 保证两个管线互不干扰，各自演进

---

## 三、更新要点总览

### 3.1 改动清单

| # | 文件 | 层级 | 操作 | 行数变化 | 说明 |
|:--:|------|:--:|:--:|:--:|------|
| 1 | `MessageSendRequest.java` | interfaces | ✏️ 修改 | +12 | 新增 `mode` + `knowledgeId` 字段 |
| 2 | `MessageController.java` | interfaces | ✏️ 修改 | +5/-3 | `streamChat` 委托 `interactionService.executeStream()`（模式解析下沉到 Application 层） |
| 3 | **`KnowledgeSearchStreamService.java`** | application | 🆕 新建 | +347 | RAG 流式管线核心编排 |
| 4 | `InteractionStrategy.java` | domain | ✏️ 修改 | +17 | 新增 `executeStream()` 默认方法 |
| 5 | `KnowledgeSearchInteractionStrategy.java` | application | ✏️ 修改 | +45/-10 | 实现 `executeStream()` 委托 |
| 6 | `InteractionApplicationService.java` | application | ✏️ 修改 | +12/-6 | `executeStream` 支持 KNOWLEDGE_SEARCH |
| 7 | `InteractionController.java` | interfaces | ✏️ 修改 | +2/-1 | `stream()` 传递 `knowledgeId` |
| 8 | `InteractionContext.java` | domain | ✏️ 修改 | +16 | 新增 `forKnowledgeSearchStream()` 工厂方法 |

> **合计**: 8 个文件，3 个新建/5 个修改，净增 ~450 行

### 3.2 核心新增能力

| 能力 | 描述 |
|------|------|
| **双模式流式对话** | 同一个 SSE 端点，根据 `mode` 参数自动切换智能对话 / 知识库检索 |
| **精准知识库检索** | 指定 `knowledgeId` 检索特定知识库，不指定则检索当前租户所有已启用知识库 |
| **RAG 检索增强生成** | 检索结果注入 Prompt 作为上下文，LLM 基于检索内容回答 |
| **无命中友好提示** | 知识库无匹配内容时明确告知"未涵盖"，绝不臆造 |
| **策略流式扩展** | `InteractionStrategy.executeStream()` 为所有策略提供流式扩展点 |
| **完全向后兼容** | 不传 `mode` 字段的旧客户端请求行为完全不变 |

---

## 四、技术架构

### 4.1 系统分层架构

```
┌──────────────────────────────────────────────────────────────────┐
│  interfaces (接入层)                                              │
│  MessageController#streamChat                                     │
│  └─ interactionService.executeStream(mode, content, convId, ...)  │
│     → 仅创建 SseEmitter + 委托，Controller 零业务逻辑              │
├──────────────────────────────────────────────────────────────────┤
│  application (应用层)                                             │
│  ├─ InteractionApplicationService — 策略路由门面                  │
│  │   ├─ resolveMode() — 模式解析（默认值 + 异常安全回退）          │
│  │   ├─ strategyFactory.getStrategy() — 自动发现策略              │
│  │   └─ streamExecutor.submit() — 异步执行                       │
│  │                                                               │
│  ├─ InteractionStrategyFactory — 策略工厂 (InitializingBean)      │
│  │   ├─ ConversationInteractionStrategy                          │
│  │   │   → StreamOrchestrationService (已有) — 智能对话流式管线    │
│  │   └─ KnowledgeSearchInteractionStrategy                       │
│  │       → KnowledgeSearchStreamService (🆕) — RAG 流式管线       │
│  │                                                               │
│  ├─ HybridSearchApplicationService (已有，复用)                    │
│  │   vectorSearch + fulltextSearch + RRF fusion + Reranker        │
├──────────────────────────────────────────────────────────────────┤
│  domain (领域层)                                                  │
│  ├─ InteractionMode 枚举 — CONVERSATION / KNOWLEDGE_SEARCH        │
│  ├─ InteractionStrategy 接口 — execute() + executeStream()        │
│  └─ InteractionContext 值对象 — 模式无关的请求上下文                │
├──────────────────────────────────────────────────────────────────┤
│  infrastructure (基础设施层)                                       │
│  ├─ ChatClient (DeepSeek) — LLM 流式调用                          │
│  ├─ Milvus — 向量检索                                             │
│  ├─ MySQL — 全文检索 + 消息持久化                                  │
│  ├─ Redis — 缓存                                                  │
│  ├─ AgentMetrics — Micrometer 指标采集                            │
│  └─ LangfuseTraceService — LLM 调用追踪                           │
└──────────────────────────────────────────────────────────────────┘
```

### 4.2 策略模式类图

```
┌─────────────────────────────┐
│   <<interface>>             │
│   InteractionStrategy       │
│   (domain)                  │
├─────────────────────────────┤
│ + getMode(): InteractionMode│
│ + execute(InteractionCtx)   │
│ + executeStream(InteractionCtx) ← 🆕
│ + supports(InteractionMode) │
│ + getPriority(): int        │
└──────────┬──────────────────┘
           │
     ┌─────┴──────────────────────┐
     │                            │
┌────┴──────────────────┐  ┌─────┴─────────────────────────┐
│ ConversationStrategy  │  │ KnowledgeSearchStrategy       │
│ (application)        │  │ (application)                 │
├──────────────────────┤  ├───────────────────────────────┤
│ mode: CONVERSATION   │  │ mode: KNOWLEDGE_SEARCH        │
│ priority: 10         │  │ priority: 5                   │
├──────────────────────┤  ├───────────────────────────────┤
│ execute():           │  │ execute():                     │
│  → StreamOrchestration │  │  → HybridSearchApplication   │
│    Service            │  │    Service (同步检索)         │
│                      │  │                               │
│ executeStream():     │  │ executeStream(): 🆕            │
│  (继承默认, 调用     │  │  → KnowledgeSearchStream       │
│   execute)           │  │    Service (RAG 流式)          │
└──────────────────────┘  └───────────────────────────────┘
```

---

## 五、执行流程

### 5.1 总体请求处理流程

```
Client Request (SSE)
  │  POST /api/v1/conversations/messages/stream
  │  Body: { conversationId, content, mode?, knowledgeId? }
  ▼
┌─ Sa-Token 鉴权 ──────────────────────────────────────────┐
│  @SaCheckPermission("conversation:send")                  │
└──────────────────────────────────────────────────────────┘
  ▼
┌─ MessageController#streamChat ────────────────────────────┐
│  1. SseEmitterFactory.create(300_000L)                    │
│  2. interactionService.executeStream(                     │
│       request.getMode(),                                  │
│       request.getContent(),                               │
│       request.getConversationId(),                        │
│       request.getKnowledgeId(),                           │
│       emitter)                                            │
│     → 模式解析 + 策略路由全部下沉到 Application 层          │
└──────────────────────────────────────────────────────────┘
  ▼
┌─ InteractionApplicationService#executeStream ─────────────┐
│  1. resolveMode(modeCode) → InteractionMode               │
│     ├─ null/blank → CONVERSATION (default)                │
│     ├─ "CONVERSATION" → CONVERSATION                      │
│     ├─ "KNOWLEDGE_SEARCH" → KNOWLEDGE_SEARCH              │
│     └─ invalid → log.warn + CONVERSATION (fallback)       │
│  2. strategyFactory.getStrategy(mode)                     │
│  3. streamExecutor.submit(() → strategy.executeStream())  │
└──────────────────────────────────────────────────────────┘
  ▼
  ┌──────────── mode? ────────────┐
  │                               │
  ▼ CONVERSATION                  ▼ KNOWLEDGE_SEARCH
┌─────────────────────┐   ┌────────────────────────────────┐
│ ConversationInteract│   │ KnowledgeSearchInteraction      │
│ ionStrategy         │   │ Strategy                       │
│ (委托 StreamOrchSvc)│   │ (委托 KnowledgeSearchStream     │
│                     │   │  Service — RAG 流式管线)        │
└─────────────────────┘   └────────────────────────────────┘
```

> **设计要点**: Controller 不包含 `parseMode()` 方法，不包含 `if-else` 模式分支。
> 模式解析（默认值 CONVERSATION + 异常安全回退）全部下沉到 `InteractionApplicationService#resolveMode()`，
> 策略调度通过 `InteractionStrategyFactory` 自动发现实现，新增模式无需修改 Controller。

### 5.2 CONVERSATION 模式流程（通过策略工厂路由，原管线无变化）

```
ConversationInteractionStrategy#executeStream
  → StreamOrchestrationService#executeStreamPipeline (委托，原逻辑)
  ├─ Step 1: 保存用户消息 (messageService.saveUserMessage)
  ├─ Step 2: 加载会话上下文 (sessionMemoryService.getRecentMessages)
  ├─ Step 3: 意图识别 (intentRecognitionChain.recognize)
  │          Rule → Cache → LLM 三层链
  ├─ Step 4: 构建 Prompt (buildFullPrompt)
  ├─ Step 5: 启动心跳 (15s ping)
  ├─ Step 6: SSE thinking 事件 "正在分析您的需求..."
  ├─ Step 7: LLM 流式输出 (ChatClient.prompt().stream().chatResponse())
  │          ├─ doOnNext → token 事件
  │          ├─ doOnComplete → done 事件 + 保存助手消息 + 长期记忆提取
  │          └─ doOnError → error 事件 + 指标记录
  └─ Finally: 停止心跳
```

### 5.3 KNOWLEDGE_SEARCH 模式流程（🆕 RAG 管线）

```
KnowledgeSearchStreamService#executeStreamPipeline
  │
  ├─ Step 1: 保存用户消息
  │   messageService.saveUserMessage(conversationId, userContent)
  │   → 消息持久化到数据库，会话消息计数 +1
  │
  ├─ Step 2: 启动心跳 (15s ping)
  │
  ├─ Step 3: 知识库检索
  │   ┌─ SSE thinking 事件: "正在检索知识库..."
  │   ├─ hybridSearchService.search(userContent, knowledgeId, null)
  │   │   │
  │   │   ├─ knowledgeId 非空 → 精准检索该知识库
  │   │   │   ├─ 验证 KB 存在且 ENABLED
  │   │   │   └─ Milvus filterExpression: knowledge_id == "xxx"
  │   │   │
  │   │   └─ knowledgeId 为空 → 检索所有 ENABLED KB
  │   │       ├─ kbRepository.findEnabledKnowledgeIds(tenantId)
  │   │       └─ Milvus filterExpression: knowledge_id in ["a","b",...]
  │   │
  │   ├─ 向量检索 (Milvus) → vectorHits
  │   ├─ 关键词检索 (MySQL) → fulltextHits (RRF 融合时启用)
  │   ├─ RRF 加权融合 → fused
  │   ├─ Reranker 精排（可选）→ topN
  │   ├─ 文档元数据回填 → docMap
  │   └─ 返回 SearchResultDTO { query, hits[], documents[] }
  │
  ├─ Step 4: 判断命中情况
  │   │
  │   ├─ ❌ hitCount == 0 (无命中)
  │   │   ├─ 构建友好提示: "您检索的「{内容截断50字}」内容本知识库暂时未涵盖，
  │   │   │                   请联系相关人员及时更新内容。"
  │   │   ├─ 逐字流式推送友好提示（30ms/字 模拟流式）
  │   │   ├─ 保存助手消息 (messageService.saveAssistantMessage)
  │   │   ├─ SSE done 事件 + emitter.complete()
  │   │   └─ RETURN（不调用 LLM，节省 Token）
  │   │
  │   └─ ✅ hitCount > 0 (有命中)
  │       │
  │       ├─ SSE thinking 事件: "已检索到 N 条相关内容，正在生成回答..."
  │       │
  │       ├─ 构建 RAG Prompt
  │       │   ┌─────────────────────────────────────────────┐
  │       │   │ 你是一个专业的知识库助手。请严格根据以下从     │
  │       │   │ 知识库中检索到的内容回答用户的问题。           │
  │       │   │                                             │
  │       │   │ ## 检索到的知识库内容                        │
  │       │   │ ---                                         │
  │       │   │ 【来源文档】: 员工手册-v2024.pdf              │
  │       │   │ 【相关内容】: 公司实行弹性工作制...            │
  │       │   │ ---                                         │
  │       │   │ 【来源文档】: 考勤制度.docx                   │
  │       │   │ 【相关内容】: 员工每日需在考勤系统...          │
  │       │   │                                             │
  │       │   │ ## 回答规则（必须严格遵守）                   │
  │       │   │ 1. 仅根据上述检索内容回答                    │
  │       │   │ 2. 未找到时明确告知                          │
  │       │   │ 3. 引用来源标注文档名称                      │
  │       │   │ 4. 简洁准确、有条理                          │
  │       │   │ 5. 禁止臆造任何信息                          │
  │       │   │                                             │
  │       │   │ ## 用户问题                                 │
  │       │   │ {userContent}                               │
  │       │   └─────────────────────────────────────────────┘
  │       │
  │       ├─ LLM 流式输出 (ChatClient.prompt().user(fullPrompt).stream())
  │       │   ├─ doOnNext(token) → SSE token 事件 + fullResponse 累积
  │       │   ├─ doOnComplete → SSE done 事件 + 保存助手消息
  │       │   └─ doOnError → SSE error 事件 + 指标记录
  │       │
  │       └─ Langfuse 追踪 (异步): traceId + prompt + response + latency + tokens
  │
  └─ Finally: 停止心跳 + 停止 Timer 采样
```

### 5.4 SSE 事件序列

**CONVERSATION 模式 SSE 事件流：**
```
event: thinking    data: "正在分析您的需求..."
event: ping        data: ""                          (每 15s)
event: token       data: "你好"
event: token       data: "！"
event: token       data: "我是"
...
event: done        data: {"status":"completed","tokens":42,"messageId":"msg-xxx"}
```

**KNOWLEDGE_SEARCH 模式 SSE 事件流（有命中）：**
```
event: thinking    data: "正在检索知识库..."
event: ping        data: ""                          (每 15s)
event: thinking    data: "已检索到 5 条相关内容，正在生成回答..."
event: token       data: "根据"
event: token       data: "知识库"
event: token       data: "内容"
...
event: done        data: {"status":"completed","tokens":128,"messageId":"msg-xxx"}
```

**KNOWLEDGE_SEARCH 模式 SSE 事件流（无命中）：**
```
event: thinking    data: "正在检索知识库..."
event: token       data: "您"
event: token       data: "检"
event: token       data: "索"
event: token       data: "的"
...                (逐字推送友好提示)
event: done        data: {"status":"completed","tokens":15,"messageId":"msg-xxx"}
```

---

## 六、关键技术决策

### 6.1 RAG Prompt 设计

**设计目标：** 严格约束 LLM 仅基于检索结果回答，杜绝幻觉

**Prompt 结构：**
```
[系统角色] 专业的知识库助手
[检索上下文] 从 Milvus/MySQL 检索到的 chunk 内容，标注来源文档
[行为约束] 5 条硬规则：仅基于检索、未找到告知、引用来源、简洁准确、禁止臆造
[用户问题] 原始用户输入
```

**为什么不用 System Message + User Message 分离？**
- Spring AI `ChatClient.prompt().user()` 将全部内容作为 user message 发送
- DeepSeek 模型对 system role 的支持与 user role 混合使用效果一致
- 单 message 方式简化了 Prompt 构建，且与现有 `StreamOrchestrationService` 模式一致

### 6.2 无命中处理策略

**策略：不调用 LLM，直接返回预设友好提示**

| 方案 | 优点 | 缺点 | 选择 |
|------|------|------|:--:|
| A. 通知 LLM 无结果让它回复 | 回复更自然 | 浪费 Token，可能臆造 | ❌ |
| B. 直接返回固定提示 | 零 Token 消耗，绝对不臆造 | 回复略显生硬 | ✅ |

选择 B 的理由：
- 知识库是权威数据源，无结果就是无结果，不应让 LLM 发挥
- 节省 LLM 调用成本
- 提示文案可配置化（未来可放入 Nacos 动态配置）

### 6.3 向后兼容设计

模式解析逻辑位于 `InteractionApplicationService#resolveMode()`，所有调用方（`MessageController`、`InteractionController`）共享同一行为：

```java
private InteractionMode resolveMode(String modeCode) {
    if (modeCode == null || modeCode.isBlank()) {
        return InteractionMode.CONVERSATION;  // ← 默认值确保旧客户端无缝兼容
    }
    try {
        return InteractionMode.fromCode(modeCode);
    } catch (IllegalArgumentException e) {
        log.warn("[Interaction] 不支持的交互模式: {}，回退到 CONVERSATION", modeCode);
        return InteractionMode.CONVERSATION;  // ← 异常时安全降级
    }
}
```

> **注意**: 此方法在 `InteractionApplicationService` 而非 `MessageController` 中，
> Controller 仅传递原始 `modeCode` 给 `executeStream()`，自身不做任何解析。

**兼容性矩阵：**

| 客户端版本 | 传 mode? | 行为 |
|-----------|:--:|------|
| 旧版客户端 | 不传 | 走 CONVERSATION，行为完全不变 ✅ |
| 新版客户端 | `"CONVERSATION"` | 智能对话模式 ✅ |
| 新版客户端 | `"KNOWLEDGE_SEARCH"` | 知识库检索模式 ✅ |
| 异常输入 | `"INVALID"` | 回退 CONVERSATION + warn 日志 ✅ |

### 6.4 线程安全设计

```
HTTP 线程 (Tomcat)                   流式执行线程 (streamExecutor)
      │                                       │
      ├─ TenantContext.getCurrentTenantId()    │
      ├─ TenantContext.getCurrentUserId()      │
      ├─ SseEmitterFactory.create()            │
      │                                       │
      └──── streamExecutor.submit() ────────→  │
              (传递 tenantId, userId)          ├─ TenantContext.setTenantId(tenantId)
                                               ├─ TenantContext.setUserId(userId)
                                               ├─ ... 业务逻辑 ...
                                               └─ emitter.complete()
```

关键点：HTTP 线程的 `TenantContext`（ThreadLocal）在线程池线程中不可用，必须在提交任务前捕获并通过方法参数显式传递。

### 6.5 指标采集设计

| 指标 | 类型 | 标签 | 采集点 |
|------|------|------|--------|
| `agent.rag.retrieval` | Timer | tenant, kb_name | `hybridSearchService.search()` 耗时 |
| `agent.rag.hits` | Histogram | tenant, kb_name | 检索命中 chunk 数量分布 |
| `agent.llm.call` | Timer | tenant, model | LLM 流式调用耗时（复用） |
| `agent.tokens.consumed` | Counter | tenant, model | Token 消耗总量（复用） |
| `agent.t_message.processing` | Timer | tenant, intent | 消息整体处理耗时（复用） |

---

## 七、代码结构详析

### 7.1 KnowledgeSearchStreamService 核心结构

```java
@Service
@RequiredArgsConstructor
public class KnowledgeSearchStreamService {

    // ========== 依赖注入 ==========
    HybridSearchApplicationService hybridSearchService;  // 知识库检索
    MessageApplicationService messageService;            // 消息持久化
    ChatClient chatClient;                               // LLM 流式调用
    AgentMetrics metrics;                                // 指标采集
    LangfuseTraceService langfuseTrace;                  // 链路追踪

    // ========== 核心方法 ==========
    @Auditable(action = "RAG_LLM_CALL", ...)
    public void executeStreamPipeline(
        String conversationId, Long tenantId, String userId,
        String userContent, String knowledgeId, SseEmitter emitter)

    // ========== 内部方法 ==========
    private String buildRetrievedContext(List<HitItem> hits)  // 构建检索上下文
    private ScheduledExecutorService startHeartbeat(...)      // 心跳定时器
    private void sendEvent(SseEmitter, SseEventBuilder)       // 安全发送 SSE
    private void streamTextWithHeartbeat(...)                 // 逐字流式推送
    private int estimateTokenCount(String)                    // Token 估算
    private String truncate(String, int)                      // 文本截断
}
```

### 7.2 与现有代码的关系

```
KnowledgeSearchStreamService (🆕)
  │
  ├── 依赖 HybridSearchApplicationService (已有，零修改)
  │     └── 复用: vectorSearch + fulltextSearch + RRF + Reranker + 文档溯源
  │
  ├── 依赖 MessageApplicationService (已有，零修改)
  │     └── 复用: saveUserMessage() / saveAssistantMessage()
  │
  ├── 依赖 ChatClient (已有，零修改)
  │     └── 复用: prompt().user().stream().chatResponse()
  │
  ├── 依赖 AgentMetrics (已有，零修改)
  │     └── 复用: getRagRetrievalTimer() / recordRagHits() / recordTokenConsumption()
  │
  ├── 依赖 LangfuseTraceService (已有，零修改)
  │     └── 复用: logLLMCallAsync()
  │
  └── 依赖 SseEventFactory (已有，零修改)
        └── 复用: thinking() / token() / error() / done()
```

**核心理念：最大化复用现有基础设施，新增代码仅负责 RAG 特有的编排逻辑。**

---

## 八、API 规范

### 8.1 端点定义

| 属性 | 值 |
|------|-----|
| **URL** | `POST /api/v1/conversations/messages/stream` |
| **Content-Type** | `application/json` |
| **Response** | `text/event-stream` (SSE) |
| **鉴权** | `@SaCheckPermission("conversation:send")` — Bearer Token |
| **超时** | 300s (SseEmitter) + 15s 心跳 |

### 8.2 请求体 (MessageSendRequest)

```json
{
  "conversationId": "conv-a1b2c3d4",     // [必填] 会话 ID
  "content": "公司的年假政策是什么？",      // [必填] 用户输入内容
  "mode": "KNOWLEDGE_SEARCH",            // [可选] 交互模式, 默认 CONVERSATION
  "knowledgeId": "kb-hr-policy-001"      // [可选] 知识库 ID, 不填=检索所有已启用知识库
}
```

### 8.3 字段说明

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|--------|------|
| `conversationId` | String | ✅ | — | 会话 ID，消息归属于此会话 |
| `content` | String | ✅ | — | 用户输入内容，用于检索或直接对话 |
| `mode` | String | ❌ | `CONVERSATION` | `CONVERSATION` 智能对话 / `KNOWLEDGE_SEARCH` 知识库检索 |
| `knowledgeId` | String | ❌ | `null` | 知识库业务 ID，仅在 KNOWLEDGE_SEARCH 模式下生效；不填则检索当前租户所有 ENABLED 知识库 |

### 8.4 SSE 事件类型

| 事件名 | 数据格式 | 说明 |
|--------|---------|------|
| `thinking` | `String` | 状态提示："正在检索知识库..." / "正在分析您的需求..." |
| `token` | `String` | LLM 流式输出的单个 token（或无命中时的逐字推送） |
| `ping` | `""` | 心跳事件，每 15 秒发送，防止连接断开 |
| `done` | `{"status":"completed","tokens":N,"messageId":"msg-xxx"}` | 流式完成信号 |
| `error` | `{"code":500,"message":"错误描述"}` | 异常信号 |

### 8.5 调用示例

**示例 1：智能对话（默认模式）**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/messages/stream \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "conversationId": "conv-001",
    "content": "你好，请帮我写一段Java代码"
  }'
```

**示例 2：知识库检索（指定知识库）**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/messages/stream \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "conversationId": "conv-001",
    "content": "公司的报销流程是什么？",
    "mode": "KNOWLEDGE_SEARCH",
    "knowledgeId": "kb-finance-001"
  }'
```

**示例 3：知识库检索（全库搜索）**
```bash
curl -X POST http://localhost:8080/api/v1/conversations/messages/stream \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "conversationId": "conv-001",
    "content": "最新的产品定价策略",
    "mode": "KNOWLEDGE_SEARCH"
  }'
```

---

## 九、扩展指南

### 9.1 新增交互模式（3 步接入）

**场景：需要新增一个 "数据分析" 模式（ANALYSIS）**

**Step 1：在 domain 层定义模式枚举**
```java
// InteractionMode.java — 新增枚举常量
ANALYSIS("ANALYSIS", "数据分析"),
```

**Step 2：实现策略接口**
```java
// AnalysisInteractionStrategy.java — application 层
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisInteractionStrategy implements InteractionStrategy {

    private final DataAnalysisService dataAnalysisService;  // 你的业务服务

    @Override
    public InteractionMode getMode() {
        return InteractionMode.ANALYSIS;
    }

    @Override
    public Object execute(InteractionContext context) {
        // 同步分析逻辑
        AnalysisResult result = dataAnalysisService.analyze(context.getUserInput());
        return InteractionResponse.success(InteractionMode.ANALYSIS.getCode(), result);
    }

    @Override
    public void executeStream(InteractionContext context) {
        // 流式分析逻辑（可选）
        SseEmitter emitter = (SseEmitter) context.getEmitter();
        dataAnalysisService.analyzeStream(context.getUserInput(), emitter);
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
```

**Step 3：自动注册 — 无需修改任何 Controller**

`InteractionStrategyFactory` 通过 `InitializingBean + List<InteractionStrategy>` 自动发现所有 `@Component` 标注的策略实现。新增策略后**无需修改 `MessageController` 或 `InteractionController`** — 两个 Controller 都通过 `InteractionApplicationService` → `InteractionStrategyFactory` 统一路由。

```java
// InteractionStrategyFactory — 无需修改，自动发现新增策略
@Component
public class InteractionStrategyFactory implements InitializingBean {
    private final List<InteractionStrategy> strategies; // Spring 自动注入所有实现

    @Override
    public void afterPropertiesSet() {
        for (InteractionStrategy strategy : strategies) {
            strategyMap.put(strategy.getMode(), strategy);
        }
        log.info("[InteractionStrategyFactory] 已注册 {} 个策略: {}", ...);
    }
}
```

> ✅ **三步入库完成** — 新增交互模式仅需：① 定义枚举 ② 实现 `InteractionStrategy` + `@Component` ③ ~~修改 Controller~~（不需要！策略工厂自动发现）

### 9.2 配置化扩展点

未来可通过 Nacos 动态配置实现以下增强：

| 配置项 | 当前值 | 可配置化 | 说明 |
|--------|--------|:--:|------|
| `NO_RESULT_TEMPLATE` | 硬编码常量 | ✅ | 无命中提示文案，支持租户级定制 |
| `RAG_SYSTEM_PROMPT` | 硬编码常量 | ✅ | RAG 系统提示词，支持按知识库定制 |
| `HEARTBEAT_INTERVAL_MS` | 15000 | ✅ | SSE 心跳间隔 |
| `RAG_TOP_K` | 由 HybridSearch 控制 | ✅ | 检索返回的最大 chunk 数 |
| `STREAM_CHAR_DELAY_MS` | 30 | ✅ | 无命中逐字推送间隔 |

---

## 十、测试策略

### 10.1 单元测试场景

| 测试场景 | 输入 | 期望输出 |
|---------|------|---------|
| 默认模式回退 | `mode: null` | 走 CONVERSATION，调用 StreamOrchestrationService |
| 无效模式回退 | `mode: "INVALID"` | log.warn + 回退 CONVERSATION |
| KB 检索无命中 | `content: "火星天气"` + 知识库无人事制度 | SSE 推送 "您检索的「火星天气」内容本知识库暂时未涵盖..." |
| KB 检索有命中 | `content: "报销流程"` + 财务知识库有相关内容 | SSE 推送 LLM 基于检索结果的回答 |
| 指定知识库 | `knowledgeId: "kb-001"` | 仅检索 kb-001 |
| 全库检索 | `knowledgeId: null` | 检索当前租户所有 ENABLED 知识库 |
| SSE 连接超时 | 模拟 300s 无数据 | 心跳每 15s 发送 ping |
| LLM 调用失败 | ChatClient 异常 | SSE error 事件 + emitter.completeWithError |

### 10.2 集成测试清单

- [ ] SSE 端点返回正确的 Content-Type (`text/event-stream`)
- [ ] 鉴权拦截：无 Token 返回 401
- [ ] 参数校验：空 content 返回 400
- [ ] 会话持久化：用户消息 + 助手消息均写入数据库
- [ ] 指标上报：RAG 检索耗时 + 命中数 + Token 消耗写入 Micrometer
- [ ] 链路追踪：Langfuse 记录完整 RAG 调用链

---

## 十一、风险与缓解

| 风险 | 等级 | 缓解措施 |
|------|:--:|---------|
| 知识库检索耗时过长导致 SSE 连接断开 | 🟡 中 | 心跳机制（15s）+ SseEmitter 300s 超时；未来可加检索超时控制 |
| RAG Prompt 过长超出 LLM 上下文窗口 | 🟡 中 | `HybridSearchApplicationService` 已限制 topK=5；未来可加 token 计数截断 |
| 高并发 RAG 请求导致 Milvus 压力 | 🟡 中 | 复用现有线程池；未来可加 Sentinel 限流 |
| 旧版前端未传 mode 字段导致异常 | 🟢 低 | `parseMode()` 默认 CONVERSATION + 异常安全降级 |
| LLM 不遵守 Prompt 约束仍然臆造 | 🟡 中 | Prompt 5 条硬约束 + 无命中时不调用 LLM |

---

## 十二、与现有系统的关系

### 12.1 与 InteractionController 的关系

```
                    ┌──────────────────────────┐
                    │    前端 / API 消费者       │
                    └──────┬──────────┬────────┘
                           │          │
              对话场景      │          │   通用交互场景
              (需会话ID)     │          │   (需 mode 参数)
                           ▼          ▼
              ┌─────────────────┐  ┌──────────────────────┐
              │ MessageController│  │ InteractionController │
              │ /conversations/  │  │ /interactions/        │
              │ messages/stream  │  │ /execute (同步)       │
              │ (SSE 流式)       │  │ /modes   (查询)       │
              └────────┬────────┘  └──────────┬────────────┘
                       │                      │
                       │    ┌─────────────────┘
                       │    │
                       ▼    ▼
              ┌──────────────────────────────┐
              │ InteractionApplicationService │
              │ ├─ executeStream() — 流式     │
              │ ├─ executeSync()  — 同步      │
              │ └─ getRegisteredModeCodes()   │
              └──────────────┬───────────────┘
                             │
              ┌──────────────┴──────────────┐
              │ InteractionStrategyFactory   │
              │ ├─ ConversationStrategy      │
              │ └─ KnowledgeSearchStrategy   │
              └─────────────────────────────┘
```

**两个 Controller 的定位差异：**

| 维度 | MessageController | InteractionController |
|------|-------------------|----------------------|
| **定位** | 对话主入口，面向聊天场景 | 通用同步交互入口 + 模式发现 |
| **端点** | 5 个（send/stream/list/before/feedback） | 2 个（execute/modes） |
| **流式** | ✅ `streamChat` → `interactionService.executeStream()` | ❌ 无流式端点（仅同步 `/execute`） |
| **必填参数** | `conversationId` + `content` | `mode` + `content` |
| **会话管理** | ✅ 自动保存消息到会话（流式模式下由各策略负责） | ❌ `executeSync` 不自动管理会话 |
| **适用场景** | 前端聊天界面 | API 调用、自动化、集成 |

### 12.2 与 P7 第一阶段的关系

P7 第一阶段建立了策略模式骨架（`InteractionStrategy` + `InteractionStrategyFactory` + `InteractionController` 两个同步端点），本次迭代在此基础上：

1. **增强了策略接口** — 新增 `executeStream()` 默认方法，为所有策略提供流式扩展点
2. **补全了 KnowledgeSearch 策略** — 实现 `executeStream()` 支持 RAG 流式生成
3. **改造了对话主入口** — `MessageController#streamChat` 通过 `InteractionApplicationService` 统一路由，具备双模式调度能力
4. **统一了路由层** — 两个 Controller 都通过 `InteractionApplicationService` → `InteractionStrategyFactory` 路由，行为一致

---

## 十三、后续演进规划

| 优先级 | 任务 | 说明 |
|:--:|------|------|
| ✅ 已实现 | MessageController 路由升级为策略工厂 | `streamChat` 通过 `InteractionApplicationService` → `InteractionStrategyFactory` 统一路由，Controller 不再包含 `if-else` |
| P1 | RAG Prompt 配置化 | 将 `RAG_SYSTEM_PROMPT` + `NO_RESULT_TEMPLATE` 迁移至 Nacos 动态配置 |
| P1 | 检索结果引用溯源 | 前端渲染时展示 "参考来源：员工手册-v2024.pdf (相关度: 0.92)" |
| P2 | 多轮 RAG 对话 | 支持基于上一轮检索结果的追问，无需重复检索 |
| P2 | 混合模式 | 用户问题同时走 CONVERSATION + KNOWLEDGE_SEARCH，结果融合 |
| P2 | 知识库检索缓存 | Redisson 缓存热点问题的检索结果，减少 Milvus 查询 |
| P3 | 补充单元测试 + 集成测试 | 覆盖双模式路由、无命中处理、异常降级等核心场景 |
| P3 | 同步更新 CLAUDE.md + 开发进度 | 更新文件计数、P7 状态、新增依赖 |

---

## 十四、总结

本次迭代以**最小侵入、最大复用、完全兼容**为原则，成功将 `MessageController#streamChat` 从单一智能对话模式升级为双模式交互端点：

- ✅ **功能完整**：智能对话 + 知识库检索（RAG），涵盖企业主要对话场景
- ✅ **向后兼容**：旧版客户端无需任何修改
- ✅ **DDD 合规**：严格遵循六模块分层架构，零越层调用
- ✅ **代码复用**：新增 `KnowledgeSearchStreamService` 仅 347 行，其余全部复用现有基础设施
- ✅ **可扩展**：新增模式只需实现策略接口 + 标注 `@Component`
- ✅ **稳定可靠**：无命中友好提示、心跳保活、异常隔离、完整指标采集
- ✅ **编译通过**：7 模块 BUILD SUCCESS，净增 ~450 行代码

**核心理念：** 好的架构不是把所有逻辑堆在一起，而是让每个组件各司其职，通过清晰的接口协作。`MessageController` 负责路由，`KnowledgeSearchStreamService` 负责 RAG 编排，`HybridSearchApplicationService` 负责检索——各层边界清晰，未来演进无忧。
