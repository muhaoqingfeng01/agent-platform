package com.example.agent.domain.interaction.valueobject;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 交互上下文值对象 — 封装一次交互请求的完整上下文信息.
 * <p>
 * 由 {@code InteractionStrategyFactory} 构建后传递给对应 {@code InteractionStrategy}，
 * 策略实现根据 {@link #mode} 使用不同字段。
 *
 * <pre>
 * 字段分类：
 *   CONVERSATION 模式使用: userInput, conversationId, tenantId, userId, emitter
 *   KNOWLEDGE_SEARCH 同步模式使用: userInput, knowledgeId, tenantId, searchConfig
 *   KNOWLEDGE_SEARCH 流式模式使用: userInput, conversationId, knowledgeId, tenantId, userId, emitter
 *   TASK_EXECUTION 流式模式使用: userInput, conversationId, tenantId, userId, emitter
 *   ANALYSIS 流式模式使用: userInput, conversationId, tenantId, userId, emitter, searchConfig(权限标记)
 *   APPROVAL 模式使用: userInput, conversationId, tenantId, userId, emitter(流式), searchConfig(权限标记)
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Data
@Builder
public class InteractionContext {

    /** 交互模式 */
    private InteractionMode mode;

    /** 用户输入内容 */
    private String userInput;

    /** 会话 ID（对话型模式使用） */
    private String conversationId;

    /** 知识库 ID（知识检索型模式使用） */
    private String knowledgeId;

    /** 租户 ID */
    private Long tenantId;

    /** 用户 ID */
    private String userId;

    /** 检索配置（知识检索型模式使用） */
    private Map<String, Object> searchConfig;

    /** SSE 发射器（对话型流式模式使用，由 Controller 创建并注入） */
    private Object emitter;

    // ==================== 工厂方法 ====================

    /** 创建对话模式的上下文 */
    public static InteractionContext forConversation(String userInput, String conversationId,
                                                      Long tenantId, String userId, Object emitter) {
        return InteractionContext.builder()
                .mode(InteractionMode.CONVERSATION)
                .userInput(userInput)
                .conversationId(conversationId)
                .tenantId(tenantId)
                .userId(userId)
                .emitter(emitter)
                .build();
    }

    /** 创建知识检索模式的上下文 */
    public static InteractionContext forKnowledgeSearch(String userInput, String knowledgeId,
                                                         Long tenantId, Map<String, Object> searchConfig) {
        return InteractionContext.builder()
                .mode(InteractionMode.KNOWLEDGE_SEARCH)
                .userInput(userInput)
                .knowledgeId(knowledgeId)
                .tenantId(tenantId)
                .searchConfig(searchConfig)
                .build();
    }

    /** 创建知识检索流式模式的上下文（含会话信息，用于消息持久化 + SSE 推送） */
    public static InteractionContext forKnowledgeSearchStream(String userInput, String conversationId,
                                                               String knowledgeId, Long tenantId,
                                                               String userId, Object emitter) {
        return InteractionContext.builder()
                .mode(InteractionMode.KNOWLEDGE_SEARCH)
                .userInput(userInput)
                .conversationId(conversationId)
                .knowledgeId(knowledgeId)
                .tenantId(tenantId)
                .userId(userId)
                .emitter(emitter)
                .build();
    }

    /** 创建任务执行流式模式的上下文（对话入口规划 DAG 并推送步骤进度） */
    public static InteractionContext forTaskExecution(String userInput, String conversationId,
                                                      Long tenantId, String userId, Object emitter) {
        return InteractionContext.builder()
                .mode(InteractionMode.TASK_EXECUTION)
                .userInput(userInput)
                .conversationId(conversationId)
                .tenantId(tenantId)
                .userId(userId)
                .emitter(emitter)
                .build();
    }

    /**
     * 创建分析推理流式模式的上下文.
     * <p>
     * {@code searchConfig} 可携带权限标记（如 {@code canObservability} / {@code canEvaluation}），
     * 供策略在异步线程中决定可查询的指标范围。
     */
    public static InteractionContext forAnalysis(String userInput, String conversationId,
                                                  Long tenantId, String userId, Object emitter,
                                                  Map<String, Object> searchConfig) {
        return InteractionContext.builder()
                .mode(InteractionMode.ANALYSIS)
                .userInput(userInput)
                .conversationId(conversationId)
                .tenantId(tenantId)
                .userId(userId)
                .emitter(emitter)
                .searchConfig(searchConfig)
                .build();
    }

    /**
     * 创建安全审批模式的上下文.
     * <p>
     * {@code searchConfig} 携带权限标记（{@code canRead} / {@code canApprove}），
     * 供策略在异步线程中决定可读列表或可否同意/拒绝。
     * 同步 list 可不传 emitter。
     */
    public static InteractionContext forApproval(String userInput, String conversationId,
                                                  Long tenantId, String userId, Object emitter,
                                                  Map<String, Object> searchConfig) {
        return InteractionContext.builder()
                .mode(InteractionMode.APPROVAL)
                .userInput(userInput)
                .conversationId(conversationId)
                .tenantId(tenantId)
                .userId(userId)
                .emitter(emitter)
                .searchConfig(searchConfig)
                .build();
    }
}
