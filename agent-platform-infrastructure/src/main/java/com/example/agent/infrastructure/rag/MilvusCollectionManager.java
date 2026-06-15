package com.example.agent.infrastructure.rag;

import com.example.agent.domain.knowledge.service.MilvusStoreClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.MutationResult;
import io.milvus.param.ConnectParam;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Milvus Collection 管理器 — 基于 Milvus SDK 2.6.x 的真实实现.
 * <p>
 * V1.4.0: 新增 knowledge_id 标量字段、deleteByDocumentId/KnowledgeId、filtered search.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
public class MilvusCollectionManager implements MilvusStoreClient {

    @Value("${milvus.host:localhost}")
    private String host;

    @Value("${milvus.port:19530}")
    private int port;

    @Value("${milvus.username:root}")
    private String username;

    @Value("${milvus.password:}")
    private String password;

    @Value("${milvus.database:agent_platform}")
    private String database;

    @Value("${milvus.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${milvus.keep-alive-time-ms:60000}")
    private long keepAliveTimeMs;

    private MilvusServiceClient milvusClient;
    private final Map<String, Boolean> collectionCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .withDatabaseName(database)
                .withConnectTimeout(connectTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                .withKeepAliveTime(keepAliveTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);

        milvusClient = new MilvusServiceClient(builder.build());
        log.info("[Milvus] 客户端初始化完成: host={}:{}, db={}", host, port, database);
    }

    @PreDestroy
    public void destroy() {
        if (milvusClient != null) {
            milvusClient.close();
            log.info("[Milvus] 客户端已关闭");
        }
    }

    // ============================================================
    // Collection 管理
    // ============================================================

    @Override
    public void ensureCollection(String collectionName, int dimension, String indexType) {
        if (collectionCache.putIfAbsent(collectionName, true) != null) {
            return;
        }

        R<Boolean> hasColl = milvusClient.hasCollection(
                HasCollectionParam.newBuilder().withCollectionName(collectionName).build());
        if (hasColl.getData() != null && hasColl.getData()) {
            log.info("[Milvus] Collection 已存在: {}", collectionName);
            loadIfNeeded(collectionName);
            return;
        }

        // Schema: id + embedding + content + document_id + knowledge_id
        FieldType idField = FieldType.newBuilder()
                .withName("id").withDataType(DataType.Int64)
                .withPrimaryKey(true).withAutoID(false)
                .build();
        FieldType vectorField = FieldType.newBuilder()
                .withName("embedding").withDataType(DataType.FloatVector)
                .withDimension(dimension)
                .build();
        FieldType contentField = FieldType.newBuilder()
                .withName("content").withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();
        FieldType docIdField = FieldType.newBuilder()
                .withName("document_id").withDataType(DataType.VarChar)
                .withMaxLength(128)
                .build();
        FieldType kbIdField = FieldType.newBuilder()
                .withName("knowledge_id").withDataType(DataType.VarChar)
                .withMaxLength(128)
                .build();

        CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("KB: " + collectionName)
                .addFieldType(idField).addFieldType(vectorField)
                .addFieldType(contentField).addFieldType(docIdField)
                .addFieldType(kbIdField)
                .build();

        R<RpcStatus> createResp = milvusClient.createCollection(createParam);
        if (createResp.getStatus() != 0) {
            log.error("[Milvus] Collection 创建失败: {}, error={}", collectionName, createResp.getMessage());
            collectionCache.remove(collectionName);
            return;
        }
        log.info("[Milvus] Collection 创建成功: {}, dim={}", collectionName, dimension);

        // 向量索引
        io.milvus.param.IndexType idxType = resolveIndexType(indexType);
        R<RpcStatus> indexResp = milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName("embedding")
                .withIndexType(idxType)
                .withMetricType(MetricType.COSINE)
                .withExtraParam(buildIndexExtraParam(idxType))
                .build());
        if (indexResp.getStatus() != 0) {
            log.warn("[Milvus] 向量索引创建异常: {}", indexResp.getMessage());
        } else {
            log.info("[Milvus] 向量索引创建成功: collection={}, index={}", collectionName, idxType);
        }

