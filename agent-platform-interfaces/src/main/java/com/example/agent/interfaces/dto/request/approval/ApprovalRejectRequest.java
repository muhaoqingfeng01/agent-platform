package com.example.agent.interfaces.dto.request.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审批拒绝请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "审批拒绝请求")
public class ApprovalRejectRequest {

    @NotBlank(message = "审批ID不能为空")
    @Schema(description = "审批ID")
    private String approvalId;

    @NotBlank(message = "拒绝原因不能为空")
    @Schema(description = "拒绝原因", example = "参数异常，拒绝执行")
    private String reason;
}
