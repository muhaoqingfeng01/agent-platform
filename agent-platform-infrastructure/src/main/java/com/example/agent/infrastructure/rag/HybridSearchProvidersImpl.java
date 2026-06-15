package com.example.agent.infrastructure.rag;

import com.example.agent.domain.knowledge.service.EmbeddingServiceClient;
import com.example.agent.domain.knowledge.service.FulltextSearchProvider;
import com.example.agent.domain.knowledge.service.VectorSearchProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 混合检索 Provider — 向量检索 (Milvus) + 关键词检索 (MySQL).
 * <p>
 * V1.4.0: VectorSearch 增加 filterExpression 参数; FulltextSearch 改为接受 Set knowledgeIds.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HybridSearchProvidersImpl implements VectorSearchProvider, FulltextSearchProvider {

    private final MilvusCollectionManager milvusManager;
    private final EmbeddingServiceClient embeddingClient;
    private final JdbcTemplate jdbcTemplate;

    // ==================== VectorSearchProvider ====================

    @Override
    public List<SearchHit> search(String collectionName, String query, int topK,
                                   double threshold, String filterExpression) {
        // 1. 将查询文本向量化
        float[] queryVector;
        try {
            queryVector = embeddingClient.embed(query);
        } catch (Exception e) {
            log.error("[VectorSearch] Embedding 失败: query={}", query, e);
            return Collections.emptyList();
        }

        // 2. Milvus ANN 检索（传递 filterExpression）
        List<Float> vecList = new ArrayList<>(queryVector.length);
        for (float v : queryVector) vecList.add(v);

        List<MilvusCollectionManager.VectorSearchResult> milvusResults =
                milvusManager.search(collectionName, vecList, topK, (float) threshold, filterExpression);

        // 3. 转换结果
        return milvusResults.stream()
                .filter(r -> r.score() >= threshold)
                .map(r -> new SearchHit(
                        r.id(),
                        r.documentId() != null ? r.documentId() : "",
                        r.content(),
                        0,  // chunkIndex
                        r.score()))
                .toList();
    }

    // ==================== FulltextSearchProvider ====================

    @Override
    public List<SearchHit> search(Set<String> knowledgeIds, String query, int topK) {
        if (knowledgeIds == null || knowledgeIds.isEmpty()) {
            // 无过滤，检索全部
            String sql = """
                    SELECT dc.id, dc.document_id, dc.content, dc.chunk_index
                    FROM t_document_chunk dc
                    JOIN t_document d ON dc.document_id = d.document_id
                    WHERE dc.deleted = 0
                      AND d.status NOT IN ('DEPRECATED', 'DELETED')
                      AND dc.content LIKE ?
                    ORDER BY dc.chunk_index ASC
                    LIMIT ?
                    """;
            try {
                return jdbcTemplate.query(sql,
                        (rs, rowNum) -> new SearchHit(
                                rs.getLong("id"),
                                rs.getString("document_id"),
                                rs.getString("content"),
                                rs.getInt("chunk_index"),
                                0.0),
                        "%" + query + "%", topK);
            } catch (Exception e) {
                log.warn("[FulltextSearch] 检索异常: {}", e.getMessage());
                return Collections.emptyList();
            }
        }

        // 按指定 knowledgeIds 过滤
        StringBuilder sql = new StringBuilder("""
                SELECT dc.id, dc.document_id, dc.content, dc.chunk_index
                FROM t_document_chunk dc
                JOIN t_document d ON dc.document_id = d.document_id
                WHERE dc.deleted = 0
                  AND d.status NOT IN ('DEPRECATED', 'DELETED')
                  AND dc.knowledge_id IN (
                """);

        List<Object> params = new ArrayList<>();
        int idx = 0;
        for (String kid : knowledgeIds) {
            if (idx > 0) sql.append(", ");
            sql.append("?");
            params.add(kid);
            idx++;
        }
        sql.append(") AND dc.content LIKE ? ORDER BY dc.chunk_index ASC LIMIT ?");
        params.add("%" + query + "%");
        params.add(topK);

        try {
            List<SearchHit> hits = jdbcTemplate.query(
                    sql.toString(),
                    (rs, rowNum) -> new SearchHit(
                            rs.getLong("id"),
                            rs.getString("document_id"),
                            rs.getString("content"),
                            rs.getInt("chunk_index"),
                            0.0),
                    params.toArray());
            log.debug("[FulltextSearch] knowledgeIds={}, query={}, hits={}", knowledgeIds, query, hits.size());
            return hits;
        } catch (Exception e) {
            log.warn("[FulltextSearch] 检索异常，降级返回空: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
