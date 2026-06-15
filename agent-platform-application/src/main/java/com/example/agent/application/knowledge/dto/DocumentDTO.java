package com.example.agent.application.knowledge.dto;

import com.example.agent.domain.knowledge.entity.Document;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
public class DocumentDTO {
    private String documentId;
    private String knowledgeId;
    private String filename;
    private String fileType;
    private long fileSize;
    private String minioPath;
    private String contentHash;
    private int chunkCount;
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

    public static DocumentDTO from(Document doc) {
        return DocumentDTO.builder()
                .documentId(doc.getDocumentId())
                .knowledgeId(doc.getKnowledgeId())
                .filename(doc.getFilename())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .minioPath(doc.getMinioPath())
                .contentHash(doc.getContentHash())
                .chunkCount(doc.getChunkCount())
                .status(doc.getStatus() != null ? doc.getStatus().name() : null)
                .errorMessage(doc.getErrorMessage())
                .uploadedBy(doc.getUploadedBy())
                .uploadedAt(doc.getUploadedAt())
                .chunkStrategy(doc.getChunkStrategy())
                .chunkConfigJson(doc.getChunkConfigJson())
                .searchStrategyOverride(doc.getSearchStrategyOverride())
                .searchParamsOverrideJson(doc.getSearchParamsOverrideJson())
                .multiStageOverrideJson(doc.getMultiStageOverrideJson())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
