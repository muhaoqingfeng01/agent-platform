# Web 聊天界面技术方案

> 版本: 1.0.0 | 生效日期: 2026-06-21 | 所属: P5-交互端  
> 技术栈: React 18+ + TypeScript + Vite + Ant Design 5.x + WebSocket + SSE  
> 关联文档: [[前端架构设计方案]] | [[前后端权限对应技术方案]]

---

## 一、概述

Web 聊天界面是 Agent Platform 的核心交互入口，用户通过该界面与 AI Agent 进行多轮对话。界面需支持：

- **流式对话**: SSE 实时接收 AI 回复，逐字渲染
- **多轮会话**: 会话创建/切换/删除，历史消息分页加载
- **审批卡片**: 敏感操作审批内嵌在消息流中
- **用户反馈**: 每条 AI 回复支持点赞/点踩
- **文件附件**: 图片/文档上传与预览
- **Markdown 渲染**: 代码高亮、表格、列表、LaTeX 公式

---

## 二、消息模型

### 2.1 统一消息格式

所有消息通过 `type` 字段进行多态分发：

```typescript
// src/types/message.d.ts

interface BaseMessage {
  id: string;              // 消息唯一 ID
  conversationId: string;  // 所属会话 ID
  type: MessageType;       // 消息类型
  timestamp: number;        // 时间戳
  status: MessageStatus;    // 消息状态
  channel?: 'web' | 'wecom' | 'dingtalk';  // 消息来源渠道（默认 web）
}

type MessageType = 'message' | 'approval' | 'feedback' | 'system';

type MessageStatus = 'pending' | 'sent' | 'delivered' | 'read' | 'failed';

// 普通对话消息
interface ChatMessage extends BaseMessage {
  type: 'message';
  role: 'user' | 'assistant';
  content: string;          // Markdown 文本
  metadata?: {
    model?: string;         // 使用的模型
    tokens?: number;        // Token 消耗
    latency?: number;       // 响应耗时(ms)
  };
}

// 审批卡片消息
interface ApprovalMessage extends BaseMessage {
  type: 'approval';
  approvalId: string;
  executionId: string;          // 关联任务执行 ID
  title: string;
  detail: string;
  riskLevel: 'low' | 'medium' | 'high' | 'critical';
  requestedBy: string;          // 请求人
  options: string[];
  timeout: number;              // 超时秒数
  timeoutAt: string;            // ISO 8601 超时时间点
  createdAt: string;            // ISO 8601 创建时间
  result?: 'approved' | 'rejected' | 'timeout';
  metadata?: {                  // 工具/资源元数据
    toolName: string;
    toolParams: Record<string, any>;
    affectedResources: string[];
  };
}

// 反馈消息
interface FeedbackMessage extends BaseMessage {
  type: 'feedback';
  targetMessageId: string;  // 被评价的消息 ID
  rating: 1 | -1;           // 1=点赞 -1=点踩
  reason?: string;          // 点踩原因
}

// 系统通知消息
interface SystemMessage extends BaseMessage {
  type: 'system';
  level: 'info' | 'warning' | 'error';
  content: string;
}

type Message = ChatMessage | ApprovalMessage | FeedbackMessage | SystemMessage;
```

### 2.2 消息状态机

```
┌─────────┐   发送     ┌──────┐   服务端确认   ┌───────────┐   对方已读   ┌──────┐
│ pending │ ───────→ │ sent │ ────────────→ │ delivered │ ─────────→ │ read │
└─────────┘           └──────┘               └───────────┘            └──────┘
                          │                        │
                          │ 发送失败                │ 超时未确认
                          ▼                        ▼
                      ┌────────┐              ┌────────┐
                      │ failed │              │ failed │
                      └────────┘              └────────┘
                          │                        │
                          │ 重试(最多3次)           │ 重试(最多3次)
                          ▼                        ▼
                      ┌──────┐                ┌──────┐
                      │ sent │                │ sent │
                      └──────┘                └──────┘
```

---

## 三、SSE 流式对话

### 3.1 SSE 事件类型

后端 SSE 端点: `GET /api/v1/conversations/{id}/stream`

