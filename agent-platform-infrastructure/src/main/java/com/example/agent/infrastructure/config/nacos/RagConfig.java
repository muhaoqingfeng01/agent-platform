package com.example.agent.infrastructure.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * RAG 检索参数 Nacos 动态配置 — 继承 {@link NacosConfig} 模板.
 *
 * <p>对应 Nacos DataId: {@code agent-platform-rag.json}
 * <p>Group: {@code AGENT-PLATFORM-CONFIG_ENTITY}
 *
 * <p>所有字段提供 {@code getXxx()} 便捷方法，Nacos 不可用时回退到硬编码兜底值.
 * <p>🆕 P6 配置治理子方案02: 将 24 项 RAG 检索参数（召回+融合+精度监控）从 YAML 静态配置迁移到 Nacos 动态配置.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class RagConfig extends NacosConfig<RagConfig.RagProps> {

    // ========== 构造器 ==========

    public RagConfig(NacosConfigManager nacosConfigManager, ObjectMapper objectMapper) {
        super(nacosConfigManager, objectMapper);
    }

    // ========== 模板方法实现 ==========

    @Override
    protected String getDataId() {
        return "agent-platform-rag.json";
    }

    @Override
    protected String getGroup() {
        return "AGENT-PLATFORM-CONFIG_ENTITY";
    }

    @Override
    protected String getConfigName() {
        return "RagConfig";
    }

    @Override
    protected Class<RagProps> getPropsClass() {
        return RagProps.class;
    }

    // ============================================================
    // 便捷取值方法（Nacos 不可用时硬编码兜底，Optional 链式判空）
    // ============================================================

    // --- 召回参数 ---

    /** 向量检索 TopK，默认 20 */
    public int getSearchTopK() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.topK).orElse(20);
    }

    /** 相似度阈值，默认 0.50 */
    public double getSearchSimilarityThreshold() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.similarityThreshold).orElse(0.50);
    }

    /** Milvus 检索 nprobe 参数，默认 16 */
    public int getSearchNprobe() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.nprobe).orElse(16);
    }

    /** HNSW 检索 ef 参数，默认 64 */
    public int getSearchEf() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.ef).orElse(64);
    }

    /** DISKANN 检索 search_list_size，默认 100 */
    public int getSearchListSize() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.searchListSize).orElse(100);
    }

    /** Milvus 一致性级别，默认 BOUNDED */
    public String getSearchConsistencyLevel() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.consistencyLevel).orElse("BOUNDED");
    }

    /** 检索超时（毫秒），默认 5000 */
    public int getSearchTimeoutMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.search).map(s -> s.timeoutMs).orElse(5000);
    }

    // --- 索引构建参数 ---

    /** IVF 索引 nlist，默认 128 */
    public int getIndexNlist() {
        return Optional.ofNullable(getConfig()).map(p -> p.index).map(i -> i.nlist).orElse(128);
    }

    /** HNSW 索引 M，默认 16 */
    public int getIndexHnswM() {
        return Optional.ofNullable(getConfig()).map(p -> p.index).map(i -> i.hnswM).orElse(16);
    }

    /** HNSW 索引 efConstruction，默认 200 */
    public int getIndexHnswEfConstruction() {
        return Optional.ofNullable(getConfig()).map(p -> p.index).map(IndexProps::getHnswEfConstruction).orElse(200);
    }

    /** DISKANN 索引 max_degree，默认 56 */
    public int getIndexDiskannMaxDegree() {
        return Optional.ofNullable(getConfig()).map(p -> p.index).map(i -> i.diskannMaxDegree).orElse(56);
    }

    // --- 多阶段融合参数 ---

    /** 是否启用 Reranker 精排，默认 false */
    public boolean isMultiStageEnableReranker() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.enableReranker).orElse(false);
    }

    /** Reranker 类型，默认 NONE */
    public String getMultiStageRerankerType() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.rerankerType).orElse("NONE");
    }

    /** Reranker 精排 TopK，默认 10 */
    public int getMultiStageRerankerTopK() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.rerankerTopK).orElse(10);
    }

    /** 粗排 TopK，默认 50 */
    public int getMultiStageCoarseTopK() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.coarseTopK).orElse(50);
    }

    /** 是否启用 RRF 融合，默认 true */
    public boolean isMultiStageEnableRrfFusion() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.enableRrfFusion).orElse(true);
    }

    /** RRF 融合 k 值，默认 60 */
    public int getMultiStageRrfK() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.rrfK).orElse(60);
    }

    /** RRF 融合后取 TopN，默认 5 */
    public int getMultiStageFusionTopN() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.fusionTopN).orElse(5);
    }

    /** 向量检索权重，默认 0.5 */
    public double getMultiStageVectorWeight() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.vectorWeight).orElse(0.5);
    }

    /** 关键词检索权重，默认 0.5 */
    public double getMultiStageKeywordWeight() {
        return Optional.ofNullable(getConfig()).map(p -> p.multiStage).map(m -> m.keywordWeight).orElse(0.5);
    }

    // --- 精度监控参数 ---

    /** 是否启用自动调优，默认 false */
    public boolean isMonitoringEnableAutoTuning() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.enableAutoTuning).orElse(false);
    }

    /** 评估数据集大小，默认 50 */
    public int getMonitoringEvaluationDatasetSize() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.evaluationDatasetSize).orElse(50);
    }

    /** 召回率目标，默认 0.90 */
    public double getMonitoringRecallTarget() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.recallTarget).orElse(0.90);
    }

    /** 精确率目标，默认 0.80 */
    public double getMonitoringPrecisionTarget() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.precisionTarget).orElse(0.80);
    }

    /** 调优间隔（天），默认 7 */
    public int getMonitoringTuningIntervalDays() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.tuningIntervalDays).orElse(7);
    }

    /** 最大延迟目标（毫秒），默认 200 */
    public int getMonitoringMaxLatencyMsTarget() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.maxLatencyMsTarget).orElse(200);
    }

    /** 回归告警阈值，默认 0.05 */
    public double getMonitoringRegressionAlertThreshold() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.regressionAlertThreshold).orElse(0.05);
    }

    /** 是否启用 Grid Search，默认 false */
    public boolean isMonitoringGridSearchEnabled() {
        return Optional.ofNullable(getConfig()).map(p -> p.monitoring).map(m -> m.gridSearchEnabled).orElse(false);
    }

    // --- 最终输出 ---

    /** Reranker 精排后最终返回给 LLM 的 chunk 数量，默认 5 */
    public int getFinalTopK() {
        return Optional.ofNullable(getConfig()).map(p -> p.finalTopK).orElse(5);
    }

    // ============================================================
    // 内部 POJO（Jackson 反序列化，public field 风格对齐 SchedulerConfig）
    // ============================================================

    /**
     * 配置数据对象 — 可通过 {@link RagConfig#getConfig()} 获取完整快照.
     */
    @Data
    public static class RagProps {
        public SearchProps search;
        public IndexProps index;
        public MultiStageProps multiStage;
        public MonitoringProps monitoring;
        public Integer finalTopK;
    }

    @Data
    public static class IndexProps {
        public Integer nlist;
        public Integer hnswM;
        public Integer hnswEfConstruction;
        public Integer diskannMaxDegree;
    }

    @Data
    public static class SearchProps {
        public Integer topK;
        public Double similarityThreshold;
        public Integer nprobe;
        public Integer ef;
        public Integer searchListSize;
        public String consistencyLevel;
        public Integer timeoutMs;
    }

    @Data
    public static class MultiStageProps {
        public Boolean enableReranker;
        public String rerankerType;
        public Integer rerankerTopK;
        public Integer coarseTopK;
        public Boolean enableRrfFusion;
        public Integer rrfK;
        public Integer fusionTopN;
        public Double vectorWeight;
        public Double keywordWeight;
    }

    public static class MonitoringProps {
        public Boolean enableAutoTuning;
        public Integer evaluationDatasetSize;
        public Double recallTarget;
        public Double precisionTarget;
        public Integer tuningIntervalDays;
        public Integer maxLatencyMsTarget;
        public Double regressionAlertThreshold;
        public Boolean gridSearchEnabled;
    }
}
