# P7 迭代增强 — streamChat 双模式交互改造

> **日期**: 2026-07-06 23:30
> **分支**: master
> **触发**: 代码生成 — 改造 streamChat SSE 流式端点支持双模式

## 做了什么

- 改造 `MessageController#streamChat` 支持 CONVERSATION / KNOWLEDGE_SEARCH 双模式路由
- 新建 `KnowledgeSearchStreamService` — RAG（检索增强生成）流式管线编排服务（347 行）
- 扩展 `InteractionStrategy` 接口：新增 `executeStream()` 默认方法，为所有策略提供流式扩展点
- 扩展 `KnowledgeSearchInteractionStrategy`：实现 `executeStream()`，委托给 `KnowledgeSearchStreamService`
- 扩展 `InteractionContext`：新增 `forKnowledgeSearchStream()` 工厂方法
- 扩展 `MessageSendRequest`：新增 `mode` + `knowledgeId` 字段
- 更新 `InteractionApplicationService` + `InteractionController` 支持 KNOWLEDGE_SEARCH 流式模式
- 撰写完整技术方案文档：`docs/P7-多模式交互/P7-迭代增强-streamChat双模式交互改造技术方案.md`

## 关键决策

1. **独立 RAG 流式服务** — 新建 `KnowledgeSearchStreamService` 而非在 `StreamOrchestrationService` 中加分支，保证两个管线独立演进
2. **无命中不调 LLM** — 知识库无匹配内容时直接返回友好提示"未涵盖"，零 Token 消耗，绝不臆造
3. **默认模式回退** — `parseMode()` 对 null/blank/invalid mode 均回退 CONVERSATION，确保旧客户端完全兼容
4. **RAG Prompt 5 条硬约束** — 仅基于检索内容、未找到告知、引用来源、简洁准确、禁止臆造
5. **策略流式扩展点** — `executeStream()` 设为接口 default 方法，新策略无需强制实现

## 踩坑记录

- 无 — 此次迭代在已有的稳固架构上增量开发，编译一次通过，无需修复

## 变更文件清单

| # | 文件 | 操作 | 层级 |
|:--:|------|:--:|:--:|
| 1 | `MessageSendRequest.java` | ✏️ 修改 | interfaces |
| 2 | `MessageController.java` | ✏️ 修改 | interfaces |
| 3 | `KnowledgeSearchStreamService.java` | 🆕 新建 | application |
| 4 | `InteractionStrategy.java` | ✏️ 修改 | domain |
| 5 | `KnowledgeSearchInteractionStrategy.java` | ✏️ 修改 | application |
| 6 | `InteractionApplicationService.java` | ✏️ 修改 | application |
| 7 | `InteractionController.java` | ✏️ 修改 | interfaces |
| 8 | `InteractionContext.java` | ✏️ 修改 | domain |

## 技术文档

- 完整方案：`docs/P7-多模式交互/P7-迭代增强-streamChat双模式交互改造技术方案.md`（含架构图、流程图、API 规范、扩展指南）
- 原 P7 方案：`docs/P6-迭代优化方案/智能体多模式交互系统架构设计方案.md`

## 下一步

- 补充单元测试覆盖双模式路由 + 无命中处理
- MessageController 路由升级为策略工厂调度（消除 if-else）
- RAG Prompt + 无命中提示文案迁移至 Nacos 动态配置
- 同步更新 CLAUDE.md + 开发进度文件
