package com.example.agent.interfaces.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "任务执行ID请求")
public class TaskExecutionGetRequest {
    @NotBlank(message = "执行ID不能为空")
    @Schema(description = "执行ID")
    private String executionId;
}
