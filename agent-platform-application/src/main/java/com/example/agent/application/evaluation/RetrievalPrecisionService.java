package com.example.agent.application.evaluation;

import com.example.agent.application.knowledge.HybridSearchApplicationService;
import com.example.agent.application.knowledge.dto.SearchResultDTO;
import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.evaluation.entity.EvaluationDatasetItem;
import com.example.agent.domain.evaluation.repository.EvaluationDatasetRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 检索精度评估服务.
 *
 * <p>从评测数据集（t_evaluation_dataset_item）中提取 Q&A 对，
 * 将问题作为 query 执行检索 → 比较检索结果与标准答案 → 计算指标.
 *
 * <p>评估指标: Recall@5 / MRR / NDCG@5 / Hit Rate
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalPrecisionService {

    private final HybridSearchApplicationService searchService;
    private final EvaluationDatasetRepository datasetRepository;

    /**
     * 对指定知识库运行精度评估.
     *
     * @param kbId      知识库 ID
     * @param datasetId 评测数据集 ID
     * @return 评估报告
     */
    public PrecisionReport evaluate(String kbId, String datasetId) {
        List<EvaluationDatasetItem> items = datasetRepository.findItemsByDatasetId(datasetId);

        if (items.isEmpty()) {
            log.warn("[Precision] 评测数据集为空: datasetId={}", datasetId);
            return PrecisionReport.empty(kbId, datasetId);
        }

        List<QueryResult> results = new ArrayList<>();
        for (EvaluationDatasetItem item : items) {
            try {
                QueryResult qr = evaluateSingle(item, kbId);
                results.add(qr);
            } catch (Exception e) {
                log.warn("[Precision] 单题评测失败: question={}, error={}", item.getQuestion(), e.getMessage());
                results.add(QueryResult.failed(item.getQuestion()));
            }
        }

        return PrecisionReport.builder()
            .kbId(kbId)
            .datasetId(datasetId)
            .totalQueries(results.size())
            .recallAt5(calculateRecallAtK(results, 5))
            .mrr(calculateMRR(results))
            .ndcgAt5(calculateNDCG(results, 5))
            .hitRate(calculateHitRate(results))
            .perQueryResults(results)
            .evaluatedAt(TimeConverters.toEpochMilli(java.time.LocalDateTime.now()))
            .build();
    }

    /**
     * 单题评估.
     */
    private QueryResult evaluateSingle(EvaluationDatasetItem item, String kbId) {
        SearchResultDTO searchResult = searchService.search(
            item.getQuestion(), kbId, java.util.Map.of());

        List<String> retrievedChunkIds = searchResult.getHits().stream()
            .map(h -> h.getChunkId() != null ? h.getChunkId().toString() : null)
            .filter(id -> id != null)
            .toList();

        String expectedId = item.getRetrievalContext();   // 标准答案 chunk ID
        int hitRank = 0;
        if (expectedId != null && !expectedId.isEmpty()) {
            for (int i = 0; i < retrievedChunkIds.size(); i++) {
                if (expectedId.equals(retrievedChunkIds.get(i))) {
                    hitRank = i + 1;
                    break;
                }
            }
        }

        return QueryResult.builder()
            .question(item.getQuestion())
            .expectedChunkId(expectedId)
            .hitRank(hitRank)
            .retrievedChunkIds(retrievedChunkIds)
            .build();
    }

    // ==================== 指标计算 ====================

    /**
     * Recall@K: Top-K 结果中命中正确答案的比例.
     * recall@k = TP@k / (TP@k + FN)
     */
    double calculateRecallAtK(List<QueryResult> results, int k) {
        long total = results.size();
        long hits = results.stream()
            .filter(r -> r.hitRank > 0 && r.hitRank <= k)
            .count();
        return total == 0 ? 0.0 : (double) hits / total;
    }

    /**
     * MRR: 第一个正确答案的平均排名倒数.
     * MRR = 1/N * Σ(1/rank_i)
     */
    double calculateMRR(List<QueryResult> results) {
        long total = results.size();
        double sum = results.stream()
            .filter(r -> r.hitRank > 0)
            .mapToDouble(r -> 1.0 / r.hitRank)
            .sum();
        return total == 0 ? 0.0 : sum / total;
    }

    /**
     * NDCG@K: 归一化折损累计增益.
     * DCG@k = Σ(rel_i / log2(i+1)), rel_i = 1 if hit else 0
     * IDCG@k = ideal DCG (all relevant items at top)
     */
    double calculateNDCG(List<QueryResult> results, int k) {
        double totalDcg = 0.0;
        double totalIdcg = 0.0;
        for (QueryResult r : results) {
            double dcg = 0.0;
            double idcg = 1.0;  // ideal: 1 relevant item at rank 1
            if (r.hitRank > 0 && r.hitRank <= k) {
                dcg = 1.0 / (Math.log(r.hitRank + 1) / Math.log(2));
            }
            totalDcg += dcg;
            totalIdcg += idcg;
        }
        return totalIdcg == 0 ? 0.0 : totalDcg / totalIdcg;
    }

    /**
     * Hit Rate: 至少命中 1 个正确答案的问题比例.
     */
    double calculateHitRate(List<QueryResult> results) {
        long total = results.size();
        long hits = results.stream().filter(r -> r.hitRank > 0).count();
        return total == 0 ? 0.0 : (double) hits / total;
    }

    // ==================== DTOs ====================

    @Data
    @Builder
    public static class PrecisionReport {
        private String kbId;
        private String datasetId;
        private int totalQueries;
        private double recallAt5;
        private double mrr;
        private double ndcgAt5;
        private double hitRate;
        private List<QueryResult> perQueryResults;
        private Long evaluatedAt;

        static PrecisionReport empty(String kbId, String datasetId) {
            return PrecisionReport.builder()
                .kbId(kbId).datasetId(datasetId)
                .totalQueries(0).recallAt5(0).mrr(0).ndcgAt5(0).hitRate(0)
                .perQueryResults(List.of()).evaluatedAt(TimeConverters.toEpochMilli(java.time.LocalDateTime.now())).build();
        }
    }

    @Data
    @Builder
    public static class QueryResult {
        private String question;
        private String expectedChunkId;
        private int hitRank;           // 0 = 未命中, 1-N = 命中的排名
        private List<String> retrievedChunkIds;

        static QueryResult failed(String question) {
            return QueryResult.builder()
                .question(question).expectedChunkId(null)
                .hitRank(0).retrievedChunkIds(List.of()).build();
        }
    }
}
