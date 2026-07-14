package com.example.agent.interfaces.dto.request.optimization;

import com.example.agent.common.constant.ProjectConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "优化工单列表请求")
public class TicketListRequest {
    @Schema(description = "页码", example = "1")
    private int page = 1;
    @Schema(description = "每页大小", example = "20")
    private int size = ProjectConstants.Page.DEFAULT_PAGE_SIZE;
}
