package com.example.agent.infrastructure.security;

import com.example.agent.domain.security.RoleService;
import com.example.agent.domain.security.UserService;
import com.example.agent.domain.security.UserView;
import com.example.agent.domain.tenant.User;
import com.example.agent.domain.tenant.UserRepository;
import com.example.agent.infrastructure.config.security.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现 — 基于 MyBatis 数据库查询
 * <p>
 * 认证流程使用真实数据库查询，角色查询委托给 {@link RoleService}。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final RoleService roleService;

    /**
     * 校验用户名密码
     * <p>
     * 如果提供 tenantId，则在指定租户内查找；
     * 否则尝试跨租户查找。
     * <p>
     * TODO: 管理员 mock 登录流程待移除。
     * 当前 admin 用户的跨租户登录依赖 V1.2.0 种子数据中的 {@code user-admin-001} 硬编码 fallback。
     * 后续 P0 多租户模块完成后，应改为：未指定 tenantId 时要求用户输入 tenantId，
     * 或通过 username 在所有租户中匹配（需在 t_user 的 username 列加索引并移除硬编码）。
     */
    @Override
    public UserView authenticate(String tenantId, String username, String password) {
        log.info("[UserService] 认证请求: tenantId={}, username={}", tenantId, username);

        User user;
        if (tenantId != null && !tenantId.isBlank()) {
            // 指定租户查找 — 真实数据库查询
            user = userRepository.findByTenantAndUsername(tenantId, username).orElse(null);
        } else {
            // TODO: 管理员 mock 登录流程 — 待 P0 完成后替换为通用的跨租户查找
            // 当前为支持 admin 用户不指定 tenantId 登录，硬编码 user-admin-001
            log.warn("[UserService] TODO 使用管理员 mock 登录（硬编码 user-admin-001）");
            user = userRepository.findByUserId("user-admin-001").orElse(null);
            if (user == null) {
                // fallback: 尝试在默认租户 1000001 中查找
                user = userRepository.findByTenantAndUsername("1000001", username).orElse(null);
            }
        }

        if (user == null) {
            log.warn("[UserService] 认证失败（用户不存在）: username={}", username);
            return null;
        }

        if (!passwordService.matches(password, user.getPasswordHash())) {
            log.warn("[UserService] 认证失败（密码错误）: username={}", username);
            return null;
        }

        if (!user.isActive()) {
            log.warn("[UserService] 认证失败（账户已停用）: userId={}", user.getUserId());
            return null;
        }

        log.info("[UserService] 认证成功: userId={}, tenantId={}", user.getUserId(), user.getTenantId());
        return user.toView();
    }

    /**
     * 获取用户角色编码列表 — 委托给 {@link RoleService} 进行真实数据库查询
     */
    @Override
    public List<String> getRoleCodes(String userId) {
        return roleService.getRoleCodes(userId);
    }
}
