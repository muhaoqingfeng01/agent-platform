package com.example.agent.application.conversation;

import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.checkpoint.CheckpointService;
import com.example.agent.application.checkpoint.SlotAskSupport;
import com.example.agent.application.intent.IntentRecognitionChain;
import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.memory.SessionMemoryService;
import com.example.agent.common.exception.SecurityBlockedException;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
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
    /** 🆕 P6 配置治理子方案03: SSE 心跳间隔从 Nacos 动态读取 */
    private final SessionConfig sessionConfig;
    private final CheckpointService checkpointService;
    private final SlotAskSupport slotAskSupport;
    private final StreamPipelineGuard pipelineGuard;
    private final StreamRegistry streamRegistry;
    private final ConversationToolBridge conversationToolBridge;

    @Auditable(action = "LLM_CALL", resourceType = "CONVERSATION", recordResponse = false)
    public void executeStreamPipeline(String conversationId, Long tenantId, String userId,
                                        String userContent, SseEmitter emitter) {
        executeStreamPipeline(conversationId, tenantId, userId, userContent, emitter, null, true);
    }

    /**
     * @param persistUserMessage false 表示调用方已落库用户消息（避免与恢复处理器重复保存）
     */
    @Auditable(action = "LLM_CALL", resourceType = "CONVERSATION", recordResponse = false)
    public void executeStreamPipeline(String conversationId, Long tenantId, String userId,
                                        String userContent, SseEmitter emitter,
                                        InteractionContext interactionContext,
                                        boolean persistUserMessage) {
        // 将 HTTP 线程捕获的上下文注入当前执行线程，确保下游 ThreadLocal 链路完整
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        // 记录对话创建指标
        metrics.recordConversation(tenantId, "STARTED");

        // 启动消息处理耗时采样
        Timer.Sample messageSample = Timer.start();

        ScheduledExecutorService heartbeatExecutor = null;
        try {
            pipelineGuard.filterInput(conversationId, userId, tenantId, userContent);

            if (persistUserMessage) {
                messageService.saveUserMessage(conversationId, userContent);
            }

            SlotCollection restoredSlots = checkpointService.slotsOf(interactionContext);
            boolean slotsJustCompleted = interactionContext != null
                    && checkpointService.restoredOf(interactionContext)
                    .map(ckpt -> ckpt.isAwaitParam() && restoredSlots.isComplete())
                    .orElse(false);

            // Step 2: 加载会话上下文
            List<Message> history = sessionMemoryService.getRecentMessages(conversationId, 10);

            IntentResult intent;
            SlotCollection slotsForPrompt = restoredSlots;
            if (slotsJustCompleted) {
                intent = IntentResult.unknown("slots-restored");
                String intentCode = checkpointService.restoredOf(interactionContext)
                        .map(ckpt -> ckpt.getIntentCode()).orElse(null);
                if (intentCode != null) {
                    intent.setIntentCode(intentCode);
                }
            } else {
                intent = intentRecognitionChain.recognize(tenantId, userContent);
                log.info("[Stream] 意图识别: convId={}, intent={}, confidence={}",
                        conversationId, intent.getIntentCode(), intent.getConfidence());
                SlotCollection required = SlotCollection.fromRequiredParamMaps(intent.getRequiredParams());
                if (required.hasRequired() && !required.isComplete()) {
                    slotAskSupport.askAndComplete(emitter, conversationId, intent.getIntentCode(), required);
                    messageSample.stop(metrics.getMessageProcessingTimer());
                    return;
                }
                slotsForPrompt = required;
            }

            // 未匹配意图不推 intent 事件，避免前端刷「UNKNOWN → CONVERSATION」噪音
            if (intent.isMatched()) {
                InteractionMode suggestedMode = pipelineGuard.suggestMode(intent);
                sendEvent(emitter, SseEventFactory.intent(
                        intent.getIntentCode(), intent.getConfidence(), intent.getRiskLevel(),
                        suggestedMode.getCode()));
            }

            var highRiskApproval = pipelineGuard.pauseIfHighRiskIntent(intent, conversationId);
            if (highRiskApproval.isPresent()) {
                ApprovalWorkflowResponse approval = highRiskApproval.get();
                completeWithApproval(emitter, conversationId, approval, messageSample);
                return;
            }

            // Step 4: 构建 Prompt（已发布模板 + 长期记忆，缺失模板则回退拼接）
            String fullPrompt = pipelineGuard.assembleConversationPrompt(
                    userId, history, userContent, slotsForPrompt);

            // Step 5: 启动心跳
            heartbeatExecutor = startHeartbeat(emitter);

            // Step 5.5: 显式两轮工具调用（无工具/失败则 fail-open）
            ConversationToolBridge.ToolRoundResult toolRound =
                    conversationToolBridge.planAndExecute(tenantId, conversationId, userContent, emitter);
            if (toolRound.hasApproval()) {
                completeWithApproval(emitter, conversationId, toolRound.getApproval(), messageSample);
                return;
            }
            String llmUserPrompt = fullPrompt;
            if (toolRound.hasToolContext()) {
                llmUserPrompt = fullPrompt + toolRound.getToolContext()
                        + "\n请基于以上工具结果回答用户问题。\n";
            }

            // Step 6: 推送 thinking 事件
            sendEvent(emitter, SseEventFactory.thinking("正在分析您的需求..."));

            // Step 7: LLM 流式输出（异步 — 使用 Timer.Sample 捕获耗时）
            StringBuilder fullResponse = new StringBuilder();
            AtomicInteger tokenCount = new AtomicInteger(0);

            // 捕获 MDC traceId（Lambda 回调运行在不同线程，MDC 可能丢失）
            String traceId = org.slf4j.MDC.get("traceId");

            // 启动 LLM 调用耗时采样
            Timer.Sample llmSample = Timer.start();

            String streamId = StreamSessionService.streamIdOf(interactionContext);
            final String promptForTrace = llmUserPrompt;

            chatClient.prompt()
                    .user(llmUserPrompt)
                    .stream()
                    .chatResponse()
                    .doOnNext(resp -> {
                        if (streamRegistry.isCancelled(streamId)) {
                            throw new StreamCancelledException(streamId);
                        }
                        String token = resp.getResult().getOutput().getText();
                        if (token != null) {
                            fullResponse.append(token);
                            tokenCount.incrementAndGet();
                        }
                    })
                    .doOnComplete(() -> {
                        if (streamRegistry.isCancelled(streamId)) {
                            messageSample.stop(metrics.getMessageProcessingTimer());
                            llmSample.stop(metrics.getLlmCallTimer());
                            return;
                        }
                        // 停止 LLM 计时并记录指标
                        long llmDurationMs = llmSample.stop(metrics.getLlmCallTimer());
                        metrics.recordTokenConsumption(tenantId, "deepseek", tokenCount.get());
                        // 停止消息处理计时
                        messageSample.stop(metrics.getMessageProcessingTimer());

                        String safeText = pipelineGuard.desensitizeOutput(conversationId, fullResponse.toString());
                        emitDesensitizedTokens(emitter, safeText);

                        // Langfuse LLM 调用追踪（异步发送）
                        langfuseTrace.logLLMCallAsync(traceId, conversationId, "deepseek",
                                promptForTrace, safeText, llmDurationMs / 1_000_000, tokenCount.get());

                        Message assistantMsg = messageService.saveAssistantMessage(
                                conversationId, safeText, tokenCount.get());
                        sendEvent(emitter, SseEventFactory.done(tokenCount.get(), assistantMsg.getMessageId()));
                        emitter.complete();
                        messageService.extractLongTermMemoryAsync(conversationId, userId, tenantId);
                    })
                    .doOnError(error -> {
                        if (error instanceof StreamCancelledException) {
                            log.info("[Stream] 用户取消生成: convId={}, streamId={}", conversationId, streamId);
                            llmSample.stop(metrics.getLlmCallTimer());
                            messageSample.stop(metrics.getMessageProcessingTimer());
                            return;
                        }
                        // 停止 LLM 计时并记录错误指标
                        llmSample.stop(metrics.getLlmCallTimer());
                        metrics.recordLlmError(tenantId, "deepseek", error.getClass().getSimpleName());
                        messageSample.stop(metrics.getMessageProcessingTimer());

                        log.error("[Stream] LLM 调用失败: convId={}", conversationId, error);
                        sendEvent(emitter, SseEventFactory.error("LLM 调用失败", 500));
                        emitter.completeWithError(error);
                    })
                    .subscribe();

        } catch (SecurityBlockedException e) {
            messageSample.stop(metrics.getMessageProcessingTimer());
            log.warn("[Stream] 输入被围栏阻断: convId={}, reason={}", conversationId, e.getMessage());
            sendEvent(emitter, SseEventFactory.error(e.getMessage(), 403));
            emitter.complete();
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
        }, sessionConfig.getSseHeartbeatIntervalMs(), sessionConfig.getSseHeartbeatIntervalMs(), TimeUnit.MILLISECONDS);
        return executor;
    }

    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private void completeWithApproval(SseEmitter emitter, String conversationId,
                                      ApprovalWorkflowResponse approval, Timer.Sample messageSample) {
        sendEvent(emitter, SseEventFactory.approvalRequired(
                approval.getApprovalId(), approval.getExecutionId(), approval.getTitle()));
        String summary = "该操作风险较高，已提交审批（" + approval.getApprovalId() + "），请等待审批结果。";
        sendEvent(emitter, SseEventFactory.token(summary));
        Message assistantMsg = messageService.saveAssistantMessage(
                conversationId, summary, estimateTokenCount(summary));
        sendEvent(emitter, SseEventFactory.done(estimateTokenCount(summary), assistantMsg.getMessageId()));
        emitter.complete();
        messageSample.stop(metrics.getMessageProcessingTimer());
    }

    private void emitDesensitizedTokens(SseEmitter emitter, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        int chunk = 24;
        for (int i = 0; i < text.length(); i += chunk) {
            sendEvent(emitter, SseEventFactory.token(text.substring(i, Math.min(text.length(), i + chunk))));
        }
    }

    private int estimateTokenCount(String content) {
        if (content == null) {
            return 0;
        }
        return (int) Math.ceil(content.length() * 0.5);
    }
}
