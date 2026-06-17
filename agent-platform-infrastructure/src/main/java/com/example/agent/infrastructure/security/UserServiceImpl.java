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
     * 校验用户名密码.
     * <p>如果提供 tenantId，则在指定租户内查找；否则通过 username 跨租户查找.
     * <p>跨租户匹配时取第一条记录，生产环境建议要求用户必须提供 tenantId.
     */
    @Override
    public UserView authenticate(Long tenantId, String username, String password) {
        log.info("[UserService] 认证请求: tenantId={}, username={}", tenantId, username);

        User user;
        if (tenantId != null) {
            user = userRepository.findByTenantAndUsername(tenantId, username).orElse(null);
        } else {
            user = userRepository.findByUsername(username).orElse(null);
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
