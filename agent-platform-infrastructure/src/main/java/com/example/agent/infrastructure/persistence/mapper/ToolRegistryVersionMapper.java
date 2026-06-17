package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.ToolRegistryVersionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 工具版本历史 MyBatis Mapper.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Mapper
public interface ToolRegistryVersionMapper {

    int insert(ToolRegistryVersionPO po);

    Optional<ToolRegistryVersionPO> selectByToolIdAndVersion(@Param("toolId") String toolId,
                                                              @Param("version") int version);

    List<ToolRegistryVersionPO> selectByToolId(@Param("toolId") String toolId);

    Optional<Integer> selectMaxVersionByToolId(@Param("toolId") String toolId);

    int countByToolId(@Param("toolId") String toolId);
}
