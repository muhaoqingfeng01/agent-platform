# 消息收发与 SSE 流式响应

## 所属阶段
**P1 核心能力 → T3 意图识别与对话管理**

## 使用技术
- Spring Web MVC `SseEmitter`（流式响应）
- Spring AI（LLM 调用抽象）
- WebSocket（双向实时推送）
- MyBatis + XML Mapper（消息持久化，单表查询）

## 涉及数据库表
- `t_message` — 消息表
- `t_conversation` — 更新 message_count / total_tokens

## 设计模式应用

| 设计模式 | 应用场景 | 核心价值 |
|----------|----------|----------|
| **Observer** | SSE 事件订阅 / WebSocket 推送 | 一对多通知，解耦生产者与消费者 |
| **Strategy** | 消息发送模式（stream vs non-stream） | 运行时选择发送策略 |
| **Builder** | 消息实体 / 事件构建 | 链式构造复杂对象 |
| **Chain of Responsibility** | 消息处理管道（校验→意图→执行→响应） | 每个处理节点独立，可插拔 |
| **Template Method** | 消息存储骨架（save → update counter → notify） | 统一流程，保证一致性 |
| **Factory Method** | SSE 事件工厂（token/tool_call/done/error） | 统一事件格式，类型安全 |
| **Mediator** | WebSocket Session 管理（ConcurrentHashMap 注册中心） | 集中管理连接，避免点对点耦合 |
| **Command** | WebSocket 消息类型路由（type → handler） | 开放-封闭原则，新增消息类型不改路由逻辑 |

---

## API 端点

| 方法 | 路径 | 说明 | Content-Type |
|------|------|------|:--:|
| POST | `/api/v1/conversations/{id}/messages` | 发送消息（非流式） | `application/json` |
| POST | `/api/v1/conversations/{id}/stream` | 发送消息（SSE 流式） | `text/event-stream` |
| GET | `/api/v1/conversations/{id}/messages` | 历史消息列表（分页） | `application/json` |
| GET | `/api/v1/conversations/{id}/messages?before={msgId}` | 加载更早的消息 | `application/json` |
| PATCH | `/api/v1/conversations/{id}/messages/{msgId}/feedback` | 消息反馈（点赞/点踩） | `application/json` |

---

## 实现方案

### 1. 整体架构（消息处理管道）

```
HTTP Request (POST /stream)
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│  MessagePipeline (Chain of Responsibility)               │
│                                                         │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────┐ │
│  │Validate  │──→│ Intent   │──→│ Execute  │──→│Stream│ │
│  │Filter    │   │Recognition│  │Orchestra │   │Output│ │
│  └──────────┘   └──────────┘   └──────────┘   └──────┘ │
│       │              │              │              │    │
│       ▼              ▼              ▼              ▼    │
│  [400 Reject]  [意图结果]     [工具调用]    [SSE Event] │
└─────────────────────────────────────────────────────────┘
    │
    ▼
┌──────────────────────────┐
│  Message Persistence      │
│  (Builder → Repository)   │
└──────────────────────────┘
    │
    ▼
┌──────────────────────────┐
│  Notification             │
│  (WebSocket + Event)     │
└──────────────────────────┘
```

### 2. SSE 流式响应（Observer 模式）

