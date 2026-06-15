package com.example.agent.application.knowledge;

import com.example.agent.application.knowledge.dto.*;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.repository.DocumentChunkRepository;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.service.KnowledgeBaseDomainService;
import com.example.agent.domain.knowledge.service.MilvusStoreClient;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import com.example.agent.domain.knowledge.valueobject.IndexType;
import com.example.agent.domain.knowledge.valueobject.KnowledgeBaseStatus;
import com.example.agent.domain.knowledge.valueobject.SearchStrategy;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识库应用服务 — CRUD + 配置管理 + 生命周期控制.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseApplicationService {

    private final KnowledgeBaseRepository kbRepository;
    private final KnowledgeBaseDomainService domainService;
    private final MilvusStoreClient milvusStore;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;

    @Transactional
    public KnowledgeBaseDTO create(String name, String description, String embeddingModel) {
        KnowledgeBase kb = KnowledgeBase.builder()
                .knowledgeId(IdGenerator.generate("kb"))
                .tenantId(TenantContext.getCurrentTenantId())
                .name(name)
                .description(description)
                .embeddingModel(embeddingModel != null ? embeddingModel : "text-embedding-3-small")
                .chunkSize(512)
                .chunkOverlap(50)
                .documentCount(0)
                .status(KnowledgeBaseStatus.ENABLED)
                .createdBy(TenantContext.getCurrentUserId())
                .defaultChunkStrategy(ChunkStrategy.PARAGRAPH_SLIDING_WINDOW.getCode())
                .indexType(IndexType.IVF_FLAT)
                .searchStrategy(SearchStrategy.BALANCED.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        domainService.validateNewKnowledgeBase(kb);
        kbRepository.save(kb);
        log.info("[KB] 创建知识库: kbId={}, name={}, createdBy={}",
                kb.getKnowledgeId(), name, kb.getCreatedBy());
        return KnowledgeBaseDTO.from(kb);
    }

    public KnowledgeBaseDTO getByKnowledgeId(String knowledgeId) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        return KnowledgeBaseDTO.from(kb);
    }

    public PageResponse<KnowledgeBaseDTO> list(int page, int size) {
        String tenantId = TenantContext.getCurrentTenantId();
        List<KnowledgeBase> list = kbRepository.findByTenant(tenantId, page, size);
        long total = kbRepository.countByTenant(tenantId);
        return PageResponse.of(list.stream().map(KnowledgeBaseDTO::from).toList(), total, page, size);
    }

    @Transactional
    public void update(String knowledgeId, String name, String description) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        KnowledgeBase updated = kb.toBuilder()
                .name(name != null ? name : kb.getName())
                .description(description != null ? description : kb.getDescription())
                .updatedAt(LocalDateTime.now())
                .build();
        kbRepository.update(updated);
    }

    @Transactional
    public void updateChunkStrategy(String knowledgeId, ChunkStrategy strategy, String configJson) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        kbRepository.updateChunkStrategy(knowledgeId, strategy, configJson);
    }

    @Transactional
    public void updateIndexConfig(String knowledgeId, IndexType indexType, String indexParamsJson) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        kbRepository.updateIndexConfig(knowledgeId, indexType, indexParamsJson);
    }

    @Transactional
    public void updateSearchConfig(String knowledgeId, SearchStrategy strategy,
                                   String searchParamsJson, String multiStageParamsJson,
                                   String monitoringParamsJson) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        kbRepository.updateSearchConfig(knowledgeId, strategy,
                searchParamsJson, multiStageParamsJson, monitoringParamsJson);
    }

    // ========== 生命周期管理 (V1.4.0) ==========

    /**
     * 启用知识库（DISABLED → ENABLED，已删除的不可启用）.
     */
    @Transactional
    public void enable(String knowledgeId) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        domainService.assertCanEnable(kb);
        kb.enable();
        kbRepository.updateKnowledgeBaseStatus(knowledgeId, KnowledgeBaseStatus.ENABLED);
        log.info("[KB] 启用知识库: kbId={}", knowledgeId);
    }

    /**
     * 弃用知识库（ENABLED → DISABLED，不可上传/检索，但数据保留）.
     */
    @Transactional
    public void disable(String knowledgeId) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        kb.disable();
        kbRepository.updateKnowledgeBaseStatus(knowledgeId, KnowledgeBaseStatus.DISABLED);
        log.info("[KB] 弃用知识库: kbId={}", knowledgeId);
    }

    /**
     * 级联删除知识库（DISABLED → DELETED），软删除文档+切片+向量.
     * <p>
     * 前置条件：知识库必须先禁用（DISABLED 状态），ENABLED 状态禁止直接删除.
     * 仅知识库创建者可执行此操作.
     */
    @Transactional
    public void deleteWithCascade(String knowledgeId) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        domainService.assertCreatorAccess(kb, TenantContext.getCurrentUserId());

        if (kb.isEnabled()) {
            throw new IllegalStateException("必须先弃用知识库再删除");
        }

        // 级联删除: 文档(软删) + 切片(硬删) + KB标记删除
        documentRepository.softDeleteByKnowledgeId(knowledgeId);
        chunkRepository.softDeleteByKnowledgeId(knowledgeId);
        kb.markDeleted();
        kbRepository.updateKnowledgeBaseStatus(knowledgeId, KnowledgeBaseStatus.DELETED);
        log.info("[KB] 级联删除知识库: kbId={}, operator={}",
                knowledgeId, TenantContext.getCurrentUserId());
    }

    /**
     * 获取知识库文档统计（按状态分组）.
     */
    public Map<String, Long> getStats(String knowledgeId) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());

        Map<String, Long> stats = new LinkedHashMap<>();
        for (DocumentStatus status : DocumentStatus.values()) {
            long count = documentRepository.countByKnowledgeIdAndStatus(knowledgeId, status);
            stats.put(status.name(), count);
        }
        stats.put("total", documentRepository.countByKnowledgeId(knowledgeId));
        return stats;
    }
}
