package com.example.agent.interfaces.dto.request.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审批获取请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "审批获取请求")
public class ApprovalGetRequest {
    @NotBlank(message = "审批ID不能为空")
    @Schema(description = "审批ID")
    private String approvalId;
}
