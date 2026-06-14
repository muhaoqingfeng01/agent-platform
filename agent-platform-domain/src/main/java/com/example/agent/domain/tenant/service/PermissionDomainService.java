package com.example.agent.domain.tenant.service;

import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;

import java.util.List;

/**
 * 权限领域服务 — 封装权限定义与查询规则.
 *
 * <p><b>权限模型 (RBAC):</b>
 * <pre>
 * Permission
 *  ├── permissionCode (全局唯一标识, e.g. "user:write")
 *  ├── resource        (资源路径, e.g. "user", "tenant")
 *  └── action          (操作类型: READ | WRITE | DELETE | ADMIN | PUBLISH)
 * </pre>
 *
 * <p>权限通过 Role → Permission 关联表分配给用户，查询路径为:
 * <pre>
 * user → t_user_role → role → t_role_permission → permission
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public class PermissionDomainService {

    private final PermissionRepository permissionRepository;

    public PermissionDomainService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * 获取用户的所有有效权限 (三表 JOIN).
     */
    public List<Permission> getUserPermissions(String userId) {
        return permissionRepository.findByUserId(userId);
    }
}
