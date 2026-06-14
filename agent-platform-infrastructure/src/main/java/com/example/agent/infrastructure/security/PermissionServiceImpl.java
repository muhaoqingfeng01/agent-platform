package com.example.agent.infrastructure.security;

import com.example.agent.domain.security.PermissionService;
import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现 — 基于数据库三表 JOIN 查询权限码
 * <p>
 * 查询路径: t_user → t_user_role → t_role_permission → t_permission
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
        List<String> permissionCodes = permissions.stream()
                .map(Permission::getPermissionCode)
                .toList();

        log.debug("[PermissionService] 权限查询结果: userId={}, permissionCount={}, codes={}",
                userId, permissionCodes.size(), permissionCodes);
        return permissionCodes;
    }
}
