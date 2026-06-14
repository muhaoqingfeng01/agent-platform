package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储接口（领域层）
 */
public interface RoleRepository {

    Optional<Role> findById(Long id);

    List<Role> findByTenant(String tenantId);

    List<Role> findByUserId(String userId);

    void save(Role role);

    void update(Role role);

    void delete(Long id);

    void assignRoleToUser(String userId, Long roleId);

    void removeRoleFromUser(String userId, Long roleId);

    List<Long> findRoleIdsByUserId(String userId);

    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);
}
