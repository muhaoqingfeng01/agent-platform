package com.example.agent.application.user;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantRepository;
import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import com.example.agent.domain.tenant.valueobject.UserStatusEnums;
import com.example.agent.infrastructure.config.security.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户应用服务 — 编排用户 CRUD、注册、密码修改等业务逻辑.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    @Transactional
    public UserResponse register(UserCreateCommand request) {
        log.info("[User] 注册: tenantId={}, username={}", request.getTenantId(), request.getUsername());

        // 1. 校验租户存在且状态为 ACTIVE
        Tenant tenant = tenantRepository.findByTenantId(request.getTenantId())
                .orElseThrow(() -> new BusinessException(404, "租户不存在: " + request.getTenantId()));
        if (!tenant.isActive()) {
            throw new BusinessException(403, "该租户已停用，无法注册新用户");
        }

        // 2. 密码复杂度校验
        passwordService.validateComplexity(request.getPassword());

        // 3. 用户名唯一性校验
        if (userRepository.findByTenantAndUsername(request.getTenantId(), request.getUsername()).isPresent()) {
            throw new BusinessException(409, "用户名已存在");
        }

        // 4. 创建用户
        User user = User.builder()
                .tenantId(request.getTenantId())
                .userId(IdGenerator.generate("user"))
                .username(request.getUsername())
                .passwordHash(passwordService.encode(request.getPassword()))
                .email(request.getEmail()).phone(request.getPhone())
                .status(UserStatusEnums.ACTIVE).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deleted(false)
                .build();
        userRepository.save(user);

        // 5. 分配默认 VIEWER 角色
        assignDefaultViewerRole(user);

        log.info("[User] 注册成功: userId={}, tenantId={}", user.getUserId(), user.getTenantId());
        return UserResponse.from(user);
    }

    private void assignDefaultViewerRole(User user) {
        List<Role> tenantRoles = roleRepository.findByTenant(user.getTenantId());
        tenantRoles.stream()
                .filter(r -> "VIEWER".equals(r.getRoleCode()))
                .findFirst()
                .ifPresent(viewerRole -> {
                    roleRepository.assignRoleToUser(user.getUserId(), viewerRole.getId());
                    log.info("[User] 已分配默认 VIEWER 角色: userId={}, roleId={}", user.getUserId(), viewerRole.getId());
                });
    }

    public List<UserResponse> listUsers(Long tenantId, int page, int size) {
        log.debug("[User] 列表: tenantId={}, page={}, size={}", tenantId, page, size);
        return userRepository.findByTenant(tenantId, page, size).stream()
                .map(UserResponse::from).toList();
    }

    public UserResponse getUser(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new BusinessException(404, "用户不存在: " + id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateCommand request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在: " + id));
        user = User.builder()
                .id(user.getId()).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(user.getPasswordHash())
                .email(request.getEmail() != null ? request.getEmail() : user.getEmail())
                .phone(request.getPhone() != null ? request.getPhone() : user.getPhone())
                .status(user.getStatus()).createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(user.getDeleted())
                .build();
        userRepository.update(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse toggleStatus(Long id, UserUpdateStatusCommand request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在: " + id));
        user = User.builder()
                .id(user.getId()).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(user.getPasswordHash())
                .email(user.getEmail()).phone(user.getPhone())
                .status(UserStatusEnums.fromCode(request.getStatus())).createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(user.getDeleted())
                .build();
        userRepository.update(user);
        log.info("[User] 状态变更: userId={}, status={}", user.getUserId(), request.getStatus());
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(Long id, UserChangePasswordCommand request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在: " + id));
        if (!passwordService.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "旧密码错误");
        }
        passwordService.validateComplexity(request.getNewPassword());
        user = User.builder()
                .id(user.getId()).tenantId(user.getTenantId()).userId(user.getUserId())
                .username(user.getUsername()).passwordHash(passwordService.encode(request.getNewPassword()))
                .email(user.getEmail()).phone(user.getPhone())
                .status(user.getStatus()).createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(user.getDeleted())
                .build();
        userRepository.update(user);
        log.info("[User] 密码已修改: userId={}", user.getUserId());
    }
}
