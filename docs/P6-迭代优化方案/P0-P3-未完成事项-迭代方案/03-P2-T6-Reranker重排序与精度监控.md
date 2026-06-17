# P2-T6 Reranker 重排序与精度监控闭环

## 所属阶段
**P2 增强能力 → T6 RAG 知识库引擎**

## 使用技术
- Reranker: Cross-Encoder 模型（bge-reranker-v2-m3）/ ColBERT / LLM-as-Reranker
- 精度监控: recall@k / MRR / NDCG 评估指标 + Grid Search 自动调优
- 可选: Jina Reranker API / Cohere Rerank API（SaaS 方案，免部署模型）

---

## 1. 当前状态

| 功能 | 状态 | 说明 |
|------|:--:|------|
| 混合检索（向量 + 关键词） | ✅ | Milvus ANN + MySQL BM25 → RRF 融合 Top-5 |
| Reranker 枚举预留 | ✅ | `RerankerType` 枚举已定义（NONE/CROSS_ENCODER/COLBERT/LLM） |
| **Reranker 实现** | ❌ | 枚举存在但无实现类 |
| 四级精度配置 | ✅ | 文档级 > 知识库级 > 策略预设 > 系统默认 |
| 命中记录 + 人工标注 | ✅ | t_knowledge_hit_record + feedback API |
| **精度监控闭环** | ❌ | 无 recall@k 评估、无回归检测、无 Grid Search 调优 |

---

## 2. 方案一：Reranker 重排序

### 2.1 在检索管线中的位置

```
用户查询
    │
    ▼
混合检索（向量 Top-20 + 关键词 Top-20）
    │
    ▼
RRF 融合 → Top-10 候选集       ★ 现有逻辑
    │
    ▼
🆕 Reranker 精排 → Top-5       ★ 新增：更精准的语义排序
    │
    ▼
文档溯源 + 命中最录 + 返回
```

### 2.2 三种 Reranker 实现

#### 策略 A：Cross-Encoder 模型（推荐，本地部署）

```java
package com.example.agent.infrastructure.reranker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Cross-Encoder Reranker — 本地部署 bge-reranker-v2-m3.
 *
 * 原理：将 (query, document) 对一起输入 Cross-Encoder，
 * 模型输出相关性分数（比 Bi-Encoder 的余弦相似度更精准）。
 */
@Slf4j
@Component("crossEncoderReranker")
@RequiredArgsConstructor
public class CrossEncoderReranker implements Reranker {

    private final RerankerModelClient modelClient;   // HTTP 调用模型推理服务

    @Override
    public RerankerType supportedType() {
        return RerankerType.CROSS_ENCODER;
    }

    @Override
    public List<FusedHit> rerank(String query, List<FusedHit> candidates, int topK) {
        if (candidates.size() <= topK) return candidates;

        // 1. 构建 (query, chunkContent) 对
        List<CrossEncoderInput> inputs = candidates.stream()
            .map(h -> new CrossEncoderInput(query, h.getHit().getContent()))
            .toList();

        // 2. 批量推理 → 获取相关性分数
        List<Float> scores = modelClient.score(inputs);

        // 3. 按分数降序排序
        List<FusedHit> reranked = IntStream.range(0, candidates.size())
            .mapToObj(i -> {
                FusedHit hit = candidates.get(i);
                hit.setRerankScore(BigDecimal.valueOf(scores.get(i)));
                return hit;
            })
            .sorted(Comparator.comparing(FusedHit::getRerankScore).reversed())
            .toList();

        log.debug("Reranker: {} candidates → top-{} (scored)", candidates.size(), topK);
        return reranked.subList(0, Math.min(topK, reranked.size()));
    }
}
```

#### 策略 B：LLM-as-Reranker（免模型部署，但增加 LLM 成本）

```java
/**
 * LLM Reranker — 使用 DeepSeek 对候选片段打分.
 *
 * Prompt 设计（few-shot）:
 * "You are a relevance scorer. Given a query and a document chunk,
 *  rate the relevance on a scale of 0-10. Only return the number."
 *
 * 优点：无需额外模型部署
 * 缺点：每次检索增加 1 次 LLM 调用（可用小模型降低成本）
 */
@Component("llmReranker")
@RequiredArgsConstructor
public class LLMReranker implements Reranker {

    private final ChatClient chatClient;

    private static final String RERANK_PROMPT = """
        You are a relevance scorer. Rate how relevant the document is to the query.
        Query: {query}
        Document: {document}
        Score (0-10, integer only):""";

    @Override
    public RerankerType supportedType() {
        return RerankerType.LLM;
    }

    @Override
    public List<FusedHit> rerank(String query, List<FusedHit> candidates, int topK) {
        // 逐片段调 LLM 打分 → 按分排序 → 取 topK
        // 注意：生产环境应使用 batch API 或异步并发
        // ...
    }
}
```

