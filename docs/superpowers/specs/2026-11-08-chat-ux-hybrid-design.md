# 对话产品体验升级设计（混合风）

> **日期**: 2026-11-08  
> **状态**: 已实现（2026-11-08） 
> **范围**: 方案 B — 聊天壳重构 + 体验闭环 API  
> **非目标**: 管理后台、对话内真实工具调用、Agent CRUD、checkpoint 产品化

---

## 1. 背景与目标

现有 `agent-platform-web` 仅为登录 + 聊天的 Ant Design 默认浅色 demo，难以体现科技感与易用性。本轮目标：

- 前端观感接近 **Cursor 深色密度 + DeepSeek 居中对话列**，并支持浅色切换
- 补齐停生成、消息重生、SSE 事件展示、知识库下拉等对话体验
- 后端仅新增体验闭环所需 API；工具事件仅前端占位

**成功标准**

- 观感与操作路径达到混合风产品感，不增加管理面复杂度
- 停生成、重生、知识库选择可用；工具块占位不报错
- 审批卡片与反馈仍可用

---

## 2. 信息架构与布局

**页面**：仅登录页 + 聊天壳。

**布局 A（已定稿）**

- 左侧窄侧栏常驻（窄屏进抽屉）：品牌、新对话、会话列表、主题切换、用户/退出
- 主区居中限宽对话列（约 720–820px）：顶栏轻控件 + 消息流 + 贴底 Composer
- 顶栏：交互模式、知识库下拉（知识检索模式显示）、流式时可停止

**消息内嵌**

- Markdown / 代码块
- 事件块：`thinking` / `intent` / `references` / `approval_*` / `task_*`
- `tool_call` / `tool_result`：UI 占位，本轮后端不推真实工具调用
- 助手消息：赞/踩、重生；停止后标记「已停止」

---

## 3. 视觉系统与组件

**视觉**

- 默认深色：背景 `#0f1115`、侧栏略亮、细边框 `#2a2f3a`、低对比正文
- 单一青蓝强调色；避免紫渐变与大面积发光
- 浅色模式：同结构，Ant Design `ConfigProvider` theme + CSS 变量切换
- 字体：界面无衬线（如 IBM Plex Sans / Source Sans 3）；代码等宽

**组件拆分**（替换臃肿 `Chat.jsx`）

| 组件 | 职责 |
|------|------|
| `ChatShell` | 侧栏 + 主区、窄屏抽屉 |
| `ConversationSidebar` | 新对话、列表、重命名/删除 |
| `ChatHeader` | 模式、知识库、主题 |
| `MessageList` / `MessageItem` | 消息流、Markdown、操作条 |
| `EventBlocks` | thinking / references / approval / task / tool 占位 |
| `Composer` | 输入、发送、停止 |
| `LoginPage` | 同风格登录 |

**交互**

- 发送中 Composer 变为停止；按钮与客户端 abort 双保险
- 流式自动滚底；用户上滚暂停跟随
- 重生前若正在流式则先取消

---

## 4. 前后端接口

### 4.1 复用已有 API

- 会话：`/api/v1/conversations/create|list|update-title|delete`
- 消息：`messages/stream|list|feedback`
- 审批：`approvals/pending|approve|reject` + WS `/ws/conversation`
- 知识库：`/api/v1/knowledge-bases` 列表（供下拉）

### 4.2 新增 API

**取消流式**

- `POST /api/v1/conversations/messages/cancel`
- Body: `{ conversationId, streamId }`
- 权限: `conversation:send`
- 行为: 从 `StreamRegistry` 取出 `SseEmitter` 并 `complete()`；流水线尊重中断标志，停止继续推 `token`

**消息重生**

- `POST /api/v1/conversations/messages/regenerate`（SSE）
- Body: `{ conversationId, sourceMessageId, mode?, knowledgeId? }`
- 权限: `conversation:send`
- 行为: 定位 `sourceMessageId` 对应助手消息，取其前一条用户内容重新走 stream；**旧助手消息保留**，追加新助手消息（避免破坏反馈关联）
- 响应: 与 `messages/stream` 相同的 SSE 事件集；首包可带 `streamId`

### 4.3 流式契约补充

- `stream` / `regenerate` 开始时通过 SSE 事件或首包 data 下发 `streamId`（UUID）
- 前端 `streamChat` 使用 `AbortController`；停止时同时 abort fetch 并调用 cancel API
- 已有事件名沿用 `SseEventFactory`：`token` / `thinking` / `intent` / `references` / `done` / `error` / `approval_*` / `task_*` / `tool_*`

### 4.4 错误处理

- 业务层 catch 后必须 re-throw / 发 SSE `error`，禁止吞异常
- 取消成功：前端停止追加，消息标记「已停止」
- 鉴权失败沿用 Sa-Token 既有错误

---

## 5. 后端实现要点（DDD）

```
interfaces (MessageController) 
  → application (StreamSessionService / MessageApplicationService)
  → infrastructure (StreamRegistry 内存注册表)
```

- `StreamRegistry`：`ConcurrentHashMap<streamId, SseEmitter>` + conversation 维度索引；emitter 完成/超时自动 unregister
- Controller 禁止直接注入 Repository；DTO 放 application 层；列表响应用强类型 Response DTO
- 枚举继续 `code` + `fromCode`，禁止 `name()` / `valueOf()` 做业务比较

---

## 6. 前端实现要点

- 技术栈保持：React 18 + Vite + Ant Design 5 + Zustand（本轮不做全量 TypeScript）
- 新增依赖：`react-markdown`、代码高亮（如 `react-syntax-highlighter`）、字体按需引入
- `api.js` 补齐：`updateTitle`、`deleteConversation`、`listKnowledgeBases`、`cancelStream`、`regenerate`
- 主题 token 集中在 `theme.js` + `styles/tokens.css`

---

## 7. 验收与测试

**手工**

- 深色默认 / 浅色切换；侧栏 CRUD；流式 Markdown；停止；重生；反馈；审批；知识库下拉；窄屏抽屉

**自动化（最小）**

- 前端：SSE 解析单测覆盖多事件与停止场景
- 后端：`StreamRegistry` 单测；regenerate 应用服务单测（mock）

---

## 8. 明确不做

- 管理后台页面（用户/角色/KB 管理/工具台等）
- 对话内真实 MCP/HTTP Function Calling
- Agent 配置/人格 API
- 附件上传、消息编辑再发、checkpoint 断点续传产品化

---

## 9. 决策记录

| 决策 | 选择 |
|------|------|
| 范围 | A 对话体验优先 |
| 视觉 | 3 混合风 |
| 后端深度 | 1 体验闭环（不含真实工具调用） |
| 实现路径 | B 聊天壳重构 + 体验 API |
| 布局 | A 侧栏 + 居中对话列 |
| 重生策略 | 保留旧助手消息并追加新消息 |
