package com.example.agent.infrastructure.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * AI 模型配置 — 继承 {@link NacosConfig} 模板.
 *
 * <p>对应 Nacos DataId: {@code agent-platform-ai-model.json}
 * <p>Group: {@code AGENT-PLATFORM-CONFIG_ENTITY}
 *
 * <p>覆盖 DAG 任务调度参数 + LLM 调用参数 + Embedding 模型参数。
 * <p>所有字段提供 {@code getXxx()} 便捷方法，Nacos 不可用时回退到硬编码兜底值.
 * <p>🆕 P6 配置治理子方案03: 从 test 包升迁 + 补全 LLM/Embedding 参数字段.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class AiModelConfig extends NacosConfig<AiModelConfig.AiModelProps> {

    // ========== 构造器 ==========

    public AiModelConfig(NacosConfigManager nacosConfigManager, ObjectMapper objectMapper) {
        super(nacosConfigManager, objectMapper);
    }

    // ========== 模板方法实现 ==========

    @Override
    protected String getDataId() {
        return "agent-platform-ai-model.json";
    }

    @Override
    protected String getGroup() {
        return "AGENT-PLATFORM-CONFIG_ENTITY";
    }

    @Override
    protected String getConfigName() {
        return "AiModelConfig";
    }

    @Override
    protected Class<AiModelProps> getPropsClass() {
        return AiModelProps.class;
    }

    // ============================================================
    // 便捷取值方法（Nacos 不可用时硬编码兜底，Optional 链式判空）
    // ============================================================

    // --- DAG 任务调度参数（原有字段） ---

    /** DAG 最大并行度，默认 5 */
    public int getMaxParallelism() {
        return Optional.ofNullable(getConfig()).map(p -> p.maxParallelism).orElse(5);
    }

    /** 单个步骤超时时间（分钟），默认 10 */
    public int getStepTimeoutMinutes() {
        return Optional.ofNullable(getConfig()).map(p -> p.stepTimeoutMinutes).orElse(10);
    }

    /** 重试策略，默认 EXPONENTIAL_BACKOFF */
    public String getRetryStrategy() {
        return Optional.ofNullable(getConfig()).map(p -> p.retryStrategy).orElse("EXPONENTIAL_BACKOFF");
    }

    /** DAG 最大深度，默认 10 */
    public int getDagMaxDepth() {
        return Optional.ofNullable(getConfig()).map(p -> p.dagMaxDepth).orElse(10);
    }

    /** DAG 最大节点数，默认 50 */
    public int getDagMaxNodes() {
        return Optional.ofNullable(getConfig()).map(p -> p.dagMaxNodes).orElse(50);
    }

    /** 规划超时时间（秒），默认 120 */
    public int getPlanningTimeoutSeconds() {
        return Optional.ofNullable(getConfig()).map(p -> p.planningTimeoutSeconds).orElse(120);
    }

    // --- 🆕 LLM 调用参数 ---

    /** Chat 模型名称，默认 deepseek-v4-pro */
    public String getChatModel() {
        return Optional.ofNullable(getConfig()).map(p -> p.chatModel).orElse("deepseek-v4-pro");
    }

    /** Chat 温度参数，默认 0.7 */
    public double getChatTemperature() {
        return Optional.ofNullable(getConfig()).map(p -> p.chatTemperature).orElse(0.7);
    }

    /** Chat 最大 Token 数，默认 4096 */
    public int getChatMaxTokens() {
        return Optional.ofNullable(getConfig()).map(p -> p.chatMaxTokens).orElse(4096);
    }

    /** Embedding 模型名称，默认 text-embedding-v3 */
    public String getEmbeddingModel() {
        return Optional.ofNullable(getConfig()).map(p -> p.embeddingModel).orElse("text-embedding-v3");
    }

    /** Chat 调用超时（秒），默认 60 */
    public int getChatTimeoutSeconds() {
        return Optional.ofNullable(getConfig()).map(p -> p.chatTimeoutSeconds).orElse(60);
    }

    /** Chat 最大重试次数，默认 3 */
    public int getChatRetryMaxAttempts() {
        return Optional.ofNullable(getConfig()).map(p -> p.chatRetryMaxAttempts).orElse(3);
    }

    // ============================================================
    // 内部 POJO（Jackson 反序列化）
    // ============================================================

    /**
     * 配置数据对象 — 可通过 {@link AiModelConfig#getConfig()} 获取完整快照.
     */
    public static class AiModelProps {
        // 原有字段 — DAG 任务调度
        public Integer maxParallelism;
        public Integer stepTimeoutMinutes;
        public String retryStrategy;
        public Integer dagMaxDepth;
        public Integer dagMaxNodes;
        public Integer planningTimeoutSeconds;

        // 🆕 LLM 调用参数
        public String chatModel;
        public Double chatTemperature;
        public Integer chatMaxTokens;
        public String embeddingModel;
        public Integer chatTimeoutSeconds;
        public Integer chatRetryMaxAttempts;
    }
}
