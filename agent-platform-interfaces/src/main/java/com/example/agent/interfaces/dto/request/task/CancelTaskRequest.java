package com.example.agent.interfaces.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取消任务请求 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "取消任务请求")
public class CancelTaskRequest {

    @NotBlank(message = "执行 ID 不能为空")
    @Schema(description = "要取消的任务执行 ID", example = "exec_abc123")
    private String executionId;
}
