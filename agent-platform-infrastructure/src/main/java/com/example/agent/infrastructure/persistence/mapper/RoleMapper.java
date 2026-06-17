package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 角色 MyBatis Mapper
 */
@Mapper
public interface RoleMapper {

    Optional<RolePO> findById(@Param("id") Long id);

    List<RolePO> findByTenant(@Param("tenantId") Long tenantId);

    List<RolePO> findByUserId(@Param("userId") String userId);

    int insert(RolePO role);

    int update(RolePO role);

    int deleteById(@Param("id") Long id);

    int insertUserRole(@Param("userId") String userId, @Param("roleId") Long roleId);

    int deleteUserRole(@Param("userId") String userId, @Param("roleId") Long roleId);

    List<Long> findRoleIdsByUserId(@Param("userId") String userId);

    /**
     * 单表查询 t_user_role：根据角色 ID 查询拥有该角色的所有用户 ID
     */
    List<String> findUserIdsByRoleId(@Param("roleId") Long roleId);

    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    int deleteRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
