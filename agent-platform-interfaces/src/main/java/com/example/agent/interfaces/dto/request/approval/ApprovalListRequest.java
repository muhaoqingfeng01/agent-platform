package com.example.agent.interfaces.dto.request.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审批列表查询请求")
public class ApprovalListRequest {
    @Schema(description = "筛选类型: my-pending/my-resolved/my-requested/by-status/all", example = "my-pending")
    private String filter = "my-pending";
    @Schema(description = "审批人ID")
    private String approverId;
    @Schema(description = "发起人ID")
    private String requesterId;
    @Schema(description = "状态筛选", example = "PENDING")
    private String status = "PENDING";
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "20")
    private int size = 20;
}
