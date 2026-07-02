package com.example.agent.application.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 检索策略预设响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "检索策略预设")
public class StrategyPresetResponse {

    @Schema(description = "策略名称标识")
    private String strategyName;

    @Schema(description = "策略描述")
    private String description;

    @Schema(description = "检索参数")
    private Map<String, Object> searchParams;

    @Schema(description = "多阶段检索参数")
    private Map<String, Object> multiStageParams;
}
