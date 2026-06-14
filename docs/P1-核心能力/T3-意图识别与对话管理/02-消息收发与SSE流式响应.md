# 消息收发与 SSE 流式响应

## 所属阶段
**P1 核心能力 → T3 意图识别与对话管理**

## 使用技术
- Spring Web MVC `SseEmitter`
- Spring AI（LLM 调用）
- WebSocket（双向实时推送）

## 涉及数据库表
- `t_message`

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/conversations/{id}/messages` | 发送消息（非流式） |
| POST | `/api/v1/conversations/{id}/stream` | 发送消息（SSE 流式） |
| GET | `/api/v1/conversations/{id}/messages` | 历史消息列表（分页） |
| GET | `/api/v1/conversations/{id}/messages?before={msgId}` | 加载更早的消息 |

## 实现方案

### 1. SSE 流式响应

```java
@PostMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter streamChat(@PathVariable String id, @RequestBody SendMessageRequest request) {
    SseEmitter emitter = new SseEmitter(300_000L);  // 5分钟超时

    executorService.submit(() -> {
        try {
            // 1. 保存用户消息
            Message userMsg = saveUserMessage(id, request.getContent());

            // 2. 调用编排引擎，逐 token 推送
            orchestrationService.streamExecute(id, request.getContent(), token -> {
                try {
                    emitter.send(SseEmitter.event()
                            .id(UUID.randomUUID().toString())
                            .name("token")
                            .data(token));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            });

            // 3. 发送完成事件
            emitter.send(SseEmitter.event()
                    .name("done")
                    .data("{\"status\":\"completed\"}"));

            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    });

    return emitter;
}
```

### 2. SSE 事件类型

| 事件名 | 数据格式 | 说明 |
|--------|----------|------|
| `token` | `"你好"` | 逐 token 文本 |
| `tool_call` | `{"tool":"order_query","status":"calling"}` | 工具调用开始 |
| `tool_result` | `{"tool":"order_query","result":{...}}` | 工具调用结果 |
| `thinking` | `"正在查询订单信息..."` | 思考状态提示 |
| `error` | `{"code":500,"t_message":"..."}` | 错误信息 |
| `done` | `{"status":"completed","tokens":1250}` | 完成 |

### 3. WebSocket 推送

```java
@Component
public class ConversationWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        sessions.put(userId, session);
    }

    public void pushMessage(String userId, WebSocketMessage t_message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(JsonUtil.toJson(t_message)));
        }
    }
}

// 消息格式
@Data
public class WebSocketMessage {
    private String type;    // t_message, approval, progress, notification
    private Object payload;
    private long timestamp;
}
```

### 4. 消息存储

```java
@Service
public class MessageService {

    @Transactional
    public Message saveMessage(String conversationId, MessageRole role, String content, Map<String, Object> metadata) {
        Message msg = Message.builder()
                .messageId(IdGenerator.generate("msg"))
                .conversationId(conversationId)
                .role(role)
                .content(content)
                .tokenCount(estimateTokens(content))
                .metadataJson(metadata)
                .build();
        messageRepository.save(msg);

        // 更新会话计数
        conversationRepository.incrementMessageCount(conversationId, 1);
        conversationRepository.addTokens(conversationId, msg.getTokenCount());

        return msg;
    }
}
```
