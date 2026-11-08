package com.example.agent.application.conversation;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.application.memory.LongTermMemoryService;
import com.example.agent.application.prompt.PromptApplicationService;
import com.example.agent.application.security.SecurityFenceApplicationService;
import com.example.agent.application.security.filter.FilterContext;
import com.example.agent.common.constant.ProjectConstants;
import com.example.agent.domain.conversation.entity.LongTermMemory;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 对话主链路横切能力 — 围栏、提示词模板、长期记忆、高风险审批。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamPipelineGuard {

    private final SecurityFenceApplicationService securityFence;
    private final LongTermMemoryService longTermMemoryService;
    private final PromptApplicationService promptApplicationService;
    private final ApprovalWorkflowApplicationService approvalService;

    /**
     * 输入围栏：阻断时抛 {@code SecurityBlockedException}。
     */
    public void filterInput(String conversationId, String userId, Long tenantId, String content) {
        securityFence.filterInput(content, FilterContext.builder()
                .conversationId(conversationId)
                .userId(userId)
                .tenantId(tenantId)
                .build());
    }

    /**
     * 输出脱敏：完整缓冲后再调用，避免逐 token 切碎 PII。
     */
    public String desensitizeOutput(String conversationId, String content) {
        return securityFence.desensitizeOutput(content, conversationId, null);
    }

    /**
     * 组装对话 Prompt：优先渲染已发布模板，失败则回退拼接，并注入长期记忆。
     */
    public String assembleConversationPrompt(String userId, List<Message> history, String userContent,
                                             SlotCollection slots) {
        List<LongTermMemory> memories = loadMemories(userId);
        String historyText = formatHistory(history);
        String fallback = buildFallbackPrompt(historyText, userContent, slots, memories);
        Map<String, Object> context = buildRenderContext(historyText, userContent, slots, memories, null);
        String rendered = promptApplicationService.runtimeRenderLatestPublished(
                ProjectConstants.Conversation.PROMPT_NAME_CONVERSATION, context);
        return rendered != null ? rendered : fallback;
    }

    /**
     * 组装 RAG Prompt：优先已发布 knowledge_search 模板。
     */
    public String assembleKnowledgePrompt(String userId, String historyContext, String retrievedContext,
                                          String userContent) {
        List<LongTermMemory> memories = loadMemories(userId);
        Map<String, Object> context = buildRenderContext(historyContext, userContent, null, memories, retrievedContext);
        String rendered = promptApplicationService.runtimeRenderLatestPublished(
                ProjectConstants.Conversation.PROMPT_NAME_KNOWLEDGE_SEARCH, context);
        if (rendered != null) {
            return rendered;
        }
        return longTermMemoryService.injectMemoriesIntoPrompt(
                String.format("""
                        你是一个专业的知识库助手。请严格根据以下从知识库中检索到的内容回答用户的问题。

                        ## 对话历史（帮助你理解用户的追问和指代）
                        %s

                        ## 检索到的知识库内容
                        %s

                        ## 用户问题
                        %s
                        """, historyContext, retrievedContext, userContent),
                memories);
    }

    /**
     * 高风险意图：建审批单并暂停本轮 LLM。
     */
    public Optional<ApprovalWorkflowResponse> pauseIfHighRiskIntent(IntentResult intent, String conversationId) {
        if (intent == null || !intent.isHighRisk()) {
            return Optional.empty();
        }
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("intentCode", intent.getIntentCode());
        params.put("riskLevel", intent.getRiskLevel());
        params.put("reasoning", intent.getReasoning());
        ApprovalWorkflowResponse approval = approvalService.createActionApproval(
                null, conversationId, "high_risk_intent:" + intent.getIntentCode(),
                intent.getIntentCode(), params);
        log.info("[Pipeline] 高风险意图已建审批: convId={}, approvalId={}, intent={}",
                conversationId, approval.getApprovalId(), intent.getIntentCode());
        return Optional.of(approval);
    }

    /**
     * 根据意图类别建议交互模式，供前端提示，不强制改路由。
     */
    public InteractionMode suggestMode(IntentResult intent) {
        if (intent == null || !intent.isMatched()) {
            return InteractionMode.CONVERSATION;
        }
        String key = (intent.getCategory() != null ? intent.getCategory() : intent.getIntentCode()).toLowerCase();
        if (key.contains("knowledge") || key.contains("rag") || key.contains("search") || key.contains("知识")) {
            return InteractionMode.KNOWLEDGE_SEARCH;
        }
        if (key.contains("task") || key.contains("plan") || key.contains("任务")) {
            return InteractionMode.TASK_EXECUTION;
        }
        if (key.contains("approv") || key.contains("审批")) {
            return InteractionMode.APPROVAL;
        }
        if (key.contains("analy") || key.contains("分析")) {
            return InteractionMode.ANALYSIS;
        }
        return InteractionMode.CONVERSATION;
    }

    private List<LongTermMemory> loadMemories(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        try {
            return longTermMemoryService.loadUserMemories(userId);
        } catch (Exception e) {
            log.warn("[Pipeline] 加载长期记忆失败，跳过注入: userId={}", userId, e);
            return List.of();
        }
    }

    private Map<String, Object> buildRenderContext(String historyText, String userContent,
                                                   SlotCollection slots, List<LongTermMemory> memories,
                                                   String knowledgeContext) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("conversation_history", historyText != null ? historyText : "");
        context.put("user_input", userContent != null ? userContent : "");
        context.put("user_memories", formatMemories(memories));
        if (slots != null && slots.getFilled() != null) {
            context.put("slots", slots.getFilled());
        }
        if (knowledgeContext != null) {
            context.put("knowledge_context", knowledgeContext);
        }
        return context;
    }

    private String buildFallbackPrompt(String historyText, String userContent,
                                       SlotCollection slots, List<LongTermMemory> memories) {
        StringBuilder sb = new StringBuilder();
        if (historyText != null && !historyText.isBlank()) {
            sb.append(historyText);
            if (!historyText.endsWith("\n")) {
                sb.append('\n');
            }
        }
        if (slots != null && slots.getFilled() != null && !slots.getFilled().isEmpty()) {
            sb.append("已收集参数: ").append(slots.getFilled()).append('\n');
        }
        sb.append("User: ").append(userContent);
        return longTermMemoryService.injectMemoriesIntoPrompt(sb.toString(), memories);
    }

    private String formatHistory(List<Message> history) {
        if (history == null || history.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Message msg : history) {
            String role = msg.getRole() != null ? msg.getRole().getDesc() : "未知";
            sb.append(role).append(": ").append(msg.getContent()).append('\n');
        }
        return sb.toString();
    }

    private String formatMemories(List<LongTermMemory> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (LongTermMemory memory : memories) {
            sb.append("- ").append(memory.getMemoryKey()).append(": ")
                    .append(memory.getMemoryValue()).append('\n');
        }
        return sb.toString();
    }
}
