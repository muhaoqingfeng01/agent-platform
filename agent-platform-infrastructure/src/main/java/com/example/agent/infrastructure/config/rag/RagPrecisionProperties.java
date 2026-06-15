package com.example.agent.infrastructure.config.rag;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RAG 检索精度系统级默认配置 — 对应 application.yml 中 agent.rag.*.
 * <p>
 * 所有参数的四级覆盖: 文档级 > 知识库级 > 策略预设 > 系统默认（本配置）.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "agent.rag")
public class RagPrecisionProperties {

    private IndexProps index = new IndexProps();
    private SearchProps search = new SearchProps();
    private MultiStageProps multiStage = new MultiStageProps();
    private MonitoringProps monitoring = new MonitoringProps();

    @Data
    public static class IndexProps {
        private String defaultType = "IVF_FLAT";
        private String defaultMetricType = "COSINE";
        private int nlist = 128;
        private int hnswM = 16;
        private int hnswEfConstruction = 200;
        private int pqNbits = 8;
        private int pqM = 768;
        private int diskannMaxDegree = 56;
    }

    @Data
    public static class SearchProps {
        private String defaultStrategy = "balanced";
        private int nprobe = 16;
        private int ef = 64;
        private int searchListSize = 100;
        private int topK = 20;
        private double similarityThreshold = 0.50;
        private String consistencyLevel = "BOUNDED";
        private int timeoutMs = 5000;
    }

    @Data
    public static class MultiStageProps {
        private boolean enableReranker = false;
        private String rerankerType = "NONE";
        private int rerankerTopK = 10;
        private int coarseTopK = 50;
        private boolean enableRrfFusion = true;
        private int rrfK = 60;
        private int fusionTopN = 5;
        private double vectorWeight = 0.5;
        private double keywordWeight = 0.5;
    }

    @Data
    public static class MonitoringProps {
        private boolean enableAutoTuning = false;
        private int evaluationDatasetSize = 50;
        private double recallTarget = 0.90;
        private double precisionTarget = 0.80;
        private int tuningIntervalDays = 7;
        private int maxLatencyMsTarget = 200;
        private double regressionAlertThreshold = 0.05;
        private boolean gridSearchEnabled = false;
    }
}
