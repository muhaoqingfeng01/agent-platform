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
| **Strategy + Factory** | 交互模式路由（`InteractionStrategy` + `InteractionStrategyFactory`） | 新增模式不改 Controller，开闭原则 |
| **Observer** | SSE 事件订阅 | 一对多通知，解耦生产者与消费者 |
| **Builder** | 消息实体 / 事件构建 | 链式构造复杂对象 |
| **Template Method** | 消息存储骨架（save → update counter → notify） | 统一流程，保证一致性 |
| **Factory Method** | SSE 事件工厂（`SseEventFactory`）/ 上下文构建（`InteractionContext.forXxx()`） | 统一事件格式，类型安全 |

---

## API 端点

| 方法 | 路径 | 说明 | Content-Type |
|------|------|------|:--:|
| POST | `/api/v1/conversations/messages/send` | 发送消息（非流式） | `application/json` |
| POST | `/api/v1/conversations/messages/stream` | 发送消息（SSE 流式，支持 CONVERSATION / KNOWLEDGE_SEARCH 双模式） | `text/event-stream` |
| POST | `/api/v1/conversations/messages/list` | 历史消息列表（分页） | `application/json` |
| POST | `/api/v1/conversations/messages/before` | 加载更早的消息（游标分页） | `application/json` |
| POST | `/api/v1/conversations/messages/feedback` | 消息反馈（点赞/点踩） | `application/json` |

> **注意**: 所有端点统一使用 POST 方法，路径中的业务 ID（conversationId、messageId 等）均通过 Request Body 传递，
> 不使用 `@PathVariable`。流式端点通过 `InteractionApplicationService` → `InteractionStrategyFactory` 路由到对应策略，
> Controller 自身不包含 `if-else` 模式分发逻辑。

---

## 实现方案

### 1. 整体架构（策略路由模式）

```
HTTP Request (POST /api/v1/conversations/messages/stream)
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│  MessageController#streamChat  (纯粹 HTTP 适配层)             │
│                                                             │
│  1. SseEmitterFactory.create(300_000L)  — 创建 SSE 发射器    │
│  2. interactionService.executeStream(                       │
│       mode, content, conversationId, knowledgeId, emitter)  │
│     → 模式解析 + 策略路由下沉到 Application 层                │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  InteractionApplicationService#executeStream                 │
│                                                             │
│  resolveMode(modeCode) → InteractionMode                    │
│    ├─ null/blank → CONVERSATION (默认)                       │
│    ├─ "CONVERSATION" → CONVERSATION                         │
│    ├─ "KNOWLEDGE_SEARCH" → KNOWLEDGE_SEARCH                  │
│    └─ invalid → log.warn + CONVERSATION (安全回退)           │
│                                                             │
│  strategyFactory.getStrategy(mode) → InteractionStrategy     │
│  streamExecutor.submit(() → strategy.executeStream(ctx))    │
└───────────────────────────┬─────────────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              │                           │
              ▼ CONVERSATION              ▼ KNOWLEDGE_SEARCH
┌─────────────────────────┐   ┌──────────────────────────────┐
│ ConversationInteraction │   │ KnowledgeSearchInteraction    │
│ Strategy                │   │ Strategy                     │
│ (委托 StreamOrchSvc)    │   │ (委托 KnowledgeSearchStream   │
│                         │   │  Service — RAG 流式管线)      │
│ ● 保存用户消息           │   │                              │
│ ● 意图识别（3层链）      │   │ ● 保存用户消息                │
│ ● 构建 Prompt + LLM流式  │   │ ● 上下文增强检索              │
│ ● 保存助手消息           │   │ ● 无命中→友好提示             │
│ ● 长期记忆提取           │   │ ● 有命中→RAG Prompt + LLM流式 │
│                         │   │ ● 保存助手消息 + 长期记忆      │
└─────────────────────────┘   └──────────────────────────────┘
```

> **设计要点**: Controller 不包含模式分发逻辑（无 `if-else`）。模式解析与路由全部下沉到
> `InteractionApplicationService`，通过 `InteractionStrategyFactory` 自动发现策略实现。
> 新增交互模式无需修改 Controller，只需实现 `InteractionStrategy` 接口并标注 `@Component`。

