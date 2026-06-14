package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.PromptTemplateVersionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 提示词版本历史 MyBatis Mapper — 单表查询.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface PromptTemplateVersionMapper {

    int insert(PromptTemplateVersionPO po);

    Optional<PromptTemplateVersionPO> selectByPromptIdAndVersion(@Param("promptId") String promptId,
                                                                   @Param("version") int version);

    List<PromptTemplateVersionPO> selectByPromptId(@Param("promptId") String promptId);

    int countByPromptId(@Param("promptId") String promptId);

    Optional<Integer> selectMaxVersionByPromptId(@Param("promptId") String promptId);
}
