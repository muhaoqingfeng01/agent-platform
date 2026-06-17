package com.example.agent.domain.evaluation.repository;

import com.example.agent.domain.evaluation.entity.EvaluationDataset;
import com.example.agent.domain.evaluation.entity.EvaluationDatasetItem;

import java.util.List;

/**
 * 评测数据集仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface EvaluationDatasetRepository {

    void save(EvaluationDataset dataset);

    void updateById(EvaluationDataset dataset);

    EvaluationDataset findByDatasetId(String datasetId);

    List<EvaluationDataset> findByTenant(String tenantId, int page, int size);

    long countByTenant(String tenantId);

    void softDelete(String datasetId);

    // Dataset Items
    void saveItem(EvaluationDatasetItem item);

    List<EvaluationDatasetItem> findItemsByDatasetId(String datasetId);

    void deleteItem(Long itemId);

    long countItemsByDatasetId(String datasetId);
}
