# AuditLog tenant_id 空值修复

> **日期**: 2026-11-08 17:10
> **分支**: master
> **触发**: Bug修复

## 做了什么
- 修复流式 LLM 调用（`stream-pool`）写入 `t_audit_log` 时 `tenant_id` 为 null 导致的 `SQLIntegrityConstraintViolationException`
- 调整 `AuditLogAspect`：方法执行后再解析租户/用户，并回退到方法参数 `tenantId`/`userId`
- 真正接入已有的 `auditLogExecutor` 异步写入（此前注释写异步，实际在业务线程同步写）
- `tenantId` 仍为空时跳过写入并打 WARN，避免脏 SQL 噪音

## 关键决策
- 根因：`@Auditable` 切面在 `proceed()` **之前**读 `TenantContext`，而 `StreamOrchestrationService` / `KnowledgeSearchStreamService` 在方法体内才 `setTenantId`
- 不改业务方法签名/调用顺序，在切面侧做「方法后解析 + 参数回退」，兼容 HTTP 线程与 stream-pool 两种场景

## 踩坑记录
- 日志线程名 `[stream-pool-1]` + `action=LLM_CALL` 可直接定位到流式编排而非 HTTP 拦截器缺失
- `AuditLogThreadPoolConfig` 早已存在但切面未注入，异步写入是空头支票

## 下一步
- 可选：stream-pool 任务结束后 `TenantContext.clear()`，避免线程池 ThreadLocal 串扰
- 重启后端验证聊天流后审计日志可正常落库
