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
     * 批量向量化并写入 Milvus
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

        for (Chunk chunk : chunks) {
            float[] vector = embed(chunk.getContent());

            // 写入 Milvus
            long milvusId = IdGenerator.nextId();
            ids.add(milvusId);
            vectors.add(toList(vector));
            contents.add(chunk.getContent());

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

        // 批量插入 Milvus
        milvusClient.insert(collectionName, ids, vectors);
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

        CreateCollectionParam param = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("Knowledge Base: " + collectionName)
                .addFieldType(idField).addFieldType(vectorField).addFieldType(contentField)
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

        milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName).build());
    }
}
```
