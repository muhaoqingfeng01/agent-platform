package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import com.example.agent.infrastructure.persistence.mapper.DocumentMapper;
import com.example.agent.infrastructure.persistence.po.DocumentPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 文档仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentMapper mapper;

    @Override
    public int save(Document doc) {
        DocumentPO po = toPO(doc);
        int rows = mapper.insert(po);
        if (rows <= 0) {
            log.error("[DocumentRepository] INSERT 返回 {} 行: documentId={}, filename={}",
                    rows, doc.getDocumentId(), doc.getFilename());
        }
        return rows;
    }

    @Override
    public void update(Document doc) { mapper.update(toPO(doc)); }

    @Override
    public Optional<Document> findByDocumentId(String documentId) {
        return mapper.selectByDocumentId(documentId).map(this::toDomain);
    }

    @Override
    public List<Document> findByKnowledgeId(String knowledgeId, int page, int size) {
        int offset = page * size;
        return mapper.selectByKnowledgeId(knowledgeId, offset, size).stream().map(this::toDomain).toList();
    }

    @Override
    public long countByKnowledgeId(String knowledgeId) {
        return mapper.countByKnowledgeId(knowledgeId);
    }

    @Override
    public List<Document> findByStatus(DocumentStatus status) {
        return mapper.selectByStatus(status.getCode()).stream().map(this::toDomain).toList();
    }

    @Override
    public void updateStatus(String documentId, DocumentStatus status) {
        mapper.updateStatus(documentId, status.getCode());
    }

    @Override
    public void updateChunkCount(String documentId, int chunkCount) {
        mapper.updateChunkCount(documentId, chunkCount);
    }

    @Override
    public void updateErrorMessage(String documentId, String errorMessage) {
        mapper.updateErrorMessage(documentId, errorMessage);
    }

    @Override
    public void softDelete(String documentId) { mapper.softDelete(documentId); }

    @Override
    public void softDeleteByKnowledgeId(String knowledgeId) { mapper.softDeleteByKnowledgeId(knowledgeId); }

    @Override
    public long countByKnowledgeIdAndStatus(String knowledgeId, DocumentStatus status) {
        return mapper.countByKnowledgeIdAndStatus(knowledgeId, status.getCode());
    }

    @Override
    public List<Document> findByDocumentIds(Set<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) return List.of();
        return mapper.selectByDocumentIds(documentIds).stream().map(this::toDomain).toList();
    }

    // ==================== 映射方法 ====================

    private Document toDomain(DocumentPO po) {
        return Document.builder()
                .documentId(po.getDocumentId())
                .tenantId(po.getTenantId())
                .knowledgeId(po.getKnowledgeId())
                .filename(po.getFilename())
                .fileType(po.getFileType())
                .fileSize(po.getFileSize() != null ? po.getFileSize() : 0L)
                .minioPath(po.getMinioPath())
                .contentHash(po.getContentHash())
                .chunkCount(po.getChunkCount() != null ? po.getChunkCount() : 0)
                .status(po.getStatus() != null ? DocumentStatus.fromCode(po.getStatus()) : null)
                .errorMessage(po.getErrorMessage())
                .uploadedBy(po.getUploadedBy())
                .uploadedAt(po.getUploadedAt())
                .chunkStrategy(po.getChunkStrategy())
                .chunkConfigJson(po.getChunkConfigJson())
                .searchStrategyOverride(po.getSearchStrategyOverride())
                .searchParamsOverrideJson(po.getSearchParamsOverrideJson())
                .multiStageOverrideJson(po.getMultiStageOverrideJson())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private DocumentPO toPO(Document doc) {
        return DocumentPO.builder()
                .tenantId(doc.getTenantId())
                .knowledgeId(doc.getKnowledgeId())
                .documentId(doc.getDocumentId())
                .filename(doc.getFilename())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .minioPath(doc.getMinioPath())
                .contentHash(doc.getContentHash())
                .chunkCount(doc.getChunkCount())
                .status(doc.getStatus() != null ? doc.getStatus().getCode() : null)
                .errorMessage(doc.getErrorMessage())
                .uploadedBy(doc.getUploadedBy())
                .uploadedAt(doc.getUploadedAt())
                .chunkStrategy(doc.getChunkStrategy())
                .chunkConfigJson(doc.getChunkConfigJson())
                .searchStrategyOverride(doc.getSearchStrategyOverride())
                .searchParamsOverrideJson(doc.getSearchParamsOverrideJson())
                .multiStageOverrideJson(doc.getMultiStageOverrideJson())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
