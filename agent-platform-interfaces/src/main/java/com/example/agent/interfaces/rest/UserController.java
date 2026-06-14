package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.agent.common.result.Result;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;
import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import com.example.agent.infrastructure.config.security.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 用户管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户 CRUD、状态管理、密码修改")
public class UserController {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    // ==================== DTOs ====================

    @Getter @Setter
    public static class CreateUserRequest {
        @NotBlank
        @Schema(description = "所属租户标识", example = "tenant_acme")
        private String tenantId;
        @NotBlank @Size(min = 3, max = 64)
        @Schema(description = "用户名", example = "zhangsan")
        private String username;
        @NotBlank @Size(min = 8, max = 128)
        @Schema(description = "密码（至少 8 位）", example = "P@ssw0rd!")
        private String password;
        @Schema(description = "邮箱", example = "zhangsan@acme.com")
        private String email;
        @Schema(description = "手机号", example = "13800138000")
        private String phone;
    }

    @Getter @Setter
    public static class UpdateUserRequest {
        @Schema(description = "邮箱", example = "newemail@acme.com")
        private String email;
        @Schema(description = "手机号", example = "13900139000")
        private String phone;
    }

    @Getter @Setter
    public static class UpdateUserStatusRequest {
        @NotBlank
        @Schema(description = "目标状态", example = "DISABLED", allowableValues = {"ACTIVE", "DISABLED"})
        private String status;
    }

    @Getter @Setter
    public static class ChangePasswordRequest {
        @NotBlank
        @Schema(description = "旧密码")
        private String oldPassword;
        @NotBlank @Size(min = 8, max = 128)
        @Schema(description = "新密码（至少 8 位）")
        private String newPassword;
    }

    @Getter @Setter
    public static class UserResponse {
        @Schema(description = "主键 ID") private Long id;
        @Schema(description = "所属租户") private String tenantId;
        @Schema(description = "用户唯一标识") private String userId;
        @Schema(description = "用户名") private String username;
        @Schema(description = "邮箱") private String email;
        @Schema(description = "手机号") private String phone;
        @Schema(description = "状态") private String status;
        @Schema(description = "创建时间") private LocalDateTime createdAt;

        public static UserResponse from(User u) {
            UserResponse r = new UserResponse();
            r.id = u.getId(); r.tenantId = u.getTenantId(); r.userId = u.getUserId();
            r.username = u.getUsername(); r.email = u.getEmail();
            r.phone = u.getPhone(); r.status = u.getStatus(); r.createdAt = u.getCreatedAt();
            return r;
        }
    }

