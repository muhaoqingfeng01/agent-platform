package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库持久化对象 — 映射 t_knowledge_base 表.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBasePO {
    private Long id;
    private Long tenantId;
    private String knowledgeId;
    private String name;
    private String description;
    private String embeddingModel;
    private Integer chunkSize;
    private Integer chunkOverlap;
    private Integer documentCount;
    private String status;
    private String createdBy;
    private String defaultChunkStrategy;
    private String chunkConfigJson;
    private String indexType;
    private String indexParamsJson;
    private String searchStrategy;
    private String searchParamsJson;
    private String multiStageParamsJson;
    private String monitoringParamsJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