```java
/**
 * SSE 流式聊天端点 — Observer 模式的核心被观察者.
 * <p>
 * 使用 SseEmitter 实现服务端推送事件，支持：
 * - 逐 token 流式输出
 * - 工具调用过程透明展示
 * - 错误信息实时推送
 * - 超时自动断开
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/conversations/{id}")
@Tag(name = "对话管理", description = "消息收发与流式响应")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final MessageApplicationService messageService;
    private final StreamOrchestrationService streamService;
    private final ThreadPoolExecutor streamExecutor;  // 自定义线程池

    /**
     * SSE 流式聊天 — Strategy 模式选择流式策略.
     *
     * @param id 会话 ID
     * @param request 消息请求体
     * @return SseEmitter 事件发射器
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（SSE 流式）")
    public SseEmitter streamChat(@PathVariable String id,
                                  @Valid @RequestBody SendMessageRequest request) {
        // Factory Method: 创建超时 5 分钟的 Emitter
        SseEmitter emitter = SseEmitterFactory.create(300_000L);

        streamExecutor.submit(() -> {
            try {
                // Chain of Responsibility: 执行消息处理管道
                streamService.executeStreamPipeline(id, request.getContent(), emitter);
            } catch (Exception e) {
                log.error("[Conversation] SSE 流式异常: convId={}", id, e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * 非流式发送消息 — Strategy 模式选择同步策略.
     */
    @PostMapping("/messages")
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（非流式）")
    public Result<MessageResponse> sendMessage(@PathVariable String id,
                                                @Valid @RequestBody SendMessageRequest request) {
        log.info("[Conversation] 发送消息: convId={}, contentLen={}", id, request.getContent().length());
        MessageResponse response = messageService.sendAndWait(id, request);
        return Result.ok(response);
    }

    /**
     * 历史消息列表（分页）.
     */
    @GetMapping("/messages")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "历史消息列表")
    public Result<PageResponse<MessageResponse>> listMessages(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.ok(messageService.listMessages(id, page, size));
    }

    /**
     * 加载更早的消息（基于游标的分页）.
     */
    @GetMapping(value = "/messages", params = "before")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "加载更早的消息")
    public Result<List<MessageResponse>> loadBefore(@PathVariable String id,
                                                      @RequestParam String before) {
        return Result.ok(messageService.loadMessagesBefore(id, before, 50));
    }

    /**
     * 消息反馈（点赞/点踩）.
     */
    @PatchMapping("/messages/{msgId}/feedback")
    @SaCheckPermission("conversation:feedback")
    @Operation(summary = "消息反馈")
    public Result<Void> feedback(@PathVariable String id,
                                  @PathVariable String msgId,
                                  @Valid @RequestBody FeedbackRequest request) {
        messageService.updateFeedback(msgId, request.getFeedback());
        return Result.ok();
    }
}
```

### 3. 流式编排服务（Chain of Responsibility + Template Method）

