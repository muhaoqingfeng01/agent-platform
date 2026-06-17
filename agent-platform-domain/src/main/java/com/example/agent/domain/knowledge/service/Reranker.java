package com.example.agent.domain.knowledge.service;

import com.example.agent.domain.knowledge.valueobject.RerankerType;

import java.util.List;

/**
 * Reranker 重排序接口 — Strategy 模式.
 *
 * <p>在 RRF 融合后对候选集进行精排，提升检索精度.
 * 实现类通过 supportedType() 声明支持的 Reranker 类型.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public interface Reranker {

    /**
     * 返回支持的 Reranker 类型.
     */
    RerankerType supportedType();

    /**
     * 对候选列表进行重排序，返回 Top-K 结果.
     *
     * @param query      用户原始查询
     * @param candidates RRF 融合后的候选列表
     * @param topK       返回数量
     * @return 精排后的 Top-K 结果
     */
    List<RerankerHit> rerank(String query, List<RerankerHit> candidates, int topK);

    /**
     * Reranker 重排序结果项.
     */
    record RerankerHit(String chunkId, String documentId, String content, int chunkIndex,
                       double vectorScore, double rrfScore, double rerankScore) {
        public static RerankerHit from(Object searchHit, double rrfScore) {
            // 通过反射兼容不同类型的 SearchHit
            try {
                Long cId = (Long) searchHit.getClass().getMethod("chunkId").invoke(searchHit);
                String dId = (String) searchHit.getClass().getMethod("documentId").invoke(searchHit);
                String c = (String) searchHit.getClass().getMethod("content").invoke(searchHit);
                Integer cIdx = (Integer) searchHit.getClass().getMethod("chunkIndex").invoke(searchHit);
                Double vScore = null;
                try {
                    vScore = (Double) searchHit.getClass().getMethod("score").invoke(searchHit);
                } catch (NoSuchMethodException ignored) {}
                return new RerankerHit(
                    cId != null ? cId.toString() : null,
                    dId, c, cIdx != null ? cIdx : 0,
                    vScore != null ? vScore : 0.0, rrfScore, 0.0);
            } catch (Exception e) {
                return new RerankerHit(null, null, "", 0, 0.0, rrfScore, 0.0);
            }
        }
    }
}
