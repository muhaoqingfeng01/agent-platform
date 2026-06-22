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
 * 权限服务实现 — 基于数据库三表 JOIN 查询权限码
 * <p>
 * 查询路径: t_user → t_user_role → t_role_permission → t_permission
 * <p>
 * 支持通配符:
 * <ul>
 *   <li>{@code *} — 全局通配符，拥有所有权限</li>
 *   <li>{@code resource:*} — 资源级通配符，拥有该资源下所有操作权限（如 {@code knowledge:*} 匹配 knowledge:read / knowledge:write / ...）</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<String> getPermissionCodes(String userId) {
        log.debug("[PermissionService] 查询权限: userId={}", userId);

        List<Permission> permissions = permissionRepository.findByUserId(userId);
        List<String> permissionCodes = new ArrayList<>(
                permissions.stream()
                        .map(Permission::getPermissionCode)
                        .toList()
        );

        log.debug("[PermissionService] 原始权限查询结果: userId={}, permissionCount={}, codes={}",
                userId, permissionCodes.size(), permissionCodes);

        // ---- 通配符展开 ----

        // 全局通配符 * — 展开为系统中所有已定义的权限码
        if (permissionCodes.contains("*")) {
            List<Permission> allPermissions = permissionRepository.findAll();
            List<String> allCodes = allPermissions.stream()
                    .map(Permission::getPermissionCode)
                    .filter(code -> !"*".equals(code)) // 排除 * 本身，Sa-Token 只做精确匹配
                    .toList();
            log.info("[PermissionService] 用户 {} 拥有全局通配符 *，展开为 {} 个权限", userId, allCodes.size());
            return allCodes;
        }

        // 资源级通配符 resource:* — 展开为该资源的所有 action 权限
        List<String> expandedCodes = new ArrayList<>(permissionCodes);
        boolean hasResourceWildcard = false;

        for (String code : permissionCodes) {
            if (code.endsWith(":*")) {
                hasResourceWildcard = true;
                String resourcePrefix = code.substring(0, code.length() - 2); // 去掉 ":*"
                List<Permission> allPermissions = permissionRepository.findAll();
                List<String> matched = allPermissions.stream()
                        .map(Permission::getPermissionCode)
                        .filter(c -> c.startsWith(resourcePrefix + ":"))
                        .filter(c -> !c.endsWith(":*")) // 避免递归展开
                        .toList();
                expandedCodes.addAll(matched);
                log.debug("[PermissionService] 资源通配符 {} 展开为: {}", code, matched);
            }
        }

        if (hasResourceWildcard) {
            // 去重
            Set<String> uniqueCodes = new HashSet<>(expandedCodes);
            // 移除所有 :* 通配符本身（Sa-Token 不做通配符匹配）
            uniqueCodes.removeIf(c -> c.endsWith(":*"));
            log.info("[PermissionService] 用户 {} 通配符展开完成: 原始={}, 展开后={}",
                    userId, permissionCodes, uniqueCodes);
            return new ArrayList<>(uniqueCodes);
        }

        return permissionCodes;
    }
}
