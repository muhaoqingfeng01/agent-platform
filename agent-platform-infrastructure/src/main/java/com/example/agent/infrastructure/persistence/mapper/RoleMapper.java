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

    List<RolePO> findByTenant(@Param("tenantId") String tenantId);

    List<RolePO> findByUserId(@Param("userId") String userId);

    int insert(RolePO role);

    int update(RolePO role);

    int deleteById(@Param("id") Long id);

    int insertUserRole(@Param("userId") String userId, @Param("roleId") Long roleId);

    int deleteUserRole(@Param("userId") String userId, @Param("roleId") Long roleId);

    List<Long> findRoleIdsByUserId(@Param("userId") String userId);

    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    int deleteRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
