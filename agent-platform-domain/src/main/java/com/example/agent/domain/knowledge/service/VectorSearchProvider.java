package com.example.agent.domain.knowledge.service;

import java.util.List;

/**
 * 向量检索提供者端口 — 由 Infrastructure 层实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface VectorSearchProvider {
    /**
     * 向量检索.
     *
     * @param collectionName   Milvus Collection 名称
     * @param query            查询文本
     * @param topK             返回数量
     * @param threshold        相似度阈值（低于此值的结果丢弃）
     * @param filterExpression Milvus 标量过滤表达式，如 knowledge_id in ["kb_abc"]，nullable
     */
    List<SearchHit> search(String collectionName, String query, int topK, double threshold, String filterExpression);

    record SearchHit(Long chunkId, String documentId, String content, int chunkIndex, double score) {}
}
