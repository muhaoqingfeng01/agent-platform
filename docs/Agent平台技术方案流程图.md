# Agent Platform — 技术方案与流程图

> 生成日期: 2026-06-18
> 项目: 企业级 AI Agent 平台
> 架构: DDD 六模块 (bootstrap → common → domain → application → infrastructure → interfaces)

---

## 目录

1. [系统整体架构](#1-系统整体架构)
2. [请求处理全链路](#2-请求处理全链路)
3. [DDD 分层依赖关系](#3-ddd-分层依赖关系)
4. [意图识别 3 层责任链](#4-意图识别-3-层责任链)
5. [安全围栏 4 层过滤链](#5-安全围栏-4-层过滤链)
6. [RAG 知识库引擎流程](#6-rag-知识库引擎流程)
7. [任务规划与 DAG 执行引擎](#7-任务规划与-dag-执行引擎)
8. [人机协同审批状态机](#8-人机协同审批状态机)
9. [流式对话编排 (SSE/WebSocket)](#9-流式对话编排-ssewebsocket)
10. [全链路可观测性](#10-全链路可观测性)
11. [数据库核心 ER 关系](#11-数据库核心-er-关系)

---

## 1. 系统整体架构

```mermaid
graph TB
    subgraph External["🖥️ 外部客户端"]
        WEB["Web Chat<br/>React + Ant Design"]
        MOBILE["IM 接入<br/>企微/钉钉"]
        API_CLIENT["API Client<br/>Bearer Token"]
    end

    subgraph Intf["🚪 Interfaces 层 | 55文件"]
        AUTH["Sa-Token 认证"]
        FILTER["Filter Chain<br/>Trace → Tenant → CORS"]
        CTRL["20 个 Controller<br/>Tenant/User/Role/Permission<br/>Conv/Message/Intent<br/>Prompt/Knowledge/Document<br/>Tool/Security/Approval<br/>Eval/Optimization"]
        WS["WebSocket Handler<br/>审批卡片推送"]
        SSE["SSE Emitter<br/>流式响应"]
    end

    subgraph App["🧩 Application 层 | 105文件"]
        APP_SVC["19 个 ApplicationService<br/>编排业务逻辑"]
        CHAINS["责任链/策略模式<br/>IntentRecognitionChain<br/>InputFilter Chain<br/>VariableResolver Chain"]
        STREAM["StreamOrchestrationSvc<br/>SSE + Observer 模式"]
    end

    subgraph Domain["📐 Domain 层 | 103文件"]
        ENTITIES["23 聚合根/实体<br/>+ 32 值对象"]
        DOMAIN_SVC["19 DomainService<br/>业务不变量"]
        PORTS["领域端口接口<br/>TextExtractor/Embedding<br/>MilvusStore/VectorSearch<br/>ActionHandler/McpClient"]
    end

    subgraph Infra["🏗️ Infrastructure 层 | 113文件"]
        MYBATIS["23 PO + 23 Mapper<br/>+ 23 RepositoryImpl"]
        MCP["McpClientManager<br/>MCP/HTTP/BUILTIN/CUSTOM"]
        RAG_ENGINE["RAG 引擎<br/>MilvusCollectionManager<br/>6 切片策略<br/>HybridSearch"]
        OBS["可观测性<br/>AgentMetrics + AuditLog<br/>Langfuse HTTP 直连"]
        SEC_ENGINE["安全引擎<br/>Aho-Corasick WordTree<br/>PII Regex 脱敏"]
    end

    subgraph Data["💾 数据层"]
        MySQL[("MySQL 8.0<br/>28 张表")]
        Redis[("Redis<br/>缓存/会话/锁")]
        Milvus[("Milvus<br/>向量存储")]
        MinIO[("MinIO<br/>文档存储")]
    end

    subgraph AI["🤖 AI 服务"]
        DeepSeek["DeepSeek V4<br/>Chat + Embedding"]
        Langfuse["Langfuse<br/>LLM Observability"]
    end

    WEB --> AUTH
    MOBILE --> AUTH
    API_CLIENT --> AUTH
    AUTH --> FILTER --> CTRL
    CTRL --> APP_SVC
    APP_SVC --> CHAINS
    APP_SVC --> STREAM
    APP_SVC --> DOMAIN_SVC
    APP_SVC --> ENTITIES
    APP_SVC -.-> DeepSeek
    DOMAIN_SVC --> PORTS
    PORTS --> MYBATIS
    PORTS --> MCP
    PORTS --> RAG_ENGINE
    MYBATIS --> MySQL
    RAG_ENGINE --> Milvus
    RAG_ENGINE --> MinIO
    MCP --> DeepSeek
    OBS --> Langfuse
    OBS --> MySQL
    SEC_ENGINE --> Redis
    STREAM --> WS
    STREAM --> SSE
    WS --> WEB
    SSE --> WEB

    style External fill:#e1f5fe
    style Intf fill:#fff3e0
    style App fill:#e8f5e9
    style Domain fill:#fce4ec
    style Infra fill:#f3e5f5
    style Data fill:#e0e0e0
    style AI fill:#fff9c4
```

> 🔗 [在 Draw.io 中打开编辑](#)

---

## 2. 请求处理全链路

```mermaid
sequenceDiagram
    actor Client as 客户端
    participant GW as Sa-Token Filter
    participant Trace as TraceFilter
    participant Tenant as TenantInterceptor
    participant Ctrl as Controller
    participant App as ApplicationService
    participant Domain as DomainService
    participant Repo as Repository
    participant DB as MySQL/Redis/Milvus
    participant LLM as DeepSeek
    participant Langfuse as Langfuse

    Client->>GW: HTTP Request (Bearer Token)
    activate GW
    GW->>GW: 校验 Token + 提取 role/permission
    GW->>GW: 检查路由权限
    GW->>Trace: 放行
    deactivate GW

    activate Trace
    Trace->>Trace: 生成 traceId + spanId
    Trace->>Trace: MDC 注入 [traceId,spanId]
    Trace->>Tenant: 放行
    deactivate Trace

    activate Tenant
    Tenant->>Tenant: Session → tenantId
    Tenant->>Tenant: MDC 注入 [tenantId,userId]
    Tenant->>Ctrl: 放行
    deactivate Tenant

    activate Ctrl
    Ctrl->>Ctrl: 参数校验 (DTO @Valid)
    Ctrl->>App: 调用 ApplicationService
    deactivate Ctrl

    activate App
    App->>Domain: 调用 DomainService (业务校验)
    activate Domain
    Domain->>Domain: 校验业务不变量
    Domain-->>App: 校验通过
    deactivate Domain

    App->>Repo: 查询/持久化
    activate Repo
    Repo->>DB: SQL / Redis / Milvus
    DB-->>Repo: 结果
    Repo-->>App: Entity/DTO
    deactivate Repo

    opt LLM 调用
        App->>LLM: Chat / Embedding / Intent
        LLM-->>App: Response
        App->>Langfuse: 异步上报 (trace/span/metrics)
    end

    App-->>Ctrl: Result<T>
    deactivate App

    activate Ctrl
    Ctrl->>Ctrl: 包装统一响应 Result
    Ctrl-->>Client: JSON Response [X-Trace-Id, X-Request-Id]
    deactivate Ctrl
```

---

## 3. DDD 分层依赖关系

```mermaid
graph LR
    subgraph Bootstrap["bootstrap (启动)"]
        APP_CLASS["@SpringBootApplication<br/>@EnableAsync"]
    end

    subgraph Interfaces["interfaces (接口)"]
        CTRL2["Controller"]
        DTO_REQ["Request DTO"]
    end

    subgraph Application["application (应用)"]
        APP_SVC2["ApplicationService"]
        DTO_APP["Application DTO"]
    end

    subgraph Domain["domain (领域)"]
        ENTITY2["Entity / AggregateRoot"]
        VALUE_OBJ["ValueObject"]
        REPO_INTF2["Repository 接口"]
        DOMAIN_SVC2["DomainService"]
        PORT["领域端口 (Port)"]
    end

    subgraph Infra2["infrastructure (基础设施)"]
        REPO_IMPL["RepositoryImpl"]
        PO["PO (MyBatis)"]
        MAPPER["Mapper + XML"]
        ADAPTER["端口适配器<br/>McpClientManager<br/>MilvusClient<br/>TextExtractor"]
        CONFIG["Config / Aspect"]
    end

    subgraph Common["common (共享内核)"]
        RESULT["Result / Exception<br/>PageResponse / IdGenerator"]
    end

    Bootstrap --> Interfaces
    Interfaces --> Application
    Interfaces --> Common
    Application --> Domain
    Application --> Common
    Domain --> Common
    Infra2 --> Domain
    Infra2 --> Common

    CTRL2 -.->|注入| APP_SVC2
    APP_SVC2 -.->|注入| REPO_INTF2
    APP_SVC2 -.->|注入| DOMAIN_SVC2
    REPO_IMPL -.->|实现| REPO_INTF2
    ADAPTER -.->|实现| PORT
    PO -.->|映射| ENTITY2

    style Interfaces fill:#fff3e0
    style Application fill:#e8f5e9
    style Domain fill:#fce4ec
    style Infra2 fill:#f3e5f5
    style Common fill:#e0e0e0
    style Bootstrap fill:#c8e6c9
```

> 🔴 **强制规则**: Controller 绝不直接注入 Repository。依赖方向: interfaces → application → domain ← infrastructure

---

## 4. 意图识别 3 层责任链

```mermaid
flowchart TD
    INPUT["用户输入<br/>'帮我查一下订单状态'"]
    
    INPUT --> CHAIN["IntentRecognitionChain<br/>Chain of Responsibility"]
    
    CHAIN --> L1["Layer 1: RuleRecognizer<br/>🔹 规则识别 (order=1)"]
    L1 --> L1_CHECK{"关键词/正则匹配?<br/>~1ms"}
    L1_CHECK -->|"命中<br/>confidence > 0.8"| RESULT1["IntentResult.matched()<br/>ORDER_QUERY / 0.95"]
    L1_CHECK -->|未命中| L2
    
    L2["Layer 2: CacheRecognizer<br/>🔹 缓存识别 (order=2)"]
    L2 --> L2_CHECK{"Redis 缓存命中?<br/>~5ms / TTL 30min"}
    L2_CHECK -->|命中| RESULT2["IntentResult.matched()<br/>缓存意图 + confidence"]
    L2_CHECK -->|未命中| L3
    
    L3["Layer 3: LLMRecognizer<br/>🔹 LLM 识别 (order=3)"]
    L3 --> L3_PROMPT["构建 Few-shot Prompt<br/>意图列表 + 示例"]
    L3_PROMPT --> LLM_CALL["DeepSeek V4<br/>JSON 结构化输出"]
    LLM_CALL --> L3_PARSE{"解析成功?"}
    L3_PARSE -->|成功| CACHE_STORE["写入 Redis 缓存<br/>TTL 30min"]
    L3_PARSE -->|失败| FALLBACK["降级: IntentResult.unknown()<br/>意图: CHITCHAT"]
    CACHE_STORE --> RESULT3["IntentResult.matched()<br/>LLM 分类结果"]
    
    RESULT1 --> OUTPUT["输出: IntentResult<br/>{intentCode, confidence, params}"]
    RESULT2 --> OUTPUT
    RESULT3 --> OUTPUT
    FALLBACK --> OUTPUT
    
    OUTPUT --> NEXT["→ 对话状态机<br/>→ 参数提取<br/>→ 任务规划 或 RAG检索"]

    style L1 fill:#c8e6c9
    style L2 fill:#fff9c4
    style L3 fill:#ffccbc
    style OUTPUT fill:#b3e5fc
    style FALLBACK fill:#ef9a9a
```

**设计模式**: Chain of Responsibility + Strategy  
**性能**: Rule ~1ms | Cache ~5ms | LLM ~500-2000ms  
**命中率**: Rule 覆盖 80% 高频意图 → Cache 15% → LLM 5% 兜底

---

## 5. 安全围栏 4 层过滤链

```mermaid
flowchart TD
    USER_INPUT["用户输入 / LLM 输出"]
    
    USER_INPUT --> CHAIN_START["InputFilter Chain<br/>按 order() 排序执行"]
    
    CHAIN_START --> F1["🔴 Layer 1: InjectionFilter<br/>order=1 | SQL注入/提示词注入"]
    F1 --> F1_CHECK{"匹配?<br/>6种SQL模式+7种注入模式"}
    F1_CHECK -->|"命中 → BLOCK"| BLOCK["🚫 SecurityBlockedException<br/>HTTP 403<br/>写入 t_security_event"]
    F1_CHECK -->|通过| F2
    
    F2["🟠 Layer 2: JailbreakFilter<br/>order=2 | 越狱检测"]
    F2 --> F2_CHECK{"匹配?<br/>10类越狱特征<br/>DAN/角色扮演/编码绕过"}
    F2_CHECK -->|"命中 → BLOCK"| BLOCK
    F2_CHECK -->|通过| F3
    
    F3["🟡 Layer 3: SensitiveWordFilter<br/>order=3 | 敏感词匹配"]
    F3 --> F3_CACHE["@Scheduled 5min<br/>加载敏感词到 WordTree"]
    F3_CACHE --> F3_MATCH{"Aho-Corasick 匹配?<br/>EXACT / REGEX / SEMANTIC"}
    F3_MATCH -->|"命中 → action判断"| F3_ACTION{"action?"}
    F3_ACTION -->|BLOCK| BLOCK
    F3_ACTION -->|WARN| WARN["⚠️ 记录安全事件<br/>继续执行"]
    F3_ACTION -->|LOG| LOG["📝 仅记录日志<br/>继续执行"]
    F3_MATCH -->|通过| F4
    
    F4["🟢 Layer 4: LengthFilter<br/>order=4 | 长度限制"]
    F4 --> F4_CHECK{"长度 > maxInputLength?"}
    F4_CHECK -->|"超过 → BLOCK"| BLOCK
    F4_CHECK -->|通过| PASS
    
    WARN --> PASS
    LOG --> PASS
    
    PASS["✅ 过滤通过"]
    PASS --> PII["PII 脱敏<br/>6类: 身份证/手机/邮箱<br/>银行卡/固话/IP"]
    PII --> OUTPUT2["安全内容<br/>继续业务处理"]
    
    BLOCK --> AUDIT["SecurityEventRecorder<br/>Observer 模式"]
    AUDIT --> EVENT_LOG[("t_security_event")]

    style F1 fill:#ffcdd2
    style F2 fill:#ffccbc
    style F3 fill:#fff9c4
    style F4 fill:#c8e6c9
    style BLOCK fill:#ef5350,color:#fff
    style PASS fill:#66bb6a,color:#fff
```

**设计模式**: Chain of Responsibility + Observer  
**敏感词匹配**: Hutool WordTree (Aho-Corasick 算法, O(n) 复杂度)  
**PII 脱敏**: 预编译正则 Pattern, 6 类 PII 自动检测

---

## 6. RAG 知识库引擎流程

```mermaid
flowchart TB
    subgraph Upload["📤 文档上传阶段"]
        U1["用户上传文件<br/>PDF/DOCX/TXT/MD/HTML/CSV"]
        U1 --> U2["MinIO 对象存储"]
        U2 --> U3["创建 Document 记录<br/>status=PENDING_PARSE"]
    end

    subgraph Parse["🔧 手动解析阶段"]
        U3 --> P1["用户触发解析<br/>POST /documents/{id}/parse"]
        P1 --> P2["DocumentPipelineOrchestrator<br/>异步管线编排"]
        P2 --> P3["TextExtractor (Tika)<br/>提取原始文本"]
        P3 --> P4{"文件类型?"}
        P4 -->|PDF/DOCX| S1["paragraph_sliding_window"]
        P4 -->|MD/HTML| S2["markdown_header_aware"]
        P4 -->|CSV| S3["fixed_size"]
        P4 -->|TXT| S4["recursive_char_split"]
        P4 -->|未知| S5["sentence_level"]
    end

    subgraph Strategy["🔪 6 种切片策略"]
        S1 --> CHUNK["ChunkStrategyFactory<br/>按 KB 级配置分发"]
        S2 --> CHUNK
        S3 --> CHUNK
        S4 --> CHUNK
        S5 --> CHUNK
        CHUNK --> CHUNKS["生成切片列表<br/>含 token_count + content_hash"]
    end

    subgraph Embed["🧬 向量化阶段"]
        CHUNKS --> EMBED["EmbeddingServiceClient<br/>DeepSeek text-embedding-3-small"]
        EMBED --> VECTOR["768维向量"]
        VECTOR --> MILVUS["MilvusCollectionManager<br/>插入向量 + metadata"]
        MILVUS --> MYSQL_CHUNK["MySQL: t_document_chunk<br/>元数据 + milvus_id"]
        MYSQL_CHUNK --> DONE["status → PARSED ✅"]
    end

    subgraph Search["🔍 混合检索阶段"]
        QUERY["用户查询"] --> HYBRID["HybridSearchApplicationService<br/>RRF 融合"]
        HYBRID --> VEC_SEARCH["向量 ANN 检索<br/>Milvus (IVF_FLAT/HNSW)"]
        HYBRID --> KEYWORD["关键词 BM25 检索<br/>MySQL 全文索引"]
        VEC_SEARCH --> FUSION["RRF 加权融合<br/>vector_weight + keyword_weight"]
        KEYWORD --> FUSION
        FUSION --> RERANK["Reranker<br/>Cross-Encoder/ColBERT/LLM"]
        RERANK --> RESULT4["SearchResultDTO[]<br/>溯源: 文档名/类型/下载链接"]
    end

    subgraph Config["⚙️ 四级精度配置"]
        direction LR
        C1["文档级覆盖<br/>最高优先级"] --> C2["知识库级"]
        C2 --> C3["策略预设<br/>precise/balanced/fast/recall/turbo"]
        C3 --> C4["系统默认<br/>最低优先级"]
    end

    Config -.-> HYBRID

    style Upload fill:#e3f2fd
    style Parse fill:#fff3e0
    style Strategy fill:#f3e5f5
    style Embed fill:#e8f5e9
    style Search fill:#fce4ec
    style Config fill:#fff9c4
```

**检索公式**: `final_score = (vector_rank × vector_weight + keyword_rank × keyword_weight) / (vector_weight + keyword_weight)`  
**索引类型**: IVF_FLAT | IVF_SQ8 | IVF_PQ | HNSW | DISKANN | AUTOINDEX  
**一致性**: STRONG | BOUNDED | EVENTUALLY

---

## 7. 任务规划与 DAG 执行引擎

```mermaid
flowchart TB
    INTENT["复杂意图识别<br/>(如: MULTI_STEP)"]
    
    INTENT --> PLAN["TaskPlanningService<br/>调用 LLM 生成计划"]
    PLAN --> LLM_PROMPT["System Prompt:<br/>'你是任务规划专家...'<br/>+ 可用 ActionHandler 列表"]
    LLM_PROMPT --> LLM_OUT["LLM JSON 输出"]
    LLM_OUT --> JSON["解析 plan_json<br/>[{id,action,input,dep[]}]"]
    
    JSON --> DAG_PARSE["DagParser (DomainService)<br/>DFS 三色标记 + BFS Kahn"]
    DAG_PARSE --> VALID{"DAG 校验<br/>无环? 可达?"}
    VALID -->|失败| ERR["返回错误<br/>重试最多3次"]
    VALID -->|通过| PERSIST["持久化 t_task_execution<br/>status=PENDING"]
    
    PERSIST --> EXEC["DagExecutionService<br/>拓扑排序并行调度"]
    
    EXEC --> TOPO["BFS Kahn 拓扑分层"]
    TOPO --> LAYERS["Layer 0: 无依赖节点<br/>Layer 1: 依赖 Layer 0<br/>Layer 2: ..."]
    
    LAYERS --> PARALLEL["每层节点并行执行<br/>CompletableFuture.allOf()"]
    
    PARALLEL --> HANDLER["ActionHandler.execute()"]
    HANDLER --> HANDLER_TYPE{"处理器类型"}
    HANDLER_TYPE -->|内置| BUILTIN["RetrieveOrdersHandler<br/>SendEmailHandler<br/>CalculateSumHandler"]
    HANDLER_TYPE -->|MCP| MCP_HANDLER["McpClientManager<br/>调用 MCP 工具"]
    HANDLER_TYPE -->|HTTP| HTTP_HANDLER["HttpToolAdapter<br/>REST/API 调用"]
    
    BUILTIN --> CHECK{"执行结果"}
    MCP_HANDLER --> CHECK
    HTTP_HANDLER --> CHECK
    
    CHECK -->|成功| NEXT_STEP["更新 StepStatus=SUCCESS<br/>继续下一层"]
    CHECK -->|"失败 + retry < max"| RETRY["RetryPolicy<br/>指数退避: 1s→2s→4s→...→60s"]
    RETRY --> HANDLER
    CHECK -->|"失败 + retry >= max"| FAIL["StepStatus=FAILED<br/>TaskExecution=FAILED"]
    CHECK -->|"需要审批"| APPROVAL["status=WAITING_APPROVAL<br/>创建审批工单<br/>WebSocket 推送审批卡片"]
    
    NEXT_STEP --> MORE{"还有层?"}
    MORE -->|是| LAYERS
    MORE -->|否| COMPLETE["TaskExecution=COMPLETED<br/>所有步骤完成"]
    
    APPROVAL --> APPROVAL_WAIT["等待审批结果"]
    APPROVAL_WAIT -->|APPROVED| RESUME["DagExecutionService<br/>resumeExecution()"]
    APPROVAL_WAIT -->|REJECTED/TIMEOUT| CANCEL["DagExecutionService<br/>cancelExecution(reason)"]
    RESUME --> HANDLER
    
    style INTENT fill:#b3e5fc
    style DAG_PARSE fill:#c8e6c9
    style PARALLEL fill:#fff9c4
    style COMPLETE fill:#66bb6a,color:#fff
    style FAIL fill:#ef5350,color:#fff
    style APPROVAL fill:#ff9800,color:#fff
```

**关键设计模式**: 
- **Command**: ActionHandler 接口 (6 钩子: preValidate/execute/postProcess/onSuccess/onError/rollback)
- **Mediator**: DagExecutionService 编排所有 Handler
- **Observer**: WebSocket 实时推送步骤进度
- **Template Method**: RetryPolicy 指数退避重试
- **Factory**: ActionHandlerRegistry (InitializingBean 自动注册)

---

## 8. 人机协同审批状态机

```mermaid
stateDiagram-v2
    [*] --> PENDING: 创建工单<br/>createApproval()
    
    state PENDING {
        [*] --> WAITING: WebSocket推送审批卡片
        WAITING --> TIMER: @Scheduled 30s扫描
    }
    
    PENDING --> APPROVED: approver.approve(comment)<br/>→ DagExecutionService<br/>.resumeExecution()
    PENDING --> REJECTED: approver.reject(comment)<br/>→ DagExecutionService<br/>.cancelExecution(reason)
    PENDING --> TIMEOUT: ApprovalTimeoutJob<br/>5分钟超时自动拒绝
    PENDING --> CANCELLED: 请求人取消<br/>requester.cancel()
    
    TIMEOUT --> TIMEOUT_HANDLE: 自动拒绝<br/>+ 推送通知
    
    APPROVED --> [*]: 工单关闭<br/>记录 audit log
    REJECTED --> [*]: 工单关闭
    TIMEOUT --> [*]: 工单关闭
    CANCELLED --> [*]: 工单关闭

    note right of PENDING
        创建时参数:
        - toolId / executionId
        - conversationId
        - params / riskLevel
        - timeout_at = now + 5min
        - requester_id
    end note

    note left of APPROVED
        审批通过后:
        1. 状态 → APPROVED
        2. 回调 DagExecutionService
           .resumeExecution()
        3. WebSocket 推送审批结果
        4. 写入 t_audit_log
    end note
```

**API 端点**:
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/approvals?filter=my-pending` | 待审批列表 |
| GET | `/api/v1/approvals/{id}` | 审批详情(含倒计时) |
| POST | `/api/v1/approvals/{id}/approve` | 同意 |
| POST | `/api/v1/approvals/{id}/reject` | 拒绝 |
| GET | `/api/v1/approvals/stats` | 统计 |

---

## 9. 流式对话编排 (SSE/WebSocket)

```mermaid
sequenceDiagram
    actor User
    participant WS as WebSocket Handler
    participant SSE as SSE Emitter
    participant Stream as StreamOrchestrationSvc
    participant Chain as IntentRecognitionChain
    participant Fence as SecurityFenceAppSvc
    participant Mem as LongTermMemoryService
    participant LLM as DeepSeek Chat
    participant Audit as @Auditable Aspect

    User->>WS: 建立 WebSocket 连接
    WS-->>User: connected (conversation_id)
    
    User->>SSE: POST /api/v1/conversations/{id}/stream
    activate SSE
    SSE->>Stream: streamMessage(convId, userInput)
    activate Stream
    
    Stream->>Fence: filterInput(userInput)
    activate Fence
    Fence->>Fence: 4层过滤链
    Fence-->>Stream: FilterResult.pass()
    deactivate Fence
    
    Stream->>Chain: recognize(input)
    activate Chain
    Chain->>Chain: Rule → Cache → LLM
    Chain-->>Stream: IntentResult
    deactivate Chain
    
    Stream->>Mem: loadContext(convId)
    activate Mem
    Mem->>Mem: Redis 短期记忆 + MySQL 长期记忆
    Mem-->>Stream: ConversationContext
    deactivate Mem
    
    Stream->>LLM: Chat (stream=true)
    activate LLM
    LLM-->>Stream: Flux<String> token stream
    deactivate LLM
    
    loop 每个 token
        Stream->>SSE: SseEmitter.send(token)
        SSE-->>User: data: {"token":"xxx"}
        Stream->>WS: send progress update
        WS-->>User: {"type":"message","status":"streaming"}
    end
    
    Stream->>SSE: SseEmitter.complete()
    SSE-->>User: data: [DONE]
    deactivate SSE
    
    Stream->>Mem: @Async saveMemory()
    Stream->>Audit: @Auditable auditLog
    deactivate Stream
```

---

## 10. 全链路可观测性

```mermaid
flowchart LR
    subgraph MDC["MDC 上下文注入"]
        T1["TraceFilter<br/>traceId + spanId"]
        T2["TenantInterceptor<br/>tenantId + userId"]
        T1 --> T2
    end

    subgraph Metrics["📊 AgentMetrics (Prometheus)"]
        M1["agent.request.total<br/>(Counter)"]
        M2["agent.request.duration<br/>(Timer)"]
        M3["agent.llm.token.count<br/>(Histogram)"]
        M4["agent.stream.event.count<br/>(Counter)"]
        M5["agent.error.total<br/>(Counter)"]
        M6["agent.rag.retrieval.duration<br/>(Timer)"]
    end

    subgraph AuditLog["📝 @Auditable 审计切面"]
        A1["@Around @Auditable<br/>AuditLogAspect"]
        A2["自动采集:<br/>traceId/action/resourceType<br/>request/response/duration"]
        A3["@Async 写入<br/>t_audit_log"]
        A1 --> A2 --> A3
    end

    subgraph Langfuse2["🔍 Langfuse HTTP 直连"]
        L1["LangfuseTraceService<br/>RestTemplate + Basic Auth"]
        L2["异步发送:<br/>trace / span / generation<br/>token 用量 / 模型信息"]
        L1 --> L2
    end

    subgraph Logging["📋 结构化日志 (logback)"]
        LOG1["MDC 格式:<br/>[traceId,spanId,tenantId,userId]"]
        LOG2["按天分目录 + 300MB 滚动<br/>dev/prod 分级"]
        LOG1 --> LOG2
    end

    subgraph Future["🔜 待部署"]
        F1["Grafana Dashboard"]
        F2["OpenTelemetry + Jaeger"]
        F3["Loki/ELK 日志聚合"]
        F4["AlertManager 告警"]
    end

    MDC --> Logging
    MDC --> Metrics
    MDC --> AuditLog
    MDC --> Langfuse2
    Metrics -.-> F1
    Langfuse2 -.-> F2
    Logging -.-> F3
    Metrics -.-> F4

    style MDC fill:#e3f2fd
    style Metrics fill:#e8f5e9
    style AuditLog fill:#fff3e0
    style Langfuse2 fill:#fce4ec
    style Logging fill:#f3e5f5
    style Future fill:#eeeeee,stroke-dasharray:5
```

**10 个 Prometheus 指标**: `agent.request.total`, `agent.request.duration`, `agent.llm.token.total`, `agent.llm.duration`, `agent.stream.event.total`, `agent.stream.duration`, `agent.rag.retrieval.duration`, `agent.error.total`, `agent.tool.invocation.total`, `agent.tool.invocation.duration`

---

## 11. 数据库核心 ER 关系

```mermaid
erDiagram
    T_TENANT ||--o{ T_USER : "tenant_id → BIGINT"
    T_TENANT ||--o{ T_ROLE : "tenant_id"
    T_TENANT ||--o{ T_AGENT_CONFIG : "tenant_id"
    T_TENANT ||--o{ T_KNOWLEDGE_BASE : "tenant_id"
    T_TENANT ||--o{ T_TOOL_REGISTRY : "tenant_id"
    T_TENANT ||--o{ T_INTENT : "tenant_id"
    T_TENANT ||--o{ T_SENSITIVE_WORD : "tenant_id"

    T_USER ||--o{ T_USER_ROLE : "user_id FK"
    T_ROLE ||--o{ T_USER_ROLE : "role_id FK"
    T_ROLE ||--o{ T_ROLE_PERMISSION : "role_id FK"
    T_PERMISSION ||--o{ T_ROLE_PERMISSION : "permission_id FK"

    T_USER ||--o{ T_CONVERSATION : "user_id"
    T_AGENT_CONFIG ||--o{ T_CONVERSATION : "agent_id"
    T_CONVERSATION ||--o{ T_MESSAGE : "conversation_id"
    T_CONVERSATION ||--o{ T_LONG_TERM_MEMORY : "conversation_id"
    T_CONVERSATION ||--o{ T_TASK_EXECUTION : "conversation_id"
    T_CONVERSATION ||--o{ T_APPROVAL_WORKFLOW : "conversation_id"

    T_TASK_EXECUTION ||--o{ T_TASK_STEP_EXECUTION : "execution_id"
    T_TASK_EXECUTION ||--o{ T_APPROVAL_WORKFLOW : "execution_id"
    T_TOOL_REGISTRY ||--o{ T_APPROVAL_WORKFLOW : "tool_id"
    T_TOOL_REGISTRY ||--o{ T_TOOL_INVOCATION_LOG : "tool_id"

    T_KNOWLEDGE_BASE ||--o{ T_DOCUMENT : "knowledge_id"
    T_DOCUMENT ||--o{ T_DOCUMENT_CHUNK : "document_id"
    T_KNOWLEDGE_BASE ||--o{ T_KNOWLEDGE_HIT_RECORD : "chunk_id FK"

    T_CONVERSATION ||--o{ T_KNOWLEDGE_HIT_RECORD : "conversation_id"
    T_CONVERSATION ||--o{ T_SECURITY_EVENT : "conversation_id"
    T_CONVERSATION ||--o{ T_AUDIT_LOG : "conversation_id"
    T_CONVERSATION ||--o{ T_OPTIMIZATION_TICKET : "conversation_id"
    T_CONVERSATION ||--o{ T_EVALUATION_DATASET : "source"

    T_EVALUATION_DATASET ||--o{ T_EVALUATION_DATASET_ITEM : "dataset_id"
    T_EVALUATION_DATASET ||--o{ T_EVALUATION_RUN : "dataset_id"

    T_PROMPT_TEMPLATE ||--o{ T_PROMPT_TEMPLATE_VERSION : "prompt_id"
```

---

## 📊 技术栈速览

| 层次 | 技术 | 说明 |
|------|------|------|
| **语言** | Java 17 | LTS, 虚拟线程就绪 |
| **框架** | Spring Boot 3.3.7 | IOC/MVC/Actuator/DevTools |
| **持久化** | MyBatis 3.0.4 + MyBatis-Plus 3.5.9 | XML SQL + 自动 CRUD + 逻辑删除 |
| **数据库** | MySQL 8.0.33 | 28 张表, 手动版本管理 |
| **缓存** | Redis + Redisson 3.37.0 | 缓存/会话/分布式锁 |
| **向量库** | Milvus 2.6.9 | 6 种索引, 混合检索 |
| **鉴权** | Sa-Token 1.39.0 | RBAC, Bearer Token, 多租户 |
| **AI** | Spring AI 1.1.7 + DeepSeek V4 | Chat + Embedding |
| **文档** | SpringDoc OpenAPI + Knife4j | Swagger UI |
| **日志** | Logback + SLF4J | MDC 全链路追踪 |
| **观测** | Micrometer + Langfuse HTTP | Prometheus + LLM Trace |
| **安全** | BCrypt + Aho-Corasick + Regex | 4 层过滤 + PII 脱敏 |
| **流式** | SSE + WebSocket | 实时消息推送 |
| **构建** | Maven 多模块 | 7 模块 DDD 项目 |

---

## 🔗 模块依赖关系

```
bootstrap
  ├── interfaces ────── Controller + Request DTO + Swagger
  │   └── application ─ ApplicationService + Chain + Strategy
  │       └── domain ── Entity + Repository接口 + DomainService + Port
  │           └── common ─ Result + Exception + PageResponse + IdGenerator
  └── infrastructure ── RepositoryImpl + PO + Mapper + Adapter + Config
      └── domain
          └── common
```

> 📐 **DDD 强制约束**: `interfaces → application → domain ← infrastructure`  
> 🔴 Controller 绝不直接注入 Repository | Application 层不 import interfaces 层