### 2. MessageController — HTTP 适配层

```java
/**
 * 消息收发 Controller — 纯粹 HTTP 适配层.
 * <p>
 * 流式端点统一通过 {@link InteractionApplicationService} 路由到对应交互策略，
 * 支持 CONVERSATION（智能对话）和 KNOWLEDGE_SEARCH（知识库检索）两种模式。
 * 新增模式只需在策略层注册，Controller 无需改动。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "对话管理", description = "消息收发与流式响应")
public class MessageController {

    private final MessageApplicationService messageService;
    private final InteractionApplicationService interactionService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 非流式发送消息 — 仅保存用户消息，不调用 LLM.
     */
    @PostMapping("/api/v1/conversations/messages/send")
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（非流式）")
    public Result<MessageResponse> sendMessage(@Valid @RequestBody MessageSendRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.sendMessage", request, (req) ->
                MessageResponse.from(
                        messageService.saveUserMessage(req.getConversationId(), req.getContent())));
    }

    /**
     * SSE 流式聊天 — 统一通过 InteractionApplicationService 策略工厂路由.
     * <p>
     * 模式解析（默认 CONVERSATION / 异常回退）全部下沉到 Application 层，
     * Controller 仅负责 HTTP 适配：创建 SseEmitter → 委托 executeStream → 返回 emitter.
     */
    @PostMapping(value = "/api/v1/conversations/messages/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SaCheckPermission("conversation:send")
    @Operation(summary = "发送消息（SSE 流式）— 支持 CONVERSATION / KNOWLEDGE_SEARCH 双模式")
    public SseEmitter streamChat(@Valid @RequestBody MessageSendRequest request) {
        SseEmitter emitter = SseEmitterFactory.create(300_000L);
        interactionService.executeStream(
                request.getMode(),
                request.getContent(),
                request.getConversationId(),
                request.getKnowledgeId(),
                emitter);
        return emitter;
    }

    /**
     * 历史消息列表（分页）— POST + Request Body 传参.
     */
    @PostMapping("/api/v1/conversations/messages/list")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "历史消息列表")
    public Result<PageResponse<MessageResponse>> listMessages(@RequestBody MessageListRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.listMessages", request, (req) ->
                messageService.listMessages(req.getId(), req.getPage(), req.getSize()));
    }

    /**
     * 加载更早的消息（基于游标的分页）— POST + Request Body 传参.
     */
    @PostMapping("/api/v1/conversations/messages/before")
    @SaCheckPermission("conversation:read")
    @Operation(summary = "加载更早的消息")
    public Result<MessageListResponse> loadBefore(@Valid @RequestBody MessageLoadBeforeRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.loadBefore", request, (req) ->
                MessageListResponse.builder()
                        .records(messageService.loadMessagesBefore(req.getId(), req.getBefore(), 50))
                        .build());
    }

    /**
     * 消息反馈（点赞/点踩）— POST + Request Body 传参，附带发布 FeedbackEvent.
     */
    @PostMapping("/api/v1/conversations/messages/feedback")
    @SaCheckPermission("conversation:feedback")
    @Operation(summary = "消息反馈")
    public Result<Void> feedback(@Valid @RequestBody MessageFeedbackRequest request) {
        return ResultRespHelper.responseInvoke("MessageController.feedback", request, (req) -> {
            FeedbackType feedbackType = FeedbackType.fromCode(req.getFeedback());
            messageService.updateFeedback(req.getMsgId(), feedbackType);

            Long tenantId = TenantContext.getCurrentTenantId();
            eventPublisher.publishEvent(new MessageFeedbackEvent(
                    this, req.getMsgId(), req.getConversationId(), tenantId, feedbackType));

            return null;
        });
    }
}
```

### 3. 交互应用服务 — 策略路由核心（Strategy + Factory）

