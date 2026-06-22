package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 权限 MyBatis Mapper
 */
@Mapper
public interface PermissionMapper {

    Optional<PermissionPO> findById(@Param("id") Long id);

    List<PermissionPO> findAll();

    /** 分页查询 */
    List<PermissionPO> findAllPaginated(@Param("offset") int offset, @Param("limit") int limit);

    /** 统计总数 */
    long count();

    List<PermissionPO> findByRoleId(@Param("roleId") Long roleId);

    List<PermissionPO> findByUserId(@Param("userId") String userId);

    int insert(PermissionPO permission);

    /** 批量插入 */
    int batchInsert(@Param("permissions") List<PermissionPO> permissions);

    /** 逻辑删除 */
    int deleteById(@Param("id") Long id);

    /** 级联删除：删除角色-权限关联 */
    int deleteRolePermissionByPermissionId(@Param("permissionId") Long permissionId);
}
