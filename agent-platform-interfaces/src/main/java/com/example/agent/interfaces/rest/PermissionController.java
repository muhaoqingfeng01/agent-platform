package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.permission.PermissionApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.permission.CreatePermissionRequest;
import com.example.agent.application.permission.PermissionResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "权限管理", description = "权限定义列表、创建权限")
public class PermissionController {

    private final PermissionApplicationService permissionService;

    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "权限列表")
    public Result<List<PermissionResponse>> list() {
        return Result.ok(permissionService.listPermissions());
    }

    @PostMapping
    @SaCheckPermission("user:write")
    @Operation(summary = "创建权限")
    public Result<PermissionResponse> create(@Valid @RequestBody CreatePermissionRequest request) {
        return Result.ok(permissionService.createPermission(request));
    }
}