```typescript
// src/types/sse.d.ts — SSE 事件类型

// Token 用量统计
interface TokenUsage {
  inputTokens: number;
  outputTokens: number;
  totalTokens: number;
}

// SSE 事件流联合类型
type SSEEvent =
  | { type: 'start'; messageId: string }                        // 流式开始
  | { type: 'token'; content: string }                          // 增量 Token
  | { type: 'tool_call'; toolName: string; args: any }          // 工具调用
  | { type: 'tool_result'; toolName: string; result: any }      // 工具结果
  | { type: 'thinking'; content: string }                       // 思考过程
  | { type: 'error'; code: string; message: string }            // 错误
  | { type: 'done'; usage: TokenUsage }                          // 流式结束
  | { type: 'approval_required'; approvalId: string }           // 触发审批
```

### 3.2 SSE Hook 实现

```typescript
// src/hooks/useSSE.ts
import { useState, useCallback, useRef, useEffect } from 'react';
import { useAuthStore } from '@/stores/useAuthStore';
import { useChatStore } from '@/stores/useChatStore';
import { API_PREFIX } from '@/config/constants';
import type { SSEEvent } from '@/types/sse';

interface UseSSEReturn {
  isStreaming: boolean;
  connect: (conversationId: string, content: string) => void;
  disconnect: () => void;
  abort: () => void;
}

export function useSSE(): UseSSEReturn {
  const [isStreaming, setIsStreaming] = useState(false);
  const eventSourceRef = useRef<EventSource | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);
  const token = useAuthStore(state => state.token);
  const { appendToken, finishMessage, appendAssistantMessage, setApprovalRequired } = useChatStore();

  const connect = useCallback((conversationId: string, content: string) => {
    setIsStreaming(true);
    
    // 先插入用户消息和空的助手消息占位
    useChatStore.getState().addUserMessage(content);
    const assistantMsgId = appendAssistantMessage();

    const url = `${API_PREFIX}/conversations/${conversationId}/stream`;

    // 使用 fetch + ReadableStream 替代 EventSource（支持 POST）
    abortControllerRef.current = new AbortController();

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({ content }),
      signal: abortControllerRef.current.signal,
    })
    .then(response => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      
      const reader = response.body?.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      function processChunk({ done, value }: ReadableStreamReadResult<Uint8Array>): any {
        if (done) {
          setIsStreaming(false);
          finishMessage(assistantMsgId);
          return;
        }

        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';

        for (const line of lines) {
          if (line.startsWith('data: ')) {
            try {
              const event: SSEEvent = JSON.parse(line.slice(6));
              handleSSEEvent(event, assistantMsgId);
            } catch {
              // 非 JSON 行（如注释）忽略
            }
          }
        }

        return reader!.read().then(processChunk);
      }

      return reader!.read().then(processChunk);
    })
    .catch(err => {
      if (err.name !== 'AbortError') {
        setIsStreaming(false);
        useChatStore.getState().setMessageError(assistantMsgId, err.message);
      }
    });
  }, [token]);

  const handleSSEEvent = (event: SSEEvent, msgId: string) => {
    switch (event.type) {
      case 'start':
        // 流式开始，可选显示 loading 动画
        break;
      case 'token':
        appendToken(msgId, event.content);
        break;
      case 'tool_call':
        useChatStore.getState().appendToolCall(msgId, event);
        break;
      case 'tool_result':
        useChatStore.getState().appendToolResult(msgId, event);
        break;
      case 'thinking':
        useChatStore.getState().appendThinking(msgId, event.content);
        break;
      case 'approval_required':
        setApprovalRequired(event.approvalId);
        break;
      case 'error':
        useChatStore.getState().setMessageError(msgId, event.message);
        setIsStreaming(false);
        break;
      case 'done':
        finishMessage(msgId);
        setIsStreaming(false);
        break;
    }
  };

  const disconnect = useCallback(() => {
    abortControllerRef.current?.abort();
    setIsStreaming(false);
  }, []);

  const abort = useCallback(() => {
    disconnect();
  }, [disconnect]);

  useEffect(() => {
    return () => disconnect();
  }, [disconnect]);

  return { isStreaming, connect, disconnect, abort };
}
```

---

## 四、WebSocket 实时通信

### 4.1 连接管理

