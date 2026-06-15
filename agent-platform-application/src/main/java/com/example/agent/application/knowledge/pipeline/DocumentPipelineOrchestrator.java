package com.example.agent.application.knowledge.pipeline;

import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.entity.DocumentChunk;
import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.repository.DocumentChunkRepository;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.service.*;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import com.example.agent.application.knowledge.strategy.ChunkStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 文档处理管线编排器 — Template Method 模式.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPipelineOrchestrator {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final KnowledgeBaseRepository kbRepository;
    private final KnowledgeBaseDomainService kbDomainService;
    private final ChunkStrategyFactory strategyFactory;
    private final TextExtractor textExtractor;
    private final EmbeddingServiceClient embeddingClient;
    private final MilvusStoreClient milvusStore;

    @Async
    @Transactional
    public void processAsync(String documentId) {
        try {
            Document doc = documentRepository.findByDocumentId(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("文档不存在: " + documentId));

            updateStatus(documentId, DocumentStatus.PARSING);
            String text = textExtractor.extractText(doc);
            log.info("[Pipeline] 文档解析完成: docId={}, textLen={}", documentId, text.length());

            updateStatus(documentId, DocumentStatus.CHUNKING);
            List<ChunkStrategyService.ChunkResult> chunks = chunkDocument(doc, text);
            log.info("[Pipeline] 文档切分完成: docId={}, chunkCount={}", documentId, chunks.size());

            updateStatus(documentId, DocumentStatus.EMBEDDING);
            storeChunks(doc, chunks);
            log.info("[Pipeline] 向量化存储完成: docId={}, chunks={}", documentId, chunks.size());

            documentRepository.updateChunkCount(documentId, chunks.size());
            updateStatus(documentId, DocumentStatus.PARSED);
            kbRepository.incrementDocumentCount(doc.getKnowledgeId());
            log.info("[Pipeline] 文档处理完成: docId={}", documentId);

        } catch (Exception e) {
            log.error("[Pipeline] 文档处理失败: docId={}", documentId, e);
            documentRepository.updateStatus(documentId, DocumentStatus.FAILED);
            documentRepository.updateErrorMessage(documentId, e.getMessage());
        }
    }

    public List<ChunkStrategyService.ChunkResult> chunkDocument(Document doc, String text) {
        String strategyCode = resolveStrategy(doc);
        ChunkStrategyService strategy = strategyFactory.getStrategy(strategyCode);
        Map<String, Object> config = resolveChunkConfig(doc);
        log.info("[Pipeline] 使用切片策略: strategy={}, docId={}", strategyCode, doc.getDocumentId());
        return strategy.split(text, config);
    }

    private String resolveStrategy(Document doc) {
        if (doc.getChunkStrategy() != null && !doc.getChunkStrategy().isBlank()) {
            return doc.getChunkStrategy();
        }
        KnowledgeBase kb = kbRepository.findByKnowledgeId(doc.getKnowledgeId()).orElse(null);
        if (kb != null && kb.getDefaultChunkStrategy() != null && !kb.getDefaultChunkStrategy().isBlank()) {
            return kb.getDefaultChunkStrategy();
        }
        return kbDomainService.resolveFallbackStrategy(doc.getFileType()).getCode();
    }

    private Map<String, Object> resolveChunkConfig(Document doc) {
        Map<String, Object> config = new HashMap<>();
        config.putIfAbsent("chunk_size", 512);
        config.putIfAbsent("chunk_overlap", 50);
        return config;
    }

    private void storeChunks(Document doc, List<ChunkStrategyService.ChunkResult> chunks) {
        KnowledgeBase kb = kbRepository.findByKnowledgeId(doc.getKnowledgeId())
                .orElseThrow(() -> new IllegalArgumentException("知识库不存在: " + doc.getKnowledgeId()));
        String collectionName = "kb_" + kb.getTenantId();

        int embeddingDim = getEmbeddingDimension();
        milvusStore.ensureCollection(collectionName, embeddingDim,
                kb.getIndexType() != null ? kb.getIndexType().name() : "IVF_FLAT");

        List<DocumentChunk> chunkEntities = new ArrayList<>();
        List<MilvusStoreClient.VectorEntry> vectorEntries = new ArrayList<>();

        for (ChunkStrategyService.ChunkResult cr : chunks) {
            float[] vector = embeddingClient.embed(cr.content());
            long milvusId = Math.abs(UUID.randomUUID().getLeastSignificantBits());

            DocumentChunk entity = DocumentChunk.builder()
                    .documentId(doc.getDocumentId())
                    .knowledgeId(doc.getKnowledgeId())
                    .chunkIndex(cr.chunkIndex())
                    .content(cr.content())
                    .tokenCount(cr.tokenCount())
                    .contentHash(cr.contentHash())
                    .milvusId(String.valueOf(milvusId))
                    .metadataJson(toJson(cr.metadata()))
                    .createdAt(LocalDateTime.now())
                    .build();
            chunkEntities.add(entity);
            vectorEntries.add(new MilvusStoreClient.VectorEntry(
                    milvusId, vector, cr.content(), doc.getDocumentId(), doc.getKnowledgeId()));
        }

        chunkRepository.batchSave(chunkEntities);
        milvusStore.insert(collectionName, vectorEntries);
    }

    private int getEmbeddingDimension() {
        try {
            float[] testVector = embeddingClient.embed("test");
            return testVector.length;
        } catch (Exception e) {
            return 1536;
        }
    }

    private String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{");
        map.forEach((k, v) -> sb.append("\"").append(k).append("\":\"").append(v).append("\","));
        if (sb.charAt(sb.length() - 1) == ',') sb.setLength(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private void updateStatus(String documentId, DocumentStatus status) {
        documentRepository.updateStatus(documentId, status);
    }
}
