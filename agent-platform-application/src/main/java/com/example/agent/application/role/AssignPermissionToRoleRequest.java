package com.example.agent.application.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AssignPermissionToRoleRequest {
    @Schema(description = "权限主键 ID", example = "1")
    private Long permissionId;
}
