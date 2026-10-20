package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.TenantModeQuotaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 租户模式配额 Mapper.
 */
@Mapper
public interface TenantModeQuotaMapper {

    Optional<TenantModeQuotaPO> findByTenantIdAndModeCode(@Param("tenantId") Long tenantId,
                                                          @Param("modeCode") String modeCode);

    List<TenantModeQuotaPO> findByTenantId(@Param("tenantId") Long tenantId);

    int insert(TenantModeQuotaPO po);

    int update(TenantModeQuotaPO po);
}
