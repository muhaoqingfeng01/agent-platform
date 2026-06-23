package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import com.example.agent.domain.knowledge.valueobject.IndexType;
import com.example.agent.domain.knowledge.valueobject.KnowledgeBaseStatus;
import com.example.agent.domain.knowledge.valueobject.SearchStrategy;
import com.example.agent.infrastructure.persistence.mapper.KnowledgeBaseMapper;
import com.example.agent.infrastructure.persistence.po.KnowledgeBasePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 知识库仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class KnowledgeBaseRepositoryImpl implements KnowledgeBaseRepository {

    private final KnowledgeBaseMapper mapper;

    @Override
    public void save(KnowledgeBase kb) { mapper.insert(toPO(kb)); }

    @Override
    public void update(KnowledgeBase kb) { mapper.update(toPO(kb)); }

    @Override
    public Optional<KnowledgeBase> findByKnowledgeId(String knowledgeId) {
        return mapper.selectByKnowledgeId(knowledgeId).map(this::toDomain);
    }

    @Override
    public List<KnowledgeBase> findByTenant(Long tenantId, int page, int size) {
        int offset = page * size;
        return mapper.selectByTenant(tenantId, offset, size).stream().map(this::toDomain).toList();
    }

    @Override
    public long countByTenant(Long tenantId) { return mapper.countByTenant(tenantId); }

    @Override
    public void updateDocumentCount(String knowledgeId, int documentCount) {
        mapper.updateDocumentCount(knowledgeId, documentCount);
    }

    @Override
    public void incrementDocumentCount(String knowledgeId) {
        mapper.incrementDocumentCount(knowledgeId);
    }

    @Override
    public void decrementDocumentCount(String knowledgeId) {
        mapper.decrementDocumentCount(knowledgeId);
    }

    @Override
    public void updateChunkStrategy(String knowledgeId, ChunkStrategy strategy, String configJson) {
        mapper.updateChunkStrategy(knowledgeId, strategy.getCode(), configJson);
    }

    @Override
    public void updateIndexConfig(String knowledgeId, IndexType indexType, String indexParamsJson) {
        mapper.updateIndexConfig(knowledgeId, indexType.getCode(), indexParamsJson);
    }

    @Override
    public void updateSearchConfig(String knowledgeId, SearchStrategy strategy,
                                   String searchParamsJson, String multiStageParamsJson,
                                   String monitoringParamsJson) {
        mapper.updateSearchConfig(knowledgeId, strategy != null ? strategy.getCode() : null,
                searchParamsJson, multiStageParamsJson, monitoringParamsJson);
    }

    @Override
    public void updateStatus(String knowledgeId, String status) {
        mapper.updateStatus(knowledgeId, status);
    }

    @Override
    public Set<String> findEnabledKnowledgeIds(Long tenantId) {
        return mapper.findEnabledKnowledgeIds(tenantId);
    }

    @Override
    public void updateKnowledgeBaseStatus(String knowledgeId, KnowledgeBaseStatus status) {
        mapper.updateStatus(knowledgeId, status.getCode());
    }

    @Override
    public List<KnowledgeBase> findByTenantAndStatus(Long tenantId, KnowledgeBaseStatus status) {
        return mapper.selectByTenantAndStatus(tenantId, status.getCode()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public void softDelete(String knowledgeId) { mapper.softDelete(knowledgeId); }

    // ==================== 映射方法 ====================

    private KnowledgeBase toDomain(KnowledgeBasePO po) {
        return KnowledgeBase.builder()
                .knowledgeId(po.getKnowledgeId())
                .tenantId(po.getTenantId())
                .name(po.getName())
                .description(po.getDescription())
                .embeddingModel(po.getEmbeddingModel())
                .chunkSize(po.getChunkSize() != null ? po.getChunkSize() : 512)
                .chunkOverlap(po.getChunkOverlap() != null ? po.getChunkOverlap() : 50)
                .documentCount(po.getDocumentCount() != null ? po.getDocumentCount() : 0)
                .status(KnowledgeBaseStatus.fromCode(po.getStatus()))
                .defaultChunkStrategy(po.getDefaultChunkStrategy())
                .chunkConfigJson(po.getChunkConfigJson())
                .indexType(po.getIndexType() != null ? IndexType.fromCode(po.getIndexType()) : IndexType.IVF_FLAT)
                .indexParamsJson(po.getIndexParamsJson())
                .searchStrategy(po.getSearchStrategy())
                .searchParamsJson(po.getSearchParamsJson())
                .multiStageParamsJson(po.getMultiStageParamsJson())
                .monitoringParamsJson(po.getMonitoringParamsJson())
                .createdBy(po.getCreatedBy())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private KnowledgeBasePO toPO(KnowledgeBase kb) {
        return KnowledgeBasePO.builder()
                .tenantId(kb.getTenantId())
                .knowledgeId(kb.getKnowledgeId())
                .name(kb.getName())
                .description(kb.getDescription())
                .embeddingModel(kb.getEmbeddingModel())
                .chunkSize(kb.getChunkSize())
                .chunkOverlap(kb.getChunkOverlap())
                .documentCount(kb.getDocumentCount())
                .status(kb.getStatus().getCode())
                .defaultChunkStrategy(kb.getDefaultChunkStrategy())
                .chunkConfigJson(kb.getChunkConfigJson())
                .indexType(kb.getIndexType() != null ? kb.getIndexType().getCode() : null)
                .indexParamsJson(kb.getIndexParamsJson())
                .searchStrategy(kb.getSearchStrategy())
                .searchParamsJson(kb.getSearchParamsJson())
                .multiStageParamsJson(kb.getMultiStageParamsJson())
                .monitoringParamsJson(kb.getMonitoringParamsJson())
                .createdBy(kb.getCreatedBy())
                .createdAt(kb.getCreatedAt())
                .updatedAt(kb.getUpdatedAt())
                .build();
    }
}
