package com.example.agent.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.application.security.AuthProviderFactory;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.helper.ResultRespHelper;
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

import java.util.ArrayList;
import java.util.List;
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
        return ResultRespHelper.responseInvoke("AuthController.login", request, (req) -> {
            UserView user;
            if (req.getProvider() != null && !req.getProvider().isBlank()
                    && !"LOCAL".equalsIgnoreCase(req.getProvider())) {
                user = authProviderFactory.authenticate(
                    req.getProvider(), req.getUsername(), req.getPassword());
            } else {
                user = userService.authenticate(
                    req.getTenantId(), req.getUsername(), req.getPassword());
            }
            if (user == null) {
                throw new BusinessException(401, "用户名或密码错误");
            }
            if (!user.isActive()) {
                throw new BusinessException(403, "账户已停用，请联系管理员");
            }

            StpUtil.login(user.getUserId());
            StpUtil.getSession().set("tenantId", user.getTenantId());
            StpUtil.getSession().set("username", user.getUsername());

            String refreshToken = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set(
                    REFRESH_KEY_PREFIX + user.getUserId(),
                    refreshToken,
                    REFRESH_TTL_DAYS, TimeUnit.DAYS);

            return buildLoginResponse(
                    StpUtil.getTokenValue(),
                    refreshToken,
                    user.getUserId(),
                    user.getUsername(),
                    user.getTenantId());
        });
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 RefreshToken 换取新的 AccessToken，旧 RefreshToken 立即失效")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "刷新成功，返回新 Token"),
            @ApiResponse(responseCode = "401", description = "RefreshToken 无效或已过期")
    })
    public Result<LoginResponse> refresh(@Valid @RequestBody AuthRefreshTokenRequest request) {
        return ResultRespHelper.responseInvoke("AuthController.refresh", request, (req) -> {
            String storedToken = stringRedisTemplate.opsForValue()
                    .get(REFRESH_KEY_PREFIX + req.getUserId());
            if (storedToken == null) {
                throw new BusinessException(401, "RefreshToken 已过期，请重新登录");
            }
            if (!storedToken.equals(req.getRefreshToken())) {
                throw new BusinessException(401, "RefreshToken 无效");
            }

            stringRedisTemplate.delete(REFRESH_KEY_PREFIX + req.getUserId());
            StpUtil.login(req.getUserId());

            String newRefreshToken = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set(
                    REFRESH_KEY_PREFIX + req.getUserId(),
                    newRefreshToken,
                    REFRESH_TTL_DAYS, TimeUnit.DAYS);

            // 刷新会建立新 Session，username/tenantId 需前端用登录态或再调 /me 补齐
            return buildLoginResponse(
                    StpUtil.getTokenValue(),
                    newRefreshToken,
                    req.getUserId(),
                    null,
                    null);
        });
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "从 Redis 中删除当前 AccessToken 和 RefreshToken")
    public Result<Void> logout() {
        return ResultRespHelper.responseInvoke("AuthController.logout", null, (__) -> {
            String userId = null;
            try {
                userId = (String) StpUtil.getLoginId();
            } catch (Exception e) {
                log.debug("[Auth] 登出时无法获取 loginId（可能已过期）");
            }
            StpUtil.logout();
            if (userId != null) {
                stringRedisTemplate.delete(REFRESH_KEY_PREFIX + userId);
            }
            return null;
        });
    }

    @RequestMapping(value = "/me", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "获取当前用户信息", description = "从 Sa-Token Session 返回登录用户上下文与权限码，需登录")
    public Result<UserInfo> currentUser() {
        return ResultRespHelper.responseInvoke("AuthController.currentUser", null, (__) -> {
            String userId = StpUtil.getLoginIdAsString();
            String username = StpUtil.getSession().getString("username");
            Long tenantId = StpUtil.getSession().getLong("tenantId");
            return new UserInfo(userId, username, tenantId, safeRoleList(), safePermissionList());
        });
    }

    private LoginResponse buildLoginResponse(String token, String refreshToken,
                                             String userId, String username, Long tenantId) {
        LoginResponse response = new LoginResponse(token, refreshToken);
        response.setUserId(userId);
        response.setUsername(username);
        response.setTenantId(tenantId);
        response.setRoles(safeRoleList());
        response.setPermissions(safePermissionList());
        return response;
    }

    private static List<String> safeRoleList() {
        try {
            return new ArrayList<>(StpUtil.getRoleList());
        } catch (Exception e) {
            return List.of();
        }
    }

    private static List<String> safePermissionList() {
        try {
            return new ArrayList<>(StpUtil.getPermissionList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
