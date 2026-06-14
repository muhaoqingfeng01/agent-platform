package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓储接口（领域层）
 */
public interface PermissionRepository {

    Optional<Permission> findById(Long id);

    List<Permission> findAll();

    List<Permission> findByRoleId(Long roleId);

    /**
     * 通过用户 ID 查询权限（三表 JOIN: user_role → role_permission → permission）
     */
    List<Permission> findByUserId(String userId);

    void save(Permission permission);

    void delete(Long id);
}
