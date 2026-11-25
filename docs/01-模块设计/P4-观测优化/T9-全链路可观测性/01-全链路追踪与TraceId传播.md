# 全链路追踪与 TraceId 传播

> **状态**: ✅ 已实现 | **日期**: 2026-06-18 | **所属**: P4-T9

---

## 1. 设计目标

在一次请求的完整生命周期中，实现以下能力：

| 目标 | 说明 |
|------|------|
| **请求串联** | 同一个 traceId 贯穿请求→认证→业务→LLM→响应，所有日志可关联 |
| **层级隔离** | traceId/spanId（请求级）与 tenantId/userId（认证级）分层注入，解耦时序 |
| **上游兼容** | 接受上游传入的 `X-Trace-Id`，实现跨服务追踪 |
| **客户端可定位** | 响应头返回 `X-Trace-Id` + `X-Request-Id`，前端/客户端可关联 |

---

## 2. 请求生命周期与 MDC 注入时序

```
┌─────────────────────────────────────────────────────────────────┐
│                         HTTP Request                            │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
              ┌────────────────────────────────┐
              │  TraceFilter (Filter, 最先执行) │
              │  • X-Trace-Id → 复用或生成      │
              │  • X-Request-Id → 生成          │
              │  • spanId → 生成                │
              │  • MDC ← traceId, spanId        │
              │  • Response ← X-Trace-Id,       │
              │               X-Request-Id      │
              └────────────────────────────────┘
                               │
                               ▼
              ┌────────────────────────────────┐
              │  SaInterceptor (Interceptor)    │
              │  • 白名单放行 / 认证校验         │
              │  • StpUtil.checkLogin()         │
              └────────────────────────────────┘
                               │
                               ▼
              ┌────────────────────────────────┐
              │  TenantInterceptor (Interceptor)│
              │  • StpUtil.isLogin() → userId   │
              │  • Session → tenantId           │
              │  • TenantContext ← tenant/user   │
              │  • MDC ← tenantId, userId       │
              └────────────────────────────────┘
                               │
                               ▼
              ┌────────────────────────────────┐
              │  Controller → Service → LLM     │
              │  • 日志自动携带 MDC 四元组       │
              │  • @Auditable 审计切面采集       │
              └────────────────────────────────┘
                               │
                               ▼
              ┌────────────────────────────────┐
              │  afterCompletion                │
              │  • MDC.clear()                  │
              │  • TenantContext.clear()        │
              └────────────────────────────────┘
```

### 为什么 Filter 和 Interceptor 分开注入？

| 层面 | 注入内容 | 可用时机 | 原因 |
|------|---------|----------|------|
| **Filter** | traceId, spanId | 请求到达后立即 | 不需要任何认证信息 |
| **Interceptor** | tenantId, userId | Sa-Token 认证完成后 | 需要 Session 中的登录态 |

Filter 先于 Interceptor 执行，如果在 TraceFilter 中尝试读取 `TenantContext`（由 Interceptor 设置），会得到 null。

---

## 3. 组件关系图

```
┌──────────────┐     ┌──────────────────┐     ┌─────────────────────┐
│  TraceFilter  │────▶│  SLF4J MDC       │◀────│  TenantInterceptor   │
│  (Filter)     │     │  (ThreadLocal)   │     │  (Interceptor)       │
│               │     │                  │     │                      │
│  traceId ─────┼────▶│  map["traceId"]  │◀────│  — (不设置)           │
│  spanId ──────┼────▶│  map["spanId"]   │◀────│  — (不设置)           │
│  —            │     │  map["tenantId"] │◀────│  tenantId             │
│  —            │     │  map["userId"]   │◀────│  userId               │
└──────────────┘     └────────┬─────────┘     └──────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  logback-spring.xml           │
              │  %X{traceId},%X{spanId},      │
              │  %X{tenantId},%X{userId}      │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  日志输出                      │
              │  [a1b2, c3d4, t001, u001]     │
              │  未登录时 tenantId/userId 为空  │
              └───────────────────────────────┘
```

---

## 4. 响应头契约

| Header | 方向 | 生成规则 | 用途 |
|--------|------|---------|------|
| `X-Trace-Id` | 请求→响应 | 复用上游值 或 32位UUID(无连字符) | 全链路串联 |
| `X-Request-Id` | 仅响应 | 36位UUID(标准格式) | 客户端定位单次请求 |
| `X-Span-Id` | 内部 | 16位UUID(无连字符) | 日志内区分同trace下的不同span |

---

## 5. Langfuse LLM 追踪

### 5.1 方案选型

```
方案 A: langfuse-java SDK (0.2.0)
├── 包: com.langfuse.client.resources.*
├── 问题: auto-generated OpenAPI 客户端
│         └── 没有 Langfuse / TraceHandle / Usage 等高层 API
├── 结论: ❌ 不采用
│
方案 B: HTTP Ingestion API 直连
├── 端点: POST {host}/api/public/ingestion
├── 认证: Basic Auth (public-key:secret-key)
├── 协议: JSON Batch (trace-create + generation-create + span-create)
├── 优势: 版本无关、可控超时、简单可靠
└── 结论: ✅ 采用
```

### 5.2 数据流

