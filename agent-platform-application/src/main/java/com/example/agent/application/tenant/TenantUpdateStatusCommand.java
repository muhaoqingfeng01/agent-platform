package com.example.agent.application.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantUpdateStatusCommand {
    @NotBlank
    @Schema(description = "目标状态", example = "SUSPENDED", allowableValues = {"ACTIVE", "SUSPENDED"})
    private String status;
}