#### 策略 C：ColBERT（Token-level 交互，最精准但最重）

> ColBERT 需要独立部署 ColBERT 模型服务。适合对检索精度要求极高的场景（如法律/医疗知识库）。在知识库规模 < 10 万 chunk 时，Cross-Encoder 已足够。

### 2.3 Reranker 类型选择建议

| 场景 | 推荐 Reranker | 理由 |
|------|:--:|------|
| 开发/测试环境 | `NONE` | 跳过 Reranker，RRF 直接返回 |
| 知识库 < 1 万 chunk | `LLM` | 免模型部署，LLM 调用成本可控 |
| 知识库 1-10 万 chunk | `CROSS_ENCODER` | 精度/成本最优平衡 |
| 知识库 > 10 万 chunk | `CROSS_ENCODER` | 高性能 GPU 推理 |
| 法律/医疗高精度 | `COLBERT` | Token 级精细匹配 |

### 2.4 混合检索服务集成

```java
// HybridSearchApplicationService.java 修改

public SearchResultDTO hybridSearch(String query, String kbId, SearchStrategy strategy) {
    // 1. 现有逻辑：向量检索 + 关键词检索 → RRF 融合 → Top-10
    List<FusedHit> candidates = rrfFusion(vectorHits, fulltextHits, RRF_K)
        .subList(0, Math.min(CANDIDATE_TOP_K, fused.size()));

    // 2. 🆕 Reranker 精排（如果 KB 配置了 Reranker）
    KnowledgeBase kb = kbRepository.findById(kbId).orElseThrow();
    if (kb.getRerankerType() != null && kb.getRerankerType() != RerankerType.NONE) {
        Reranker reranker = rerankerRegistry.get(kb.getRerankerType());
        candidates = reranker.rerank(query, candidates, FINAL_TOP_K);
    }

    // 3. 文档溯源 + 写入命中记录（现有逻辑不变）
    enrichDocumentMetadata(candidates);
    recordHits(query, candidates);

    return SearchResultDTO.from(candidates);
}
```

### 2.5 数据库变更

```sql
-- t_knowledge_base 已有字段（V1.3.0 预留），无需新增
-- reranker_type VARCHAR(20) DEFAULT 'NONE'  已在 V1.3.0 中定义
```

---

## 3. 方案二：精度监控闭环

### 3.1 设计目标

建立「检索质量可度量 → 自动调优 → 回归检测」的监控闭环，让知识库质量不再依赖人工抽查。

```
┌─────────────────────────────────────────────────────────┐
│                    精度监控闭环                          │
│                                                         │
│  评测数据集 ──→ 检索执行 ──→ 指标计算 ──→ 回归检测       │
│      ↑                                        │         │
│      │                                        ▼         │
│  知识库更新                          低于基线? → 告警    │
│      │                                        │         │
│      └──── Grid Search 推荐更优参数 ←─────────┘         │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 3.2 评估指标

| 指标 | 公式 | 含义 | 基线建议 |
|------|------|------|:--:|
| **Recall@5** | `TP@5 / (TP@5 + FN)` | Top-5 结果中命中了多少正确答案 | >0.80 |
| **MRR** | `1/N * Σ(1/rank_i)` | 第一个正确答案的平均排名倒数 | >0.60 |
| **NDCG@5** | `DCG@5 / IDCG@5` | 考虑排名位置的归一化折损累计增益 | >0.70 |
| **Hit Rate** | `has_hit / total_queries` | 至少命中 1 个正确答案的问题比例 | >0.90 |

### 3.3 实现方案

```java
package com.example.agent.application.evaluation;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 检索精度评估服务.
 *
 * 从评测数据集（t_evaluation_dataset_item）中提取 Q&A 对，
 * 将问题作为 query 执行检索 → 比较检索结果与标准答案 → 计算指标.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetrievalPrecisionService {

    private final HybridSearchApplicationService searchService;
    private final EvaluationDatasetRepository datasetRepository;

    /**
     * 对指定知识库运行精度评估.
     *
     * @param kbId 知识库 ID
     * @param datasetId 评测数据集 ID
     * @return 评估报告
     */
    public PrecisionReport evaluate(String kbId, String datasetId) {
        List<EvaluationDatasetItem> items = datasetRepository.findItemsByDatasetId(datasetId);

        List<QueryResult> results = items.stream()
            .map(item -> evaluateSingle(item, kbId))
            .toList();

        return PrecisionReport.builder()
            .kbId(kbId)
            .datasetId(datasetId)
            .totalQueries(results.size())
            .recallAt5(calculateRecallAtK(results, 5))
            .mrr(calculateMRR(results))
            .ndcgAt5(calculateNDCG(results, 5))
            .hitRate(calculateHitRate(results))
            .perQueryResults(results)
            .evaluatedAt(LocalDateTime.now())
            .build();
    }

    private QueryResult evaluateSingle(EvaluationDatasetItem item, String kbId) {
        // 1. 用 item.question 执行混合检索
        SearchResultDTO searchResult = searchService.hybridSearch(
            item.getQuestion(), kbId, SearchStrategy.PRECISE);

        // 2. 判断 item.expectedChunkId 或 item.expectedKeywords 是否命中
        List<String> retrievedChunkIds = searchResult.getHits().stream()
            .map(h -> h.getChunkId())
            .toList();

        int hitRank = retrievedChunkIds.indexOf(item.getExpectedChunkId()) + 1;

        return QueryResult.builder()
            .question(item.getQuestion())
            .expectedChunkId(item.getExpectedChunkId())
            .hitRank(hitRank)   // 0 = 未命中, 1-N = 命中的排名
            .retrievedChunkIds(retrievedChunkIds)
            .build();
    }

    // recall@k / MRR / NDCG 计算逻辑 ...
}
```

### 3.4 Grid Search 自动调优

```java
/**
 * Grid Search 自动搜索最优参数组合.
 *
 * 搜索空间示例:
 *   - 切片策略: [paragraph_sliding_window, markdown_header_aware, recursive_char_split]
 *   - 检索策略: [precise, balanced, fast]
 *   - chunkSize: [256, 512, 768]
 *   - chunkOverlap: [25, 50, 100]
 *   - TopK: [3, 5, 10]
 *   - Reranker: [NONE, CROSS_ENCODER]
 *
 * 目标函数: max(Recall@5 * 0.5 + MRR * 0.3 + NDCG@5 * 0.2)
 */
