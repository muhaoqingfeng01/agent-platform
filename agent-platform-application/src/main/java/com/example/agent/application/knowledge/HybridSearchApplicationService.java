package com.example.agent.application.knowledge;

import com.example.agent.application.knowledge.dto.*;
import com.example.agent.application.knowledge.rerank.RerankerRegistry;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.entity.KnowledgeHitRecord;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.repository.KnowledgeHitRecordRepository;
import com.example.agent.domain.knowledge.service.*;
import com.example.agent.domain.knowledge.valueobject.RerankerType;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 混合检索应用服务 — 向量检索 + 关键词检索 + RRF 融合 + 文档溯源.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HybridSearchApplicationService {

    private final VectorSearchProvider vectorSearch;
    private final FulltextSearchProvider fulltextSearch;
    private final KnowledgeHitRecordRepository hitRecordRepository;
    private final PrecisionConfigDomainService precisionDomainService;
    private final DocumentRepository documentRepository;
    private final KnowledgeBaseRepository kbRepository;                // ★ V1.4.0: KB 过滤
    private final RerankerRegistry rerankerRegistry;                   // ★ V1.5.0: Reranker 精排

    private static final int DEFAULT_VECTOR_TOP_K = 20;
    private static final int DEFAULT_FULLTEXT_TOP_K = 20;
    private static final int DEFAULT_FUSION_TOP_N = 5;
    private static final int FINAL_TOP_K = 5;            // ★ V1.5.0: Reranker 后最终返回数
    private static final int RRF_K = 60;

    public SearchResultDTO search(String query, String knowledgeId, Map<String, Object> searchConfig) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String collectionName = "kb_" + tenantId;

        PrecisionConfigDomainService.MergedPrecisionConfig config =
                precisionDomainService.mergeConfig(null, null, searchConfig, null);

        int vectorTopK = config.coarseTopK() > 0 ? config.coarseTopK() : DEFAULT_VECTOR_TOP_K;
        int fulltextTopK = DEFAULT_FULLTEXT_TOP_K;
        int fusionTopN = config.fusionTopN() > 0 ? config.fusionTopN() : DEFAULT_FUSION_TOP_N;
        double threshold = config.similarityThreshold();

        // ★ 修复: 按 knowledgeId 构建 Milvus filterExpression
        String filterExpression = buildKbFilterExpression(knowledgeId);
        Set<String> searchKbIds = resolveSearchKbIds(knowledgeId);

        List<VectorSearchProvider.SearchHit> vectorHits = vectorSearch.search(
                collectionName, query, vectorTopK, threshold, filterExpression);
        log.debug("[HybridSearch] 向量检索: hits={}, filter={}", vectorHits.size(), filterExpression);

        List<VectorSearchProvider.SearchHit> fulltextHits = config.enableRrfFusion()
                ? fulltextSearch.search(searchKbIds, query, fulltextTopK)
                : Collections.emptyList();
        log.debug("[HybridSearch] 关键词检索: hits={}, kbIds={}", fulltextHits.size(), searchKbIds);

        List<FusedHit> fused;
        if (config.enableRrfFusion()) {
            fused = rrfFusion(vectorHits, fulltextHits, RRF_K, config.vectorWeight(), config.keywordWeight());
        } else {
            fused = vectorHits.stream()
                    .map(h -> new FusedHit(h, 1.0 / (RRF_K + 1)))
                    .toList();
        }

        List<FusedHit> topN = fused.subList(0, Math.min(fusionTopN, fused.size()));

        // ★ V1.5.0: Reranker 精排（如果 KB 配置了 Reranker）
        RerankerType rerankerType = resolveRerankerType(knowledgeId, config);
        if (rerankerType != null && rerankerType != RerankerType.NONE) {
            topN = applyReranker(query, topN, rerankerType, FINAL_TOP_K);
        }

        // ★ 新增: 批量查询文档元数据
        Map<String, Document> docMap = enrichDocumentMetadata(topN);

        recordHits(query, topN);

        // 构建 HitItem 列表（含文档溯源字段）
        List<SearchResultDTO.HitItem> items = new ArrayList<>();
        for (int i = 0; i < topN.size(); i++) {
            FusedHit fh = topN.get(i);
            VectorSearchProvider.SearchHit hit = fh.hit;

            var builder = SearchResultDTO.HitItem.builder()
                    .chunkId(hit.chunkId())
                    .documentId(hit.documentId())
                    .content(hit.content())
                    .chunkIndex(hit.chunkIndex())
                    .rrfScore(BigDecimal.valueOf(fh.rrfScore))
                    .rankPosition(i + 1);

            // ★ 填充文档溯源字段
            Document doc = docMap.get(hit.documentId());
            if (doc != null) {
                builder.documentFilename(doc.getFilename())
                        .documentFileType(doc.getFileType())
                        .documentAccessUrl(buildAccessUrl(doc.getDocumentId()))
                        .documentUploadedAt(TimeConverters.toEpochMilli(doc.getUploadedAt()));
            }
            items.add(builder.build());
        }

        // ★ 构建去重后的文档引用列表
        List<SearchResultDTO.DocumentRef> docRefs = docMap.values().stream()
                .map(d -> SearchResultDTO.DocumentRef.builder()
                        .documentId(d.getDocumentId())
                        .filename(d.getFilename())
                        .fileType(d.getFileType())
                        .fileSize(d.getFileSize())
                        .accessUrl(buildAccessUrl(d.getDocumentId()))
                        .uploadedAt(TimeConverters.toEpochMilli(d.getUploadedAt()))
                        .build())
                .toList();

        return SearchResultDTO.builder()
                .query(query)
                .hits(items)
                .documents(docRefs)           // ★ 新增
                .build();
    }

    /**
     * ★ V1.4.0: 根据 knowledgeId 构建 Milvus 标量过滤表达式.
     */
    private String buildKbFilterExpression(String knowledgeId) {
        if (knowledgeId != null && !knowledgeId.isBlank()) {
            return "knowledge_id == \"" + knowledgeId + "\"";
        }
        // 未指定 KB → 检索所有 ENABLED KB
        Long tenantId = TenantContext.getCurrentTenantId();
        Set<String> enabledIds = kbRepository.findEnabledKnowledgeIds(tenantId);
        if (enabledIds.isEmpty()) return null;
        StringBuilder sb = new StringBuilder("knowledge_id in [");
        int i = 0;
        for (String id : enabledIds) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(id).append("\"");
            i++;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * ★ V1.4.0: 解析检索范围（用于全文检索）.
     */
    private Set<String> resolveSearchKbIds(String knowledgeId) {
        if (knowledgeId != null && !knowledgeId.isBlank()) {
            // 验证 KB 存在且 ENABLED
            KnowledgeBase kb = kbRepository.findByKnowledgeId(knowledgeId)
                    .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
            if (!kb.isEnabled()) {
                throw new IllegalStateException("知识库已弃用/删除，不可检索: " + knowledgeId);
            }
            return Set.of(knowledgeId);
        }
        // 所有 ENABLED KB
        return kbRepository.findEnabledKnowledgeIds(TenantContext.getCurrentTenantId());
    }

    /**
     * ★ 新增: 批量查询文档元数据（1 次 IN 查询，避免 N+1）.
     */
    private Map<String, Document> enrichDocumentMetadata(List<FusedHit> topN) {
        Set<String> docIds = topN.stream()
                .map(h -> h.hit.documentId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (docIds.isEmpty()) return Collections.emptyMap();

        return documentRepository.findByDocumentIds(docIds).stream()
                .collect(Collectors.toMap(Document::getDocumentId, Function.identity()));
    }

    /** ★ 新增: 生成文档访问链接 — 代理下载模式（MinIO 不可公网访问时推荐） */
    private String buildAccessUrl(String documentId) {
        return "/api/v1/documents/" + documentId + "/download";
    }

    private List<FusedHit> rrfFusion(List<VectorSearchProvider.SearchHit> vectorHits,
                                     List<VectorSearchProvider.SearchHit> fulltextHits,
                                     int k, double vectorWeight, double keywordWeight) {
        Map<Long, Double> scores = new LinkedHashMap<>();
        Map<Long, VectorSearchProvider.SearchHit> hitMap = new LinkedHashMap<>();

        double totalWeight = vectorWeight + keywordWeight;
        double vw = totalWeight > 0 ? vectorWeight / totalWeight : 0.5;
        double kw = totalWeight > 0 ? keywordWeight / totalWeight : 0.5;

        for (int i = 0; i < vectorHits.size(); i++) {
            var hit = vectorHits.get(i);
            scores.merge(hit.chunkId(), vw / (k + i + 1), Double::sum);
            hitMap.putIfAbsent(hit.chunkId(), hit);
        }

        for (int i = 0; i < fulltextHits.size(); i++) {
            var hit = fulltextHits.get(i);
            scores.merge(hit.chunkId(), kw / (k + i + 1), Double::sum);
            hitMap.putIfAbsent(hit.chunkId(), hit);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(e -> new FusedHit(hitMap.get(e.getKey()), e.getValue()))
                .toList();
    }

    private void recordHits(String query, List<FusedHit> topN) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String conversationId = "unknown";
        for (int i = 0; i < topN.size(); i++) {
            FusedHit fh = topN.get(i);
            KnowledgeHitRecord record = KnowledgeHitRecord.builder()
                    .tenantId(tenantId)
                    .conversationId(conversationId)
                    .chunkId(fh.hit.chunkId())
                    .queryText(query)
                    .relevanceScore(BigDecimal.valueOf(fh.rrfScore))
                    .rankPosition(i + 1)
                    .usedInPrompt(i < 3)
                    .createdAt(LocalDateTime.now())
                    .build();
            hitRecordRepository.save(record);
        }
    }

    public List<HitRecordDTO> listHits(String conversationId, int page, int size) {
        return hitRecordRepository.findByConversationId(conversationId, page, size)
                .stream().map(HitRecordDTO::from).toList();
    }

    public void recordFeedback(Long hitRecordId, String feedback, String note) {
        hitRecordRepository.updateFeedback(hitRecordId, feedback, note);
    }

    public record FusedHit(VectorSearchProvider.SearchHit hit, double rrfScore) {}

    // ==================== V1.5.0: Reranker 精排 ====================

    /**
     * 解析知识库的 Reranker 类型配置.
     */
    private RerankerType resolveRerankerType(String knowledgeId, PrecisionConfigDomainService.MergedPrecisionConfig config) {
        if (config.enableReranker() && config.rerankerType() != null) {
            RerankerType type = RerankerType.fromCode(config.rerankerType());
            if (type != RerankerType.NONE && rerankerRegistry.hasImpl(type)) {
                return type;
            }
        }
        return RerankerType.NONE;
    }

    /**
     * 应用 Reranker 精排.
     */
    private List<FusedHit> applyReranker(String query, List<FusedHit> topN, RerankerType type, int finalTopK) {
        Reranker reranker = rerankerRegistry.get(type).orElse(null);
        if (reranker == null) return topN;

        // 转换为 RerankerHit
        List<Reranker.RerankerHit> candidates = topN.stream()
            .map(fh -> Reranker.RerankerHit.from(fh.hit, fh.rrfScore))
            .toList();

        // 模型精排
        List<Reranker.RerankerHit> reranked = reranker.rerank(query, candidates, finalTopK);
        log.info("[HybridSearch] Reranker 精排完成: type={}, {} candidates → top-{}", type, candidates.size(), reranked.size());

        // 转换回 FusedHit（保留原始 SearchHit 引用）
        Map<String, VectorSearchProvider.SearchHit> hitMap = topN.stream()
            .collect(Collectors.toMap(fh -> fh.hit.chunkId().toString(), fh -> fh.hit, (a, b) -> a));

        return reranked.stream()
            .map(rh -> {
                VectorSearchProvider.SearchHit originalHit = hitMap.get(rh.chunkId());
                return new FusedHit(originalHit != null ? originalHit : topN.get(0).hit, rh.rerankScore());
            })
            .toList();
    }
}
