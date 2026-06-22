package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.evaluation.entity.EvaluationRun;
import com.example.agent.domain.evaluation.repository.EvaluationRunRepository;
import com.example.agent.infrastructure.persistence.mapper.EvaluationRunMapper;
import com.example.agent.infrastructure.persistence.po.EvaluationRunPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class EvaluationRunRepositoryImpl implements EvaluationRunRepository {

    private final EvaluationRunMapper mapper;

    @Override
    public void save(EvaluationRun run) {
        mapper.insert(toPO(run));
    }

    @Override
    public void updateById(EvaluationRun run) {
        mapper.updateById(toPO(run));
    }

    @Override
    public EvaluationRun findByEvaluationId(String evaluationId) {
        EvaluationRunPO po = mapper.selectByEvaluationId(evaluationId);
        return po != null ? toDomain(po) : null;
    }

    @Override
    public List<EvaluationRun> findByTenant(Long tenantId, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenant(tenantId, offset, size).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<EvaluationRun> findByDatasetId(String datasetId, int page, int size) {
        int offset = page * size;
        return mapper.selectByDatasetId(datasetId, offset, size).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    private EvaluationRun toDomain(EvaluationRunPO po) {
        return EvaluationRun.builder()
                .id(po.getId()).tenantId(po.getTenantId()).evaluationId(po.getEvaluationId())
                .agentId(po.getAgentId()).datasetId(po.getDatasetId()).status(po.getStatus())
                .overallScore(po.getOverallScore()).metricsJson(po.getMetricsJson())
                .createdAt(po.getCreatedAt()).finishedAt(po.getFinishedAt()).build();
    }

    private EvaluationRunPO toPO(EvaluationRun run) {
        return EvaluationRunPO.builder()
                .id(run.getId()).tenantId(run.getTenantId()).evaluationId(run.getEvaluationId())
                .agentId(run.getAgentId()).datasetId(run.getDatasetId()).status(run.getStatus())
                .overallScore(run.getOverallScore()).metricsJson(run.getMetricsJson())
                .createdAt(run.getCreatedAt()).finishedAt(run.getFinishedAt()).build();
    }
}
