package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储接口（领域层）
 */
public interface RoleRepository {

    Optional<Role> findById(Long id);

    List<Role> findByTenant(Long tenantId);

    List<Role> findByUserId(String userId);

    void save(Role role);

    void update(Role role);

    void delete(Long id);

    void assignRoleToUser(String userId, Long roleId);

    void removeRoleFromUser(String userId, Long roleId);

    List<Long> findRoleIdsByUserId(String userId);

    /**
     * 查询拥有指定角色的所有用户 ID（单表查询 t_user_role）
     *
     * @param roleId 角色主键 ID
     * @return 用户唯一标识列表
     */
    List<String> findUserIdsByRoleId(Long roleId);

    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);
}
