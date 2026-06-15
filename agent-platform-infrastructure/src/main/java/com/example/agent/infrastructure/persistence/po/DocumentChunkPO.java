package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档切片持久化对象 — 映射 t_document_chunk 表.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunkPO {
    private Long id;
    private String documentId;
    private String knowledgeId;
    private Integer chunkIndex;
    private String content;
    private Integer tokenCount;
    private String contentHash;
    private String milvusId;
    private String metadataJson;
    private LocalDateTime createdAt;
}
