package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.TenantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 租户 MyBatis Mapper
 */
@Mapper
public interface TenantMapper {

    Optional<TenantPO> findById(@Param("id") Long id);

    Optional<TenantPO> findByTenantId(@Param("tenantId") String tenantId);

    List<TenantPO> findAll(@Param("offset") int offset,
                           @Param("size") int size);

    long count();

    int insert(TenantPO tenant);

    int update(TenantPO tenant);

    int deleteById(@Param("id") Long id);
}