```typescript
// src/hooks/useWebSocket.ts
import { useState, useEffect, useCallback, useRef } from 'react';
import { getAppConfig } from '@/config/app.config';
import { useAuthStore } from '@/stores/useAuthStore';
import { message } from 'antd';

interface WSMessage {
  type: string;
  payload: any;
  timestamp: number;
}

interface UseWebSocketOptions {
  onMessage?: (msg: WSMessage) => void;
  onReconnect?: () => void;
}

const MAX_RECONNECT_ATTEMPTS = 5;
const RECONNECT_DELAYS = [1000, 2000, 4000, 8000, 16000]; // 指数退避
const HEARTBEAT_INTERVAL = 30000;  // 30s 心跳
const HEARTBEAT_TIMEOUT = 60000;   // 60s 无响应判定断线

export function useWebSocket(options: UseWebSocketOptions = {}) {
  const [isConnected, setIsConnected] = useState(false);
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimerRef = useRef<ReturnType<typeof setTimeout>>();
  const heartbeatTimerRef = useRef<ReturnType<typeof setInterval>>();
  const heartbeatTimeoutRef = useRef<ReturnType<typeof setTimeout>>();
  const reconnectAttempts = useRef(0);
  const pendingMessages = useRef<string[]>([]); // 断线期间消息队列

  const config = getAppConfig();
  const token = useAuthStore(state => state.token);

  const connect = useCallback(() => {
    if (!token) return;

    const wsUrl = `${config.websocket.url}?token=${encodeURIComponent(token)}`;
    const ws = new WebSocket(wsUrl);

    ws.onopen = () => {
      setIsConnected(true);
      reconnectAttempts.current = 0;
      startHeartbeat(ws);

      // 补发断线期间的消息
      while (pendingMessages.current.length > 0) {
        const msg = pendingMessages.current.shift()!;
        ws.send(msg);
      }
    };

    ws.onmessage = (event) => {
      resetHeartbeatTimeout();

      if (event.data === 'pong') return;

      try {
        const msg: WSMessage = JSON.parse(event.data);
        options.onMessage?.(msg);
      } catch (err) {
        console.error('[WS] Parse error:', err);
      }
    };

    ws.onclose = (event) => {
      setIsConnected(false);
      clearHeartbeat();

      // 非正常关闭时自动重连
      if (!event.wasClean && reconnectAttempts.current < MAX_RECONNECT_ATTEMPTS) {
        scheduleReconnect();
      }
    };

    ws.onerror = (err) => {
      console.error('[WS] Error:', err);
    };

    wsRef.current = ws;
  }, [token]);

  // 心跳检测
  const startHeartbeat = (ws: WebSocket) => {
    heartbeatTimerRef.current = setInterval(() => {
      if (ws.readyState === WebSocket.OPEN) {
        ws.send('ping');
        // 60s 内未收到 pong 则判定断线
        heartbeatTimeoutRef.current = setTimeout(() => {
          console.warn('[WS] Heartbeat timeout, reconnecting...');
          ws.close();
        }, HEARTBEAT_TIMEOUT);
      }
    }, HEARTBEAT_INTERVAL);
  };

  const resetHeartbeatTimeout = () => {
    if (heartbeatTimeoutRef.current) {
      clearTimeout(heartbeatTimeoutRef.current);
    }
  };

  const clearHeartbeat = () => {
    if (heartbeatTimerRef.current) clearInterval(heartbeatTimerRef.current);
    if (heartbeatTimeoutRef.current) clearTimeout(heartbeatTimeoutRef.current);
  };

  // 指数退避重连
  const scheduleReconnect = () => {
    const delay = RECONNECT_DELAYS[reconnectAttempts.current] || 16000;
    reconnectTimerRef.current = setTimeout(() => {
      reconnectAttempts.current++;
      options.onReconnect?.();
      connect();
    }, delay);
  };

  // 发送消息（断线时入本地队列）
  const send = useCallback((data: unknown) => {
    const payload = JSON.stringify(data);
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(payload);
    } else {
      pendingMessages.current.push(payload);
      message.warning('消息已入队，连接恢复后自动发送');
    }
  }, []);

  const disconnect = useCallback(() => {
    clearHeartbeat();
    if (reconnectTimerRef.current) clearTimeout(reconnectTimerRef.current);
    wsRef.current?.close(1000, 'User disconnect');
    wsRef.current = null;
    setIsConnected(false);
  }, []);

  useEffect(() => {
    connect();
    return () => disconnect();
  }, [connect, disconnect]);

  return { isConnected, send, disconnect };
}
```

### 4.2 WebSocket 消息类型对照

| 服务端 → 客户端 | type | 说明 |
|:--|------|------|
| 审批卡片推送 | `approval_card` | 新审批工单通知 |
| 审批结果推送 | `approval_result` | 审批状态变更 |
| 任务进度推送 | `task_progress` | DAG 节点状态更新 |
| 系统通知 | `system_notification` | 系统公告/告警 |
| 权限变更通知 | `permission_changed` | 强制刷新权限 |
| 心跳响应 | `pong` | 心跳确认 |