```
StreamOrchestrationService
        │
        │  LLM 调用完成
        ▼
LangfuseTraceService.logLLMCallAsync(...)
        │
        │  @Async（异步，不阻塞 SSE 流）
        ▼
构建 Ingestion Event Batch:
  [
    { type: "trace-create",    body: { name, userId, metadata } },
    { type: "generation-create", body: { traceId, model, input, output, usage, startTime, endTime } }
  ]
        │
        ▼
RestTemplate → POST /api/public/ingestion
        │
        ├─ 成功 → trace 日志
        └─ 失败 → debug 日志（fail-open，不抛异常）
```

### 5.3 条件装配

```
langfuse.enabled = true
  → @ConditionalOnProperty 创建 RestTemplate Bean
  → LangfuseTraceService 正常工作

langfuse.enabled = false（默认）
  → 无 RestTemplate Bean
  → @Autowired(required = false) → null
  → LangfuseTraceService 所有方法空操作
  → 零性能影响
```

---

## 6. 审计日志（@Auditable AOP）

### 6.1 架构层次

```
┌─────────────────────────────────────────────────────┐
│  @Auditable 注解（声明式）                             │
│  @Target(METHOD) @Retention(RUNTIME)                │
│  action / resourceType / resourceId / recordRequest │
└──────────────────────┬──────────────────────────────┘
                       │ 触发
                       ▼
┌─────────────────────────────────────────────────────┐
│  AuditLogAspect（AOP 环绕增强）                       │
│  @Around("@annotation(auditable)")                  │
│                                                     │
│  采集阶段:                                           │
│    MDC.get("traceId")          → 请求追踪            │
│    TenantContext.getXxx()       → 租户/用户           │
│    System.currentTimeMillis()   → 计时起点            │
│    ObjectMapper.writeValue()    → 参数序列化          │
│                                                     │
│  执行: joinPoint.proceed()                           │
│                                                     │
│  记录阶段:                                           │
│    计算 durationMs                                    │
│    判断 status (SUCCESS / FAILED)                    │
│    AuditLog.builder()... → repository.save()        │
│    写入失败 → log.warn()（不抛异常）                   │
└──────────────────────┬──────────────────────────────┘
                       │ 写入
                       ▼
┌─────────────────────────────────────────────────────┐
│  DDD 四层持久化                                       │
│                                                     │
│  domain/         AuditLog (Entity)                  │
│                  AuditLogRepository (Interface)     │
│                                                     │
│  infrastructure/ AuditLogPO → AuditLogMapper.xml    │
│                  AuditLogRepositoryImpl             │
│                                                     │
│  DB              t_audit_log (V1.1.0 已建表)        │
└─────────────────────────────────────────────────────┘
```

### 6.2 已标注的业务方法

| 方法 | action | resourceType | 说明 |
|------|--------|-------------|------|
| `StreamOrchestrationService.executeStreamPipeline` | LLM_CALL | CONVERSATION | 每次 LLM 对话 |
| `IntentRecognitionChain.recognize` | INTENT_RECOGNITION | INTENT | 三层意图识别 |
| `ToolApplicationService.test` | TOOL_TEST | TOOL | 工具调用测试 |

### 6.3 异步写入策略

```
业务线程                         audit-log- 线程池
    │                                  │
    │  @Auditable 方法执行              │
    │  AuditLogAspect 构建实体          │
    │  repository.save(auditLog) ──────▶│  写入 MySQL
    │  （同步调用，毫秒级）              │
    │                                  │
    │  写入失败 → log.warn()            │
    │  业务不受影响 ←───────────────────┘
    │
    ▼
  返回业务结果
```

> 大规模部署时，建议将 `repository.save()` 替换为 Kafka 发送，由独立消费者写入 Elasticsearch。

---

## 7. 涉及的 DDD 层次

```
interfaces/
  (Controller 无感知 — Filter + Interceptor 在基础设施层自动工作)

application/
  StreamOrchestrationService  ← @Auditable + LangfuseTraceService + AgentMetrics
  IntentRecognitionChain      ← @Auditable
  ToolApplicationService      ← @Auditable

domain/
  audit/
    entity/AuditLog           ← 新增领域实体
    repository/AuditLogRepository ← 新增仓储接口

infrastructure/
  filter/TraceFilter          ← 增强
  interceptor/TenantInterceptor ← 增强
  aop/AuditLogAspect          ← 新增
  annotation/Auditable        ← 新增
  metrics/AgentMetrics        ← 新增
  observability/LangfuseTraceService ← 新增
  config/observability/LangfuseConfig ← 新增
  config/security/SaTokenWebMvcConfig ← 修改
  config/threadpool/AuditLogThreadPoolConfig ← 新增
  persistence/
    po/AuditLogPO             ← 新增
    mapper/AuditLogMapper     ← 新增
    impl/AuditLogRepositoryImpl ← 新增
```

---

## 8. 配置汇总

| 配置项 | 文件 | 说明 |
|--------|------|------|
| MDC 日志格式 | `logback-spring.xml` | `[%X{traceId},%X{spanId},%X{tenantId},%X{userId}]` |
| 响应头 CORS | `CorsConfig.java` | 已暴露 `X-Trace-Id`, `X-Request-Id` |
| 路由白名单 | `SaTokenWebMvcConfig.java` | `/actuator/prometheus` 加入白名单 |
| TenantInterceptor | `SaTokenWebMvcConfig.java` | 在 SaInterceptor 之后注册 |
| Langfuse 连接 | `application.yml` | `langfuse.host / public-key / secret-key / enabled` |
