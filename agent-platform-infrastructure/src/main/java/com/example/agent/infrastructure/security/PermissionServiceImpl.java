package com.example.agent.infrastructure.security;

import com.example.agent.domain.security.PermissionService;
import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限服务实现 — Redis 缓存 + 数据库三表 JOIN，支持通配符展开.
 * <p>
 * 查询路径: t_user → t_user_role → t_role_permission → t_permission
 *
 * @author Agent Platform Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final AuthzCacheStore authzCacheStore;

    @Override
    public List<String> getPermissionCodes(String userId) {
        return authzCacheStore.getPermissions(userId)
                .orElseGet(() -> {
                    List<String> codes = loadAndExpandFromDb(userId);
                    authzCacheStore.putPermissions(userId, codes);
                    log.debug("[PermissionService] 缓存未命中，已回源并写入: userId={}, count={}",
                            userId, codes.size());
                    return codes;
                });
    }

    @Override
    public List<String> refreshPermissionCodes(String userId) {
        List<String> codes = loadAndExpandFromDb(userId);
        authzCacheStore.putPermissions(userId, codes);
        log.debug("[PermissionService] 已刷新权限缓存: userId={}, count={}", userId, codes.size());
        return codes;
    }

    private List<String> loadAndExpandFromDb(String userId) {
        log.debug("[PermissionService] 查询权限 DB: userId={}", userId);

        List<Permission> permissions = permissionRepository.findByUserId(userId);
        List<String> permissionCodes = new ArrayList<>(
                permissions.stream()
                        .map(Permission::getPermissionCode)
                        .toList()
        );

        // 全局通配符 * — 展开为系统中所有已定义的权限码
        if (permissionCodes.contains("*")) {
            List<Permission> allPermissions = permissionRepository.findAll();
            List<String> allCodes = allPermissions.stream()
                    .map(Permission::getPermissionCode)
                    .filter(code -> !"*".equals(code))
                    .toList();
            log.debug("[PermissionService] 用户 {} 拥有全局通配符 *，展开为 {} 个权限", userId, allCodes.size());
            return allCodes;
        }

        // 资源级通配符 resource:*
        List<String> expandedCodes = new ArrayList<>(permissionCodes);
        boolean hasResourceWildcard = false;

        for (String code : permissionCodes) {
            if (code.endsWith(":*")) {
                hasResourceWildcard = true;
                String resourcePrefix = code.substring(0, code.length() - 2);
                List<Permission> allPermissions = permissionRepository.findAll();
                List<String> matched = allPermissions.stream()
                        .map(Permission::getPermissionCode)
                        .filter(c -> c.startsWith(resourcePrefix + ":"))
                        .filter(c -> !c.endsWith(":*"))
                        .toList();
                expandedCodes.addAll(matched);
                log.debug("[PermissionService] 资源通配符 {} 展开为: {}", code, matched);
            }
        }

        if (hasResourceWildcard) {
            Set<String> uniqueCodes = new HashSet<>(expandedCodes);
            uniqueCodes.removeIf(c -> c.endsWith(":*"));
            log.debug("[PermissionService] 用户 {} 通配符展开完成: 原始={}, 展开后={}",
                    userId, permissionCodes, uniqueCodes);
            return new ArrayList<>(uniqueCodes);
        }

        return permissionCodes;
    }
}
