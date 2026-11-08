# AuditLog trace_id 空值与流式 MDC 透传

> **日期**: 2026-11-08 17:20
> **分支**: master
> **触发**: Bug修复

## 做了什么
- `InteractionApplicationService` 提交 `streamExecutor` 时用 `MdcContext.wrap` 传播 HTTP 线程的 `traceId/spanId`
- `AuditLogAspect`：`traceId` 缺失时生成兜底 UUID（满足 `t_audit_log.trace_id NOT NULL`）；补充 `conversationId`；`ActorType` 改用 `getCode()`

## 关键决策
- 根因：stream-pool 未透传 MDC，与已有 `MdcContext` 工具用法（DAG/TaskCenter）不一致
- 双保险：提交点透传（根治）+ 切面兜底生成（防其它异步入口再踩坑）

## 踩坑记录
- 项目已有 `MdcContext` 注释说明 Spring 6.1 `TaskDecorator` 对 `submit` 无效，但流式入口漏接

## 下一步
- 重启后端后验证审计日志可写入，且 `trace_id` 与请求 `X-Trace-Id` 一致
