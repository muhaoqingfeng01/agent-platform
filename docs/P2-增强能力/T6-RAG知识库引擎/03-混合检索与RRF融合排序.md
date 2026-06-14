# 混合检索与 RRF 融合排序

## 所属阶段
**P2 增强能力 → T6 RAG 知识库引擎**

## 使用技术
- Milvus 向量检索（ANN 近似最近邻）
- MySQL 全文索引 / Elasticsearch（BM25 关键词检索）
- RRF（Reciprocal Rank Fusion）融合算法

## 涉及数据库表
- `t_document_chunk`, `t_knowledge_hit_record`

## 实现方案

### 1. 混合检索流程

```
用户查询
    │
    ├──────────────────┐
    ▼                  ▼
┌─────────┐    ┌─────────────┐
│向量检索  │    │关键词检索     │
│Milvus   │    │MySQL/ES BM25│
│Top-20   │    │Top-20       │
└────┬────┘    └──────┬──────┘
     │                │
     └───────┬────────┘
             ▼
     ┌──────────────┐
     │ RRF 融合排序  │
     │ score = Σ 1/(k+rank) │
     └──────┬───────┘
             ▼
        Top-5 结果
             │
             ▼
     ┌──────────────┐
     │ 写入 hit_record│
     └──────────────┘
```

### 2. HybridSearchService

```java
@Service
public class HybridSearchService {

    private final MilvusVectorRepository milvusRepo;
    private final FulltextSearchRepository fulltextRepo;
    private final KnowledgeHitRecordRepository hitRecordRepository;
    private final EmbeddingService embeddingService;

    private static final int VECTOR_TOP_K = 20;
    private static final int FULLTEXT_TOP_K = 20;
    private static final int FUSION_TOP_N = 5;
    private static final int RRF_K = 60;  // RRF 常数

    public SearchResult hybridSearch(String query, String knowledgeId) {
        String tenantId = TenantContext.getCurrentTenantId();
        String collectionName = "kb_" + tenantId;

        // 1. 向量检索
        float[] queryVector = embeddingService.embed(query);
        List<SearchHit> vectorHits = milvusRepo.search(collectionName, queryVector, VECTOR_TOP_K);

        // 2. 关键词检索
        List<SearchHit> fulltextHits = fulltextRepo.search(knowledgeId, query, FULLTEXT_TOP_K);

        // 3. RRF 融合
        List<FusedHit> fused = rrfFusion(vectorHits, fulltextHits, RRF_K);
        List<FusedHit> topN = fused.subList(0, Math.min(FUSION_TOP_N, fused.size()));

        // 4. 记录命中
        recordHits(query, topN);

        return new SearchResult(query, topN);
    }

    /**
     * RRF 算法: score(chunk) = Σ 1/(k + rank_i(chunk))
     */
    private List<FusedHit> rrfFusion(List<SearchHit> vectorHits, List<SearchHit> fulltextHits, int k) {
        Map<Long, Double> scores = new HashMap<>();
        Map<Long, SearchHit> hitMap = new HashMap<>();

        // 向量检索分数
        for (int i = 0; i < vectorHits.size(); i++) {
            SearchHit hit = vectorHits.get(i);
            scores.merge(hit.getChunkId(), 1.0 / (k + i + 1), Double::sum);
            hitMap.putIfAbsent(hit.getChunkId(), hit);
        }

        // 关键词检索分数
        for (int i = 0; i < fulltextHits.size(); i++) {
            SearchHit hit = fulltextHits.get(i);
            scores.merge(hit.getChunkId(), 1.0 / (k + i + 1), Double::sum);
            hitMap.putIfAbsent(hit.getChunkId(), hit);
        }

        // 按 RRF 分数降序排序
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(e -> new FusedHit(hitMap.get(e.getKey()), e.getValue()))
                .toList();
    }

    /**
     * 记录检索命中（用于后续分析和人工标注）
     */
    private void recordHits(String query, List<FusedHit> topN) {
        for (int i = 0; i < topN.size(); i++) {
            FusedHit hit = topN.get(i);
            KnowledgeHitRecord record = KnowledgeHitRecord.builder()
                    .tenantId(TenantContext.getCurrentTenantId())
                    .conversationId(ConversationContext.getCurrentConversationId())
                    .chunkId(hit.getHit().getChunkId())
                    .queryText(query)
                    .relevanceScore(BigDecimal.valueOf(hit.getRrfScore()))
                    .rankPosition(i + 1)
                    .usedInPrompt(i < 3)  // Top3 注入 Prompt
                    .build();
            hitRecordRepository.save(record);
        }
    }
}
```

### 3. 命中记录人工标注 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/knowledge/hits` | 命中记录列表 |
| POST | `/api/v1/knowledge/hits/{id}/feedback` | 人工标注（EXCELLENT/NEEDS_FIX/SUPPLEMENT） |
| GET | `/api/v1/knowledge/hits/stats` | 命中率统计数据 |
