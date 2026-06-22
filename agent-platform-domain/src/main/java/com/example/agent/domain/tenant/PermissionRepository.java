package com.example.agent.domain.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓储接口（领域层）
 */
public interface PermissionRepository {

    Optional<Permission> findById(Long id);

    List<Permission> findAll();

    /** 分页查询 */
    List<Permission> findAllPaginated(int page, int size);

    /** 统计总数 */
    long count();

    List<Permission> findByRoleId(Long roleId);

    /**
     * 通过用户 ID 查询权限（三表 JOIN: user_role → role_permission → permission）
     */
    List<Permission> findByUserId(String userId);

    void save(Permission permission);

    /** 批量保存 */
    void saveAll(List<Permission> permissions);

    /** 级联删除：先删关联再删权限 */
    void deleteCascade(Long id);

    void delete(Long id);
}
