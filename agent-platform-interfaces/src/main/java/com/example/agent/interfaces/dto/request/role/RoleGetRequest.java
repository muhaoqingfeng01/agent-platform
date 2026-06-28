package com.example.agent.interfaces.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "角色ID请求")
public class RoleGetRequest {
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID")
    private Long id;
}
