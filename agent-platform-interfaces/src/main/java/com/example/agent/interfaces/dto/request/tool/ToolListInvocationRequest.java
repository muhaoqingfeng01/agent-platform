package com.example.agent.interfaces.dto.request.tool;

import com.example.agent.common.constant.ProjectConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "调用日志列表请求")
public class ToolListInvocationRequest {
    @Schema(description = "工具ID（可选）")
    private String toolId;
    @Schema(description = "页码", example = "0")
    private int page = ProjectConstants.Page.DEFAULT_PAGE_NUM;
    @Schema(description = "每页数量", example = "20")
    private int size = ProjectConstants.Page.DEFAULT_PAGE_SIZE;
}
