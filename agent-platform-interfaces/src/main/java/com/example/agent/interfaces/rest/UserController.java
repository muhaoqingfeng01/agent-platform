package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.application.user.UserApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.user.ChangePasswordRequest;
import com.example.agent.application.user.CreateUserRequest;
import com.example.agent.application.user.UpdateUserRequest;
import com.example.agent.application.user.UpdateUserStatusRequest;
import com.example.agent.application.user.UserResponse;
import com.example.agent.infrastructure.context.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller — 纯粹 HTTP 适配层，业务逻辑委托给 {@link UserApplicationService}.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户 CRUD、状态管理、密码修改")
public class UserController {

    private final UserApplicationService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "公开接口，新用户注册并分配默认 VIEWER 角色")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "注册成功"))
    public Result<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return Result.ok(userService.register(request));
    }

    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "用户列表")
    public Result<List<UserResponse>> list(
            @Parameter(description = "页码（从 0 开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return Result.ok(userService.listUsers(tenantId, page, size));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("user:read")
    @Operation(summary = "用户详情")
    public Result<UserResponse> get(
            @Parameter(description = "用户主键 ID") @PathVariable Long id) {
        return Result.ok(userService.getUser(id));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "更新用户信息")
    public Result<UserResponse> update(
            @Parameter(description = "用户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return Result.ok(userService.updateUser(id, request));
    }

    @PutMapping("/{id}/status")
    @SaCheckPermission("user:write")
    @Operation(summary = "启停用户")
    public Result<UserResponse> toggleStatus(
            @Parameter(description = "用户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return Result.ok(userService.toggleStatus(id, request));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "当前用户修改自己的密码（需验证旧密码）")
    public Result<Void> changePassword(
            @Parameter(description = "用户主键 ID") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        // 密码变更后强制下线，需重新登录
        StpUtil.kickout(userService.getUser(id).getUserId());
        return Result.ok();
    }
}
