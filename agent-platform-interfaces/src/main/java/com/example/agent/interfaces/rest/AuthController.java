package com.example.agent.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.common.result.Result;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.interfaces.dto.LoginRequest;
import com.example.agent.interfaces.dto.LoginResponse;
import com.example.agent.interfaces.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证 Controller — 登录、登出、获取当前用户信息
 * <p>
 * 使用 Sa-Token 替代 JWT + Spring Security：
 * <ul>
 *   <li>登录：校验密码 → StpUtil.login(userId) → 返回 Token</li>
 *   <li>登出：StpUtil.logout() → Token 从 Redis 删除</li>
 *   <li>会话信息：StpUtil.getSession() 获取登录时写入的上下文数据</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、Token 管理")
public class AuthController {

    private final UserService userService;

    // ==================== API ====================

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验用户名密码，返回 Sa-Token 令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功，返回 Token"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[Auth] 用户登录请求: username={}, tenantId={}",
                request.getUsername(), request.getTenantId());

        // 1. 校验账号密码
        UserView user = userService.authenticate(
                request.getTenantId(), request.getUsername(), request.getPassword());
        if (user == null) {
            log.warn("[Auth] 登录失败（用户名或密码错误）: username={}", request.getUsername());
            return Result.fail(401, "用户名或密码错误");
        }
        if (!user.isActive()) {
            log.warn("[Auth] 登录失败（账户已停用）: userId={}", user.getUserId());
            return Result.fail(403, "账户已停用，请联系管理员");
        }

        // 2. Sa-Token 登录（一行代码完成 Token 生成 + Redis 存储）
        StpUtil.login(user.getUserId());

        // 3. 将用户上下文写入 Sa-Token Session
        StpUtil.getSession().set("tenantId", user.getTenantId());
        StpUtil.getSession().set("username", user.getUsername());

        // 4. 构造响应
        LoginResponse response = new LoginResponse(StpUtil.getTokenValue());
        log.info("[Auth] 登录成功: userId={}, tenantId={}", user.getUserId(), user.getTenantId());

        return Result.ok("登录成功", response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "从 Redis 中删除当前 Token")
    public Result<Void> logout() {
        String userId = null;
        try {
            userId = (String) StpUtil.getLoginId();
            log.info("[Auth] 用户登出: userId={}", userId);
        } catch (Exception e) {
            log.debug("[Auth] 登出时无法获取 loginId（可能已过期）");
        }
        StpUtil.logout();
        log.info("[Auth] 登出完成: userId={}", userId);
        return Result.ok();
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "从 Sa-Token Session 返回登录时写入的用户上下文，需登录")
    public Result<UserInfo> currentUser() {
        String userId = (String) StpUtil.getLoginId();
        String username = StpUtil.getSession().getString("username");
        String tenantId = StpUtil.getSession().getString("tenantId");
        log.debug("[Auth] 查询当前用户: userId={}, tenantId={}", userId, tenantId);
        return Result.ok(new UserInfo(userId, username, tenantId));
    }
}
