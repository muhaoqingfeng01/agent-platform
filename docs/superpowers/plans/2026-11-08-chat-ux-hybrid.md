# 对话产品体验升级 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `agent-platform-web` 升级为 Cursor/DeepSeek 混合风聊天壳，并补齐停生成与消息重生后端能力。

**Architecture:** 前端拆分 ChatShell 组件体系 + Ant Design 主题 token；后端在 application 层增加 StreamRegistry 与 cancel/regenerate，经 MessageController 暴露，复用现有 InteractionApplicationService 流式管线。

**Tech Stack:** React 18、Vite、Ant Design 5、Zustand、react-markdown；Spring Boot 3.3.7、SseEmitter、Sa-Token。

**Spec:** [docs/superpowers/specs/2026-11-08-chat-ux-hybrid-design.md](../specs/2026-11-08-chat-ux-hybrid-design.md)

## Global Constraints

- DDD：Controller → ApplicationService → Domain；禁止 Controller 注入 Repository
- Controller/Application 返回值禁止裸 Map/List，须强类型 Response DTO
- 枚举必须 `code` + `desc` + `fromCode`；禁止业务路径使用 `name()` / `valueOf()`
- catch 后必须 re-throw 或发 SSE `error`，禁止吞异常
- 本轮不做管理后台、真实工具调用、Agent CRUD
- 仅在用户明确要求时 git commit

---

## File Map

**后端新增/修改**

- 新增 `application/.../conversation/StreamRegistry.java`（或 infrastructure 组件由 application 端口封装）
- 新增 `application/.../conversation/StreamSessionService.java`
- 修改 `interfaces/.../MessageController.java`：cancel + regenerate
- 新增 Request DTO：`MessageCancelRequest`、`MessageRegenerateRequest`
- 新增/扩展 Response DTO：含 `streamId` 的流式启动信息（若用 SSE 事件则可不另开 REST Response）
- 修改流式入口：`InteractionApplicationService` / `StreamOrchestrationService` 注册 emitter 并检查取消

**前端新增/修改**

- `src/theme.js`、`src/styles/tokens.css`
- `src/components/chat/*`：ChatShell、Sidebar、Header、MessageList、MessageItem、EventBlocks、Composer
- 改造 `src/pages/Login.jsx`、`src/App.jsx`、`src/main.jsx`
- 扩展 `src/api.js`、`src/streamChat.js`、`src/parseSse.js`
- 删除或瘦身原 `src/pages/Chat.jsx`

---

## Task 1: StreamRegistry + cancel API

- [ ] 编写 `StreamRegistry` 单测（register / cancel / auto-remove on complete）
- [ ] 实现 Registry 与 `StreamSessionService.cancel`
- [ ] `MessageController` 增加 `POST .../messages/cancel`
- [ ] 现有 `stream` 入口生成 `streamId`，注册 emitter，SSE 首事件下发 `streamId`
- [ ] `mvn test` 相关模块通过

## Task 2: regenerate API

- [ ] 编写 regenerate 应用服务单测（定位用户内容、拒绝非法 sourceMessageId）
- [ ] 实现 regenerate：不重复落库用户消息，走现有 `executeStream` / 策略工厂
- [ ] Controller SSE 端点 + Request DTO
- [ ] 验证旧助手消息保留、新消息追加

## Task 3: 前端主题与壳骨架

- [ ] 增加 tokens.css + theme.js（深色默认、浅色切换）
- [ ] 实现 ChatShell + ConversationSidebar + ChatHeader + Composer 空壳
- [ ] LoginPage 对齐视觉
- [ ] `npm run build` 通过

## Task 4: 流式体验与消息渲染

- [ ] streamChat 支持 AbortController、streamId、cancel 调用
- [ ] MessageList/Item + Markdown/代码高亮
- [ ] EventBlocks（thinking/references/approval/task/tool 占位）
- [ ] 停止、重生、反馈、知识库下拉（list knowledge-bases）
- [ ] 会话重命名/删除对接已有 API
- [ ] 扩展 parseSse 单测；`npm test` 通过

## Task 5: 联调与文档同步

- [ ] 本地联调手工验收清单（见 spec §7）
- [ ] 更新 `docs/开发进度.md`、`docs/project-memory/` 递增序号快照与 README 索引
- [ ] 更新 `docs/api-documentation/前端开发进度.md` 本轮完成项
