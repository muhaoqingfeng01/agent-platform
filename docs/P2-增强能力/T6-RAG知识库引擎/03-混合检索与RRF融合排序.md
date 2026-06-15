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
│含doc_id │    │可JOIN doc   │
└────┬────┘    └──────┬──────┘
     │                │
     └───────┬────────┘
             ▼
     ┌──────────────┐
     │ RRF 融合排序  │
     │ score = Σ 1/(k+rank) │
     └──────┬───────┘
             ▼
        Top-5 结果 (含 documentId)
             │
             ▼
     ┌──────────────┐
     │ 批量查文档元数据│  ★ 新增
     │ t_document    │
     │ (filename,type,size,minioPath) │
     └──────┬───────┘
             ▼
     ┌──────────────┐
     │ 生成访问链接   │  ★ 新增
     │ MinIO 预签名  │
     │ URL / API代理 │
     └──────┬───────┘
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

### 4. 文档溯源链接增强 ★ 新增

> **设计目标**: 检索结果中的每个 chunk 必须能追溯到原始文档，前端可直接渲染 "来自 `部署手册.md` 第 3 段" 并提供下载链接。

#### 4.1 数据架构

```
Milvus (向量检索)                    MySQL (元数据源)
┌──────────────────────┐          ┌──────────────────────┐
│ id: 123              │          │ t_document_chunk     │
│ embedding: [...]     │    ┌────→│  - document_id (FK)  │
│ content: "..."       │    │     │  - chunk_index       │
│ document_id: "doc_x" │────┘     ├──────────────────────┤
└──────────────────────┘          │ t_document           │
                                  │  - document_id (PK)  │
                                  │  - filename          │
                                  │  - file_type         │
                                  │  - file_size         │
                                  │  - minio_path        │
                                  │  - uploaded_at       │
                                  └──────────────────────┘
```

**关键原则**:
- Milvus 仅存不可变的 `document_id`，不存文件名等可变元数据
- MySQL `t_document` 为文档元数据 Source of Truth
- 检索后批量 IN 查询 MySQL→仅 1 次查询，延迟 < 5ms
- MinIO 访问链接通过预签名 URL 或 API 代理生成，不直接暴露对象存储地址

#### 4.2 搜索结果文档元数据聚合

```java
/**
 * 检索结果增强 — 批量查询文档元数据 + 生成访问链接.
 * 在 RRF 融合 Top-5 之后、写 hit_record 之前调用.
 */
private void enrichDocumentMetadata(List<FusedHit> topN) {
    // 1. 提取去重的 document_id 集合
    Set<String> docIds = topN.stream()
            .map(h -> h.getHit().getDocumentId())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    if (docIds.isEmpty()) return;

    // 2. 批量查询文档元数据（1 次 IN 查询）
    Map<String, DocumentDoc> docMap = documentRepository
            .findByDocumentIds(docIds).stream()
            .collect(Collectors.toMap(DocumentDoc::getDocumentId, Function.identity()));

    // 3. 为每个 hit 填充文档元数据
    for (FusedHit fh : topN) {
        DocumentDoc doc = docMap.get(fh.getHit().getDocumentId());
        if (doc != null) {
            fh.getHit().setDocumentFilename(doc.getFilename());
            fh.getHit().setDocumentFileType(doc.getFileType());
            fh.getHit().setDocumentAccessUrl(buildAccessUrl(doc.getDocumentId()));
            fh.getHit().setDocumentUploadedAt(doc.getUploadedAt());
        }
    }
}

/** 生成文档访问链接 — 代理下载模式（MinIO 不可公网访问时） */
private String buildAccessUrl(String documentId) {
    return "/api/v1/documents/" + documentId + "/download";
}

/** 生成文档访问链接 — MinIO 预签名 URL 模式（MinIO 可公网访问时） */
private String buildPresignedUrl(String minioPath) {
    return minioClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .bucket("knowledge-docs").object(minioPath)
            .expiry(10, TimeUnit.MINUTES)    // 10 分钟有效期
            .method(Method.GET).build());
}
```

#### 4.3 文档下载 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/documents/{documentId}/download` | 下载原始文档 |

**实现方式**（按 MinIO 部署模式选择）：

| 模式 | 实现 | 适用场景 |
|------|------|----------|
| **代理下载** | Controller 从 MinIO 读 InputStream → 流式写入 Response | MinIO 不可公网访问（推荐） |
| **预签名重定向** | 302 Redirect → MinIO 预签名 URL (10min TTL) | MinIO 可公网访问 |

```java
// 代理下载模式（推荐）
@GetMapping("/{documentId}/download")
public void download(@PathVariable String documentId, HttpServletResponse response) {
    Document doc = documentRepository.findByDocumentId(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));

    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition",
            "attachment; filename=\"" + URLEncoder.encode(doc.getFilename(), StandardCharsets.UTF_8) + "\"");

    try (InputStream is = minioClient.getObject(GetObjectArgs.builder()
            .bucket("knowledge-docs").object(doc.getMinioPath()).build())) {
        is.transferTo(response.getOutputStream());
    }
}
```

#### 4.4 响应格式

**搜索结果响应**（带文档溯源信息）：

```json
{
    "code": 200,
    "data": {
        "query": "Docker 如何部署",
        "hits": [{
            "chunkId": 123,
            "documentId": "doc_a1b2c3d4",
            "documentFilename": "部署手册.md",        ★ 新增
            "documentFileType": "MD",                 ★ 新增
            "documentAccessUrl": "/api/v1/documents/doc_a1b2c3d4/download",  ★ 新增
            "chunkIndex": 15,
            "content": "使用 docker-compose up -d 启动服务...",
            "rrfScore": 0.0852,
            "rankPosition": 1
        }],
        "documents": [{                               ★ 新增: 去重后的文档引用列表
            "documentId": "doc_a1b2c3d4",
            "filename": "部署手册.md",
            "fileType": "MD",
            "fileSize": 245760,
            "accessUrl": "/api/v1/documents/doc_a1b2c3d4/download",
            "uploadedAt": "2026-06-15T10:30:00"
        }]
    }
}
```

