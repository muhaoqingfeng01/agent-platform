package com.example.agent.interfaces.dto.request.tool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 启停工具请求 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
public class ToggleToolStatusRequest {

    /** 目标状态 — ACTIVE 或 DISABLED */
    @NotBlank(message = "状态不能为空")
    @Schema(description = "目标状态", example = "DISABLED", allowableValues = {"ACTIVE", "DISABLED"})
    private String status;
}
