package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.EvaluationRunPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationRunMapper {

    int insert(EvaluationRunPO po);

    int updateById(EvaluationRunPO po);

    EvaluationRunPO selectByEvaluationId(@Param("evaluationId") String evaluationId);

    List<EvaluationRunPO> selectByTenant(@Param("tenantId") String tenantId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    List<EvaluationRunPO> selectByDatasetId(@Param("datasetId") String datasetId,
                                             @Param("offset") int offset,
                                             @Param("size") int size);
}
