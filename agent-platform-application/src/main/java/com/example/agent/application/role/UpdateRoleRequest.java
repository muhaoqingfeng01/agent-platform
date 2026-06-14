package com.example.agent.application.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotBlank @Size(min = 2, max = 128)
    @Schema(description = "角色名称", example = "高级数据分析师")
    private String roleName;

    @Schema(description = "角色描述")
    private String description;
}
