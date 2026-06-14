package com.example.agent.application.conversation;

import com.example.agent.application.intent.IntentRecognitionChain;
import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.memory.SessionMemoryService;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流式编排服务 — Chain of Responsibility + Observer 模式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamOrchestrationService {

    private final MessageApplicationService messageService;
    private final IntentRecognitionChain intentRecognitionChain;
    private final SessionMemoryService sessionMemoryService;
    private final ChatClient chatClient;

    private static final long HEARTBEAT_INTERVAL_MS = 15_000L;

    public void executeStreamPipeline(String conversationId, String userContent, SseEmitter emitter) {
        ScheduledExecutorService heartbeatExecutor = null;
        try {
            // Step 1: 保存用户消息
            Message userMsg = messageService.saveUserMessage(conversationId, userContent);

            // Step 2: 加载会话上下文
            List<Message> history = sessionMemoryService.getRecentMessages(conversationId, 10);

            // Step 3: 意图识别
            IntentResult intent = intentRecognitionChain.recognize(userContent);
            log.info("[Stream] 意图识别: convId={}, intent={}, confidence={}",
                    conversationId, intent.getIntentCode(), intent.getConfidence());

            // Step 4: 构建 Prompt
            String fullPrompt = buildFullPrompt(history, userContent);

            // Step 5: 启动心跳
            heartbeatExecutor = startHeartbeat(emitter);

            // Step 6: 推送 thinking 事件
            sendEvent(emitter, SseEventFactory.thinking("正在分析您的需求..."));

            // Step 7: LLM 流式输出
            StringBuilder fullResponse = new StringBuilder();
            AtomicInteger tokenCount = new AtomicInteger(0);

            chatClient.prompt()
                    .user(fullPrompt)
                    .stream()
                    .chatResponse()
                    .doOnNext(resp -> {
                        String token = resp.getResult().getOutput().getText();
                        if (token != null) {
                            fullResponse.append(token);
                            tokenCount.incrementAndGet();
                            sendEvent(emitter, SseEventFactory.token(token));
                        }
                    })
                    .doOnComplete(() -> {
                        Message assistantMsg = messageService.saveAssistantMessage(
                                conversationId, fullResponse.toString(), tokenCount.get());
                        sendEvent(emitter, SseEventFactory.done(tokenCount.get(), assistantMsg.getMessageId()));
                        emitter.complete();
                        messageService.extractLongTermMemoryAsync(conversationId);
                    })
                    .doOnError(error -> {
                        log.error("[Stream] LLM 调用失败: convId={}", conversationId, error);
                        sendEvent(emitter, SseEventFactory.error("LLM 调用失败", 500));
                        emitter.completeWithError(error);
                    })
                    .subscribe();

        } catch (Exception e) {
            log.error("[Stream] 流式管道异常: convId={}", conversationId, e);
            sendEvent(emitter, SseEventFactory.error(e.getMessage(), 500));
            emitter.completeWithError(e);
        } finally {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
            }
        }
    }

    private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "sse-heartbeat"));
        executor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data(""));
            } catch (Exception ignored) {
            }
        }, HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        return executor;
    }

    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
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
}
