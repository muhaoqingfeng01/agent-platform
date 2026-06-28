package com.example.agent.interfaces.dto.request.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "启停租户请求（含ID）")
public class TenantToggleStatusRequest {
    @NotNull(message = "租户ID不能为空")
    @Schema(description = "租户ID")
    private Long id;
    @Schema(description = "目标状态")
    private String status;
}
