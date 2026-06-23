package com.example.agent.domain.knowledge.repository;

import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;

import java.util.List;
import java.util.Optional;

/**
 * 文档仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface DocumentRepository {

    /**
     * 保存文档实体.
     *
     * @return 受影响行数（INSERT 成功应为 1，0 表示插入失败）
     */
    int save(Document doc);

    void update(Document doc);

    Optional<Document> findByDocumentId(String documentId);

    List<Document> findByKnowledgeId(String knowledgeId, int page, int size);

    long countByKnowledgeId(String knowledgeId);

    List<Document> findByStatus(DocumentStatus status);

    void updateStatus(String documentId, DocumentStatus status);

    void updateChunkCount(String documentId, int chunkCount);

    void updateErrorMessage(String documentId, String errorMessage);

    void softDelete(String documentId);

    /** ★ 新增: 级联软删除知识库下所有文档 */
    void softDeleteByKnowledgeId(String knowledgeId);

    /** ★ 新增: 按知识库+状态统计文档数量（用于知识库统计） */
    long countByKnowledgeIdAndStatus(String knowledgeId, DocumentStatus status);

    /** ★ 新增: 批量获取文档元数据，用于检索结果溯源 */
    List<Document> findByDocumentIds(java.util.Set<String> documentIds);
}