```java
/**
 * 流式编排服务 — Chain of Responsibility 模式串联处理步骤.
 * <p>
 * 每个步骤是独立的责任节点，失败时立即终止链路.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StreamOrchestrationService {

    private final MessageApplicationService messageService;
    private final IntentRecognitionService intentService;
    private final SessionMemoryService sessionMemoryService;
    private final ChatClient chatClient;

    /** SSE 事件超时时间（毫秒） */
    private static final long SSE_TIMEOUT_MS = 300_000L;
    /** SSE 心跳间隔（毫秒） */
    private static final long HEARTBEAT_INTERVAL_MS = 15_000L;

    /**
     * 流式执行管道 — Chain of Responsibility.
     *
     * <pre>
     *   Step 1: 保存用户消息
     *   Step 2: 加载会话上下文（短期记忆 + 长期记忆）
     *   Step 3: 意图识别（规则→缓存→LLM）
     *   Step 4: 构建系统提示词 + 上下文
     *   Step 5: 调用 LLM 逐 token 推送
     *   Step 6: 保存助手回复
     *   Step 7: 异步提取长期记忆
     * </pre>
     */
    public void executeStreamPipeline(String conversationId, String userContent, SseEmitter emitter) {
        ScheduledExecutorService heartbeatExecutor = null;

        try {
            // Step 1: 保存用户消息（Builder 模式）
            Message userMsg = messageService.saveUserMessage(conversationId, userContent);

            // Step 2: 加载会话上下文
            List<Message> history = sessionMemoryService.getRecentMessages(conversationId, 10);

            // Step 3: 意图识别（参见 03-意图识别引擎）
            IntentResult intent = intentService.recognize(userContent);
            log.info("[Conversation] 意图识别: convId={}, intent={}, confidence={}",
                    conversationId, intent.getIntentCode(), intent.getConfidence());

            // Step 4: 构建完整上下文
            String systemPrompt = buildSystemPrompt(intent, history);
            String fullPrompt = buildFullPrompt(history, userContent);

            // Step 5: 启动心跳
            heartbeatExecutor = startHeartbeat(emitter);

            // Step 6: 推送 thinking 事件
            sendEvent(emitter, SseEventFactory.thinking("正在分析您的需求..."));

            // Step 7: 调用 LLM 流式输出（Observer 模式：每 token 推一个事件）
            StringBuilder fullResponse = new StringBuilder();
            AtomicInteger tokenCount = new AtomicInteger(0);

            chatClient.prompt()
                    .system(systemPrompt)
                    .user(fullPrompt)
                    .stream()
                    .chatResponse()
                    .doOnNext(resp -> {
                        String token = resp.getResult().getOutput().getContent();
                        fullResponse.append(token);
                        tokenCount.incrementAndGet();
                        sendEvent(emitter, SseEventFactory.token(token));
                    })
                    .doOnComplete(() -> {
                        // Step 8: 保存助手回复
                        Message assistantMsg = messageService.saveAssistantMessage(
                                conversationId, fullResponse.toString(), tokenCount.get());

                        // 发送完成事件
                        sendEvent(emitter, SseEventFactory.done(tokenCount.get(), assistantMsg.getMessageId()));
                        emitter.complete();

                        // Step 9: 异步提取长期记忆（不阻塞）
                        messageService.extractLongTermMemoryAsync(conversationId);
                    })
                    .doOnError(error -> {
                        log.error("[Conversation] LLM 调用失败: convId={}", conversationId, error);
                        sendEvent(emitter, SseEventFactory.error("LLM 调用失败", 500));
                        emitter.completeWithError(error);
                    })
                    .subscribe();  // 触发订阅执行

        } catch (Exception e) {
            log.error("[Conversation] 流式管道异常: convId={}", conversationId, e);
            sendEvent(emitter, SseEventFactory.error(e.getMessage(), 500));
            emitter.completeWithError(e);
        } finally {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
            }
        }
    }

    /**
     * 启动 SSE 心跳 — 防止代理/负载均衡器超时断开连接.
     */
    private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "sse-heartbeat"));
        executor.scheduleAtFixedRate(() -> {
            try {
                sendEvent(emitter, SseEmitter.event().name("ping").data(""));
            } catch (Exception ignored) {
                // 客户端已断开，忽略
            }
        }, HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        return executor;
    }

    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            // 客户端断开连接，标记完成
            emitter.completeWithError(e);
        }
    }

    private String buildFullPrompt(List<Message> history, String userContent) {
        StringBuilder sb = new StringBuilder();
        for (Message msg : history) {
            sb.append(msg.getRole().getLabel()).append(": ").append(msg.getContent()).append("\n");
        }
        sb.append("User: ").append(userContent);
        return sb.toString();
    }

    private String buildSystemPrompt(IntentResult intent, List<Message> history) {
        return """
            你是一个智能助手。请根据用户意图提供准确、有帮助的回答。

            当前意图: %s (置信度: %.2f)
            对话历史轮次: %d 轮
            """.formatted(intent.getIntentName(), intent.getConfidence(), history.size() / 2);
    }
}
```

### 4. SSE 事件工厂（Factory Method 模式）

