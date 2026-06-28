package com.example.agent.application.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TenantCreateCommand {
    @NotBlank @Size(min = 2, max = 64)
    @Schema(description = "租户唯一标识", example = "tenant_acme")
    private Long tenantId;

    @NotBlank @Size(min = 2, max = 128)
    @Schema(description = "租户名称", example = "ACME 科技有限公司")
    private String name;

    @Schema(description = "套餐等级", example = "STANDARD", allowableValues = {"STANDARD", "PREMIUM", "ENTERPRISE"})
    private String tier = "STANDARD";
}
