package com.example.agent.domain.evaluation.repository;

import com.example.agent.domain.evaluation.entity.EvaluationRun;

import java.util.List;

/**
 * 评测执行记录仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface EvaluationRunRepository {

    void save(EvaluationRun run);

    void updateById(EvaluationRun run);

    EvaluationRun findByEvaluationId(String evaluationId);

    List<EvaluationRun> findByTenant(Long tenantId, int page, int size);

    List<EvaluationRun> findByDatasetId(String datasetId, int page, int size);
}
