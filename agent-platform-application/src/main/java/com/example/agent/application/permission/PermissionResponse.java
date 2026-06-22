package com.example.agent.application.permission;

import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.tenant.Permission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PermissionResponse {
    @Schema(description = "主键 ID") private Long id;
    @Schema(description = "权限编码") private String permissionCode;
    @Schema(description = "资源路径") private String resource;
    @Schema(description = "操作类型") private String action;
    @Schema(description = "权限描述") private String description;
    @Schema(description = "创建时间") private Long createdAt;

    public static PermissionResponse from(Permission p) {
        PermissionResponse r = new PermissionResponse();
        r.setId(p.getId());
        r.setPermissionCode(p.getPermissionCode());
        r.setResource(p.getResource());
        r.setAction(p.getAction());
        r.setDescription(p.getDescription());
        r.setCreatedAt(TimeConverters.toEpochMilli(p.getCreatedAt()));
        return r;
    }
}
