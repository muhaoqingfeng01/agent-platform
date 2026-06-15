package com.example.agent.domain.knowledge.service;

import java.util.List;
import java.util.Set;

/**
 * 关键词全文检索提供者端口 — 由 Infrastructure 层实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface FulltextSearchProvider {
    /**
     * 关键词检索.
     *
     * @param knowledgeIds 知识库 ID 集合（支持多 KB 同时检索），nullable=全部
     * @param query        查询文本
     * @param topK         返回数量
     */
    List<VectorSearchProvider.SearchHit> search(Set<String> knowledgeIds, String query, int topK);
}
