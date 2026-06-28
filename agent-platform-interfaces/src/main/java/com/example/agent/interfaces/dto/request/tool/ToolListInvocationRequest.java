package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "调用日志列表请求")
public class ToolListInvocationRequest {
    @Schema(description = "工具ID（可选）")
    private String toolId;
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页数量", example = "20")
    private int size = 20;
}
