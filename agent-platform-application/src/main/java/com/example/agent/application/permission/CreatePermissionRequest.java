package com.example.agent.application.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePermissionRequest {
    @NotBlank @Size(min = 2, max = 128)
    @Schema(description = "权限编码", example = "report:export")
    private String permissionCode;

    @NotBlank @Size(min = 2, max = 128)
    @Schema(description = "资源路径", example = "report")
    private String resource;

    @NotBlank
    @Schema(description = "操作类型", example = "WRITE", allowableValues = {"READ", "WRITE", "DELETE", "ADMIN", "PUBLISH"})
    private String action;

    @Schema(description = "权限描述", example = "导出报表")
    private String description;
}