```java
/**
 * SSE 事件工厂 — Factory Method 模式统一事件构建.
 * <p>
 * 确保所有 SSE 事件格式一致、ID 唯一、类型安全.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public final class SseEventFactory {

    /** 事件类型常量 */
    public static final String EVENT_TOKEN = "token";
    public static final String EVENT_TOOL_CALL = "tool_call";
    public static final String EVENT_TOOL_RESULT = "tool_result";
    public static final String EVENT_THINKING = "thinking";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_DONE = "done";

    private SseEventFactory() { /* 工具类禁止实例化 */ }

    /** 逐 token 文本事件 */
    public static SseEmitter.SseEventBuilder token(String token) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TOKEN)
                .data(token);
    }

    /** 工具调用开始事件 */
    public static SseEmitter.SseEventBuilder toolCall(String toolName, Map<String, Object> params) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TOOL_CALL)
                .data(Map.of("tool", toolName, "status", "calling", "params", params));
    }

    /** 工具调用结果事件 */
    public static SseEmitter.SseEventBuilder toolResult(String toolName, Object result) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_TOOL_RESULT)
                .data(Map.of("tool", toolName, "status", "done", "result", result));
    }

    /** 思考状态提示 */
    public static SseEmitter.SseEventBuilder thinking(String message) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_THINKING)
                .data(message);
    }

    /** 错误事件 */
    public static SseEmitter.SseEventBuilder error(String message, int code) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_ERROR)
                .data(Map.of("code", code, "message", message));
    }

    /** 完成事件 */
    public static SseEmitter.SseEventBuilder done(int totalTokens, String messageId) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(EVENT_DONE)
                .data(Map.of(
                        "status", "completed",
                        "tokens", totalTokens,
                        "messageId", messageId
                ));
    }
}

/**
 * SseEmitter 工厂.
 */
public final class SseEmitterFactory {

    private SseEmitterFactory() {}

    /**
     * 创建一个标准超时的 SseEmitter.
     *
     * @param timeoutMs 超时毫秒数
     * @return 配置好的 SseEmitter
     */
    public static SseEmitter create(long timeoutMs) {
        SseEmitter emitter = new SseEmitter(timeoutMs);

        // 注册超时/完成/错误回调
        emitter.onTimeout(() -> log.warn("[SSE] 连接超时"));
        emitter.onCompletion(() -> log.debug("[SSE] 流式完成"));
        emitter.onError(ex -> log.error("[SSE] 连接错误", ex));

        return emitter;
    }
}
```

### 5. SSE 事件类型规范

| 事件名 | 数据格式 | 说明 | 触发时机 |
|--------|----------|------|----------|
| `ping` | `""` | 心跳，每 15s | 流式进行中 |
| `thinking` | `"正在分析您的需求..."` | 思考状态提示 | LLM 调用前 |
| `token` | `"你好"` | 逐 token 文本 | LLM 流式输出中 |
| `tool_call` | `{"tool":"order_query","status":"calling"}` | 工具调用开始 | 执行工具前 |
| `tool_result` | `{"tool":"order_query","result":{...}}` | 工具调用结果 | 工具返回后 |
| `error` | `{"code":500,"message":"..."}` | 错误信息 | 异常发生时 |
| `done` | `{"status":"completed","tokens":1250,"messageId":"msg_xxx"}` | 流式完成 | LLM 输出完毕 |

### 6. 消息实体与 Builder 模式

```java
/**
 * 消息领域实体 — Builder 模式构建.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder
public class Message {

    private String messageId;
    private String conversationId;
    private MessageRole role;
    private String content;
    private Integer tokenCount;
    private Map<String, Object> metadata;
    private FeedbackType feedback;
    private LocalDateTime createdAt;

    /**
     * 是否为有效消息内容.
     */
    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    /**
     * 估算内容 token 数（中文字符 × 0.5 + 英文单词 × 1.3）.
     */
    public int estimateTokens() {
        if (content == null) return 0;
        int chineseChars = 0;
        int englishWords = 0;
        for (char c : content.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                chineseChars++;
            }
        }
        // 非中文字符按空格拆词
        String nonChinese = content.replaceAll("[\\u4e00-\\u9fff]", "");
        englishWords = nonChinese.isBlank() ? 0 : nonChinese.split("\\s+").length;
        return (int) Math.ceil(chineseChars * 0.5 + englishWords * 1.3);
    }

    /**
     * 更新反馈.
     */
    public void updateFeedback(FeedbackType type) {
        this.feedback = type;
    }
}

/**
 * 消息角色枚举.
 */
public enum MessageRole {
    USER("用户"),
    ASSISTANT("助手"),
    SYSTEM("系统"),
    TOOL("工具");

    @Getter
    private final String label;

    MessageRole(String label) { this.label = label; }
}

/**
 * 反馈类型枚举.
 */
public enum FeedbackType {
    LIKE,
    DISLIKE
}
```

### 7. 消息存储服务（Template Method 模式）