| 客户端 → 服务端 | type | 说明 |
|:--|------|------|
| 审批响应 | `approval_response` | 用户同意/拒绝 |
| 聊天消息 | `chat_message` | 用户发送消息 |
| 心跳请求 | `ping` | 心跳探活 |

---

## 五、虚拟滚动

### 5.1 设计原理

长会话（>100 条消息）场景下，全量渲染 DOM 节点会导致严重卡顿。使用 `react-window` 的 `VariableSizeList` 实现虚拟滚动：

```typescript
// src/components/Chat/MessageList.tsx
import { useRef, useCallback } from 'react';
import { VariableSizeList } from 'react-window';
import { MessageBubble } from './MessageBubble';
import type { Message } from '@/types/message';

interface MessageListProps {
  messages: Message[];
  loadMore: () => void;  // 滚动到顶部加载更早的消息
  hasMore: boolean;
}

// 预估每条消息的高度
const ESTIMATED_HEIGHT = 120;

export function MessageList({ messages, loadMore, hasMore }: MessageListProps) {
  const listRef = useRef<VariableSizeList>(null);
  const sizeMap = useRef<Map<number, number>>(new Map());

  // 动态获取每条消息的真实高度
  const getItemSize = useCallback((index: number) => {
    return sizeMap.current.get(index) || ESTIMATED_HEIGHT;
  }, []);

  // 消息渲染后更新高度缓存
  const onItemRendered = useCallback((index: number, height: number) => {
    sizeMap.current.set(index, height);
    listRef.current?.resetAfterIndex(index);
  }, []);

  // 新消息到达自动滚动到底部
  const scrollToBottom = useCallback(() => {
    listRef.current?.scrollToItem(messages.length - 1, 'end');
  }, [messages.length]);

  return (
    <VariableSizeList
      ref={listRef}
      height={window.innerHeight - 200}  // 减去 Header + InputArea
      itemCount={messages.length}
      itemSize={getItemSize}
      width="100%"
      onItemsRendered={({ visibleStopIndex }) => {
        // 滚动到顶部时加载更多
        if (visibleStopIndex === 0 && hasMore) {
          loadMore();
        }
      }}
    >
      {({ index, style }) => (
        <div style={style}>
          <MessageBubble
            message={messages[index]}
            onHeightReady={(h) => onItemRendered(index, h)}
          />
        </div>
      )}
    </VariableSizeList>
  );
}
```

---

## 六、聊天状态管理

### 6.1 Zustand Store

