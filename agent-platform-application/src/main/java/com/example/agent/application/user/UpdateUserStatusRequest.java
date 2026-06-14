package com.example.agent.application.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotBlank
    @Schema(description = "目标状态", example = "DISABLED", allowableValues = {"ACTIVE", "DISABLED"})
    private String status;
}
