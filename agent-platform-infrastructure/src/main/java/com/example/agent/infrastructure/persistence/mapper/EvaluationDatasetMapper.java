package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.EvaluationDatasetItemPO;
import com.example.agent.infrastructure.persistence.po.EvaluationDatasetPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationDatasetMapper {

    int insert(EvaluationDatasetPO po);

    int updateById(EvaluationDatasetPO po);

    int softDelete(@Param("datasetId") String datasetId);

    EvaluationDatasetPO selectByDatasetId(@Param("datasetId") String datasetId);

    List<EvaluationDatasetPO> selectByTenant(@Param("tenantId") Long tenantId,
                                              @Param("offset") int offset,
                                              @Param("size") int size);

    long countByTenant(@Param("tenantId") Long tenantId);

    // Items
    int insertItem(EvaluationDatasetItemPO po);

    List<EvaluationDatasetItemPO> selectItemsByDatasetId(@Param("datasetId") String datasetId);

    int deleteItem(@Param("id") Long id);

    long countItemsByDatasetId(@Param("datasetId") String datasetId);
}
