# P5-01 Web 聊天界面

## 技术栈
- React 18+ (Vite 构建)
- Ant Design 5.x
- WebSocket 长连接
- react-window（虚拟滚动）

## 架构设计

### 消息模型
所有消息统一为 JSON 格式，通过 `type` 字段区分：
- `message` — 普通对话消息
- `approval` — 审批卡片
- `feedback` — 用户反馈
- `system` — 系统通知

### 虚拟滚动
长会话场景下消息列表可能上千条，使用 `react-window` 的 `FixedSizeList` 实现虚拟滚动，只渲染可视区域内的消息 DOM 节点。

### WebSocket 连接管理
- 基于 `useWebSocket` 自定义 Hook 封装
- 自动重连 + 指数退避（1s → 2s → 4s → max 30s）
- 心跳检测：每 30s 发送 ping，60s 无 pong 则重连
- 断线期间的消息入本地队列，重连后批量补发

### 消息状态机
```
pending → sent → delivered → read
                 ↘ failed → retry(3次) → failed
```
