package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.permission.PermissionApplicationService;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.application.permission.CreatePermissionRequest;
import com.example.agent.application.permission.BatchImportPermissionRequest;
import com.example.agent.application.permission.PermissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理 Controller — 纯粹 HTTP 适配层，业务逻辑委托给 {@link PermissionApplicationService}.
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

    /**
     * 分页查询权限列表
     */
    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "分页查询权限列表")
    public Result<PageResponse<PermissionResponse>> list(
            @Parameter(description = "页码，0-based") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        return Result.ok(permissionService.listPermissionsPaginated(page, size));
    }

    @PostMapping
    @SaCheckPermission("user:write")
    @Operation(summary = "创建单个权限")
    public Result<PermissionResponse> create(@Valid @RequestBody CreatePermissionRequest request) {
        return Result.ok(permissionService.createPermission(request));
    }

    /**
     * 级联删除权限 — 同时删除 t_role_permission 关联记录
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "级联删除权限",
            description = "事务内依次删除 t_role_permission 关联 → 逻辑删除 t_permission")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deletePermissionCascade(id);
        return Result.ok();
    }

    /**
     * 批量导入权限
     */
    @PostMapping("/import")
    @SaCheckPermission("user:write")
    @Operation(summary = "批量导入权限",
            description = "JSON 数组上传；事务保证原子性，全部成功或全部回滚")
    public Result<List<PermissionResponse>> batchImport(
            @Valid @RequestBody BatchImportPermissionRequest request) {
        return Result.ok(permissionService.batchImport(request));
    }
}
