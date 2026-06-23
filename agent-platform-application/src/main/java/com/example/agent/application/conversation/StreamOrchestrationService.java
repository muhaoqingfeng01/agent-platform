package com.example.agent.application.conversation;

import com.example.agent.application.intent.IntentRecognitionChain;
import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.memory.SessionMemoryService;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.infrastructure.metrics.AgentMetrics;
import com.example.agent.infrastructure.observability.LangfuseTraceService;
import io.micrometer.core.instrument.Timer;
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
    private final AgentMetrics metrics;
    private final LangfuseTraceService langfuseTrace;

    private static final long HEARTBEAT_INTERVAL_MS = 15_000L;

    @Auditable(action = "LLM_CALL", resourceType = "CONVERSATION", recordResponse = false)
    public void executeStreamPipeline(String conversationId, Long tenantId, String userId,
                                        String userContent, SseEmitter emitter) {
        // 将 HTTP 线程捕获的上下文注入当前执行线程，确保下游 ThreadLocal 链路完整
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        // 记录对话创建指标
        metrics.recordConversation(tenantId, "STARTED");

        // 启动消息处理耗时采样
        Timer.Sample messageSample = Timer.start();

        ScheduledExecutorService heartbeatExecutor = null;
        try {
            // Step 1: 保存用户消息
            Message userMsg = messageService.saveUserMessage(conversationId, userContent);

            // Step 2: 加载会话上下文
            List<Message> history = sessionMemoryService.getRecentMessages(conversationId, 10);

            // Step 3: 意图识别（显式传入 tenantId，因当前执行在线程池线程中，ThreadLocal 不可用）
            IntentResult intent = intentRecognitionChain.recognize(tenantId, userContent);
            log.info("[Stream] 意图识别: convId={}, intent={}, confidence={}",
                    conversationId, intent.getIntentCode(), intent.getConfidence());

            // Step 4: 构建 Prompt
            String fullPrompt = buildFullPrompt(history, userContent);

            // Step 5: 启动心跳
            heartbeatExecutor = startHeartbeat(emitter);

            // Step 6: 推送 thinking 事件
            sendEvent(emitter, SseEventFactory.thinking("正在分析您的需求..."));

            // Step 7: LLM 流式输出（异步 — 使用 Timer.Sample 捕获耗时）
            StringBuilder fullResponse = new StringBuilder();
            AtomicInteger tokenCount = new AtomicInteger(0);

            // 捕获 MDC traceId（Lambda 回调运行在不同线程，MDC 可能丢失）
            String traceId = org.slf4j.MDC.get("traceId");

            // 启动 LLM 调用耗时采样
            Timer.Sample llmSample = Timer.start();

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
                        // 停止 LLM 计时并记录指标
                        long llmDurationMs = llmSample.stop(metrics.getLlmCallTimer());
                        metrics.recordTokenConsumption(tenantId, "deepseek", tokenCount.get());
                        // 停止消息处理计时
                        messageSample.stop(metrics.getMessageProcessingTimer());

                        // Langfuse LLM 调用追踪（异步发送）
                        langfuseTrace.logLLMCallAsync(traceId, conversationId, "deepseek",
                                fullPrompt, fullResponse.toString(), llmDurationMs / 1_000_000, tokenCount.get());

                        Message assistantMsg = messageService.saveAssistantMessage(
                                conversationId, fullResponse.toString(), tokenCount.get());
                        sendEvent(emitter, SseEventFactory.done(tokenCount.get(), assistantMsg.getMessageId()));
                        emitter.complete();
                        messageService.extractLongTermMemoryAsync(conversationId, userId, tenantId);
                    })
                    .doOnError(error -> {
                        // 停止 LLM 计时并记录错误指标
                        llmSample.stop(metrics.getLlmCallTimer());
                        metrics.recordLlmError(tenantId, "deepseek", error.getClass().getSimpleName());
                        messageSample.stop(metrics.getMessageProcessingTimer());

                        log.error("[Stream] LLM 调用失败: convId={}", conversationId, error);
                        sendEvent(emitter, SseEventFactory.error("LLM 调用失败", 500));
                        emitter.completeWithError(error);
                    })
                    .subscribe();

        } catch (Exception e) {
            messageSample.stop(metrics.getMessageProcessingTimer());
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
            sb.append(msg.getRole().getDesc()).append(": ").append(msg.getContent()).append("\n");
        }
        sb.append("User: ").append(userContent);
        return sb.toString();
    }
}
