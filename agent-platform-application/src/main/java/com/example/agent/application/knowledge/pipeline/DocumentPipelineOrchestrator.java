package com.example.agent.application.knowledge.pipeline;

import com.example.agent.common.exception.TaskTimeoutException;
import com.example.agent.common.lock.DistributeLockService;
import com.example.agent.common.lock.LockEnum;
import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.entity.DocumentChunk;
import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.repository.DocumentChunkRepository;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.service.*;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import com.example.agent.application.knowledge.strategy.ChunkStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private final PlatformTransactionManager transactionManager;
    private final DistributeLockService distributeLockService;

    @Async
    @Transactional
    public void processAsync(String documentId) {
        // ★ 分布式互斥锁（DistributeLockService）：与 triggerParse / deleteDocument / deprecateDocument 互斥
        String lockKey = LockEnum.getLockKey(LockEnum.DOCUMENT_MUTEX, documentId).getLockName();
        try {
            distributeLockService.executeWithLock(lockKey, 0, null, TimeUnit.SECONDS, () -> {
                doProcess(documentId);
                return null;
            });
        } catch (RuntimeException e) {
            log.info("[Pipeline] 文档正在执行其他操作（解析/弃用/删除），跳过: docId={}", documentId);
        }
    }

    /**
     * 带截止时间的文档处理（由 TaskCenter 调用）.
     * <p>
     * 在关键步骤边界检查 deadline，超时抛出 TaskTimeoutException 协作式退出.
     *
     * @param documentId 文档 ID
     * @param deadlineMs 截止时间（System.currentTimeMillis() 毫秒值）
     * @throws TaskTimeoutException 超过截止时间
     */
    public void doProcessWithDeadline(String documentId, long deadlineMs) {
        doProcess(documentId, deadlineMs);
    }

    /**
     * 实际文档处理逻辑（在分布式锁保护下执行）.
     *
     * @param deadlineMs 截止时间毫秒值，null 表示无超时
     */
    private void doProcess(String documentId) {
        doProcess(documentId, null);
    }

    private void doProcess(String documentId, Long deadlineMs) {
        try {
            Document doc = documentRepository.findByDocumentId(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("文档不存在: " + documentId));

            if (!doc.isParseable()) {
                log.info("[Pipeline] 文档状态不可解析，跳过: docId={}, status={}", documentId,
                        doc.getStatus() != null ? doc.getStatus().getDesc() : null);
                return;
            }

            // Step 1: 文本提取
            String text = textExtractor.extractText(doc);
            log.info("[Pipeline] 文档解析完成: docId={}, textLen={}", documentId, text.length());
            checkDeadline(deadlineMs, documentId);

            // Step 2: 切片
            updateStatusImmediate(documentId, DocumentStatus.CHUNKING);
            List<ChunkStrategyService.ChunkResult> chunks = chunkDocument(doc, text);
            log.info("[Pipeline] 文档切分完成: docId={}, chunkCount={}", documentId, chunks.size());
            checkDeadline(deadlineMs, documentId);

            // Step 3: 向量化 + 存储
            updateStatusImmediate(documentId, DocumentStatus.EMBEDDING);
            storeChunks(doc, chunks);
            log.info("[Pipeline] 向量化存储完成: docId={}, chunks={}", documentId, chunks.size());
            checkDeadline(deadlineMs, documentId);

            // Step 4: 收尾
            documentRepository.updateChunkCount(documentId, chunks.size());
            updateStatusImmediate(documentId, DocumentStatus.PARSED);
            kbRepository.incrementDocumentCount(doc.getKnowledgeId());
            log.info("[Pipeline] 文档处理完成: docId={}", documentId);

        } catch (TaskTimeoutException e) {
            log.warn("[Pipeline] 任务超时退出: docId={}", documentId);
            throw e;
        } catch (Exception e) {
            log.error("[Pipeline] 文档处理失败: docId={}", documentId, e);
            updateStatusImmediate(documentId, DocumentStatus.FAILED);
            documentRepository.updateErrorMessage(documentId, e.getMessage());
        }
    }

    private void checkDeadline(Long deadlineMs, String documentId) {
        if (deadlineMs != null && System.currentTimeMillis() > deadlineMs) {
            throw new TaskTimeoutException(documentId, deadlineMs, System.currentTimeMillis());
        }
    }

    /**
     * 在新事务中立即提交状态变更 — 确保其他事务（如 triggerParse）立即可见.
     */
    private void updateStatusImmediate(String documentId, DocumentStatus status) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus txStatus = transactionManager.getTransaction(def);
        try {
            documentRepository.updateStatus(documentId, status);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    public List<ChunkStrategyService.ChunkResult> chunkDocument(Document doc, String text) {
        ChunkStrategy chunkStrategy = resolveStrategy(doc);
        ChunkStrategyService strategy = strategyFactory.getStrategy(chunkStrategy);
        Map<String, Object> config = resolveChunkConfig(doc);
        log.info("[Pipeline] 使用切片策略: chunkStrategy={}, docId={}", chunkStrategy, doc.getDocumentId());
        return strategy.split(text, config);
    }

    private ChunkStrategy resolveStrategy(Document doc) {
        if (doc.getChunkStrategy() != null && !doc.getChunkStrategy().isBlank()) {
            return ChunkStrategy.fromCode(doc.getChunkStrategy());
        }
        KnowledgeBase kb = kbRepository.findByKnowledgeId(doc.getKnowledgeId()).orElse(null);
        if (kb != null && kb.getDefaultChunkStrategy() != null && !kb.getDefaultChunkStrategy().isBlank()) {
            return ChunkStrategy.fromCode(kb.getDefaultChunkStrategy());
        }
        return kbDomainService.resolveFallbackStrategy(doc.getFileType());
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
}
