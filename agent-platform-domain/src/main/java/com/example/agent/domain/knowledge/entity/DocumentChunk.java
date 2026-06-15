package com.example.agent.domain.knowledge.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 文档切片实体.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@Builder(toBuilder = true)
public class DocumentChunk {

    private Long id;
    private String documentId;
    private String knowledgeId;
    private int chunkIndex;
    private String content;
    private int tokenCount;
    private String contentHash;
    private String milvusId;
    private String metadataJson;
    private LocalDateTime createdAt;
}
