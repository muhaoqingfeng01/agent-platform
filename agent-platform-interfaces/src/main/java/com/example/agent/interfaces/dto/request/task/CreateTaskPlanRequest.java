package com.example.agent.interfaces.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建任务规划请求 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "任务规划请求")
public class CreateTaskPlanRequest {

    @NotBlank(message = "用户意图不能为空")
    @Schema(description = "用户原始意图描述", example = "帮我查一下上周的订单总额并发送邮件给张三")
    private String userIntent;

    @Schema(description = "关联会话 ID", example = "conv_abc123")
    private String conversationId;

    @NotBlank(message = "Agent ID 不能为空")
    @Schema(description = "关联 Agent ID", example = "agent_001")
    private String agentId;
}
