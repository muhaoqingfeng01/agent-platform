package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.common.result.Result;
import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限定义列表、创建权限")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    // ==================== DTOs ====================

    @Getter @Setter
    public static class CreatePermissionRequest {
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

    @Getter @Setter
    public static class PermissionResponse {
        @Schema(description = "主键 ID") private Long id;
        @Schema(description = "权限编码") private String permissionCode;
        @Schema(description = "资源路径") private String resource;
        @Schema(description = "操作类型") private String action;
        @Schema(description = "权限描述") private String description;
        @Schema(description = "创建时间") private LocalDateTime createdAt;

        public static PermissionResponse from(Permission p) {
            PermissionResponse r = new PermissionResponse();
            r.id = p.getId(); r.permissionCode = p.getPermissionCode();
            r.resource = p.getResource(); r.action = p.getAction();
            r.description = p.getDescription(); r.createdAt = p.getCreatedAt();
            return r;
        }
    }

    // ==================== API ====================

    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "权限列表", description = "获取系统所有权限定义，需 user:read 权限")
    public Result<List<PermissionResponse>> list() {
        log.debug("[Permission] 查询权限列表");
        List<PermissionResponse> list = permissionRepository.findAll().stream()
                .map(PermissionResponse::from).toList();
        return Result.ok(list);
    }

    @PostMapping
    @SaCheckPermission("user:write")
    @Operation(summary = "创建权限", description = "定义新的权限编码，需 user:write 权限")
    public Result<PermissionResponse> create(@Valid @RequestBody CreatePermissionRequest request) {
        log.info("[Permission] 创建权限: permissionCode={}", request.getPermissionCode());
        Permission permission = Permission.builder()
                .permissionCode(request.getPermissionCode())
                .resource(request.getResource()).action(request.getAction())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now()).deleted(false)
                .build();
        permissionRepository.save(permission);
        return Result.ok(PermissionResponse.from(permission));
    }
}
