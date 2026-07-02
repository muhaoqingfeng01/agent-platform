package com.example.agent.application.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动作处理器信息响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "动作处理器信息")
public class ActionHandlerResponse {

    @Schema(description = "动作类型标识")
    private String action;

    @Schema(description = "动作描述")
    private String description;

    @Schema(description = "参数 Schema（JSON）")
    private String paramsSchema;

    @Schema(description = "是否高风险操作")
    private boolean highRisk;

    @Schema(description = "最大重试次数")
    private int maxRetries;

    @Schema(description = "超时时间（毫秒）")
    private long timeoutMs;
}
