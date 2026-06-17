package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.LongTermMemoryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 长期记忆 MyBatis Mapper — 单表查询，禁止 JOIN.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface LongTermMemoryMapper {

    int upsert(LongTermMemoryPO po);

    int insert(LongTermMemoryPO po);

    int deleteById(@Param("id") Long id);

    int deleteExpired();

    int deleteBySource(@Param("source") String source);

    List<LongTermMemoryPO> selectByUserId(@Param("tenantId") Long tenantId,
                                           @Param("userId") String userId);

    List<LongTermMemoryPO> selectByUserIdAndType(@Param("tenantId") Long tenantId,
                                                  @Param("userId") String userId,
                                                  @Param("memoryType") String memoryType);
}
