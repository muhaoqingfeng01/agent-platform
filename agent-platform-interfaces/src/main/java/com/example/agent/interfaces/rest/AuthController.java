package com.example.agent.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.common.result.Result;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.interfaces.dto.LoginRequest;
import com.example.agent.interfaces.dto.LoginResponse;
import com.example.agent.interfaces.dto.RefreshTokenRequest;
import com.example.agent.interfaces.dto.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证 Controller — 登录、登出、Token 刷新、获取当前用户信息
 * <p>
 * 使用 Sa-Token 替代 JWT + Spring Security：
 * <ul>
 *   <li>登录：校验密码 → StpUtil.login(userId) → 返回 Token + RefreshToken</li>
 *   <li>登出：StpUtil.logout() → Token 从 Redis 删除</li>
 *   <li>刷新：验证 RefreshToken → 签发新 AccessToken + 新 RefreshToken</li>
 *   <li>会话信息：StpUtil.getSession() 获取登录时写入的上下文数据</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、Token 刷新")
public class AuthController {

    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String REFRESH_KEY_PREFIX = "refresh:";
    private static final long REFRESH_TTL_DAYS = 7;

    // ==================== API ====================

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验用户名密码，返回 Sa-Token 访问令牌和刷新令牌")
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

        // 4. 生成 RefreshToken，写入 Redis（7 天有效）
        String refreshToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(
                REFRESH_KEY_PREFIX + user.getUserId(),
                refreshToken,
                REFRESH_TTL_DAYS, TimeUnit.DAYS);

        // 5. 构造响应
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
    public Result<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("[Auth] Token 刷新请求: userId={}", request.getUserId());

        // 1. 从 Redis 校验 RefreshToken
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

        // 2. 旧 RefreshToken 立即删除（防止重放攻击）
        stringRedisTemplate.delete(REFRESH_KEY_PREFIX + request.getUserId());

        // 3. 重新登录（签发新 AccessToken）
        StpUtil.login(request.getUserId());

        // 4. 生成新 RefreshToken（Rotation 策略）
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
        // 同时删除 RefreshToken
        if (userId != null) {
            stringRedisTemplate.delete(REFRESH_KEY_PREFIX + userId);
        }
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
