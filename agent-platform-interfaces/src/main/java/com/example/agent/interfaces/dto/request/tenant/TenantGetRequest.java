package com.example.agent.interfaces.dto.request.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "租户ID请求")
public class TenantGetRequest {
    @NotNull(message = "租户ID不能为空")
    @Schema(description = "租户ID")
    private Long id;
}
