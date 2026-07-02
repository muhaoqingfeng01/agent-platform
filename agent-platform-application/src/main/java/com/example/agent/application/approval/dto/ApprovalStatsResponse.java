package com.example.agent.application.approval.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审批统计响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "审批统计")
public class ApprovalStatsResponse {

    @Schema(description = "待审批数量")
    private long pending;

    @Schema(description = "已同意数量")
    private long approved;

    @Schema(description = "已拒绝数量")
    private long rejected;

    @Schema(description = "已超时数量")
    private long timeout;

    @Schema(description = "总工单数")
    private long total;
}
