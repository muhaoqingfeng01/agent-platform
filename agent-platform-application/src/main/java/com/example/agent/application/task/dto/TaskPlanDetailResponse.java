package com.example.agent.application.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 任务执行计划详情响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务执行计划详情")
public class TaskPlanDetailResponse {

    @Schema(description = "执行 ID")
    private String executionId;

    @Schema(description = "总步骤数")
    private int totalSteps;

    @Schema(description = "拓扑层级数")
    private int levels;

    @Schema(description = "DAG 节点列表")
    private List<?> nodes;
}
