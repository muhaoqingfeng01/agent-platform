# Agent Platform Web（A1-01 聊天界面）

企业级 Agent 平台的 Web 聊天前端，对应方案 `docs/未实现事项设计方案/A1-01-P5-Web聊天界面.md`。

## 目录位置

本工程位于后端仓库内的 `agent-platform-web/`，便于前后端联调与同一 PR 交付。

若本地习惯使用兄弟目录：

```text
D:\mhqf_project\heavenly-craft-agent\agent-platform-web
```

可将本目录完整复制过去，或把该路径做成指向本目录的 junction/symlink。API 契约与后端一致，不依赖 Maven 模块。

## 技术栈

React 18 + Vite 5 + TypeScript + Ant Design 5 + Zustand + Axios。

流式对话使用 `fetch` + `ReadableStream` 解析 SSE（**不能**用原生 `EventSource`，因其无法携带 `Authorization: Bearer`）。

## 启动

1. 后端：`mvn spring-boot:run -pl agent-platform-bootstrap`（默认 `http://localhost:8080`）
2. 前端：

```bash
cd agent-platform-web
npm install
npm run dev
```

浏览器打开 `http://localhost:5173`。开发环境通过 Vite 把 `/api` 代理到 `8080`。

## 已对接 API（均为 POST + JSON，与现网 Controller 一致）

| 能力 | 路径 |
|------|------|
| 登录 | `POST /api/v1/auth/login` |
| 当前用户（含 permissions） | `POST/GET /api/v1/auth/me` |
| 会话列表/创建/删除 | `/api/v1/conversations/list` `/create` `/delete` |
| 历史消息 | `POST /api/v1/conversations/messages/list` |
| 流式聊天 | `POST /api/v1/conversations/messages/stream` |
| 知识库列表 | `POST /api/v1/knowledge-bases/list`（需 `kb:read`） |

Token 仅放在 **sessionStorage**，请求头 `Authorization: Bearer <token>`。

## 验收范围（A1-01）

- 默认 `CONVERSATION` 流式出字
- `KNOWLEDGE_SEARCH` 先展示 `references` 再出字；无 `kb:read` 时隐藏模式切换
- 刷新后拉历史消息
- 桌面 1280px / 移动 375px 布局
- 断线显示「重连」，**不自动重放**用户消息

未包含：审批卡片（A1-02）、点赞点踩（A1-03）、IM（A1-04）、管理后台其余 26 批页面。