```java
/**
 * 消息应用服务 — Template Method 定义消息存储骨架.
 * <p>
 * 所有消息存储操作遵循统一流程：构建 → 持久化 → 更新计数器 → 通知.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageApplicationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationWebSocketHandler webSocketHandler;
    private final LongTermMemoryService longTermMemoryService;

    /**
     * 保存用户消息.
     *
     * @return 持久化后的消息实体
     */
    @Transactional
    public Message saveUserMessage(String conversationId, String content) {
        return saveMessage(conversationId, MessageRole.USER, content, Map.of());
    }

    /**
     * 保存助手消息.
     */
    @Transactional
    public Message saveAssistantMessage(String conversationId, String content, int tokenCount) {
        return saveMessage(conversationId, MessageRole.ASSISTANT, content,
                Map.of("tokenCount", tokenCount));
    }

    /**
     * 发送并等待完整回复（非流式）— Strategy 模式.
     */
    @Transactional
    public MessageResponse sendAndWait(String conversationId, SendMessageRequest request) {
        // Step 1: 保存用户消息
        Message userMsg = saveUserMessage(conversationId, request.getContent());

        // Step 2: 调用 LLM 获取完整回复
        String fullResponse = callLLMSync(conversationId, request.getContent());

        // Step 3: 保存助手消息
        Message assistantMsg = saveAssistantMessage(conversationId, fullResponse,
                assistantMsg.estimateTokens());

        return MessageAssembler.toResponse(assistantMsg);
    }

    /**
     * Template Method: 消息存储骨架.
     *
     * <pre>
     *   1. 构建 Message 实体（Builder 模式）
     *   2. 持久化到 MySQL（Repository 模式）
     *   3. 更新会话计数（conversation.messageCount++）
     *   4. 更新会话 Token（conversation.totalTokens += ...）
     *   5. 写入短期记忆（Redis）
     *   6. WebSocket 推送新消息通知（Observer 模式）
     * </pre>
     */
    private Message saveMessage(String conversationId, MessageRole role,
                                 String content, Map<String, Object> metadata) {
        // 1. 构建实体
        Message message = Message.builder()
                .messageId(IdGenerator.generate("msg"))
                .conversationId(conversationId)
                .role(role)
                .content(content)
                .tokenCount(estimateTokens(content))
                .metadata(metadata != null ? metadata : new HashMap<>())
                .createdAt(LocalDateTime.now())
                .build();

        // 2. 持久化
        messageRepository.save(message);

        // 3-4. 更新会话统计
        conversationRepository.incrementMessageCount(conversationId, 1);
        conversationRepository.addTokens(conversationId, message.getTokenCount());

        // 5. 短期记忆（Redis）
        sessionMemoryService.appendMessage(conversationId, message);

        // 6. WebSocket 通知
        webSocketHandler.pushMessage(conversationId, WebSocketMessage.builder()
                .type("new_message")
                .payload(MessageAssembler.toResponse(message))
                .timestamp(System.currentTimeMillis())
                .build());

        return message;
    }

    /**
     * 异步提取长期记忆 — 不阻塞主流程.
     */
    @Async
    public void extractLongTermMemoryAsync(String conversationId) {
        longTermMemoryService.extractAndSave(conversationId, TenantContext.getCurrentUserId());
    }

    private int estimateTokens(String content) {
        if (content == null) return 0;
        return (int) Math.ceil(content.length() * 0.5);
    }
}
```

### 8. WebSocket 推送（Mediator 模式）

