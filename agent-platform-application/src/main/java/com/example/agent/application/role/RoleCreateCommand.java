package com.example.agent.application.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleCreateCommand {
    @NotBlank
    @Schema(description = "所属租户标识", example = "tenant_acme")
    private Long tenantId;

    @NotBlank @Size(min = 2, max = 64)
    @Schema(description = "角色编码", example = "DATA_ANALYST")
    private String roleCode;

    @NotBlank @Size(min = 2, max = 128)
    @Schema(description = "角色名称", example = "数据分析师")
    private String roleName;

    @Schema(description = "角色描述", example = "可查看和导出数据分析报表")
    private String description;
}
