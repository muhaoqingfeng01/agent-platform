# Agent Platform — 架构设计图

> 生成日期: 2026-06-18
> 对应文档: `docs/Agent平台技术方案流程图.md`

---

## 目录

1. [4+1 架构视图](#1-41-架构视图)
2. [部署架构 (物理视图)](#2-部署架构-物理视图)
3. [DDD 分层架构 (逻辑视图)](#3-ddd-分层架构-逻辑视图)
4. [模块包结构 (开发视图)](#4-模块包结构-开发视图)
5. [核心组件交互 (进程视图)](#5-核心组件交互-进程视图)
6. [多租户数据隔离架构](#6-多租户数据隔离架构)
7. [安全架构全景](#7-安全架构全景)
8. [MCP 工具平台架构](#8-mcp-工具平台架构)
9. [提示词管理架构](#9-提示词管理架构)
10. [效果评估闭环架构](#10-效果评估闭环架构)

---

## 1. 4+1 架构视图

```mermaid
graph TB
    subgraph Logical["📐 逻辑视图 (DDD Layers)"]
        L1["Interfaces<br/>Controller + Filter"]
        L2["Application<br/>ApplicationService + Chain"]
        L3["Domain<br/>Entity + DomainService + Port"]
        L4["Infrastructure<br/>RepositoryImpl + Adapter"]
        L1 --> L2 --> L3
        L4 --> L3
    end

    subgraph Process["⚙️ 进程视图 (Runtime)"]
        P1["HTTP Request<br/>→ Filter Chain"]
        P2["ApplicationService<br/>编排业务"]
        P3["DomainService<br/>业务规则"]
        P4["Repository<br/>数据访问"]
        P5["External<br/>LLM/Milvus/Redis"]
        P1 --> P2 --> P3 --> P4 --> P5
    end

    subgraph Physical["🖥️ 物理视图 (Deployment)"]
        H1["App Server<br/>Spring Boot JAR"]
        H2["MySQL<br/>:3306"]
        H3["Redis<br/>:6379"]
        H4["Milvus<br/>:19530"]
        H5["MinIO<br/>:9000"]
        H1 --- H2
        H1 --- H3
        H1 --- H4
        H1 --- H5
    end

    subgraph Development["💻 开发视图 (Modules)"]
        D1["bootstrap"]
        D2["common"]
        D3["domain"]
        D4["application"]
        D5["infrastructure"]
        D6["interfaces"]
        D1 --> D6 --> D4 --> D3 --> D2
        D5 --> D3
    end

    subgraph Scenario["📖 场景视图 (Use Cases)"]
        S1["智能对话<br/>流式 SSE"]
        S2["知识检索<br/>RAG 混合搜索"]
        S3["任务编排<br/>DAG 并行执行"]
        S4["安全审查<br/>4层过滤"]
        S5["人机审批<br/>状态机流转"]
    end

    style Logical fill:#e8f5e9
    style Process fill:#fff3e0
    style Physical fill:#e3f2fd
    style Development fill:#fce4ec
    style Scenario fill:#f3e5f5
```

---

## 2. 部署架构 (物理视图)

```mermaid
graph TB
    subgraph DMZ["🌐 DMZ / CDN"]
        CDN["CDN / Nginx<br/>静态资源 + SSL"]
    end

    subgraph AppTier["🖥️ 应用层 (可横向扩展)"]
        subgraph App1["App Instance 1"]
            BOOT1["Spring Boot 3.3.7<br/>JAR / Docker<br/>:8080"]
        end
        subgraph App2["App Instance 2"]
            BOOT2["Spring Boot 3.3.7<br/>JAR / Docker<br/>:8080"]
        end
        subgraph AppN["App Instance N"]
            BOOTN["Spring Boot 3.3.7<br/>JAR / Docker<br/>:8080"]
        end
    end

    subgraph DataTier["💾 数据层"]
        MySQL_MASTER[("MySQL 8.0<br/>Master<br/>读写")]
        MySQL_SLAVE[("MySQL 8.0<br/>Slave<br/>只读")]
        RedisCluster[("Redis<br/>主从 + Sentinel<br/>缓存/会话/锁")]
        MilvusCluster[("Milvus<br/>Standalone/Cluster<br/>向量检索")]
        MinIOCluster[("MinIO<br/>对象存储<br/>S3 兼容")]
    end

    subgraph Observability["📊 可观测性栈"]
        Prometheus["Prometheus<br/>指标采集 :9090"]
        Grafana["Grafana<br/>可视化 :3000"]
        Jaeger["Jaeger/Tempo<br/>分布式追踪"]
        ELK["Loki/ELK<br/>日志聚合"]
        Langfuse2["Langfuse<br/>LLM 观测"]
    end

    subgraph External2["🌍 外部服务"]
        DeepSeekAPI["DeepSeek API<br/>Chat + Embedding"]
        LDAP["LDAP/SSO<br/>企业认证"]
        Presidio["Presidio<br/>PII 增强脱敏"]
    end

    CDN --> App1
    CDN --> App2
    CDN --> AppN

    BOOT1 --> MySQL_MASTER
    BOOT1 --> RedisCluster
    BOOT1 --> MilvusCluster
    BOOT1 --> MinIOCluster
    BOOT2 --> MySQL_MASTER
    BOOT2 --> RedisCluster
    BOOT2 --> MilvusCluster
    BOOTN --> MySQL_MASTER
    BOOTN --> RedisCluster
    BOOTN --> MilvusCluster
    
    MySQL_MASTER -.->|主从复制| MySQL_SLAVE
    
    BOOT1 -.->|Micrometer| Prometheus
    BOOT2 -.->|Micrometer| Prometheus
    BOOTN -.->|Micrometer| Prometheus
    Prometheus --> Grafana
    
    BOOT1 -.->|OTel| Jaeger
    BOOT1 -.->|HTTP| Langfuse2
    BOOT1 -.->|日志| ELK
    
    BOOT1 -.->|API Key| DeepSeekAPI
    BOOT1 -.->|LDAP协议| LDAP
    BOOT1 -.->|HTTP| Presidio

    style AppTier fill:#e8f5e9
    style DataTier fill:#e3f2fd
    style Observability fill:#fff3e0
    style External2 fill:#fce4ec
```

**端口规划**:
| 服务 | 端口 | 说明 |
|------|:--:|------|
| App | 8080 | Spring Boot |
| Prometheus | 9090 | 指标采集 |
| Grafana | 3000 | 可视化 |
| MySQL | 3306 | 主库 |
| Redis | 6379 | 缓存 |
| Milvus | 19530 | 向量库 |
| MinIO | 9000/9001 | 存储/控制台 |
| Langfuse | 3000 | LLM 观测 |

---

## 3. DDD 分层架构 (逻辑视图)

```mermaid
graph TB
    subgraph Top[" "]
        BOOT["🎯 bootstrap<br/>@SpringBootApplication<br/>@EnableAsync<br/>@ComponentScan"]
    end

    subgraph Layer4["🔵 Interfaces 层 — 用户接口"]
        direction LR
        REST["REST Controllers<br/>20 个 @RestController"]
        REQ_DTO["Request DTOs<br/>28 个 @Validated"]
        FILTERS["Filters/Interceptors<br/>Sa-Token / Trace / Tenant / CORS"]
        WS_SSE["WebSocket / SSE<br/>实时消息推送"]
        SWAGGER2["Swagger / Knife4j<br/>API 文档"]
        EX_HANDLER["GlobalExceptionHandler<br/>@ControllerAdvice"]
    end

    subgraph Layer3["🟢 Application 层 — 应用编排"]
        direction LR
        APP_SVCS["ApplicationServices<br/>19 个 @Service"]
        DTO2["Application DTOs<br/>Request/Response/VO"]
        CHAINS2["责任链<br/>IntentRecognitionChain<br/>InputFilterChain"]
        STRATEGY2["策略模式<br/>ChunkStrategy x6<br/>MemoryExtractor x5<br/>VariableResolver x4"]
        EVENTS["领域事件<br/>ConversationCreatedEvent<br/>StatusChangedEvent<br/>MessageFeedbackEvent"]
        ORCH["编排器<br/>StreamOrchestrationSvc<br/>DocumentPipelineOrch<br/>DagExecutionSvc"]
    end

    subgraph Layer2["🔴 Domain 层 — 业务核心"]
        direction LR
        ENTITIES2["聚合根 + 实体<br/>23 个 @Data @Builder"]
        VALUE_OBJS["值对象<br/>32 个 Enum / Record"]
        DOMAIN_SVCS["DomainServices<br/>19 个 @Service<br/>业务不变量"]
        REPO_PORTS["仓储端口<br/>23 个 Interface<br/>DDD Port"]
        EXT_PORTS["外部端口<br/>10 个 Interface<br/>TextExtractor/Embedding<br/>MilvusStore/VectorSearch<br/>ActionHandler/McpClient"]
    end

    subgraph Layer1["🟣 Infrastructure 层 — 技术实现"]
        direction LR
        PO2["持久化对象<br/>23 个 PO<br/>@TableName + @TableLogic"]
        MAPPER2["MyBatis 映射<br/>23 Mapper + XML<br/>RepositoryImpl"]
        ADAPTERS["端口适配器<br/>McpClientManager<br/>HttpToolAdapter<br/>MilvusCollectionManager<br/>HybridSearchProviders"]
        CONFIGS["配置<br/>13 个 @Configuration<br/>线程池/安全/AI/观测"]
        ASPECTS["切面<br/>@Auditable Aspect<br/>@RateLimit Aspect"]
        METRICS2["指标<br/>AgentMetrics<br/>LangfuseTraceService"]
        CACHE2["缓存<br/>Redisson<br/>WordTree Cache"]
    end

    subgraph Foundation["⚪ Common 层 — 共享内核"]
        RESULT2["Result<T><br/>统一响应体"]
        EXCEPTIONS["6 异常类<br/>Auth/Business/Dup<br/>NotFound/Tenant/Blocked"]
        PAGE["PageResponse<br/>通用分页"]
        IDGEN["IdGenerator<br/>雪花+业务前缀"]
    end

    BOOT --> Layer4
    Layer4 -->|"调用"| Layer3
    Layer3 -->|"调用"| Layer2
    Layer1 -->|"实现"| Layer2
    Layer2 --> Foundation
    Layer3 --> Foundation
    Layer1 --> Foundation

    style Top fill:#c8e6c9
    style Layer4 fill:#bbdefb
    style Layer3 fill:#c8e6c9
    style Layer2 fill:#ffcdd2
    style Layer1 fill:#e1bee7
    style Foundation fill:#eeeeee
```

**依赖方向 (强制)**:
```
interfaces → application → domain ← infrastructure
                              ↑
                           common
```

**禁止事项**:
- ❌ Controller 直接注入 Repository
- ❌ Application 层 import interfaces 层
- ❌ DomainService 泄漏到 Application 层
- ❌ Domain 层依赖 Infrastructure 层具体实现

---

## 4. 模块包结构 (开发视图)

```mermaid
graph LR
    subgraph Pkg["com.example.agent 包结构"]
        direction TB
        
        subgraph Bootstrap2["bootstrap"]
            APP_MAIN["AgentPlatformApplication.java"]
        end
        
        subgraph Common2["common (9)"]
            C1["Result.java"]
            C2["6 Exceptions"]
            C3["PageResponse"]
            C4["IdGenerator"]
        end
        
        subgraph Domain2["domain (103)"]
            D_DIR["{module}/"]
            D_ENT["model/entity/"]
            D_VO["model/valueobject/"]
            D_REPO["repository/ (interface)"]
            D_SVC["service/ (DomainService)"]
            D_PORT["port/ (interface)"]
        end
        
        subgraph App2["application (105)"]
            A_DIR["{module}/"]
            A_SVC["*ApplicationService.java"]
            A_DTO["dto/ (Req/Resp/VO)"]
            A_CHAIN["recognizer/ handler/"]
            A_STRATEGY["strategy/ resolver/"]
            A_EVENT["event/"]
        end
        
        subgraph InfraPkg["infrastructure (113)"]
            I_PERSIST["persistence/po/"]
            I_MAPPER["persistence/mapper/"]
            I_IMPL["persistence/impl/"]
            I_ADAPTER["adapter/"]
            I_CONFIG["config/"]
            I_ASPECT["aspect/"]
            I_OBS["observability/"]
        end
        
        subgraph IntfPkg["interfaces (55)"]
            IF_CTRL["controller/"]
            IF_DTO["dto/request/"]
            IF_CONFIG["config/"]
            IF_WS["websocket/"]
        end
    end

    style Bootstrap2 fill:#c8e6c9
    style Common2 fill:#eeeeee
    style Domain2 fill:#ffcdd2
    style App2 fill:#c8e6c9
    style InfraPkg fill:#e1bee7
    style IntfPkg fill:#bbdefb
```

**包命名约定**: 每个业务模块在 domain/application/infrastructure/interfaces 中保持一致的子包名:
```
{tenant, user, role, permission, conversation, message,
 intent, prompt/knowledge, document, tool, security,
 approval, evaluation, optimization, agent}
```

---

## 5. 核心组件交互 (进程视图)

```mermaid
C4Context
    title Agent Platform — 核心组件交互

    Person(user, "用户", "Web/IM/API")
    
    System_Boundary(platform, "Agent Platform") {
        Container(gateway, "Gateway Layer", "Spring Boot Filter", "认证/限流/追踪/租户")
        Container(chat, "Chat Engine", "StreamOrchestrationSvc", "流式对话 + SSE/WebSocket")
        Container(intent, "Intent Recognition", "3-Layer Chain", "Rule → Cache → LLM")
        Container(rag, "RAG Engine", "HybridSearchAppSvc", "向量 + 关键词 + RRF")
        Container(task, "Task Engine", "DagExecutionSvc", "DAG 解析 + 并行调度")
        Container(security, "Security Fence", "4-Layer FilterChain", "注入/越狱/敏感词/长度")
        Container(approval, "Approval System", "StateMachine", "人机协同审批")
        Container(prompt, "Prompt Manager", "PromptAppSvc", "版本控制 + 变量渲染")
        Container(mcp, "MCP Platform", "McpClientManager", "工具注册/发现/调用")
    }
    
    System_Ext(llm, "DeepSeek V4", "Chat + Embedding")
    System_Ext(mysql, "MySQL", "28 Tables")
    System_Ext(redis, "Redis", "Cache/Session/Lock")
    System_Ext(milvus, "Milvus", "Vector Store")
    System_Ext(minio, "MinIO", "Object Storage")
    
    Rel(user, gateway, "HTTPS/WS", "Bearer Token")
    Rel(gateway, chat, "路由")
    Rel(gateway, security, "输入过滤")
    Rel(chat, intent, "意图识别")
    Rel(chat, rag, "知识检索")
    Rel(chat, task, "任务调度")
    Rel(chat, approval, "审批流转")
    Rel(chat, prompt, "模板渲染")
    Rel(task, mcp, "工具调用")
    Rel(rag, mcp, "MCP工具检索")
    
    Rel(chat, llm, "Chat API")
    Rel(rag, llm, "Embedding API")
    Rel(intent, llm, "分类兜底")
    
    Rel(gateway, mysql, "读写")
    Rel(gateway, redis, "缓存")
    Rel(rag, milvus, "向量检索")
    Rel(rag, minio, "文档存储")
```

---

## 6. 多租户数据隔离架构

```mermaid
flowchart TB
    subgraph Request2["请求入口"]
        REQ["HTTP Request<br/>Header: Authorization: Bearer {token}"]
    end

    subgraph Auth["认证鉴权层"]
        SA_TOKEN["Sa-Token<br/>解析 Token → Session"]
        SA_TOKEN --> SESSION["Session 数据:<br/>userId / tenantId<br/>roleCodes / permissions"]
    end

    subgraph TenantCtx["租户上下文注入"]
        INTERCEPTOR["TenantInterceptor<br/>preHandle()"]
        INTERCEPTOR --> MDC_INJECT["MDC.put('tenantId', ...)<br/>MDC.put('userId', ...)"]
        MDC_INJECT --> THREAD_LOCAL["TenantContext.set(tenantId)<br/>ThreadLocal 透传"]
    end

    subgraph SQLInjection["SQL 自动注入"]
        SQL_PLUGIN["TenantSqlInterceptor<br/>MyBatis Plugin<br/>intercept(Invocation)"]
        SQL_PLUGIN --> REWRITE["自动修改 SQL:<br/>WHERE 条件追加<br/>'AND tenant_id = ?'"]
        REWRITE --> BOUND_SQL["BoundSql:<br/>SELECT * FROM t_user<br/>WHERE tenant_id = ?<br/>AND username = ?"]
    end

    subgraph DataIsolation["数据隔离策略"]
        direction LR
        DB1[("t_tenant = 1000001<br/>租户A 数据")]
        DB2[("t_tenant = 1000002<br/>租户B 数据")]
        DB3[("t_tenant = 1000003<br/>租户C 数据")]
        ISOLATION["同库同表<br/>tenant_id 列隔离<br/>逻辑删除 (deleted=0/1)"]
    end

    subgraph Validation["权限校验"]
        TENANT_VALID["TenantPermissionValidator<br/>校验当前用户<br/>是否有权访问该租户数据"]
        ROLE_VALID["@SaCheckPermission('tool:create')<br/>角色权限校验"]
    end

    REQ --> SA_TOKEN
    THREAD_LOCAL --> SQL_PLUGIN
    THREAD_LOCAL --> TENANT_VALID
    STREAM["流式线程池<br/>@Async 场景"] -.->|"手动透传<br/>TenantContext"| THREAD_LOCAL
    
    style Request2 fill:#e3f2fd
    style Auth fill:#fff3e0
    style TenantCtx fill:#e8f5e9
    style SQLInjection fill:#fce4ec
    style DataIsolation fill:#f3e5f5
    style Validation fill:#ffe0b2
```

**多租户隔离关键点**:
1. **认证层**: Sa-Token 从 Token 解析 tenantId → Session
2. **上下文层**: TenantInterceptor → MDC + ThreadLocal 透传
3. **SQL 层**: MyBatis Plugin 自动注入 `WHERE tenant_id = ?` (防漏写)
4. **校验层**: TenantPermissionValidator 防跨租户访问
5. **异步场景**: 流式线程池需手动从父线程获取 TenantContext

---

## 7. 安全架构全景

```mermaid
flowchart TB
    subgraph Input["📥 输入安全"]
        direction LR
        I1["SQL 注入检测<br/>6 种模式"]
        I2["提示词注入检测<br/>7 种模式"]
        I3["越狱检测<br/>10 类特征"]
        I4["敏感词过滤<br/>Aho-Corasick"]
        I5["长度限制<br/>maxInputLength"]
        I6["PII 脱敏<br/>6 种正则"]
    end

    subgraph Processing["⚙️ 处理安全"]
        direction LR
        P_AUTH["身份认证<br/>Sa-Token + BCrypt"]
        P_RBAC["RBAC 鉴权<br/>角色+权限码"]
        P_TENANT2["租户隔离<br/>SQL 自动注入"]
        P_APPROVAL2["审批控制<br/>高风险操作拦截"]
    end

    subgraph Output["📤 输出安全"]
        direction LR
        O1["输出脱敏<br/>PII 遮蔽"]
        O2["内容过滤<br/>敏感信息检测"]
        O3["审计日志<br/>@Auditable 自动记录"]
    end

    subgraph Infrastructure2["🛡️ 基础设施安全"]
        direction LR
        N1["CORS 精确控制<br/>白名单域名"]
        N2["Rate Limit<br/>Sentinel 限流熔断"]
        N3["BCrypt 密码哈希<br/>不可逆加密"]
        N4["Token 过期控制<br/>1h有效期+30min续期"]
    end

    subgraph Monitor["📊 安全监控"]
        direction LR
        M_EVENT["SecurityEvent<br/>事件追溯"]
        M_AUDIT["AuditLog<br/>审计链完整"]
        M_ALERT["高危告警<br/>(待部署 AlertManager)"]
    end

    Input --> Processing --> Output
    Infrastructure2 --> Input
    Infrastructure2 --> Processing
    Output --> Monitor

    style Input fill:#ffcdd2
    style Processing fill:#fff9c4
    style Output fill:#c8e6c9
    style Infrastructure2 fill:#bbdefb
    style Monitor fill:#e1bee7
```

---

## 8. MCP 工具平台架构

```mermaid
flowchart TB
    subgraph ToolType["4 种工具类型"]
        T1["MCP<br/>标准 Model Context Protocol"]
        T2["HTTP<br/>REST API 包装"]
        T3["BUILTIN<br/>内置 ActionHandler"]
        T4["CUSTOM<br/>自定义扩展"]
    end

    subgraph Registry["注册中心"]
        DB[("t_tool_registry<br/>MySQL 持久化")]
        CACHE3["ConcurrentHashMap<br/>内存缓存"]
        SCHEDULER["@Scheduled 5min<br/>自动刷新缓存"]
    end

    subgraph Manager["McpClientManager"]
        INIT["@PostConstruct<br/>InitializingBean.afterPropertiesSet()"]
        CONNECT["异步连接所有<br/>MCP 类型工具"]
        HEALTH["心跳检测<br/>(P6 待实现)"]
        POOL["连接池管理<br/>线程安全"]
        INIT --> CONNECT
    end

    subgraph Adapter["HttpToolAdapter"]
        REST_CLIENT["RestClient<br/>Spring 6 同步 HTTP"]
        AUTH_INJECT["认证注入<br/>API_KEY/BEARER<br/>BASIC/NONE<br/>自定义 Headers"]
        TIMEOUT["超时控制<br/>连接+读取超时"]
    end

    subgraph Invoke["工具调用"]
        TEST["测试调用<br/>POST /tools/{id}/test"]
        DAG_CALL["DAG 执行调用<br/>DagExecutionService"]
        LLM_CALL2["LLM Function Calling<br/>(未来)"]
    end

    subgraph Version["版本管理 (P6)"]
        V_TABLE[("t_tool_registry_version<br/>变更历史")]
        V_ROLLBACK["版本回滚<br/>任意版本恢复"]
    end

    DB --> CACHE3
    SCHEDULER --> CACHE3
    T1 --> CONNECT
    T2 --> REST_CLIENT
    T3 --> DAG_CALL
    TEST --> Manager
    TEST --> Adapter
    DAG_CALL --> Manager
    Manager -.-> HEALTH

    style ToolType fill:#e8f5e9
    style Registry fill:#fff3e0
    style Manager fill:#bbdefb
    style Adapter fill:#f3e5f5
    style Invoke fill:#fce4ec
    style Version fill:#eeeeee
```

---

## 9. 提示词管理架构

```mermaid
flowchart TB
    subgraph CRUD["CRUD 管理"]
        CREATE["创建模板<br/>POST /prompts"]
        EDIT["编辑模板<br/>PUT /prompts/{id}"]
        LIST["模板列表<br/>GET /prompts"]
    end

    subgraph Versioning["版本控制 (Memento 模式)"]
        PUBLISH["发布版本<br/>POST /{id}/publish<br/>version++ + 快照"]
        ROLLBACK2["版本回滚<br/>POST /{id}/rollback?version=N"]
        HISTORY["版本历史<br/>GET /{id}/versions"]
        DIFF["差异对比<br/>POST /{id}/diff?v1&v2"]
    end

    subgraph Render["变量渲染 (Strategy + Chain)"]
        DIRECTION["VariableResolver 链<br/>按 priority 排序"]
        SYS_VAR["SystemVariableResolver<br/>user_name / tenant_name<br/>current_time"]
        CTX_VAR["ContextVariableResolver<br/>conversation_history<br/>knowledge_context"]
        DEFAULT_VAR["DefaultVariableResolver<br/>兜底: Map 取值"]
        DIRECTION --> SYS_VAR --> CTX_VAR --> DEFAULT_VAR
    end

    subgraph States["模板状态机"]
        DRAFT["DRAFT<br/>草稿"]
        PUBLISHED["PUBLISHED<br/>已发布 (可渲染)"]
        ARCHIVED["ARCHIVED<br/>已归档"]
        DRAFT -->|"publish()"| PUBLISHED
        PUBLISHED -->|"archive()"| ARCHIVED
        DRAFT -->|"archive()"| ARCHIVED
        PUBLISHED -->|"rollback()"| DRAFT
    end

    subgraph DB2["持久化"]
        TPL[("t_prompt_template<br/>当前版本")]
        VER[("t_prompt_template_version<br/>历史快照")]
    end

    PUBLISH --> VER
    ROLLBACK2 --> VER
    HISTORY --> VER
    DIFF --> VER
    TPL --> PUBLISH
    RENDER_RESULT["渲染结果<br/>PromptRenderService.render()"] --> DIRECTION
    RENDER_RESULT --> TPL

    style CRUD fill:#e3f2fd
    style Versioning fill:#fff3e0
    style Render fill:#e8f5e9
    style States fill:#fce4ec
```

**13 个 API 端点**: CRUD (5) + 发布/回滚/历史/详情/差异/预览/渲染 (8)

---

## 10. 效果评估闭环架构

```mermaid
flowchart TB
    subgraph DataCollection["📊 数据采集"]
        FEEDBACK["用户反馈 (点赞/点踩)<br/>MessageFeedbackEvent"]
        AUTO_SAMPLE["自动采样<br/>生产对话 → 数据集"]
        MANUAL["手动标注<br/>Q&A 对录入"]
    end

    subgraph Dataset["📁 数据集管理"]
        DS[("t_evaluation_dataset<br/>数据集 CRUD")]
        ITEM[("t_evaluation_dataset_item<br/>样本 Q&A 对")]
    end

    subgraph Evaluation["🔬 评测执行"]
        RUN["创建评测<br/>POST /evaluations/run"]
        LLM_JUDGE["LLM-as-Judge<br/>4 维度评分引擎"]
        DIMENSIONS["评分维度:<br/>1. 准确性 (Accuracy)<br/>2. 相关性 (Relevance)<br/>3. 完整性 (Completeness)<br/>4. 安全性 (Safety)"]
        SCORE["综合评分<br/>overall_score (0-100)"]
    end

    subgraph Ticket["🔧 优化工单闭环"]
        BADCASE["BadCase 自动识别<br/>@EventListener<br/>score < 阈值"]
        AUTO_ANALYZE["LLM 自动分析<br/>根因归类:<br/>HALLUCINATION<br/>IRRELEVANT<br/>INCOMPLETE<br/>WRONG"]
        CREATE_TICKET["创建工单<br/>POST /tickets"]
        TICKET_STATE["工单状态机<br/>OPEN → ANALYZING<br/>→ IN_PROGRESS<br/>→ RESOLVED → CLOSED"]
        FIX["修复措施:<br/>KNOWLEDGE_FIX<br/>PROMPT_ADJUST<br/>MODEL_FINETUNE<br/>OTHER"]
    end

    subgraph Feedback2["🔄 反馈闭环"]
        RE_EVAL["重新评测<br/>验证修复效果"]
        METRICS_TREND["指标趋势<br/>precision/recall 变化"]
    end

    FEEDBACK --> DS
    AUTO_SAMPLE --> DS
    MANUAL --> DS
    DS --> ITEM
    ITEM --> RUN
    RUN --> LLM_JUDGE
    LLM_JUDGE --> DIMENSIONS --> SCORE
    SCORE --> BADCASE
    BADCASE --> AUTO_ANALYZE --> CREATE_TICKET
    CREATE_TICKET --> TICKET_STATE --> FIX
    FIX --> RE_EVAL
    RE_EVAL --> METRICS_TREND
    METRICS_TREND -.->|"持续优化"| FEEDBACK

    style DataCollection fill:#e3f2fd
    style Dataset fill:#fff3e0
    style Evaluation fill:#e8f5e9
    style Ticket fill:#fce4ec
    style Feedback2 fill:#f3e5f5
```

---

## 🎯 架构设计原则总结

| 原则 | 实现 |
|------|------|
| **分层架构** | DDD 4 层: interfaces → application → domain ← infrastructure |
| **依赖倒置** | Domain 定义端口 (Port), Infrastructure 提供实现 (Adapter) |
| **单一职责** | 每层职责明确: 接口/编排/业务/技术 |
| **开闭原则** | Strategy/Chain 模式: 新增策略不修改现有代码 |
| **多租户隔离** | Token → Session → ThreadLocal → MyBatis Plugin → SQL 注入 |
| **安全纵深防御** | 4 层过滤链 + PII 脱敏 + 审计日志 + 审批控制 |
| **可观测性** | MDC 全链路追踪 + Prometheus 指标 + Langfuse LLM 观测 |
| **配置分离** | dev/prod profile + 环境变量注入 |
