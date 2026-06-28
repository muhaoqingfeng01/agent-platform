package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "启停工具请求（含ID）")
public class ToolToggleStatusRequest {
    @NotBlank(message = "工具ID不能为空")
    @Schema(description = "工具ID")
    private String id;
    @NotBlank(message = "状态不能为空")
    @Schema(description = "目标状态")
    private String status;
}
