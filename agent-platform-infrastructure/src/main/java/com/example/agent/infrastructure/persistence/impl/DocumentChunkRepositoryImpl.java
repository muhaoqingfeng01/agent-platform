package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.knowledge.entity.DocumentChunk;
import com.example.agent.domain.knowledge.repository.DocumentChunkRepository;
import com.example.agent.infrastructure.persistence.mapper.DocumentChunkMapper;
import com.example.agent.infrastructure.persistence.po.DocumentChunkPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文档切片仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class DocumentChunkRepositoryImpl implements DocumentChunkRepository {

    private final DocumentChunkMapper mapper;

    @Override
    public void save(DocumentChunk chunk) { mapper.insert(toPO(chunk)); }

    @Override
    public void batchSave(List<DocumentChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) return;
        List<DocumentChunkPO> poList = chunks.stream().map(this::toPO).toList();
        mapper.batchInsert(poList);
    }

    @Override
    public List<DocumentChunk> findByDocumentId(String documentId) {
        return mapper.selectByDocumentId(documentId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<DocumentChunk> findByDocumentId(String documentId, int offset, int limit) {
        return mapper.selectByDocumentIdPaged(documentId, offset, limit).stream().map(this::toDomain).toList();
    }

    @Override
    public List<DocumentChunk> findByIds(List<Long> ids) {
        return mapper.selectByIds(ids).stream().map(this::toDomain).toList();
    }

    @Override
    public int countByDocumentId(String documentId) {
        return mapper.countByDocumentId(documentId);
    }

    @Override
    public void deleteByDocumentId(String documentId) { mapper.deleteByDocumentId(documentId); }

    @Override
    public void deleteByKnowledgeId(String knowledgeId) { mapper.deleteByKnowledgeId(knowledgeId); }

    @Override
    public void softDeleteByDocumentId(String documentId) { mapper.softDeleteByDocumentId(documentId); }

    @Override
    public void softDeleteByKnowledgeId(String knowledgeId) { mapper.softDeleteByKnowledgeId(knowledgeId); }

    // ==================== 映射方法 ====================

    private DocumentChunk toDomain(DocumentChunkPO po) {
        return DocumentChunk.builder()
                .id(po.getId())
                .documentId(po.getDocumentId())
                .knowledgeId(po.getKnowledgeId())
                .chunkIndex(po.getChunkIndex() != null ? po.getChunkIndex() : 0)
                .content(po.getContent())
                .tokenCount(po.getTokenCount() != null ? po.getTokenCount() : 0)
                .contentHash(po.getContentHash())
                .milvusId(po.getMilvusId())
                .metadataJson(po.getMetadataJson())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private DocumentChunkPO toPO(DocumentChunk chunk) {
        return DocumentChunkPO.builder()
                .documentId(chunk.getDocumentId())
                .knowledgeId(chunk.getKnowledgeId())
                .chunkIndex(chunk.getChunkIndex())
                .content(chunk.getContent())
                .tokenCount(chunk.getTokenCount())
                .contentHash(chunk.getContentHash())
                .milvusId(chunk.getMilvusId())
                .metadataJson(chunk.getMetadataJson())
                .createdAt(chunk.getCreatedAt())
                .build();
    }
}
