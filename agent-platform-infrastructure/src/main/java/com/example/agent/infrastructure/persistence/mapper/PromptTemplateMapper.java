package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.PromptTemplatePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 提示词模板 MyBatis Mapper — 单表查询.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface PromptTemplateMapper {

    int insert(PromptTemplatePO po);

    Optional<PromptTemplatePO> selectByPromptId(@Param("promptId") String promptId);

    List<PromptTemplatePO> selectByTenantId(@Param("tenantId") String tenantId,
                                             @Param("offset") int offset,
                                             @Param("size") int size);

    long countByTenantId(@Param("tenantId") String tenantId);

    List<PromptTemplatePO> selectByTenantIdAndStatus(@Param("tenantId") String tenantId,
                                                       @Param("status") String status,
                                                       @Param("offset") int offset,
                                                       @Param("size") int size);

    Optional<PromptTemplatePO> selectByTenantIdAndName(@Param("tenantId") String tenantId,
                                                         @Param("name") String name);

    int updateContent(@Param("promptId") String promptId,
                      @Param("name") String name,
                      @Param("description") String description,
                      @Param("templateText") String templateText,
                      @Param("variablesJson") String variablesJson);

    int publish(@Param("promptId") String promptId,
                @Param("version") int version,
                @Param("templateText") String templateText,
                @Param("variablesJson") String variablesJson);

    int updateStatus(@Param("promptId") String promptId,
                     @Param("status") String status);

    int softDelete(@Param("promptId") String promptId);
}
