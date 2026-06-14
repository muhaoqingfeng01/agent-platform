package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.common.result.Result;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色 CRUD、角色-用户分配、角色-权限分配")
public class RoleController {

    private final RoleRepository roleRepository;

    // ==================== DTOs ====================

    @Getter @Setter
    public static class CreateRoleRequest {
        @NotBlank
        @Schema(description = "所属租户标识", example = "tenant_acme")
        private String tenantId;
        @NotBlank @Size(min = 2, max = 64)
        @Schema(description = "角色编码", example = "DATA_ANALYST")
        private String roleCode;
        @NotBlank @Size(min = 2, max = 128)
        @Schema(description = "角色名称", example = "数据分析师")
        private String roleName;
        @Schema(description = "角色描述", example = "可查看和导出数据分析报表")
        private String description;
    }

    @Getter @Setter
    public static class UpdateRoleRequest {
        @NotBlank @Size(min = 2, max = 128)
        @Schema(description = "角色名称", example = "高级数据分析师")
        private String roleName;
        @Schema(description = "角色描述")
        private String description;
    }

    @Getter @Setter
    public static class AssignRoleRequest {
        @NotBlank
        @Schema(description = "用户唯一标识", example = "user_abc123")
        private String userId;
    }

    @Getter @Setter
    public static class AssignPermissionRequest {
        @Schema(description = "权限主键 ID", example = "1")
        private Long permissionId;
    }

    @Getter @Setter
    public static class RoleResponse {
        @Schema(description = "主键 ID") private Long id;
        @Schema(description = "所属租户") private String tenantId;
        @Schema(description = "角色编码") private String roleCode;
        @Schema(description = "角色名称") private String roleName;
        @Schema(description = "角色描述") private String description;
        @Schema(description = "创建时间") private LocalDateTime createdAt;

        public static RoleResponse from(Role r) {
            RoleResponse resp = new RoleResponse();
            resp.id = r.getId(); resp.tenantId = r.getTenantId();
            resp.roleCode = r.getRoleCode(); resp.roleName = r.getRoleName();
            resp.description = r.getDescription(); resp.createdAt = r.getCreatedAt();
            return resp;
        }
    }

    // ==================== API ====================

    @PostMapping
    @SaCheckPermission("user:write")
    @Operation(summary = "创建角色", description = "创建新角色，需 user:write 权限")
    public Result<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        log.info("[Role] 创建角色: roleCode={}, tenantId={}", request.getRoleCode(), request.getTenantId());
        Role role = Role.builder()
                .tenantId(request.getTenantId()).roleCode(request.getRoleCode())
                .roleName(request.getRoleName()).description(request.getDescription())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deleted(false)
                .build();
        roleRepository.save(role);
        return Result.ok(RoleResponse.from(role));
    }

    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "角色列表", description = "获取指定租户下的角色列表")
    public Result<List<RoleResponse>> list(
            @Parameter(description = "租户标识", required = true) @RequestParam String tenantId) {
        log.debug("[Role] 查询角色列表: tenantId={}", tenantId);
        List<RoleResponse> list = roleRepository.findByTenant(tenantId).stream()
                .map(RoleResponse::from).toList();
        return Result.ok(list);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "更新角色", description = "更新角色名称和描述")
    public Result<RoleResponse> update(
            @Parameter(description = "角色主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) return Result.notFound("角色不存在: " + id);
        role = Role.builder()
                .id(role.getId()).tenantId(role.getTenantId()).roleCode(role.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription() != null ? request.getDescription() : role.getDescription())
                .createdAt(role.getCreatedAt()).updatedAt(LocalDateTime.now()).deleted(role.getDeleted())
                .build();
        roleRepository.update(role);
        return Result.ok(RoleResponse.from(role));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "删除角色", description = "删除角色（逻辑删除）")
    public Result<Void> delete(
            @Parameter(description = "角色主键 ID") @PathVariable Long id) {
        if (roleRepository.findById(id).isEmpty()) return Result.notFound("角色不存在: " + id);
        roleRepository.delete(id);
        return Result.ok();
    }

    @PostMapping("/{id}/users")
    @SaCheckPermission("user:write")
    @Operation(summary = "为用户分配角色", description = "将角色分配给指定用户，权限变更后强制用户下线")
    public Result<Void> assignRoleToUser(
            @Parameter(description = "角色主键 ID") @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request) {
        log.info("[Role] 分配角色: roleId={}, userId={}", id, request.getUserId());
        if (roleRepository.findById(id).isEmpty()) return Result.notFound("角色不存在: " + id);
        roleRepository.assignRoleToUser(request.getUserId(), id);
        // 权限变更，强制下线
        StpUtil.kickout(request.getUserId());
        log.info("[Role] 权限变更，用户被强制下线: userId={}", request.getUserId());
        return Result.ok();
    }

    @GetMapping("/{id}/users")
    @SaCheckPermission("user:read")
    @Operation(summary = "查看角色下的用户", description = "获取拥有该角色的用户 ID 列表")
    public Result<List<String>> getUsersByRole(
            @Parameter(description = "角色主键 ID") @PathVariable Long id) {
        // Stub: return empty list
        return Result.ok(List.of());
    }

    @PostMapping("/{id}/permissions")
    @SaCheckPermission("user:write")
    @Operation(summary = "为角色分配权限", description = "将权限分配给指定角色")
    public Result<Void> assignPermissionToRole(
            @Parameter(description = "角色主键 ID") @PathVariable Long id,
            @Valid @RequestBody AssignPermissionRequest request) {
        log.info("[Role] 分配权限: roleId={}, permissionId={}", id, request.getPermissionId());
        if (roleRepository.findById(id).isEmpty()) return Result.notFound("角色不存在: " + id);
        roleRepository.assignPermissionToRole(id, request.getPermissionId());
        return Result.ok();
    }
}
