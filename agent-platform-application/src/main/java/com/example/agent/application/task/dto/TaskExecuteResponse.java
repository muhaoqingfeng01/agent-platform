package com.example.agent.application.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务执行响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务执行结果")
public class TaskExecuteResponse {

    @Schema(description = "执行 ID")
    private String executionId;

    @Schema(description = "执行状态: RUNNING / PENDING / COMPLETED / FAILED")
    private String status;

    @Schema(description = "结果描述（异步提交时返回）")
    private String message;
}
