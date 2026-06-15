package com.example.agent.domain.knowledge.repository;

import com.example.agent.domain.knowledge.entity.DocumentChunk;

import java.util.List;

/**
 * 文档切片仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface DocumentChunkRepository {

    void save(DocumentChunk chunk);

    void batchSave(List<DocumentChunk> chunks);

    List<DocumentChunk> findByDocumentId(String documentId);

    List<DocumentChunk> findByDocumentId(String documentId, int offset, int limit);

    List<DocumentChunk> findByIds(List<Long> ids);

    int countByDocumentId(String documentId);

    void deleteByDocumentId(String documentId);

    void deleteByKnowledgeId(String knowledgeId);

    /** ★ V1.4.0: 软删除单个文档的切片（弃用时标记） */
    void softDeleteByDocumentId(String documentId);

    /** ★ 新增: 级联软删除知识库下所有切片 */
    void softDeleteByKnowledgeId(String knowledgeId);
}
