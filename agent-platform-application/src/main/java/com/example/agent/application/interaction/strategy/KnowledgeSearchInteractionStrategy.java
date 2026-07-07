package com.example.agent.application.interaction.strategy;

import com.example.agent.application.conversation.KnowledgeSearchStreamService;
import com.example.agent.application.interaction.dto.InteractionResponse;
import com.example.agent.application.knowledge.HybridSearchApplicationService;
import com.example.agent.application.knowledge.dto.SearchResultDTO;
import com.example.agent.domain.interaction.service.InteractionStrategy;
import com.example.agent.domain.interaction.valueobject.InteractionContext;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 知识库检索交互策略 — 向量检索 + 关键词检索 + RRF 融合 + Reranker 精排 + 文档溯源.
 * <p>
 * 支持两种执行模式：
 * <ul>
 *   <li><b>同步模式</b>（{@link #execute(InteractionContext)}）— 返回 {@link InteractionResponse} 含检索结果</li>
 *   <li><b>流式模式</b>（{@link #executeStream(InteractionContext)}）— 检索后通过 LLM 流式生成回答</li>
 * </ul>
 *
 * <h3>同步模式流程</h3>
 * <ol>
 *   <li>四级精度配置合并（文档 → KB → 策略预设 → 系统默认）</li>
 *   <li>向量检索（Milvus）</li>
 *   <li>关键词检索（可选，RRF 融合时启用）</li>
 *   <li>RRF 加权融合</li>
 *   <li>Reranker 精排（可选）</li>
 *   <li>文档元数据批量回填</li>
 *   <li>命中记录持久化</li>
 * </ol>
 *
 * <h3>流式模式流程</h3>
 * <ol>
 *   <li>委托给 {@link KnowledgeSearchStreamService} 执行完整 RAG 流式管线</li>
 *   <li>检索知识库 → 构建 RAG Prompt → LLM 流式生成 → SSE 推送</li>
 *   <li>无命中时友好提示"未找到相关信息"</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeSearchInteractionStrategy implements InteractionStrategy {

    private final HybridSearchApplicationService hybridSearchService;
    private final KnowledgeSearchStreamService knowledgeSearchStreamService;

    @Override
    public InteractionMode getMode() {
        return InteractionMode.KNOWLEDGE_SEARCH;
    }

    /**
     * 同步执行知识检索 — 返回检索结果 DTO.
     * <p>
     * 适用于非流式场景（如 {@code POST /api/v1/interactions/execute}），
     * 直接返回检索到的 chunk 列表 + 文档引用。
     */
    @Override
    public Object execute(InteractionContext context) {
        log.info("[KnowledgeSearchStrategy] 执行同步检索: query={}, kbId={}, tenantId={}",
                context.getUserInput(),
                context.getKnowledgeId(),
                context.getTenantId());

        // 调用现有的混合检索服务
        SearchResultDTO searchResult = hybridSearchService.search(
                context.getUserInput(),
                context.getKnowledgeId(),
                context.getSearchConfig()
        );

        log.info("[KnowledgeSearchStrategy] 检索完成: hitCount={}, docCount={}",
                searchResult.getHits() != null ? searchResult.getHits().size() : 0,
                searchResult.getDocuments() != null ? searchResult.getDocuments().size() : 0);

        // 包装为统一交互响应
        return InteractionResponse.success(
                InteractionMode.KNOWLEDGE_SEARCH.getCode(),
                searchResult
        );
    }

    /**
     * 流式执行知识检索 + LLM 生成 — 通过 SseEmitter 推送 token 事件.
     * <p>
     * 适用于 SSE 流式场景（如 {@code POST /api/v1/conversations/messages/stream}），
     * 完整流程：检索 → 构建 RAG Prompt → LLM 流式输出 → 保存消息。
     * <p>
     * 注意：流式模式需要 {@link InteractionContext#getConversationId()} 和
     * {@link InteractionContext#getUserId()} 字段，用于消息持久化。
     */
    @Override
    public void executeStream(InteractionContext context) {
        log.info("[KnowledgeSearchStrategy] 执行流式 RAG: convId={}, kbId={}, userId={}",
                context.getConversationId(),
                context.getKnowledgeId(),
                context.getUserId());

        // 从上下文中取出由 Controller 创建的 SseEmitter
        SseEmitter emitter = (SseEmitter) context.getEmitter();
        if (emitter == null) {
            log.error("[KnowledgeSearchStrategy] SseEmitter 为空，无法执行流式 RAG");
            throw new IllegalStateException("知识检索流式模式需要 SseEmitter，但上下文中未提供");
        }

        if (context.getConversationId() == null || context.getConversationId().isBlank()) {
            log.error("[KnowledgeSearchStrategy] conversationId 为空，无法执行流式 RAG");
            throw new IllegalStateException("知识检索流式模式需要 conversationId，但上下文中未提供");
        }

        // 委托给知识库检索流式服务执行完整 RAG 管线
        knowledgeSearchStreamService.executeStreamPipeline(
                context.getConversationId(),
                context.getTenantId(),
                context.getUserId(),
                context.getUserInput(),
                context.getKnowledgeId(),
                emitter
        );
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
