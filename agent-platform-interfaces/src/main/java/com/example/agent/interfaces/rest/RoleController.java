package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.application.role.RoleApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.role.RoleAssignPermissionCommand;
import com.example.agent.application.role.RoleAssignToUserCommand;
import com.example.agent.application.role.RoleCreateCommand;
import com.example.agent.application.role.RoleUpdateCommand;
import com.example.agent.application.role.RoleResponse;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.interfaces.dto.request.role.RoleGetRequest;
import com.example.agent.interfaces.dto.request.role.RoleUpdateRequest;
import com.example.agent.interfaces.dto.request.role.RoleAssignUserRequest;
import com.example.agent.interfaces.dto.request.role.RoleAssignPermissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller — 纯粹 HTTP 适配层.
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

    @PostMapping("/create")
    @SaCheckPermission("user:write")
    @Operation(summary = "创建角色")
    public Result<RoleResponse> create(@Valid @RequestBody RoleCreateCommand request) {
        return Result.ok(roleService.createRole(request));
    }

    @PostMapping("/list")
    @SaCheckPermission("user:read")
    @Operation(summary = "角色列表")
    public Result<List<RoleResponse>> list() {
        Long tenantId = TenantContext.getCurrentTenantId();
        return Result.ok(roleService.listRoles(tenantId));
    }

    @PostMapping("/update")
    @SaCheckPermission("user:write")
    @Operation(summary = "更新角色")
    public Result<RoleResponse> update(@Valid @RequestBody RoleUpdateRequest request) {
        RoleUpdateCommand updateReq = new RoleUpdateCommand();
        updateReq.setRoleName(request.getRoleName());
        updateReq.setDescription(request.getDescription());
        return Result.ok(roleService.updateRole(request.getId(), updateReq));
    }

    @PostMapping("/delete")
    @SaCheckPermission("user:write")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@Valid @RequestBody RoleGetRequest request) {
        roleService.deleteRole(request.getId());
        return Result.ok();
    }

    @PostMapping("/assign-user")
    @SaCheckPermission("user:write")
    @Operation(summary = "为用户分配角色", description = "将角色分配给指定用户，权限变更后强制用户下线")
    public Result<Void> assignRoleToUser(@Valid @RequestBody RoleAssignUserRequest request) {
        RoleAssignToUserCommand assignReq = new RoleAssignToUserCommand();
        assignReq.setUserId(request.getUserId());
        roleService.assignRoleToUser(request.getRoleId(), assignReq);
        StpUtil.kickout(request.getUserId());
        return Result.ok();
    }

    @PostMapping("/users")
    @SaCheckPermission("user:read")
    @Operation(summary = "查看角色下的用户")
    public Result<List<String>> getUsersByRole(@Valid @RequestBody RoleGetRequest request) {
        return Result.ok(roleService.getUsersByRole(request.getId()));
    }

    @PostMapping("/assign-permission")
    @SaCheckPermission("user:write")
    @Operation(summary = "为角色分配权限")
    public Result<Void> assignPermissionToRole(@Valid @RequestBody RoleAssignPermissionRequest request) {
        RoleAssignPermissionCommand assignReq = new RoleAssignPermissionCommand();
        assignReq.setPermissionId(request.getPermissionId());
        roleService.assignPermissionToRole(request.getRoleId(), assignReq);
        return Result.ok();
    }
}
