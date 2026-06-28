package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.permission.PermissionApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.application.permission.PermissionCreateCommand;
import com.example.agent.application.permission.PermissionBatchImportCommand;
import com.example.agent.application.permission.PermissionResponse;
import com.example.agent.interfaces.dto.request.permission.PermissionDeleteRequest;
import com.example.agent.interfaces.dto.request.permission.PermissionListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理 Controller — 纯粹 HTTP 适配层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限定义的分页查询、创建、删除、批量导入")
public class PermissionController {

    private final PermissionApplicationService permissionService;

    @PostMapping("/list")
    @SaCheckPermission("user:read")
    @Operation(summary = "分页查询权限列表")
    public Result<PageResponse<PermissionResponse>> list(@RequestBody PermissionListRequest request) {
        return Result.ok(permissionService.listPermissionsPaginated(request.getPage(), request.getSize()));
    }

    @PostMapping("/create")
    @SaCheckPermission("user:write")
    @Operation(summary = "创建单个权限")
    public Result<PermissionResponse> create(@Valid @RequestBody PermissionCreateCommand request) {
        return Result.ok(permissionService.createPermission(request));
    }

    @PostMapping("/delete")
    @SaCheckPermission("user:write")
    @Operation(summary = "级联删除权限",
            description = "事务内依次删除 t_role_permission 关联 → 逻辑删除 t_permission")
    public Result<Void> delete(@Valid @RequestBody PermissionDeleteRequest request) {
        permissionService.deletePermissionCascade(request.getId());
        return Result.ok();
    }

    @PostMapping("/import")
    @SaCheckPermission("user:write")
    @Operation(summary = "批量导入权限",
            description = "JSON 数组上传；事务保证原子性，全部成功或全部回滚")
    public Result<List<PermissionResponse>> batchImport(
            @Valid @RequestBody PermissionBatchImportCommand request) {
        return Result.ok(permissionService.batchImport(request));
    }
}
