package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.SensitiveWordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 敏感词 MyBatis Mapper — 对应 t_sensitive_word 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface SensitiveWordMapper {

    int insert(SensitiveWordPO po);

    int update(SensitiveWordPO po);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    SensitiveWordPO selectById(@Param("id") Long id);

    List<SensitiveWordPO> selectByTenant(@Param("tenantId") Long tenantId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    long countByTenant(@Param("tenantId") Long tenantId);

    /** 查询租户专属 + 全局的启用规则 */
    List<SensitiveWordPO> selectActiveByTenantAndGlobal(@Param("tenantId") Long tenantId);

    List<SensitiveWordPO> selectActiveByCategory(@Param("tenantId") Long tenantId,
                                                   @Param("category") String category);

    List<SensitiveWordPO> selectActiveGlobal();

    int deleteById(@Param("id") Long id);
}
