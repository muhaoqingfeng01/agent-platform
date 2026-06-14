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

    List<PermissionPO> findByRoleId(@Param("roleId") Long roleId);

    List<PermissionPO> findByUserId(@Param("userId") String userId);

    int insert(PermissionPO permission);

    int deleteById(@Param("id") Long id);
}
