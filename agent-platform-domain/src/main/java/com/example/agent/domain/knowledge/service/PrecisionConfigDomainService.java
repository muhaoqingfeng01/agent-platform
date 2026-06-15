package com.example.agent.domain.knowledge.service;

import com.example.agent.domain.knowledge.valueobject.ConsistencyLevel;
import com.example.agent.domain.knowledge.valueobject.IndexType;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 精度配置领域服务 — 配置解析与合并.
 * <p>
 * 四级配置覆盖: 文档级 > 知识库级 > 策略预设 > 系统默认
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class PrecisionConfigDomainService {

    /**
     * 四级配置深度合并，上层覆盖下层每个独立参数.
     *
     * @param systemDefaults   Layer 1: application.yml 系统默认值
     * @param strategyPreset   Layer 2: 策略预设 (precise/balanced/fast/recall/turbo)
     * @param kbConfig         Layer 3: t_knowledge_base 知识库级配置
     * @param docOverride      Layer 4: t_document 文档级覆盖
     * @return 合并后的最终配置
     */
    public MergedPrecisionConfig mergeConfig(
            Map<String, Object> systemDefaults,
            Map<String, Object> strategyPreset,
            Map<String, Object> kbConfig,
            Map<String, Object> docOverride) {

        Map<String, Object> merged = new java.util.LinkedHashMap<>();
        if (systemDefaults != null) merged.putAll(systemDefaults);
        if (strategyPreset != null) merged.putAll(strategyPreset);
        if (kbConfig != null) merged.putAll(kbConfig);
        if (docOverride != null) merged.putAll(docOverride);

        return MergedPrecisionConfig.from(merged);
    }

    /**
     * 校验单个检索参数的合法性.
     */
    public void validateSearchParams(int nprobe, int nlist, double similarityThreshold,
                                     int fusionTopN, int topK, double vectorWeight,
                                     double keywordWeight, boolean enableReranker,
                                     String rerankerType) {
        if (nprobe > nlist) {
            throw new IllegalArgumentException("nprobe(" + nprobe + ") 不能超过 nlist(" + nlist + ")");
        }
        if (similarityThreshold < 0.0 || similarityThreshold > 1.0) {
            throw new IllegalArgumentException("相似度阈值必须在 0.0 ~ 1.0 之间");
        }
        if (fusionTopN > topK) {
            throw new IllegalArgumentException("融合返回数(" + fusionTopN + ")不应大于向量检索返回数(" + topK + ")");
        }
        if (vectorWeight + keywordWeight == 0) {
            throw new IllegalArgumentException("向量权重和关键词权重不能同时为 0");
        }
        if (enableReranker && (rerankerType == null || "NONE".equalsIgnoreCase(rerankerType))) {
            throw new IllegalArgumentException("启用 Reranker 时必须选择 Reranker 类型");
        }
    }

    /**
     * 合并后的精度配置 — 领域值对象.
     */
    public record MergedPrecisionConfig(
            String nprobeRatio,
            int topK,
            double similarityThreshold,
            ConsistencyLevel consistencyLevel,
            int timeoutMs,
            boolean enableReranker,
            String rerankerType,
            int rerankerTopK,
            int coarseTopK,
            boolean enableRrfFusion,
            int rrfK,
            int fusionTopN,
            double vectorWeight,
            double keywordWeight,
            String searchStrategy
    ) {
        @SuppressWarnings("unchecked")
        public static MergedPrecisionConfig from(Map<String, Object> map) {
            return new MergedPrecisionConfig(
                    getString(map, "nprobeRatio", "0.10"),
                    getInt(map, "topK", 20),
                    getDouble(map, "similarityThreshold", 0.50),
                    ConsistencyLevel.fromCode(getString(map, "consistencyLevel", "BOUNDED")),
                    getInt(map, "timeoutMs", 5000),
                    getBoolean(map, "enableReranker", false),
                    getString(map, "rerankerType", "NONE"),
                    getInt(map, "rerankerTopK", 10),
                    getInt(map, "coarseTopK", 50),
                    getBoolean(map, "enableRrfFusion", true),
                    getInt(map, "rrfK", 60),
                    getInt(map, "fusionTopN", 5),
                    getDouble(map, "vectorWeight", 0.5),
                    getDouble(map, "keywordWeight", 0.5),
                    getString(map, "searchStrategy", "balanced")
            );
        }

        private static String getString(Map<String, Object> m, String k, String def) {
            Object v = m.get(k);
            return v != null ? v.toString() : def;
        }

        private static int getInt(Map<String, Object> m, String k, int def) {
            Object v = m.get(k);
            if (v instanceof Number n) return n.intValue();
            if (v instanceof String s) {
                try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
            }
            return def;
        }

        private static double getDouble(Map<String, Object> m, String k, double def) {
            Object v = m.get(k);
            if (v instanceof Number n) return n.doubleValue();
            if (v instanceof String s) {
                try { return Double.parseDouble(s); } catch (NumberFormatException e) { return def; }
            }
            return def;
        }

        private static boolean getBoolean(Map<String, Object> m, String k, boolean def) {
            Object v = m.get(k);
            if (v instanceof Boolean b) return b;
            if (v instanceof String s) return "true".equalsIgnoreCase(s);
            return def;
        }
    }
}
