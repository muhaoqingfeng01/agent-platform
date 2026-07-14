package com.example.agent.interfaces.dto.request.tenant;

import com.example.agent.common.constant.ProjectConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户列表查询请求")
public class TenantListRequest {
    @Schema(description = "页码（从 0 开始）", example = "0")
    private int page = ProjectConstants.Page.DEFAULT_PAGE_NUM;
    @Schema(description = "每页数量", example = "20")
    private int size = ProjectConstants.Page.DEFAULT_PAGE_SIZE;
}
