# T9 全链路可观测性实现

> **日期**: 2026-06-18
> **分支**: master
> **触发**: 代码生成 — P4 观测优化 T9 全链路可观测性

## 做了什么

### Phase 1: Trace/MDC 增强
- 增强 `TraceFilter`：新增 `X-Request-Id` 响应头，更新 Javadoc 说明 MDC 责任分离
- 增强 `TenantInterceptor`：在 `preHandle` 中注入 `tenantId`/`userId` 到 MDC，`afterCompletion` 清理
- 修复 `SaTokenWebMvcConfig`：注册 `TenantInterceptor`，白名单增加 `/actuator/prometheus`
- 更新 `logback-spring.xml`：MDC 格式从 `[traceId,spanId]` 扩展为 `[traceId,spanId,tenantId,userId]`

### Phase 2: AgentMetrics (Micrometer/Prometheus)
- 新建 `AgentMetrics` 组件：10 个指标（3 Counter + 4 Timer + 1 Histogram + 2 Counter）
- 集成到 `StreamOrchestrationService`：`Timer.Sample` 精确计时 LLM 调用 + 消息处理

### Phase 3: AuditLog DDD 栈 + @Auditable AOP
- 新建 `AuditLog` 领域实体 + `AuditLogRepository` 接口
- 新建 `AuditLogPO` + `AuditLogMapper` + `AuditLogMapper.xml` + `AuditLogRepositoryImpl`
- 新建 `@Auditable` 注解（action, resourceType, resourceId, recordRequest, recordResponse）
- 新建 `AuditLogAspect`：AOP 环绕增强，自动采集 traceId/tenantId/耗时/状态
- 新建 `AuditLogThreadPoolConfig`：独立线程池（core=2, max=4, queue=1000）
- 应用 `@Auditable` 到 3 个关键服务方法

### Phase 4: Langfuse LLM 追踪
- 新建 `LangfuseConfig`：`@ConditionalOnProperty`，RestTemplate + Basic Auth
- 新建 `LangfuseTraceService`：HTTP Ingestion API 直接集成（异步发送），含 `logLLMCallAsync` 和 `logToolCallAsync`
- 集成到 `StreamOrchestrationService`

## 关键决策

1. **Langfuse 改用 HTTP API 直接集成** — 放弃 langfuse-java SDK（auto-generated 客户端 API 不稳定），直接用 RestTemplate + `/api/public/ingestion` 端点
2. **MDC 注入分离** — Filter 注入 traceId+spanId，Interceptor 注入 tenantId+userId（时序决定）
3. **审计日志异步写入** — 独立线程池 fail-open，不阻断业务
4. **Micrometer Timer.Sample 精确计时** — 而非 `@Timed` 注解，因为流式响应需要手动控制采样边界

## 踩坑记录

- **langfuse-java 0.2.0 SDK API 不匹配**：`com.langfuse.client` 包下是 auto-generated OpenAPI 客户端，没有 `Langfuse`/`TraceHandle` 等高层 API
- **`@Slf4j` + `AuditLog log` 参数名冲突**：方法参数名 `log` 遮蔽了 Lombok 生成的 `log` 字段，导致 `log.trace()` 编译失败

## 文件清单

### 新建 (12)
- `infrastructure/metrics/AgentMetrics.java`
- `domain/audit/entity/AuditLog.java`
- `domain/audit/repository/AuditLogRepository.java`
- `infrastructure/persistence/po/AuditLogPO.java`
- `infrastructure/persistence/mapper/AuditLogMapper.java`
- `infrastructure/resources/mapper/AuditLogMapper.xml`
- `infrastructure/persistence/impl/AuditLogRepositoryImpl.java`
- `infrastructure/annotation/Auditable.java`
- `infrastructure/aop/AuditLogAspect.java`
- `infrastructure/config/threadpool/AuditLogThreadPoolConfig.java`
- `infrastructure/config/observability/LangfuseConfig.java`
- `infrastructure/observability/LangfuseTraceService.java`

### 修改 (5)
- `infrastructure/filter/TraceFilter.java`
- `infrastructure/interceptor/TenantInterceptor.java`
- `infrastructure/config/security/SaTokenWebMvcConfig.java`
- `bootstrap/resources/logback-spring.xml`
- `application/conversation/StreamOrchestrationService.java`
