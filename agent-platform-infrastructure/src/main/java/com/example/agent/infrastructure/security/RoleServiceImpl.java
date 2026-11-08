package com.example.agent.infrastructure.security;

import com.example.agent.domain.security.RoleService;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现 — Redis 缓存 + 数据库查询 t_role + t_user_role.
 *
 * @author Agent Platform Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthzCacheStore authzCacheStore;

    @Override
    public List<String> getRoleCodes(String userId) {
        return authzCacheStore.getRoles(userId)
                .orElseGet(() -> {
                    List<String> codes = loadFromDb(userId);
                    authzCacheStore.putRoles(userId, codes);
                    log.debug("[RoleService] 缓存未命中，已回源并写入: userId={}, roles={}", userId, codes);
                    return codes;
                });
    }

    @Override
    public List<String> refreshRoleCodes(String userId) {
        List<String> codes = loadFromDb(userId);
        authzCacheStore.putRoles(userId, codes);
        log.debug("[RoleService] 已刷新角色缓存: userId={}, roles={}", userId, codes);
        return codes;
    }

    private List<String> loadFromDb(String userId) {
        log.debug("[RoleService] 查询角色 DB: userId={}", userId);
        List<Role> roles = roleRepository.findByUserId(userId);
        return roles.stream().map(Role::getRoleCode).toList();
    }
}
