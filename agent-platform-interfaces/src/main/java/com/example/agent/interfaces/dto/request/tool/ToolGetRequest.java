package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "工具ID请求")
public class ToolGetRequest {
    @NotBlank(message = "工具ID不能为空")
    @Schema(description = "工具ID")
    private String id;
}
