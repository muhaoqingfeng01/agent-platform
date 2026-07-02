package com.example.agent.application.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务规划响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务规划结果")
public class TaskPlanResponse {

    @Schema(description = "执行 ID")
    private String executionId;

    @Schema(description = "总步骤数")
    private int totalSteps;

    @Schema(description = "拓扑层级数")
    private int levels;

    @Schema(description = "规划结果描述")
    private String message;
}
