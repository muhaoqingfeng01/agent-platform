# Agent Platform Web

P5 交互端：登录、会话列表、SSE 流式聊天、审批卡片、消息反馈。

## 启动

```bash
npm install
npm run dev
```

前端默认 `http://localhost:5173`，通过 Vite 代理转发到后端。

端口配置（改后端端口时只改环境变量即可）：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `VITE_BACKEND_URL` | `http://127.0.0.1:8081` | 后端地址（`/api`、`/ws` 代理目标；勿用 localhost 以免 IPv6 拒连） |
| `VITE_DEV_PORT` | `5173` | 前端开发端口 |

本地可编辑 `.env.development`，模板见 `.env.example`。业务代码仍使用相对路径 `/api/...`，无需硬编码主机端口。

Token 仅存 `sessionStorage`。

## 验证

```bash
npm test
npm run build
```
