# 语义切分、向量化与 Milvus 存储

## 所属阶段
**P2 增强能力 → T6 RAG 知识库引擎**

## 使用技术
- 语义切分算法（段落 + 滑动窗口）
- Embedding 模型（text-embedding-3-small / DashScope Embedding）
- Milvus SDK 2.x

## 涉及数据库表
- `t_document_chunk`（MySQL 元数据），Milvus Collection（向量数据）

## 实现方案

### 1. 语义切分算法

```java
@Component
public class ChunkService {

    /**
     * 基于段落 + 滑动窗口切分
     * @param text 纯文本
     * @param chunkSize 目标大小（token 数，近似值）
     * @param overlap 重叠大小（token 数）
     */
    public List<Chunk> split(String text, int chunkSize, int overlap) {
        List<Chunk> chunks = new ArrayList<>();

        // 1. 按段落分割
        String[] paragraphs = text.split("\\n\\s*\\n");

        // 2. 合并短段落，拆分长段落
        List<String> segments = mergeAndSplit(paragraphs, chunkSize);

        // 3. 滑动窗口重叠
        int index = 0;
        for (int i = 0; i < segments.size(); i++) {
            StringBuilder content = new StringBuilder(segments.get(i));
            // 加入上一段尾部作为重叠
            if (i > 0) {
                String prevTail = getTail(segments.get(i - 1), overlap);
                content.insert(0, prevTail + "\n");
            }

            Chunk chunk = new Chunk();
            chunk.setChunkIndex(index++);
            chunk.setContent(content.toString());
            chunk.setTokenCount(estimateTokens(content.toString()));
            chunk.setContentHash(sha256(content.toString()));
            chunks.add(chunk);
        }

        return chunks;
    }

    private int estimateTokens(String text) {
        // 粗略估算：中文 ~1.5 字符/token，英文 ~4 字符/token
        int chineseChars = countChinese(text);
        int otherChars = text.length() - chineseChars;
        return (int) (chineseChars / 1.5 + otherChars / 4.0);
    }
}
```

### 2. Embedding 向量化

```java
@Service
public class EmbeddingService {

    private final SpringAiEmbeddingClient embeddingClient;

    public float[] embed(String text) {
        return embeddingClient.embed(text);
    }

    /**
     * 批量向量化并写入 Milvus（同时写入 document_id 标量字段，支持检索结果溯源）.
     *
     * Milvus 中存储 document_id 的理由：
     *   - 向量检索返回结果时直接携带所属文档 ID，无需二次查 MySQL
     *   - 前端直接渲染"来自 xxx.pdf 第 N 段"的溯源链接
     *   - 检索结果可按 document_id 做聚合去重
     */
    public void embedAndStore(String documentId, List<Chunk> chunks) {
        Document doc = documentRepository.findByDocumentId(documentId);
        String collectionName = "kb_" + doc.getTenantId();

        // 确保 Collection 存在
        milvusClient.ensureCollection(collectionName, 1536);  // embedding 维度

        List<InsertParam.Field> fields = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        List<List<Float>> vectors = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        List<String> docIds = new ArrayList<>();               // ★ 新增: 文档 ID

        for (Chunk chunk : chunks) {
            float[] vector = embed(chunk.getContent());

            // 写入 Milvus
            long milvusId = IdGenerator.nextId();
            ids.add(milvusId);
            vectors.add(toList(vector));
            contents.add(chunk.getContent());
            docIds.add(documentId);                            // ★ 新增: 存储 document_id

            // 写入 MySQL 元数据
            DocumentChunk chunkEntity = DocumentChunk.builder()
                    .documentId(documentId)
                    .knowledgeId(doc.getKnowledgeId())
                    .chunkIndex(chunk.getChunkIndex())
                    .content(chunk.getContent())
                    .tokenCount(chunk.getTokenCount())
                    .contentHash(chunk.getContentHash())
                    .milvusId(String.valueOf(milvusId))
                    .build();
            chunkRepository.save(chunkEntity);
        }

        // 批量插入 Milvus（含 document_id 标量字段）
        milvusClient.insert(collectionName, ids, vectors, contents, docIds);
        milvusClient.flush(collectionName);
    }
}
```

### 3. Milvus Collection 管理

```java
@Component
public class MilvusCollectionManager {

    private final MilvusServiceClient milvusClient;

    public void ensureCollection(String collectionName, int dimension) {
        // 检查是否存在
        if (hasCollection(collectionName)) return;

        // 创建 Collection
        FieldType idField = FieldType.newBuilder()
                .withName("id").withDataType(DataType.Int64).withPrimaryKey(true).withAutoID(false)
                .build();
        FieldType vectorField = FieldType.newBuilder()
                .withName("embedding").withDataType(DataType.FloatVector).withDimension(dimension)
                .build();
        FieldType contentField = FieldType.newBuilder()
                .withName("content").withDataType(DataType.VarChar).withMaxLength(65535)
                .build();
        // ★ 新增: document_id 标量字段 — 检索时直接返回所属文档，支持溯源
        FieldType docIdField = FieldType.newBuilder()
                .withName("document_id").withDataType(DataType.VarChar).withMaxLength(128)
                .build();

        CreateCollectionParam param = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("Knowledge Base: " + collectionName)
                .addFieldType(idField).addFieldType(vectorField)
                .addFieldType(contentField).addFieldType(docIdField)    // ★
                .build();

        milvusClient.createCollection(param);

        // 创建向量索引（IVF_FLAT）
        milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName("embedding")
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withExtraParam("{\"nlist\":128}")
                .build());

        // ★ 为 document_id 创建标量索引，加速按文档筛选
        milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName("document_id")
                .withIndexType(IndexType.TRIE)                // Trie 适合字符串精确匹配
                .build());

        milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName).build());
    }
}
```

> **为什么要在 Milvus 中存储 `document_id` 而不是仅在 MySQL 中？**
>
> 1. **检索即溯源**: 向量检索的 topK 结果直接附带 `document_id`，省去一次 MySQL 回表查询
> 2. **前端直连**: 搜索结果渲染时，"来自 `部署手册.md` 第 3 段" 无需额外 HTTP 请求
> 3. **存储开销可忽略**: `document_id`(VarChar 128) / chunk，1 万向量仅 ≈ 1.2 MB
> 4. **MySQL 仍为 Source of Truth**: 文档元数据（文件名、大小、上传时间等）变更只改 MySQL，Milvus 中仅存不可变的 `document_id`
> 5. **按文档筛选**: 删除文档时直接用 `document_id == "doc_xxx"` 表达式过滤删除，无需先查 MySQL 再删 Milvus
