package com.example.agent.application.evaluation;

import com.example.agent.application.evaluation.dto.*;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.evaluation.entity.EvaluationDataset;
import com.example.agent.domain.evaluation.entity.EvaluationDatasetItem;
import com.example.agent.domain.evaluation.repository.EvaluationDatasetRepository;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationDatasetService {

    private final EvaluationDatasetRepository datasetRepository;

    @Transactional
    public DatasetResponse create(CreateDatasetRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        String datasetId = IdGenerator.generate("ds");

        EvaluationDataset dataset = EvaluationDataset.builder()
                .tenantId(tenantId).datasetId(datasetId)
                .name(request.getName()).description(request.getDescription())
                .source(request.getSource() != null ? request.getSource() : "MANUAL")
                .itemCount(0).deleted(false).build();
        datasetRepository.save(dataset);

        log.info("[Evaluation] 数据集已创建: datasetId={}, name={}", datasetId, request.getName());
        return toResponse(dataset);
    }

    public DatasetResponse getByDatasetId(String datasetId) {
        EvaluationDataset ds = datasetRepository.findByDatasetId(datasetId);
        if (ds == null) throw new RuntimeException("数据集不存在: " + datasetId);
        return toResponse(ds);
    }

    public List<DatasetResponse> list(int page, int size) {
        String tenantId = TenantContext.getCurrentTenantId();
        return datasetRepository.findByTenant(tenantId, page, size).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(String datasetId) {
        datasetRepository.softDelete(datasetId);
        log.info("[Evaluation] 数据集已软删除: datasetId={}", datasetId);
    }

    // ==================== Items ====================

    @Transactional
    public ItemResponse addItem(String datasetId, AddItemRequest request) {
        EvaluationDatasetItem item = EvaluationDatasetItem.builder()
                .datasetId(datasetId).question(request.getQuestion())
                .expectedAnswer(request.getExpectedAnswer())
                .retrievalContext(request.getRetrievalContext())
                .metadataJson(request.getMetadataJson()).build();
        datasetRepository.saveItem(item);

        // 更新 item_count
        EvaluationDataset ds = datasetRepository.findByDatasetId(datasetId);
        if (ds != null) {
            ds = ds.toBuilder().itemCount((int) datasetRepository.countItemsByDatasetId(datasetId)).build();
            datasetRepository.updateById(ds);
        }

        return toItemResponse(item);
    }

    public List<ItemResponse> listItems(String datasetId) {
        return datasetRepository.findItemsByDatasetId(datasetId).stream()
                .map(this::toItemResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteItem(Long itemId) {
        datasetRepository.deleteItem(itemId);
    }

    // ==================== Mappers ====================

    private DatasetResponse toResponse(EvaluationDataset ds) {
        return DatasetResponse.builder()
                .datasetId(ds.getDatasetId()).name(ds.getName())
                .description(ds.getDescription()).itemCount(ds.getItemCount())
                .source(ds.getSource()).createdAt(ds.getCreatedAt()).build();
    }

    private ItemResponse toItemResponse(EvaluationDatasetItem item) {
        return ItemResponse.builder()
                .id(item.getId()).question(item.getQuestion())
                .expectedAnswer(item.getExpectedAnswer()).createdAt(item.getCreatedAt()).build();
    }
}
