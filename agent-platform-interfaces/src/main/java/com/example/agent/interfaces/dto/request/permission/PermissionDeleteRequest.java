package com.example.agent.interfaces.dto.request.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "删除权限请求")
public class PermissionDeleteRequest {
    @NotNull(message = "权限ID不能为空")
    @Schema(description = "权限ID")
    private Long id;
}
