package com.example.agent.application.role;

import com.example.agent.domain.tenant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleResponse {
    @Schema(description = "主键 ID") private Long id;
    @Schema(description = "所属租户") private Long tenantId;
    @Schema(description = "角色编码") private String roleCode;
    @Schema(description = "角色名称") private String roleName;
    @Schema(description = "角色描述") private String description;
    @Schema(description = "创建时间") private LocalDateTime createdAt;

    public static RoleResponse from(Role r) {
        RoleResponse resp = new RoleResponse();
        resp.setId(r.getId());
        resp.setTenantId(r.getTenantId());
        resp.setRoleCode(r.getRoleCode());
        resp.setRoleName(r.getRoleName());
        resp.setDescription(r.getDescription());
        resp.setCreatedAt(r.getCreatedAt());
        return resp;
    }
}