public class GridSearchOptimizer {

    /**
     * 执行 Grid Search，返回最优参数组合及对应指标.
     * 注意：搜索空间为笛卡尔积，大空间下建议用 Random Search 或 Bayesian Optimization.
     */
    public OptimizationResult optimize(String kbId, String datasetId, SearchSpace space) {
        List<ParamCombination> combinations = space.generateCombinations();
        List<ScoredCombination> results = new ArrayList<>();

        for (ParamCombination params : combinations) {
            // 1. 临时应用参数
            applyParams(kbId, params);
            // 2. 运行评估
            PrecisionReport report = evaluate(kbId, datasetId);
            // 3. 计算综合得分
            double score = report.getRecallAt5() * 0.5
                         + report.getMrr() * 0.3
                         + report.getNdcgAt5() * 0.2;
            results.add(new ScoredCombination(params, score, report));
        }

        // 按得分降序 → Top-3 推荐
        return OptimizationResult.fromTopN(results, 3);
    }
}
```

### 3.5 API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/knowledge-bases/{kbId}/precision/evaluate` | 运行精度评估（传入 datasetId） |
| GET | `/api/v1/knowledge-bases/{kbId}/precision/reports` | 历史评估报告列表 |
| GET | `/api/v1/knowledge-bases/{kbId}/precision/reports/{id}` | 报告详情（含逐题结果） |
| POST | `/api/v1/knowledge-bases/{kbId}/precision/optimize` | 触发 Grid Search 自动调优 |
| GET | `/api/v1/knowledge-bases/{kbId}/precision/baseline` | 获取当前基线指标 |

---

## 4. 实施建议

| 事项 | 优先级 | 理由 |
|------|:--:|------|
| Cross-Encoder Reranker | **P1** | 直接提升检索精度，ROI 最高 |
| LLM Reranker（小模型） | P2 | 免部署，适合快速验证 Reranker 效果 |
| recall@k + MRR 评估 | P2 | 先有度量，才能驱动优化 |
| Grid Search 自动调优 | P3 | 依赖评估体系先跑通 |
| ColBERT Reranker | P3 | 知识库规模不足时 ROI 低 |

**推荐路线**：先做 LLM Reranker（0 部署成本快速验证）→ 评估指标上线 → 视效果决定是否部署 Cross-Encoder 模型。

---

## 5. 工作量

| 活动 | 时间 |
|------|:--:|
| Reranker 接口 + RerankerRegistry | 0.5天 |
| LLM Reranker 实现 | 0.5天 |
| HybridSearchService 集成 Reranker | 0.5天 |
| RetrievalPrecisionService（评估指标） | 1天 |
| Grid Search Optimizer | 0.5天 |
| API + 测试 | 0.5天 |
| **合计（两项）** | **3.5天** |
| Reranker 单独 | 1.5天 |
| 精度监控单独 | 1.5天 |
