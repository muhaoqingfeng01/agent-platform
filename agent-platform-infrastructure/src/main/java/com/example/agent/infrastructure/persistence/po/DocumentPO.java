package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档持久化对象 — 映射 t_document 表.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPO {
    private Long id;
    private Long tenantId;
    private String knowledgeId;
    private String documentId;
    private String filename;
    private String fileType;
    private Long fileSize;
    private String minioPath;
    private String contentHash;
    private Integer chunkCount;
    private String status;
    private String errorMessage;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String chunkStrategy;
    private String chunkConfigJson;
    private String searchStrategyOverride;
    private String searchParamsOverrideJson;
    private String multiStageOverrideJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
