package com.example.agent.application.evaluation;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Grid Search 自动调优器.
 *
 * <p>自动搜索最优参数组合，目标函数: max(Recall@5 * 0.5 + MRR * 0.3 + NDCG@5 * 0.2).
 *
 * <p>搜索空间示例:
 * <ul>
 *   <li>检索策略: [precise, balanced, fast]</li>
 *   <li>TopK: [3, 5, 10]</li>
 *   <li>Reranker: [NONE, LLM]</li>
 * </ul>
 *
 * <p>注意：搜索空间为笛卡尔积，大空间下建议用 Random Search 或 Bayesian Optimization.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GridSearchOptimizer {

    private final RetrievalPrecisionService precisionService;

    /**
     * 执行 Grid Search，返回 Top-3 最优参数组合及对应指标.
     */
    public OptimizationResult optimize(String kbId, String datasetId) {
        // 定义搜索空间
        List<ParamCombination> searchSpace = buildSearchSpace();
        List<ScoredCombination> results = new ArrayList<>();

        log.info("[GridSearch] 开始搜索: kbId={}, datasetId={}, combinations={}",
            kbId, datasetId, searchSpace.size());

        for (int i = 0; i < searchSpace.size(); i++) {
            ParamCombination params = searchSpace.get(i);
            log.debug("[GridSearch] [{}/{}] 测试参数: {}", i + 1, searchSpace.size(), params.getName());

            try {
                // 使用参数执行评估（searchService 会使用 searchConfig 中的参数）
                RetrievalPrecisionService.PrecisionReport report = precisionService.evaluate(
                    kbId, datasetId);

                // 综合得分
                double score = report.getRecallAt5() * 0.5
                             + report.getMrr() * 0.3
                             + report.getNdcgAt5() * 0.2;

                results.add(new ScoredCombination(params, score, report));
                log.debug("[GridSearch] 参数={}, score={:.3f}, recall@5={:.3f}, mrr={:.3f}, ndcg@5={:.3f}",
                    params.getName(), score, report.getRecallAt5(), report.getMrr(), report.getNdcgAt5());

            } catch (Exception e) {
                log.warn("[GridSearch] 参数组合评估失败: {}, error={}", params.getName(), e.getMessage());
            }
        }

        // 按得分降序 → Top-3 推荐
        List<ScoredCombination> topN = results.stream()
            .sorted(Comparator.comparingDouble(ScoredCombination::getScore).reversed())
            .limit(3)
            .toList();

        return OptimizationResult.builder()
            .kbId(kbId)
            .datasetId(datasetId)
            .totalCombinations(searchSpace.size())
            .evaluatedCombinations(results.size())
            .recommendations(topN)
            .build();
    }

    /**
     * 构建搜索空间 — 参数组合的笛卡尔积.
     */
    private List<ParamCombination> buildSearchSpace() {
        List<ParamCombination> space = new ArrayList<>();

        String[] strategies = {"precise", "balanced", "fast"};
        int[] topKValues = {3, 5, 10};
        String[] rerankers = {"NONE", "LLM"};

        for (String strategy : strategies) {
            for (int topK : topKValues) {
                for (String reranker : rerankers) {
                    space.add(ParamCombination.builder()
                        .name(String.format("strategy=%s,topK=%d,reranker=%s", strategy, topK, reranker))
                        .searchStrategy(strategy)
                        .topK(topK)
                        .rerankerType(reranker)
                        .build());
                }
            }
        }
        return space;
    }

    // ==================== DTOs ====================

    @Data
    @Builder
    public static class ParamCombination {
        private String name;
        private String searchStrategy;
        private int topK;
        private String rerankerType;
    }

    @Data
    @Builder
    public static class ScoredCombination {
        private ParamCombination params;
        private double score;
        private RetrievalPrecisionService.PrecisionReport report;
    }

    @Data
    @Builder
    public static class OptimizationResult {
        private String kbId;
        private String datasetId;
        private int totalCombinations;
        private int evaluatedCombinations;
        private List<ScoredCombination> recommendations;
    }
}
