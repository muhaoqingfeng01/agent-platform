package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.IntentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 意图 MyBatis Mapper — 单表查询，禁止 JOIN.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface IntentMapper {

    int insert(IntentPO po);

    int update(IntentPO po);

    int updateStatus(@Param("intentId") String intentId, @Param("status") String status);

    int softDelete(@Param("intentId") String intentId);

    Optional<IntentPO> selectByIntentId(@Param("intentId") String intentId);

    List<IntentPO> selectActiveByTenant(@Param("tenantId") String tenantId);

    List<IntentPO> selectByTenant(@Param("tenantId") String tenantId,
                                   @Param("offset") int offset,
                                   @Param("size") int size);

    long countByTenant(@Param("tenantId") String tenantId);

    boolean existsByTenantAndCode(@Param("tenantId") String tenantId,
                                  @Param("intentCode") String intentCode);
}