```java
/**
 * WebSocket 会话管理器 — Mediator 模式集中管理所有连接.
 * <p>
 * 使用 ConcurrentHashMap 维护 userId → WebSocketSession 映射，
 * 作为所有 WebSocket 消息的中介者，避免模块间点对点耦合.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class ConversationWebSocketHandler extends TextWebSocketHandler {

    /** 中介者注册表：userId → WebSocketSession */
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        if (userId != null) {
            // 关闭旧连接（同一用户只保留最新连接）
            WebSocketSession oldSession = sessions.put(userId, session);
            closeQuietly(oldSession);
            log.info("[WebSocket] 连接建立: userId={}, sessionId={}", userId, session.getId());
        } else {
            closeQuietly(session);
            log.warn("[WebSocket] 无法识别用户，关闭连接");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            sessions.remove(userId, session);  // 仅移除当前 session，防止误删新连接
            log.info("[WebSocket] 连接关闭: userId={}, status={}", userId, status);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 客户端主动发送的消息（如心跳 ping），回 pong
        String payload = message.getPayload();
        if ("ping".equals(payload)) {
            sendMessage(session, new TextMessage("pong"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("[WebSocket] 传输错误: userId={}", extractUserId(session), exception);
        closeQuietly(session);
    }

    /**
     * 向指定用户推送消息.
     */
    public void pushMessage(String userId, WebSocketMessage message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = JsonUtil.toJson(message);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error("[WebSocket] 推送失败: userId={}", userId, e);
                sessions.remove(userId);
            }
        }
    }

    /**
     * 广播消息给所有在线用户.
     */
    public void broadcast(WebSocketMessage message) {
        String json = JsonUtil.toJson(message);
        sessions.forEach((userId, session) -> {
            if (session.isOpen()) {
                sendMessage(session, new TextMessage(json));
            }
        });
    }

    /**
     * 获取在线用户数.
     */
    public int getOnlineCount() {
        return sessions.size();
    }

    private String extractUserId(WebSocketSession session) {
        // 从 WebSocket 握手时的 Header/Token 中提取 userId
        String token = session.getHandshakeHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return StpUtil.getLoginIdByToken(token.substring(7)).toString();
        }
        return null;
    }

    private void closeQuietly(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            try { session.close(); } catch (IOException ignored) {}
        }
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (IOException ignored) {}
    }
}

/**
 * WebSocket 消息体 — Builder 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
public class WebSocketMessage {
    /** 消息类型: new_message, conversation_created, approval, progress, notification */
    private String type;
    /** 消息载荷 */
    private Object payload;
    /** 毫秒时间戳 */
    private long timestamp;
}
```

### 9. WebSocket 配置

```java
/**
 * WebSocket 配置 — 注册 Handler 与拦截器.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ConversationWebSocketHandler handler;

    public WebSocketConfig(ConversationWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/conversation")
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketAuthInterceptor());
    }
}

/**
 * WebSocket 认证拦截器 — 握手阶段校验 Token.
 */
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            return false;  // 拒绝无 Token 的连接
        }
        String token = authHeaders.get(0).replace("Bearer ", "");
        try {
            Object loginId = StpUtil.getLoginIdByToken(token);
            attributes.put("userId", loginId.toString());
            return true;
        } catch (Exception e) {
            log.warn("[WebSocket] Token 校验失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
```