        // 为 document_id + knowledge_id 创建标量索引
        createScalarIndex(collectionName, "document_id");
        createScalarIndex(collectionName, "knowledge_id");

        loadIfNeeded(collectionName);
    }

    // ============================================================
    // 数据写入
    // ============================================================

    @Override
    public void insert(String collectionName, List<VectorEntry> entries) {
        if (entries == null || entries.isEmpty()) return;

        List<Long> ids = new ArrayList<>();
        List<List<Float>> vectors = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        List<String> docIds = new ArrayList<>();
        List<String> kbIds = new ArrayList<>();

        for (VectorEntry e : entries) {
            ids.add(e.id());
            List<Float> vec = new ArrayList<>(e.vector().length);
            for (float v : e.vector()) vec.add(v);
            vectors.add(vec);
            contents.add(e.content());
            docIds.add(e.documentId() != null ? e.documentId() : "");
            kbIds.add(e.knowledgeId() != null ? e.knowledgeId() : "");
        }

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", ids));
        fields.add(new InsertParam.Field("embedding", vectors));
        fields.add(new InsertParam.Field("content", contents));
        fields.add(new InsertParam.Field("document_id", docIds));
        fields.add(new InsertParam.Field("knowledge_id", kbIds));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        R<MutationResult> insertResp = milvusClient.insert(insertParam);
        if (insertResp.getStatus() != 0) {
            log.error("[Milvus] 插入失败: collection={}, error={}", collectionName, insertResp.getMessage());
            return;
        }
        log.info("[Milvus] 批量插入成功: collection={}, count={}", collectionName, entries.size());

        milvusClient.flush(FlushParam.newBuilder()
                .withCollectionNames(Collections.singletonList(collectionName)).build());
    }

    // ============================================================
    // 向量检索（★ 修复: 从 TODO 变为真实实现）
    // ============================================================

    /**
     * 向量检索 — 返回 topK 个最相似的 chunk.
     *
     * @param collectionName  Milvus Collection
     * @param queryVector     查询向量
     * @param topK            topK
     * @param threshold       相似度阈值（低于此值丢弃）
     * @param filterExpression 标量过滤表达式，如 knowledge_id in ["kb_abc"]
     */
    public List<VectorSearchResult> search(String collectionName, List<Float> queryVector,
                                           int topK, float threshold, String filterExpression) {
        try {
            SearchParam.Builder builder = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withVectorFieldName("embedding")
                    .withVectors(Collections.singletonList(queryVector))
                    .withTopK(topK)
                    .withMetricType(MetricType.COSINE)
                    .withParams("{\"nprobe\": 16}")
                    .addOutField("content")
                    .addOutField("document_id")
                    .addOutField("knowledge_id");

            // ★ 新增: 标量过滤
            if (filterExpression != null && !filterExpression.isBlank()) {
                builder.withExpr(filterExpression);
            }

            R<io.milvus.grpc.SearchResults> searchResp = milvusClient.search(builder.build());
            if (searchResp.getStatus() != 0) {
                log.error("[Milvus] 检索失败: collection={}, error={}", collectionName, searchResp.getMessage());
                return Collections.emptyList();
            }

            io.milvus.grpc.SearchResults results = searchResp.getData();
            if (results == null || results.getResults() == null) {
                return Collections.emptyList();
            }

            List<VectorSearchResult> hits = new ArrayList<>();
            for (var fieldData : results.getResults().getFieldsDataList()) {
                // 遍历 score
                for (int i = 0; i < results.getResults().getScoresCount(); i++) {
                    double score = results.getResults().getScores(i);
                    if (score < threshold) continue;

                    long id = results.getResults().getIds().getIntId().getData(i);
                    String content = "";
                    String documentId = "";
                    String knowledgeId = "";

                    // 解析输出的标量字段
                    for (var fd : results.getResults().getFieldsDataList()) {
                        if ("content".equals(fd.getFieldName()) && i < fd.getScalars().getStringData().getDataCount()) {
                            content = fd.getScalars().getStringData().getData(i);
                        }
                        if ("document_id".equals(fd.getFieldName()) && i < fd.getScalars().getStringData().getDataCount()) {
                            documentId = fd.getScalars().getStringData().getData(i);
                        }
                        if ("knowledge_id".equals(fd.getFieldName()) && i < fd.getScalars().getStringData().getDataCount()) {
                            knowledgeId = fd.getScalars().getStringData().getData(i);
                        }
                    }
                    hits.add(new VectorSearchResult(id, score, content, documentId, knowledgeId));
                }
            }

            log.debug("[Milvus] 向量检索完成: collection={}, topK={}, threshold={}, filter={}, hits={}",
                    collectionName, topK, threshold, filterExpression, hits.size());
            return hits;

        } catch (Exception e) {
            log.error("[Milvus] 检索异常: collection={}", collectionName, e);
            return Collections.emptyList();
        }
    }

    // ============================================================
    // ★ 新增: 向量删除
    // ============================================================

    @Override
    public void deleteByDocumentId(String collectionName, String documentId) {
        if (documentId == null || documentId.isBlank()) return;
        String expr = "document_id == \"" + documentId + "\"";
        executeDelete(collectionName, expr);
    }

    @Override
    public void deleteByKnowledgeId(String collectionName, String knowledgeId) {
        if (knowledgeId == null || knowledgeId.isBlank()) return;
        String expr = "knowledge_id == \"" + knowledgeId + "\"";
        executeDelete(collectionName, expr);
    }

    private void executeDelete(String collectionName, String expr) {
        try {
            R<MutationResult> resp = milvusClient.delete(
                    DeleteParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withExpr(expr)
                            .build());
            if (resp.getStatus() == 0) {
                log.info("[Milvus] 向量删除成功: collection={}, expr={}", collectionName, expr);
            } else {
                log.warn("[Milvus] 向量删除异常: collection={}, expr={}, error={}",
                        collectionName, expr, resp.getMessage());
            }
        } catch (Exception e) {
            log.error("[Milvus] 向量删除异常: collection={}, expr={}", collectionName, expr, e);
        }
    }

    // ============================================================
    // 内部工具方法
    // ============================================================

    private void loadIfNeeded(String collectionName) {
        R<RpcStatus> loadResp = milvusClient.loadCollection(
                LoadCollectionParam.newBuilder().withCollectionName(collectionName).build());
        if (loadResp.getStatus() != 0) {
            log.warn("[Milvus] 加载 Collection 异常: {}", loadResp.getMessage());
        }
    }

    private void createScalarIndex(String collectionName, String fieldName) {
        try {
            R<RpcStatus> resp = milvusClient.createIndex(CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName(fieldName)
                    .withIndexType(io.milvus.param.IndexType.TRIE)
                    .build());
            if (resp.getStatus() != 0) {
                log.debug("[Milvus] 标量索引创建: collection={}, field={}, msg={}",
                        collectionName, fieldName, resp.getMessage());
            }
        } catch (Exception e) {
            log.debug("[Milvus] 标量索引创建异常: collection={}, field={}", collectionName, fieldName, e.getMessage());
        }
    }

    private io.milvus.param.IndexType resolveIndexType(String indexType) {
        return switch (indexType != null ? indexType.toUpperCase() : "IVF_FLAT") {
            case "IVF_SQ8" -> io.milvus.param.IndexType.IVF_SQ8;
            case "IVF_PQ" -> io.milvus.param.IndexType.IVF_PQ;
            case "HNSW" -> io.milvus.param.IndexType.HNSW;
            case "DISKANN" -> io.milvus.param.IndexType.DISKANN;
            default -> io.milvus.param.IndexType.IVF_FLAT;
        };
    }

    private String buildIndexExtraParam(io.milvus.param.IndexType indexType) {
        return switch (indexType) {
            case IVF_FLAT, IVF_SQ8, IVF_PQ -> "{\"nlist\":128}";
            case HNSW -> "{\"M\":16,\"efConstruction\":200}";
            case DISKANN -> "{\"max_degree\":56,\"search_list_size\":100}";
            default -> "{\"nlist\":128}";
        };
    }

    /**
     * 向量检索结果 — 内部类型.
     */
    public record VectorSearchResult(long id, double score, String content,
                                     String documentId, String knowledgeId) {}
}
