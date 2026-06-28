package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.application.user.UserApplicationService;
import com.example.agent.common.result.Result;
import com.example.agent.application.user.UserCreateCommand;
import com.example.agent.application.user.UserResponse;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.interfaces.dto.request.user.UserListRequest;
import com.example.agent.interfaces.dto.request.user.UserGetRequest;
import com.example.agent.interfaces.dto.request.user.UserUpdateRequest;
import com.example.agent.interfaces.dto.request.user.UserToggleStatusRequest;
import com.example.agent.interfaces.dto.request.user.UserChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理 Controller — 纯粹 HTTP 适配层.
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
    public Result<UserResponse> register(@Valid @RequestBody UserCreateCommand request) {
        return Result.ok(userService.register(request));
    }

    @PostMapping("/list")
    @SaCheckPermission("user:read")
    @Operation(summary = "用户列表")
    public Result<java.util.List<UserResponse>> list(@RequestBody UserListRequest request) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return Result.ok(userService.listUsers(tenantId, request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("user:read")
    @Operation(summary = "用户详情")
    public Result<UserResponse> get(@Valid @RequestBody UserGetRequest request) {
        return Result.ok(userService.getUser(request.getId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("user:write")
    @Operation(summary = "更新用户信息")
    public Result<UserResponse> update(@Valid @RequestBody UserUpdateRequest request) {
        com.example.agent.application.user.UserUpdateCommand updateReq =
                new com.example.agent.application.user.UserUpdateCommand();
        updateReq.setId(request.getId());
        updateReq.setEmail(request.getEmail());
        updateReq.setPhone(request.getPhone());
        return Result.ok(userService.updateUser(request.getId(), updateReq));
    }

    @PostMapping("/toggle-status")
    @SaCheckPermission("user:write")
    @Operation(summary = "启停用户")
    public Result<UserResponse> toggleStatus(@Valid @RequestBody UserToggleStatusRequest request) {
        com.example.agent.application.user.UserUpdateStatusCommand statusReq =
                new com.example.agent.application.user.UserUpdateStatusCommand();
        statusReq.setStatus(request.getStatus());
        return Result.ok(userService.toggleStatus(request.getId(), statusReq));
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "当前用户修改自己的密码（需验证旧密码）")
    public Result<Void> changePassword(@Valid @RequestBody UserChangePasswordRequest request) {
        com.example.agent.application.user.UserChangePasswordCommand pwdReq =
                new com.example.agent.application.user.UserChangePasswordCommand();
        pwdReq.setOldPassword(request.getOldPassword());
        pwdReq.setNewPassword(request.getNewPassword());
        userService.changePassword(request.getId(), pwdReq);
        StpUtil.kickout(userService.getUser(request.getId()).getUserId());
        return Result.ok();
    }
}
