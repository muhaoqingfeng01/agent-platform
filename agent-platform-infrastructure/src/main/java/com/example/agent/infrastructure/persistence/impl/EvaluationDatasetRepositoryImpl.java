package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.evaluation.entity.EvaluationDataset;
import com.example.agent.domain.evaluation.entity.EvaluationDatasetItem;
import com.example.agent.domain.evaluation.repository.EvaluationDatasetRepository;
import com.example.agent.infrastructure.persistence.mapper.EvaluationDatasetMapper;
import com.example.agent.infrastructure.persistence.po.EvaluationDatasetItemPO;
import com.example.agent.infrastructure.persistence.po.EvaluationDatasetPO;
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
public class EvaluationDatasetRepositoryImpl implements EvaluationDatasetRepository {

    private final EvaluationDatasetMapper mapper;

    @Override
    public void save(EvaluationDataset dataset) {
        mapper.insert(toPO(dataset));
    }

    @Override
    public void updateById(EvaluationDataset dataset) {
        mapper.updateById(toPO(dataset));
    }

    @Override
    public EvaluationDataset findByDatasetId(String datasetId) {
        EvaluationDatasetPO po = mapper.selectByDatasetId(datasetId);
        return po != null ? toDomain(po) : null;
    }

    @Override
    public List<EvaluationDataset> findByTenant(Long tenantId, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenant(tenantId, offset, size).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByTenant(Long tenantId) {
        return mapper.countByTenant(tenantId);
    }

    @Override
    public void softDelete(String datasetId) {
        mapper.softDelete(datasetId);
    }

    @Override
    public void saveItem(EvaluationDatasetItem item) {
        mapper.insertItem(toItemPO(item));
    }

    @Override
    public List<EvaluationDatasetItem> findItemsByDatasetId(String datasetId) {
        return mapper.selectItemsByDatasetId(datasetId).stream()
                .map(this::toItemDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        mapper.deleteItem(itemId);
    }

    @Override
    public long countItemsByDatasetId(String datasetId) {
        return mapper.countItemsByDatasetId(datasetId);
    }

    private EvaluationDataset toDomain(EvaluationDatasetPO po) {
        return EvaluationDataset.builder()
                .id(po.getId()).tenantId(po.getTenantId()).datasetId(po.getDatasetId())
                .name(po.getName()).description(po.getDescription()).itemCount(po.getItemCount())
                .source(po.getSource()).deleted(po.getDeleted() == 1)
                .createdAt(po.getCreatedAt()).updatedAt(po.getUpdatedAt()).build();
    }

    private EvaluationDatasetPO toPO(EvaluationDataset ds) {
        return EvaluationDatasetPO.builder()
                .id(ds.getId()).tenantId(ds.getTenantId()).datasetId(ds.getDatasetId())
                .name(ds.getName()).description(ds.getDescription()).itemCount(ds.getItemCount())
                .source(ds.getSource()).deleted(ds.getDeleted() != null && ds.getDeleted() ? 1 : 0)
                .createdAt(ds.getCreatedAt()).updatedAt(ds.getUpdatedAt()).build();
    }

    private EvaluationDatasetItem toItemDomain(EvaluationDatasetItemPO po) {
        return EvaluationDatasetItem.builder()
                .id(po.getId()).datasetId(po.getDatasetId()).question(po.getQuestion())
                .expectedAnswer(po.getExpectedAnswer()).retrievalContext(po.getRetrievalContext())
                .metadataJson(po.getMetadataJson()).createdAt(po.getCreatedAt()).build();
    }

    private EvaluationDatasetItemPO toItemPO(EvaluationDatasetItem item) {
        return EvaluationDatasetItemPO.builder()
                .id(item.getId()).datasetId(item.getDatasetId()).question(item.getQuestion())
                .expectedAnswer(item.getExpectedAnswer()).retrievalContext(item.getRetrievalContext())
                .metadataJson(item.getMetadataJson()).createdAt(item.getCreatedAt()).build();
    }
}