```java
/**
 * 交互应用服务 — 编排多模式交互流程.
 * <p>
 * Controller 通过本服务完成模式路由与策略调度，自身仅负责 HTTP 适配。
 * 这是 MessageController 和 InteractionController 共同依赖的核心服务。
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>同步交互：构建上下文 → 获取策略 → 执行 → 返回统一响应</li>
 *   <li>流式交互：构建上下文 → 获取策略 → 异步提交线程池 → SSE 推送</li>
 *   <li>模式查询：返回所有已注册的模式编码</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionApplicationService {

    private final InteractionStrategyFactory strategyFactory;
    private final ThreadPoolExecutor streamExecutor;

    /**
     * 流式执行交互（SSE 流式模式：智能对话、知识检索 RAG 等）.
     * <p>
     * 在独立线程中执行策略，通过 SseEmitter 推送结果。
     * <p>
     * <b>默认模式：</b>若 {@code modeCode} 为空或无效，默认回退到 {@code CONVERSATION}，
     * 确保旧版客户端不传 mode 字段时行为不变。
     */
    public void executeStream(String modeCode, String content, String conversationId,
                               String knowledgeId, SseEmitter emitter) {
        InteractionMode mode = resolveMode(modeCode);
        Long tenantId = TenantContext.getCurrentTenantId();
        String userId = TenantContext.getCurrentUserId();

        InteractionContext context = buildContext(mode, content, conversationId,
                knowledgeId, null, emitter);
        InteractionStrategy strategy = strategyFactory.getStrategy(mode);

        streamExecutor.submit(() -> {
            try {
                strategy.executeStream(context);
            } catch (Exception e) {
                log.error("[Interaction] 流式执行异常: mode={}", mode.getDesc(), e);
                emitter.completeWithError(e);
            }
        });
    }

    /**
     * 解析交互模式 — null/blank/invalid 时安全回退到 CONVERSATION.
     * <p>
     * 将模式解析逻辑从 Controller 层下沉到应用层，确保所有调用方行为一致。
     */
    private InteractionMode resolveMode(String modeCode) {
        if (modeCode == null || modeCode.isBlank()) {
            return InteractionMode.CONVERSATION;
        }
        try {
            return InteractionMode.fromCode(modeCode);
        } catch (IllegalArgumentException e) {
            log.warn("[Interaction] 不支持的交互模式: {}，回退到 CONVERSATION", modeCode);
            return InteractionMode.CONVERSATION;
        }
    }

    /**
     * 构建交互上下文 — 根据模式和参数选择合适的工厂方法.
     */
    private InteractionContext buildContext(InteractionMode mode, String content,
                                             String conversationId, String knowledgeId,
                                             Map<String, Object> searchConfig, Object emitter) {
        Long tenantId = TenantContext.getCurrentTenantId();

        return switch (mode) {
            case CONVERSATION -> InteractionContext.forConversation(
                    content, conversationId, tenantId,
                    TenantContext.getCurrentUserId(), emitter);
            case KNOWLEDGE_SEARCH -> {
                if (emitter != null) {
                    yield InteractionContext.forKnowledgeSearchStream(
                            content, conversationId, knowledgeId, tenantId,
                            TenantContext.getCurrentUserId(), emitter);
                }
                yield InteractionContext.forKnowledgeSearch(
                        content, knowledgeId, tenantId, searchConfig);
            }
        };
    }

    /** 查询所有已注册的交互模式编码 */
    public List<String> getRegisteredModeCodes() {
        return strategyFactory.getRegisteredModes().stream()
                .map(InteractionMode::getCode)
                .toList();
    }
}
```

### 3.1 策略接口与工厂

```java
/**
 * 交互策略接口 — 定义在 domain 层，实现在 application 层.
 * <p>
 * 所有交互模式（智能对话、知识检索、任务执行...）均实现此接口，
 * 由 {@link InteractionStrategyFactory} 自动发现并注入。
 */
public interface InteractionStrategy {

    /** 该策略对应的交互模式 */
    InteractionMode getMode();

    /** 同步执行（非流式） */
    Object execute(InteractionContext context);

    /**
     * 流式执行（SSE）— 默认委托给 execute().
     * <p>
     * 需要流式能力的策略（如 KNOWLEDGE_SEARCH、CONVERSATION）覆写此方法。
     */
    default void executeStream(InteractionContext context) {
        SseEmitter emitter = (SseEmitter) context.getEmitter();
        try {
            Object result = execute(context);
            emitter.send(SseEventFactory.done(0, "").data(result));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    /** 策略优先级（数字越小优先级越高） */
    int getPriority();
}
```

