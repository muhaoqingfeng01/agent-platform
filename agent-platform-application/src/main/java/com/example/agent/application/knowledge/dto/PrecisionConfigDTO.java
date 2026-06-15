package com.example.agent.application.knowledge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 精度配置 DTO — 完整的四级精度参数.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
public class PrecisionConfigDTO {
    // 索引参数
    private String indexType;
    private Map<String, Object> indexParams;

    // 检索策略
    private String searchStrategy;
    private Map<String, Object> searchParams;

    // 多阶段检索
    private Map<String, Object> multiStageParams;

    // 精度监控
    private Map<String, Object> monitoringParams;
}