### 10. MyBatis Mapper XML（消息持久化）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.agent.infrastructure.persistence.mapper.MessageMapper">

    <resultMap id="MessagePOMap" type="com.example.agent.infrastructure.persistence.po.MessagePO">
        <id column="id" property="id"/>
        <result column="conversation_id" property="conversationId"/>
        <result column="message_id" property="messageId"/>
        <result column="role" property="role"/>
        <result column="content" property="content"/>
        <result column="token_count" property="tokenCount"/>
        <result column="metadata_json" property="metadataJson"/>
        <result column="feedback" property="feedback"/>
        <result column="created_at" property="createdAt"/>
    </resultMap>

    <insert id="insert" parameterType="MessagePO">
        INSERT INTO t_message (conversation_id, message_id, role, content,
                                token_count, metadata_json, created_at)
        VALUES (#{conversationId}, #{messageId}, #{role}, #{content},
                #{tokenCount}, #{metadataJson}, #{createdAt})
    </insert>

    <!--
        历史消息分页查询 — 单表查询，按创建时间倒序.
        必须有 ORDER BY，防止分页数据重复.
    -->
    <select id="selectByConversationId" resultMap="MessagePOMap">
        SELECT id, conversation_id, message_id, role, content,
               token_count, metadata_json, feedback, created_at
        FROM t_message
        WHERE conversation_id = #{conversationId}
        ORDER BY created_at DESC
        LIMIT #{offset}, #{size}
    </select>

    <!-- 加载更早的消息（基于游标） -->
    <select id="selectBefore" resultMap="MessagePOMap">
        SELECT id, conversation_id, message_id, role, content,
               token_count, metadata_json, feedback, created_at
        FROM t_message
        WHERE conversation_id = #{conversationId}
          AND created_at &lt; (
              SELECT created_at FROM t_message WHERE message_id = #{beforeMessageId}
          )
        ORDER BY created_at DESC
        LIMIT #{size}
    </select>

    <update id="updateFeedback">
        UPDATE t_message
        SET feedback = #{feedback}
        WHERE message_id = #{messageId}
    </update>
</mapper>
```

### 11. 线程池配置（遵循开发规范）

```java
/**
 * 流式响应线程池 — ThreadPoolExecutor 创建，禁用 Executors.
 */
@Configuration
public class StreamThreadPoolConfig {

    @Bean("streamExecutor")
    public ThreadPoolExecutor streamExecutor() {
        return new ThreadPoolExecutor(
                4,                              // 核心线程数
                8,                              // 最大线程数
                60L, TimeUnit.SECONDS,          // 空闲存活时间
                new LinkedBlockingQueue<>(200),  // 有界队列
                new ThreadFactoryBuilder()
                        .setNameFormat("stream-pool-%d")
                        .setDaemon(true)
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略：调用者线程执行
        );
    }
}
```

### 12. 测试策略

```
SseEventFactoryTest
├── token_ReturnsEventWithCorrectName
├── toolCall_IncludesToolNameAndStatus
├── done_IncludesTokenCountAndMessageId
├── error_IncludesCodeAndMessage

StreamOrchestrationServiceTest
├── executeStreamPipeline_ValidInput_CompletesSuccessfully
├── executeStreamPipeline_LLMError_SendsErrorEvent
├── executeStreamPipeline_ClientDisconnect_CompletesWithError

MessageApplicationServiceTest
├── saveUserMessage_PersistsAndUpdatesConversation
├── saveAssistantMessage_IncrementsTokenCount
├── listMessages_ReturnsPagedResults

WebSocketHandlerTest
├── afterConnectionEstablished_ValidToken_RegistersSession
├── afterConnectionEstablished_InvalidToken_ClosesConnection
├── pushMessage_SessionOpen_SendsMessage
├── pushMessage_SessionClosed_RemovesFromMap
```

---

## 关键设计决策

1. **Observer 模式驱动 SSE** — 每个 token/工具调用/错误都是独立事件，前端可按需订阅不同类型
2. **Chain of Responsibility 编排消息管道** — 校验→意图→执行→响应各步骤独立，新增步骤不改现有代码
3. **Factory Method 统一事件构建** — SseEventFactory 确保所有事件格式一致，杜绝手写 JSON 字符串
4. **Mediator 模式管理 WebSocket** — ConcurrentHashMap 集中注册所有连接，各模块通过 pushMessage() 发送而不直接操作 Session
5. **Builder 模式构建消息实体** — 7 个字段通过链式调用赋值，避免构造函数参数列表爆炸
6. **Strategy 模式区分流式/非流式** — 运行时根据端点选择策略，同一服务支持两种模式
7. **ThreadPoolExecutor 而非 Executors** — 遵守阿里规范，有界队列 + CallerRunsPolicy 防止 OOM

---

## 异常处理

| 场景 | 异常 | HTTP 状态码 | SSE 事件 |
|------|------|:--:|:--:|
| 会话不存在 | `ResourceNotFoundException` | 404 | `error` |
| 会话已关闭/归档 | `IllegalStateException` | 400 | `error` |
| LLM 调用超时 | `TimeoutException` | 504 | `error` |
| Token 不足 | `AccessDeniedException` | 401 | — |
| 客户端断开 | `IOException` | — | `completeWithError` |
| 限流触发 | `SentinelBlockException` | 429 | `error` |