    // ==================== API ====================

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "公开接口，新用户注册并分配默认 VIEWER 角色")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "注册成功"))
    public Result<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        log.info("[User] 注册: tenantId={}, username={}", request.getTenantId(), request.getUsername());

        // 1. 校验租户存在且状态为 ACTIVE
        Tenant tenant = tenantRepository.findByTenantId(request.getTenantId()).orElse(null);
        if (tenant == null) {
            return Result.notFound("租户不存在: " + request.getTenantId());
        }
        if (!tenant.isActive()) {
            return Result.forbidden("该租户已停用，无法注册新用户");
        }

        // 2. 密码复杂度校验
        passwordService.validateComplexity(request.getPassword());

        // 3. 用户名唯一性校验
        if (userRepository.findByTenantAndUsername(request.getTenantId(), request.getUsername()).isPresent()) {
            return Result.conflict("用户名已存在");
        }

        // 4. 创建用户
        User user = User.builder()
                .tenantId(request.getTenantId())
                .userId("user_" + UUID.randomUUID().toString().substring(0, 8))
                .username(request.getUsername())
                .passwordHash(passwordService.encode(request.getPassword()))
                .email(request.getEmail()).phone(request.getPhone())
                .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deleted(false)
                .build();
        userRepository.save(user);

        // 5. 分配默认 VIEWER 角色（单表查询：查角色表找 VIEWER，查用户角色关联表分配）
        List<Role> tenantRoles = roleRepository.findByTenant(request.getTenantId());
        tenantRoles.stream()
                .filter(r -> "VIEWER".equals(r.getRoleCode()))
                .findFirst()
                .ifPresent(viewerRole -> {
                    roleRepository.assignRoleToUser(user.getUserId(), viewerRole.getId());
                    log.info("[User] 已分配默认 VIEWER 角色: userId={}, roleId={}", user.getUserId(), viewerRole.getId());
                });

        log.info("[User] 注册成功: userId={}, tenantId={}", user.getUserId(), user.getTenantId());
        return Result.ok(UserResponse.from(user));
    }

    @GetMapping
    @SaCheckPermission("user:read")
    @Operation(summary = "用户列表", description = "分页获取指定租户下的用户列表，需 user:read 权限")
    public Result<List<UserResponse>> list(
            @Parameter(description = "租户标识", required = true) @RequestParam String tenantId,
            @Parameter(description = "页码（从 0 开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        log.debug("[User] 查询用户列表: tenantId={}, page={}, size={}", tenantId, page, size);
        List<UserResponse> list = userRepository.findByTenant(tenantId, page, size).stream()
                .map(UserResponse::from).toList();
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    @SaCheckPermission("user:read")
    @Operation(summary = "用户详情", description = "根据主键 ID 获取用户详情")
    public Result<UserResponse> get(
            @Parameter(description = "用户主键 ID") @PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> Result.ok(UserResponse.from(u)))
                .orElse(Result.notFound("用户不存在: " + id));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("user:write")
    @Operation(summary = "更新用户信息", description = "更新邮箱、手机号，需 user:write 权限")
    public Result<UserResponse> update(
            @Parameter(description = "用户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return Result.notFound("用户不存在: " + id);
        user = User.builder()
                .id(user.getId()).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(user.getPasswordHash())
                .email(request.getEmail() != null ? request.getEmail() : user.getEmail())
                .phone(request.getPhone() != null ? request.getPhone() : user.getPhone())
                .status(user.getStatus()).createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(user.getDeleted())
                .build();
        userRepository.update(user);
        return Result.ok(UserResponse.from(user));
    }

    @PatchMapping("/{id}/status")
    @SaCheckPermission("user:write")
    @Operation(summary = "启停用户", description = "修改用户状态（ACTIVE/DISABLED），需 user:write 权限")
    public Result<UserResponse> toggleStatus(
            @Parameter(description = "用户主键 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return Result.notFound("用户不存在: " + id);
        user = User.builder()
                .id(user.getId()).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(user.getPasswordHash())
                .email(user.getEmail()).phone(user.getPhone())
                .status(request.getStatus()).createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(user.getDeleted())
                .build();
        userRepository.update(user);
        log.info("[User] 用户状态变更: userId={}, status={}", user.getUserId(), request.getStatus());
        return Result.ok(UserResponse.from(user));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "当前用户修改自己的密码（需验证旧密码）")
    public Result<Void> changePassword(
            @Parameter(description = "用户主键 ID") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return Result.notFound("用户不存在: " + id);
        if (!passwordService.matches(request.getOldPassword(), user.getPasswordHash())) {
            return Result.fail(400, "旧密码错误");
        }
        // 新密码复杂度校验
        passwordService.validateComplexity(request.getNewPassword());
        user = User.builder()
                .id(user.getId()).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(passwordService.encode(request.getNewPassword()))
                .email(user.getEmail()).phone(user.getPhone())
                .status(user.getStatus()).createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(user.getDeleted())
                .build();
        userRepository.update(user);
        // 强制下线，需重新登录
        StpUtil.kickout(user.getUserId());
        log.info("[User] 密码已修改，用户被强制下线: userId={}", user.getUserId());
        return Result.ok();
    }
}
