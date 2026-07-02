package com.example.agent.application.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务取消响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务取消结果")
public class TaskCancelResponse {

    @Schema(description = "执行 ID")
    private String executionId;

    @Schema(description = "取消后状态")
    private String status;

    @Schema(description = "取消结果描述")
    private String message;
}
