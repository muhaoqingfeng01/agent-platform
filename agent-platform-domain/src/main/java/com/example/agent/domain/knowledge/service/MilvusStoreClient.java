package com.example.agent.domain.knowledge.service;

import java.util.List;

/**
 * Milvus 存储客户端端口 — 由 Infrastructure 层实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface MilvusStoreClient {

    void ensureCollection(String collectionName, int dimension, String indexType);

    void insert(String collectionName, List<VectorEntry> entries);

    /** ★ 新增: 按文档 ID 删除所有向量 */
    void deleteByDocumentId(String collectionName, String documentId);

    /** ★ 新增: 按知识库 ID 删除所有向量 */
    void deleteByKnowledgeId(String collectionName, String knowledgeId);

    /**
     * 向量条目 — 包含 knowledgeId 用于 Milvus 标量过滤.
     */
    record VectorEntry(long id, float[] vector, String content, String documentId, String knowledgeId) {}
}
