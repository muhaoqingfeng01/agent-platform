package com.example.agent.interfaces.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色分配用户请求 DTO
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "角色分配用户请求")
public class RoleAssignUserRequest {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID")
    private Long roleId;

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private String userId;
}
