package com.example.agent.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.application.security.AuthProviderFactory;
import com.example.agent.common.result.Result;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.interfaces.dto.request.auth.AuthLoginRequest;
import com.example.agent.interfaces.dto.request.auth.AuthRefreshTokenRequest;
import com.example.agent.interfaces.dto.LoginResponse;
import com.example.agent.interfaces.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证 Controller — 登录、登出、Token 刷新、获取当前用户信息
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、Token 刷新")
public class AuthController {

    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProviderFactory authProviderFactory;

    private static final String REFRESH_KEY_PREFIX = "refresh:";
    private static final long REFRESH_TTL_DAYS = 7;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验用户名密码，返回 Sa-Token 访问令牌和刷新令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功，返回 Token"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public Result<LoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        log.info("[Auth] 用户登录请求: username={}, tenantId={}, provider={}",
                request.getUsername(), request.getTenantId(), request.getProvider());

        UserView user;
        if (request.getProvider() != null && !request.getProvider().isBlank()
                && !"LOCAL".equalsIgnoreCase(request.getProvider())) {
            user = authProviderFactory.authenticate(
                request.getProvider(), request.getUsername(), request.getPassword());
        } else {
            user = userService.authenticate(
                request.getTenantId(), request.getUsername(), request.getPassword());
        }
        if (user == null) {
            log.warn("[Auth] 登录失败（用户名或密码错误）: username={}", request.getUsername());
            return Result.fail(401, "用户名或密码错误");
        }
        if (!user.isActive()) {
            log.warn("[Auth] 登录失败（账户已停用）: userId={}", user.getUserId());
            return Result.fail(403, "账户已停用，请联系管理员");
        }

        StpUtil.login(user.getUserId());
        StpUtil.getSession().set("tenantId", user.getTenantId());
        StpUtil.getSession().set("username", user.getUsername());

        String refreshToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(
                REFRESH_KEY_PREFIX + user.getUserId(),
                refreshToken,
                REFRESH_TTL_DAYS, TimeUnit.DAYS);

        LoginResponse response = new LoginResponse(StpUtil.getTokenValue(), refreshToken);
        log.info("[Auth] 登录成功: userId={}, tenantId={}", user.getUserId(), user.getTenantId());
        return Result.ok("登录成功", response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 RefreshToken 换取新的 AccessToken，旧 RefreshToken 立即失效")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "刷新成功，返回新 Token"),
            @ApiResponse(responseCode = "401", description = "RefreshToken 无效或已过期")
    })
    public Result<LoginResponse> refresh(@Valid @RequestBody AuthRefreshTokenRequest request) {
        log.info("[Auth] Token 刷新请求: userId={}", request.getUserId());

        String storedToken = stringRedisTemplate.opsForValue()
                .get(REFRESH_KEY_PREFIX + request.getUserId());
        if (storedToken == null) {
            log.warn("[Auth] RefreshToken 不存在或已过期: userId={}", request.getUserId());
            return Result.fail(401, "RefreshToken 已过期，请重新登录");
        }
        if (!storedToken.equals(request.getRefreshToken())) {
            log.warn("[Auth] RefreshToken 不匹配: userId={}", request.getUserId());
            return Result.fail(401, "RefreshToken 无效");
        }

        stringRedisTemplate.delete(REFRESH_KEY_PREFIX + request.getUserId());
        StpUtil.login(request.getUserId());

        String newRefreshToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(
                REFRESH_KEY_PREFIX + request.getUserId(),
                newRefreshToken,
                REFRESH_TTL_DAYS, TimeUnit.DAYS);

        LoginResponse response = new LoginResponse(StpUtil.getTokenValue(), newRefreshToken);
        log.info("[Auth] Token 刷新成功: userId={}", request.getUserId());
        return Result.ok("Token 刷新成功", response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "从 Redis 中删除当前 AccessToken 和 RefreshToken")
    public Result<Void> logout() {
        String userId = null;
        try {
            userId = (String) StpUtil.getLoginId();
            log.info("[Auth] 用户登出: userId={}", userId);
        } catch (Exception e) {
            log.debug("[Auth] 登出时无法获取 loginId（可能已过期）");
        }
        StpUtil.logout();
        if (userId != null) {
            stringRedisTemplate.delete(REFRESH_KEY_PREFIX + userId);
        }
        log.info("[Auth] 登出完成: userId={}", userId);
        return Result.ok();
    }

    @PostMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "从 Sa-Token Session 返回登录时写入的用户上下文，需登录")
    public Result<UserInfo> currentUser() {
        String userId = (String) StpUtil.getLoginId();
        String username = StpUtil.getSession().getString("username");
        Long tenantId = StpUtil.getSession().getLong("tenantId");
        log.debug("[Auth] 查询当前用户: userId={}, tenantId={}", userId, tenantId);
        return Result.ok(new UserInfo(userId, username, tenantId));
    }
}
