package com.example.agent.domain.tenant.service;

import com.example.agent.domain.tenant.Role;
import com.example.agent.domain.tenant.RoleRepository;
import com.example.agent.domain.tenant.User;

import java.util.List;

/**
 * 用户领域服务 — 封装用户聚合根的业务规则.
 *
 * <p><b>聚合设计:</b>
 * <pre>
 * User (聚合根)
 *  ├── userId (业务标识)
 *  ├── tenantId (归属租户)
 *  └── → Role (通过 UserRole 关联表)
 *       └── → Permission (通过 RolePermission 关联表)
 * </pre>
 *
 * <p><b>核心规则:</b>
 * <ol>
 *   <li>新用户注册必须分配默认 VIEWER 角色</li>
 *   <li>用户名在租户内唯一</li>
 *   <li>权限变更 (角色分配/回收) 须强制用户下线</li>
 *   <li>密码须通过复杂度校验 (至少 8 位，含大小写+数字+特殊字符中的 3 类)</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public class UserDomainService {

    private final RoleRepository roleRepository;

    public UserDomainService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * 为新注册用户分配默认 VIEWER 角色.
     * <p>
     * 这是用户聚合创建时的核心不变式：每个用户至少拥有一个角色.
     *
     * @param user 新创建的用户
     */
    public void assignDefaultRole(User user) {
        List<Role> tenantRoles = roleRepository.findByTenant(user.getTenantId());
        tenantRoles.stream()
                .filter(r -> "VIEWER".equals(r.getRoleCode()))
                .findFirst()
                .ifPresent(viewerRole -> {
                    roleRepository.assignRoleToUser(user.getUserId(), viewerRole.getId());
                });
    }

    /**
     * 获取用户的有效角色列表.
     */
    public List<Role> getUserRoles(String userId) {
        return roleRepository.findByUserId(userId);
    }
}
