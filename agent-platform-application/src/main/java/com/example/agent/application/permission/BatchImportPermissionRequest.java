package com.example.agent.application.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量导入权限请求 — 支持 JSON 上传
 *
 * @author Agent Platform Team
 * @since 1.2.0
 */
@Data
public class BatchImportPermissionRequest {

    @NotEmpty
    @Valid
    @Schema(description = "待导入的权限列表")
    private List<CreatePermissionRequest> permissions;
}