```typescript
// src/stores/useChatStore.ts
import { create } from 'zustand';
import type { Message } from '@/types/message';
import type { SSEEvent } from '@/types/sse';

interface ChatState {
  // 当前会话
  conversationId: string | null;

  // 消息列表
  messages: Message[];

  // 流式状态
  isStreaming: boolean;
  streamingMessageId: string | null;

  // 审批状态
  pendingApprovalId: string | null;

  // 操作
  setConversation: (id: string) => void;
  addUserMessage: (content: string) => void;
  appendAssistantMessage: () => string;
  appendToken: (msgId: string, token: string) => void;
  appendThinking: (msgId: string, content: string) => void;
  appendToolCall: (msgId: string, event: SSEEvent) => void;
  appendToolResult: (msgId: string, event: SSEEvent) => void;
  finishMessage: (msgId: string) => void;
  setMessageError: (msgId: string, error: string) => void;
  setApprovalRequired: (approvalId: string) => void;
  clearMessages: () => void;
  prependMessages: (messages: Message[]) => void; // 加载历史
}

let msgCounter = 0;

export const useChatStore = create<ChatState>((set, get) => ({
  conversationId: null,
  messages: [],
  isStreaming: false,
  streamingMessageId: null,
  pendingApprovalId: null,

  setConversation: (id) => set({ conversationId: id }),

  addUserMessage: (content) => {
    const msg: ChatMessage = {
      id: `user-${++msgCounter}`,
      conversationId: get().conversationId!,
      type: 'message',
      role: 'user',
      content,
      timestamp: Date.now(),
      status: 'sent',
    };
    set(state => ({ messages: [...state.messages, msg] }));
  },

  appendAssistantMessage: () => {
    const id = `assistant-${++msgCounter}`;
    const msg: ChatMessage = {
      id,
      conversationId: get().conversationId!,
      type: 'message',
      role: 'assistant',
      content: '',
      timestamp: Date.now(),
      status: 'pending',
    };
    set(state => ({
      messages: [...state.messages, msg],
      isStreaming: true,
      streamingMessageId: id,
    }));
    return id;
  },

  appendToken: (msgId, token) => {
    set(state => ({
      messages: state.messages.map(m =>
        m.id === msgId && m.type === 'message'
          ? { ...m, content: m.content + token }
          : m
      ),
    }));
  },

  appendThinking: (msgId, content) => {
    // 思考过程折叠显示
    set(state => ({
      messages: state.messages.map(m =>
        m.id === msgId && m.type === 'message'
          ? { ...m, content: m.content + `\n\n<details><summary>🤔 思考过程</summary>\n\n${content}\n\n</details>` }
          : m
      ),
    }));
  },

  appendToolCall: (msgId, event) => {
    set(state => ({
      messages: state.messages.map(m =>
        m.id === msgId && m.type === 'message'
          ? { ...m, content: m.content + `\n\n🔧 **调用工具**: \`${event.toolName}\`\n` }
          : m
      ),
    }));
  },

  appendToolResult: (msgId, event) => {
    set(state => ({
      messages: state.messages.map(m =>
        m.id === msgId && m.type === 'message'
          ? { ...m, content: m.content + `\n✓ 工具返回: ${JSON.stringify(event.result).slice(0, 200)}...\n` }
          : m
      ),
    }));
  },

  finishMessage: (msgId) => {
    set(state => ({
      messages: state.messages.map(m =>
        m.id === msgId ? { ...m, status: 'delivered' } : m
      ),
      isStreaming: false,
      streamingMessageId: null,
    }));
  },

  setMessageError: (msgId, error) => {
    set(state => ({
      messages: state.messages.map(m =>
        m.id === msgId && m.type === 'message'
          ? { ...m, content: m.content + `\n\n❌ **错误**: ${error}`, status: 'failed' as const }
          : m
      ),
      isStreaming: false,
      streamingMessageId: null,
    }));
  },

  setApprovalRequired: (approvalId) => set({ pendingApprovalId: approvalId }),

  clearMessages: () => set({ messages: [], streamingMessageId: null }),

  prependMessages: (olderMessages) => {
    set(state => ({
      messages: [...olderMessages, ...state.messages],
    }));
  },
}));
```

---

## 七、页面布局

### 7.1 布局结构

```
┌──────────────────────────────────────────────────────────────┐
│  Header (56px)                                                │
│  [☰ 折叠] [Logo] [当前会话标题 ▾]          [通知] [用户头像] │
├────────────┬─────────────────────────────────────────────────┤
│ 侧边栏      │  消息列表区                                      │
│ (280px)    │                                                  │
│            │  ┌─────────────────────────────────────────┐    │
│ [🔍 搜索]  │  │ 👤 用户: 帮我分析这段代码               │    │
│ [✚ 新会话] │  └─────────────────────────────────────────┘    │
│            │  ┌─────────────────────────────────────────┐    │
│ ────────   │  │ 🤖 AI: 好的，请把代码发给我...          │    │
│ 会话1      │  │                                          │    │
│ 会话2 ✨   │  │  [👍] [👎]                               │    │
│ 会话3      │  └─────────────────────────────────────────┘    │
│            │  ┌─────────────────────────────────────────┐    │
│            │  │ 🔴 审批卡片                              │    │
│            │  │ 即将删除用户 [张三]                      │    │
│            │  │ ⏱ 剩余 4分32秒                           │    │
│            │  │ [✅ 同意] [❌ 拒绝]                      │    │
│            │  └─────────────────────────────────────────┘    │
│            │                                                  │
│            ├─────────────────────────────────────────────────┤
│            │  输入区域 (自适应高度)                            │
│            │  ┌──────────────────────────────────┐ [📎]     │
│            │  │ 输入您的问题...                   │ [▶ 发送] │
│            │  └──────────────────────────────────┘           │
└────────────┴─────────────────────────────────────────────────┘
```

### 7.2 消息气泡组件

```tsx
// src/components/Chat/MessageBubble.tsx
import { Avatar, Typography, Space } from 'antd';
import { UserOutlined, RobotOutlined } from '@ant-design/icons';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { ApprovalCard } from '@/components/ApprovalCard';
import { FeedbackButtons } from '@/components/FeedbackButtons';
import type { Message } from '@/types/message';

interface Props {
  message: Message;
  onHeightReady?: (height: number) => void;
}

