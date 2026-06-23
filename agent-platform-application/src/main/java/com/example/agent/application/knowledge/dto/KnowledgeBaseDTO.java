package com.example.agent.application.knowledge.dto;

import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.Builder;
import lombok.Data;

/**
 * 知识库 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
public class KnowledgeBaseDTO {
    private String knowledgeId;
    private Long tenantId;
    private String name;
    private String description;
    private String embeddingModel;
    private int chunkSize;
    private int chunkOverlap;
    private int documentCount;
    private String status;
    private String statusLabel;
    private String createdBy;
    private boolean isCreatedByCurrentUser;
    private String defaultChunkStrategy;
    private String chunkConfigJson;
    private String indexType;
    private String indexParamsJson;
    private String searchStrategy;
    private String searchParamsJson;
    private String multiStageParamsJson;
    private String monitoringParamsJson;
    private Long createdAt;
    private Long updatedAt;

    public static KnowledgeBaseDTO from(KnowledgeBase kb) {
        String currentUserId = null;
        try {
            currentUserId = TenantContext.getCurrentUserId();
        } catch (Exception ignored) {
            // no context
        }
        return KnowledgeBaseDTO.builder()
                .knowledgeId(kb.getKnowledgeId())
                .tenantId(kb.getTenantId())
                .name(kb.getName())
                .description(kb.getDescription())
                .embeddingModel(kb.getEmbeddingModel())
                .chunkSize(kb.getChunkSize())
                .chunkOverlap(kb.getChunkOverlap())
                .documentCount(kb.getDocumentCount())
                .status(kb.getStatus() != null ? kb.getStatus().name() : null)
                .statusLabel(kb.getStatus() != null ? kb.getStatus().getDesc() : null)
                .createdBy(kb.getCreatedBy())
                .isCreatedByCurrentUser(currentUserId != null && kb.isCreatedBy(currentUserId))
                .defaultChunkStrategy(kb.getDefaultChunkStrategy())
                .chunkConfigJson(kb.getChunkConfigJson())
                .indexType(kb.getIndexType() != null ? kb.getIndexType().name() : null)
                .indexParamsJson(kb.getIndexParamsJson())
                .searchStrategy(kb.getSearchStrategy())
                .searchParamsJson(kb.getSearchParamsJson())
                .multiStageParamsJson(kb.getMultiStageParamsJson())
                .monitoringParamsJson(kb.getMonitoringParamsJson())
                .createdAt(TimeConverters.toEpochMilli(kb.getCreatedAt()))
                .updatedAt(TimeConverters.toEpochMilli(kb.getUpdatedAt()))
                .build();
    }
}
