package com.example.agent.interfaces.dto.request.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新租户请求（含ID）")
public class TenantUpdateRequest {
    @NotNull(message = "租户ID不能为空")
    @Schema(description = "租户ID")
    private Long tenantId;
    @NotBlank
    @Size(min = 2, max = 128)
    @Schema(description = "租户名称", example = "ACME 科技有限公司（更新）")
    private String name;

    @Schema(description = "套餐等级", example = "PREMIUM")
    private String tier;

    @Schema(description = "租户配置 JSON", example = "{\"maxUsers\":100}")
    private String configJson;
}