export function MessageBubble({ message, onHeightReady }: Props) {
  const bubbleRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (bubbleRef.current) {
      onHeightReady?.(bubbleRef.current.offsetHeight);
    }
  }, [message.content]);

  // 审批卡片特殊渲染
  if (message.type === 'approval') {
    return <ApprovalCard message={message} />;
  }

  // 系统消息特殊渲染
  if (message.type === 'system') {
    return (
      <div className="system-message">
        <Typography.Text type="secondary">
          {message.level === 'error' ? '❌' : message.level === 'warning' ? '⚠️' : 'ℹ️'}{' '}
          {message.content}
        </Typography.Text>
      </div>
    );
  }

  const isUser = message.type === 'message' && message.role === 'user';

  return (
    <div ref={bubbleRef} className={`message-bubble ${isUser ? 'user' : 'assistant'}`}>
      <Space align="start">
        <Avatar icon={isUser ? <UserOutlined /> : <RobotOutlined />} />
        <div className="bubble-content">
          {isUser ? (
            <Typography.Paragraph>{message.content}</Typography.Paragraph>
          ) : (
            <ReactMarkdown
              components={{
                code({ inline, className, children, ...props }) {
                  const match = /language-(\w+)/.exec(className || '');
                  return !inline && match ? (
                    <SyntaxHighlighter language={match[1]} PreTag="div">
                      {String(children).replace(/\n$/, '')}
                    </SyntaxHighlighter>
                  ) : (
                    <code className={className} {...props}>{children}</code>
                  );
                },
              }}
            >
              {message.content}
            </ReactMarkdown>
          )}

          {/* AI 消息下方显示状态和反馈按钮 */}
          {!isUser && message.status === 'delivered' && (
            <div className="message-footer">
              <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                {message.metadata?.tokens && `Tokens: ${message.metadata.tokens}`}
                {message.metadata?.latency && ` · ${message.metadata.latency}ms`}
              </Typography.Text>
              <FeedbackButtons messageId={message.id} />
            </div>
          )}

          {/* 流式输出中的闪烁光标 */}
          {message.status === 'pending' && <span className="cursor-blink">▍</span>}

          {/* 发送失败重试按钮 */}
          {message.status === 'failed' && (
            <Typography.Text type="danger">发送失败，点击重试</Typography.Text>
          )}
        </div>
      </Space>
    </div>
  );
}
```

---

## 八、API 接口依赖

### 8.1 会话相关

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/conversations` | 创建新会话 |
| GET | `/api/v1/conversations` | 会话列表（分页） |
| GET | `/api/v1/conversations/{id}` | 会话详情 |
| PATCH | `/api/v1/conversations/{id}/title` | 修改会话标题 |
| DELETE | `/api/v1/conversations/{id}` | 删除会话 |

### 8.2 消息相关

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/conversations/{id}/messages` | 发送消息（非流式） |
| POST | `/api/v1/conversations/{id}/stream` | 🆕 SSE 流式对话 |
| GET | `/api/v1/conversations/{id}/messages` | 历史消息（分页） |
| PATCH | `/api/v1/conversations/{id}/messages/{msgId}/feedback` | 消息反馈 |

### 8.3 WebSocket

| 端点 | 说明 |
|------|------|
| `ws://host/ws?token={saToken}` | 实时消息推送 |

---

## 九、性能优化

| 优化项 | 方案 | 配置 |
|--------|------|------|
| **虚拟滚动** | react-window VariableSizeList | 仅渲染可视区域 ± 2 条 |
| **消息分页** | 滚动到顶加载更多 | 每页 50 条 |
| **Markdown 懒渲染** | 非可视区域延迟渲染 | IntersectionObserver |
| **图片懒加载** | `<img loading="lazy">` | 原生支持 |
| **代码分割** | React.lazy + Suspense | 按页面级 chunks |
| **API 缓存** | TanStack Query | staleTime: 5min |
| **防抖输入** | useDebounce 300ms | 减少不必要的自动保存 |

---

## 十、安全考虑

1. **XSS 防护**: Markdown 渲染使用 `react-markdown` 的 `remarkRehype` 安全选项，禁止原始 HTML
2. **内容过滤**: 前端对用户输入做基础长度校验（≤4000 字符），后端安全围栏深度过滤
3. **Token 管理**: Sa-Token 自动续期，WebSocket 连接携带 Token 鉴权
4. **敏感信息**: 审批卡片中的敏感字段（如用户手机号）使用 `****` 脱敏展示

---

> 📋 关联文档: [[前端架构设计方案]] | [[审批卡片交互技术方案]] | [[用户反馈技术方案]] | [[前后端权限对应技术方案]]
