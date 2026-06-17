package com.example.agent.domain.tenant.service;

import com.example.agent.domain.tenant.Permission;
import com.example.agent.domain.tenant.PermissionRepository;
import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;

import java.util.List;

/**
 * 角色领域服务 — 封装角色-权限关联的业务规则.
 *
 * <p><b>关联模型:</b>
 * <pre>
 * Tenant 1──N Role N──N Permission
 *                │
 * User  N──N ────┘ (通过 t_user_role)
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public class RoleDomainService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleDomainService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * 获取角色拥有的权限列表.
     */
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    /**
     * 获取租户下指定编码的角色.
     */
    public Role getByCode(Long tenantId, String roleCode) {
        return roleRepository.findByTenant(tenantId).stream()
                .filter(r -> r.getRoleCode().equals(roleCode))
                .findFirst()
                .orElse(null);
    }
}
