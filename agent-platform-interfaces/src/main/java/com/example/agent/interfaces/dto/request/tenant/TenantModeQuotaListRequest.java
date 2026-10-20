package com.example.agent.interfaces.dto.request.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "查询租户模式配额")
public class TenantModeQuotaListRequest {

    @NotNull(message = "租户标识不能为空")
    @Schema(description = "租户唯一标识 tenantId")
    private Long tenantId;
}
