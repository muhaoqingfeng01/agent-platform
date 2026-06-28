package com.example.agent.interfaces.dto.request.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审批同意请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "审批同意请求")
public class ApprovalApproveRequest {

    @NotBlank(message = "审批ID不能为空")
    @Schema(description = "审批ID")
    private String approvalId;

    @Schema(description = "审批意见", example = "同意执行")
    private String comment;
}
