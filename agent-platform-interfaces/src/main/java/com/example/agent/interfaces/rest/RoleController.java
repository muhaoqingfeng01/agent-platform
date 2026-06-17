package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.application.role.RoleApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.role.AssignPermissionToRoleRequest;
import com.example.agent.application.role.AssignRoleToUserRequest;
import com.example.agent.application.role.CreateRoleRequest;
import com.example.agent.application.role.UpdateRoleRequest;
import com.example.agent.application.role.RoleResponse;
import com.example.agent.infrastructure.context.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller — 纯粹 HTTP 适配层，业务逻辑委托给 {@link RoleApplicationService}.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色 CRUD、角色-用户分配、角色-权限分配")
public class RoleController {

    private final RoleApplicationService roleService;

    @PostMapping
    @SaCheckPermission("user:write")
    @Operation(summary = "创建角色")
    public Result<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return Result.ok(roleService.createRole(request));
    }

    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "角色列表")
    public Result<List<RoleResponse>> list() {
        Long tenantId = TenantContext.getCurrentTenantId();
        return Result.ok(roleService.listRoles(tenantId));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "更新角色")
    public Result<RoleResponse> update(
            @Parameter(description = "角色主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return Result.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "删除角色")
    public Result<Void> delete(
            @Parameter(description = "角色主键 ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @PostMapping("/{id}/users")
    @SaCheckPermission("user:write")
    @Operation(summary = "为用户分配角色", description = "将角色分配给指定用户，权限变更后强制用户下线")
    public Result<Void> assignRoleToUser(
            @Parameter(description = "角色主键 ID") @PathVariable Long id,
            @Valid @RequestBody AssignRoleToUserRequest request) {
        roleService.assignRoleToUser(id, request);
        StpUtil.kickout(request.getUserId());
        return Result.ok();
    }

    @GetMapping("/{id}/users")
    @SaCheckPermission("user:read")
    @Operation(summary = "查看角色下的用户")
    public Result<List<String>> getUsersByRole(
            @Parameter(description = "角色主键 ID") @PathVariable Long id) {
        return Result.ok(roleService.getUsersByRole(id));
    }

    @PostMapping("/{id}/permissions")
    @SaCheckPermission("user:write")
    @Operation(summary = "为角色分配权限")
    public Result<Void> assignPermissionToRole(
            @Parameter(description = "角色主键 ID") @PathVariable Long id,
            @Valid @RequestBody AssignPermissionToRoleRequest request) {
        roleService.assignPermissionToRole(id, request);
        return Result.ok();
    }
}