**策略工厂 — Spring InitializingBean + List\<T\> 自动发现：**
```java
@Component
public class InteractionStrategyFactory implements InitializingBean {

    private final List<InteractionStrategy> strategies;
    private final Map<InteractionMode, InteractionStrategy> strategyMap = new EnumMap<>(InteractionMode.class);

    @Override
    public void afterPropertiesSet() {
        for (InteractionStrategy strategy : strategies) {
            strategyMap.put(strategy.getMode(), strategy);
        }
        log.info("[InteractionStrategyFactory] 已注册 {} 个策略: {}", strategyMap.size(),
                strategyMap.keySet().stream().map(InteractionMode::getCode).toList());
    }

    public InteractionStrategy getStrategy(InteractionMode mode) { ... }
    public Set<InteractionMode> getRegisteredModes() { ... }
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
| `thinking` | `"正在检索知识库..."` | 状态提示 | 检索前 / LLM 调用前 |
| `token` | `"你好"` | 逐 token 文本 | LLM 流式输出中 |
| `references` | `[{"documentId":"doc-1","filename":"员工手册.pdf",...}]` | 文件引用列表 | 知识库检索有命中时（RAG 模式） |
| `tool_call` | `{"tool":"order_query","status":"calling"}` | 工具调用开始 | 执行工具前 |
| `tool_result` | `{"tool":"order_query","result":{...}}` | 工具调用结果 | 工具返回后 |
| `error` | `{"code":500,"message":"..."}` | 错误信息 | 异常发生时 |
| `done` | `{"status":"completed","tokens":1250,"messageId":"msg_xxx"}` | 流式完成 | 流式输出完毕 |

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

### 8. MyBatis Mapper XML（消息持久化）

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

### 9. 线程池配置（遵循开发规范）

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

### 10. 测试策略

```
SseEventFactoryTest
├── token_ReturnsEventWithCorrectName
├── toolCall_IncludesToolNameAndStatus
├── done_IncludesTokenCountAndMessageId
├── error_IncludesCodeAndMessage

InteractionApplicationServiceTest
├── executeStream_NullMode_DefaultsToConversation
├── executeStream_InvalidMode_FallsBackToConversation
├── executeStream_KnowledgeSearch_RoutesToCorrectStrategy

MessageApplicationServiceTest
├── saveUserMessage_PersistsAndUpdatesConversation
├── saveAssistantMessage_IncrementsTokenCount
├── listMessages_ReturnsPagedResults

MessageControllerTest
├── streamChat_ValidRequest_ReturnsSseEmitter
├── sendMessage_ValidRequest_ReturnsMessageResponse
├── feedback_ValidRequest_ReturnsOk
```

---

## 关键设计决策

1. **Strategy + Factory 驱动多模式路由** — `InteractionStrategy` 接口 + `InteractionStrategyFactory` 自动发现策略，新增交互模式只需实现接口并标注 `@Component`，Controller 零改动
2. **模式解析下沉到 Application 层** — `resolveMode()` 在 `InteractionApplicationService` 中统一处理 null/blank/invalid 回退，避免 Controller 重复实现
3. **Factory Method 统一事件构建** — `SseEventFactory` 确保所有 SSE 事件格式一致，杜绝手写 JSON 字符串
4. **Builder 模式构建消息实体** — 多个字段通过链式调用赋值，避免构造函数参数列表爆炸
5. **统一 POST + Request Body 传参** — 所有端点使用 POST 方法，业务 ID 通过 Request Body 传递，不使用 `@PathVariable`，保持 API 风格一致
6. **流式异步执行** — `InteractionApplicationService` 通过 `ThreadPoolExecutor` 异步提交策略执行，HTTP 线程立即返回 `SseEmitter`
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
