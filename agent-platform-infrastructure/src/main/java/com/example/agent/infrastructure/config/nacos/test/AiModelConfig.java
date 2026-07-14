package com.example.agent.infrastructure.config.nacos.test;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.example.agent.infrastructure.config.nacos.NacosConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AI 模型配置 — 继承 {@link NacosConfig} 模板，只定义字段和属性映射.
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


    // ========== 内部 POJO（Jackson 反序列化） ==========

    /**
     * 配置数据对象 — 可通过 {@link AiModelConfig#getConfig()} 获取完整快照.
     */
    @Data
    public static class AiModelProps {
        public Integer maxParallelism;
        public Integer stepTimeoutMinutes;
        public String retryStrategy;
        public Integer dagMaxDepth;
        public Integer dagMaxNodes;
        public Integer planningTimeoutSeconds;
    }
}
