package com.example.agent.interfaces.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 执行任务请求 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "执行任务请求")
public class TaskExecuteRequest {

    @NotBlank(message = "执行 ID 不能为空")
    @Schema(description = "任务规划返回的 executionId", example = "exec_abc123")
    private String executionId;

    @Schema(description = "是否异步执行（默认 true）", example = "true")
    private boolean async = true;
}
