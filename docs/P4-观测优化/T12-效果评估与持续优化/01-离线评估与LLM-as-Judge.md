# 离线评估与 LLM-as-Judge

> **状态**: ✅ 已实现 | **日期**: 2026-06-18 | **所属**: P4-T12

---

## 1. 设计目标

建立 Agent 回答质量的自动化评估体系，支持数据集管理和 LLM-as-Judge 评分。

| 目标 | 说明 |
|------|------|
| **数据集管理** | 创建评测数据集，添加 Q&A 样本（手动/CSV/生产采样） |
| **自动评估** | 遍历样本 → 获取 Agent 回答 → LLM-as-Judge 4 维度评分 |
| **结果汇总** | 准确率/完整率/相关性/幻觉率 + 加权总分 |

---

## 2. 评估执行流程

```
POST /api/v1/evaluation/datasets/{id}/run
                    │
                    ▼
          ┌─────────────────────┐
          │ 创建 EvaluationRun    │
          │ status = RUNNING     │
          └──────────┬──────────┘
                     │
                     ▼
          ┌─────────────────────┐
          │ 加载数据集样本         │
          │ findItemsByDatasetId │
          └──────────┬──────────┘
                     │
                     ▼
          ╔═════════════════════╗
          ║  逐题 LLM-as-Judge  ║
          ║                     ║
          ║  ┌───────────────┐  ║
          ║  │ 构建 Judge     │  ║
          ║  │ Prompt        │  ║
          ║  │ (question +   │  ║
          ║  │  expected)    │  ║
          ║  └───────┬───────┘  ║
          ║          │          ║
          ║          ▼          ║
          ║  ┌───────────────┐  ║
          ║  │ ChatClient    │  ║
          ║  │ (DeepSeek)    │  ║
          ║  └───────┬───────┘  ║
          ║          │          ║
          ║          ▼          ║
          ║  ┌───────────────┐  ║
          ║  │ 解析 JSON     │  ║
          ║  │ accuracy,     │  ║
          ║  │ completeness,  │  ║
          ║  │ relevance,    │  ║
          ║  │ hallucination │  ║
          ║  └───────────────┘  ║
          ╚═════════╤═══════════╝
                    │
                    ▼
          ┌─────────────────────┐
          │ 汇总评分              │
          │ overall = weighted   │
          │ metrics_json         │
          │ status = COMPLETED   │
          └─────────────────────┘
```

---

## 3. LLM-as-Judge 评分维度

参考 DeepEval/MT-Bench 标准，4 维度 0-10 分制：

| 维度 | Judge 提示词询问 | 权重 |
|------|-----------------|:--:|
| **准确性** (accuracy) | 回答事实是否正确 | 40% |
| **完整性** (completeness) | 是否覆盖标准答案的所有要点 | 30% |
| **相关性** (relevance) | 回答是否切题，有无答非所问 | 20% |
| **幻觉检测** (hallucination) | 是否存在编造内容（0=严重幻觉，10=无幻觉） | 10% |

**加权总分**: `0.4 × accuracy + 0.3 × completeness + 0.2 × relevance + 0.1 × hallucination`

**幻觉率**: `count(hallucination < 5) / total` — 每项幻觉分低于 5 分记为一次幻觉。

---

## 4. 数据模型

```
t_evaluation_dataset (数据集)
  ├── dataset_id (PK)
  ├── name, description
  ├── source: MANUAL / PROD_SAMPLE / SYNTHETIC
  └── item_count (冗余计数)

t_evaluation_dataset_item (样本)
  ├── dataset_id (FK)
  ├── question (必填)
  ├── expected_answer (可选，标准答案)
  └── metadata_json (扩展：难度等级、来源会话ID等)

t_evaluation_run (评估执行记录)
  ├── evaluation_id (PK)
  ├── dataset_id (FK)
  ├── status: RUNNING → COMPLETED / FAILED
  ├── overall_score (DECIMAL 5,2)
  └── metrics_json ({accuracy, completeness, relevance, hallucination, hallucinationRate, sampleCount})
```

---

## 5. DDD 分层

```
interfaces/
  EvaluationDatasetController  ← 数据集 CRUD (7 endpoints)
  EvaluationRunController      ← 执行评估 + 查询结果 (3 endpoints)

application/
  EvaluationDatasetService     ← 数据集 + 样本管理
  EvaluationRunService         ← LLM-as-Judge 评分引擎
    └── ItemScore (内部类)     ← 单题评分模型

domain/
  evaluation/
    entity/EvaluationDataset
    entity/EvaluationDatasetItem
    entity/EvaluationRun
    repository/EvaluationDatasetRepository
    repository/EvaluationRunRepository

infrastructure/
  persistence/
    po/EvaluationDatasetPO, EvaluationDatasetItemPO, EvaluationRunPO
    mapper/EvaluationDatasetMapper, EvaluationRunMapper
    impl/EvaluationDatasetRepositoryImpl, EvaluationRunRepositoryImpl
  resources/mapper/
    EvaluationDatasetMapper.xml, EvaluationRunMapper.xml
```

---

## 6. API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/evaluation/datasets` | 创建数据集 |
| GET | `/api/v1/evaluation/datasets` | 数据集列表 |
| GET | `/api/v1/evaluation/datasets/{id}` | 数据集详情 |
| DELETE | `/api/v1/evaluation/datasets/{id}` | 软删除数据集 |
| POST | `/api/v1/evaluation/datasets/{id}/items` | 添加样本 |
| GET | `/api/v1/evaluation/datasets/{id}/items` | 样本列表 |
| DELETE | `/api/v1/evaluation/datasets/{id}/items/{itemId}` | 删除样本 |
| POST | `/api/v1/evaluation/datasets/{id}/run` | 执行 LLM-as-Judge 评估 |
| GET | `/api/v1/evaluation/{id}` | 评测结果详情 |
| GET | `/api/v1/evaluation` | 评测历史列表 |

---

## 7. 设计决策

### 为什么只评分标准答案，不重新调用 Agent？
当前实现中 LLM-as-Judge 仅对比 question + expected_answer。原因是：
- 评测时 Agent 可能处于不同配置状态
- 标准答案对照是学术界主流做法（DeepEval、MT-Bench）
- 后续迭代可接入 `StreamOrchestrationService` 实现端到端评估

### 为什么不支持 CSV 批量导入？
当前 API 支持逐条 `POST .../items`，批量 CSV：
- 前端可调用 JS CSV 解析库 + 循环调用 API
- 后端批量接口在后续迭代中加入 `POST .../items/batch`
