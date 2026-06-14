# 离线评估与 LLM-as-Judge

## 所属阶段
**P4 观测优化 → T12 效果评估与持续优化**

## 使用技术
- Spring AI（LLM-as-Judge 评分）
- 评测数据集管理
- 评估指标计算（准确率、幻觉率、检索命中率）

## 涉及数据库表
- `t_evaluation_run`, `t_evaluation_dataset`, `t_evaluation_dataset_item`

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/evaluation/datasets` | 创建评测数据集 |
| GET | `/api/v1/evaluation/datasets` | 数据集列表 |
| POST | `/api/v1/evaluation/datasets/{id}/items` | 添加样本（单条/CSV批量） |
| POST | `/api/v1/evaluation/datasets/{id}/run` | 执行评测 |
| GET | `/api/v1/evaluations/{id}` | 评测结果详情 |
| GET | `/api/v1/evaluations` | 评测历史列表 |

## 实现方案

### 1. 评估执行流程

```
创建数据集 → 添加 Q&A 样本
    │
    ▼
触发评估执行
    │
    ▼
遍历每个样本:
  ├── 发送 question → Agent 获取 answer
  ├── LLM-as-Judge 对比 answer vs expected_answer
  ├── 评分维度: 准确性 / 完整性 / 相关性 / 幻觉检测
  └── 记录逐题结果
    │
    ▼
汇总总体评分 → 生成评估报告
```

### 2. EvaluationRunService

```java
@Service
public class EvaluationRunService {

    private final AgentOrchestrationService agentService;
    private final ChatClient judgeClient;  // LLM-as-Judge

    public EvaluationRun execute(String datasetId) {
        EvaluationDataset dataset = datasetRepo.findByDatasetId(datasetId);
        List<EvaluationDatasetItem> items = datasetRepo.findItemsByDatasetId(datasetId);

        String evaluationId = IdGenerator.generate("eval");
        EvaluationRun run = EvaluationRun.builder()
                .evaluationId(evaluationId)
                .tenantId(TenantContext.getCurrentTenantId())
                .agentId(dataset.getAgentId())
                .datasetId(datasetId)
                .status("RUNNING")
                .build();
        runRepo.save(run);

        List<ItemScore> scores = new ArrayList<>();
        for (EvaluationDatasetItem item : items) {
            ItemScore score = evaluateItem(item);
            scores.add(score);
        }

        // 汇总
        double overall = scores.stream().mapToDouble(ItemScore::getTotalScore).average().orElse(0);
        run.setOverallScore(BigDecimal.valueOf(overall));
        run.setMetricsJson(JsonUtil.toJson(buildMetrics(scores)));
        run.setStatus("COMPLETED");
        run.setFinishedAt(LocalDateTime.now());
        runRepo.updateById(run);

        return run;
    }

    private ItemScore evaluateItem(EvaluationDatasetItem item) {
        // 1. 获取 Agent 回答
        String agentAnswer = agentService.generateAnswer(item.getQuestion());

        // 2. LLM-as-Judge 评分
        String judgePrompt = buildJudgePrompt(item.getQuestion(), item.getExpectedAnswer(), agentAnswer);
        String judgeResponse = judgeClient.prompt().user(judgePrompt).call().content();
        return parseJudgeResponse(judgeResponse);
    }

    private String buildJudgePrompt(String question, String expected, String actual) {
        return """
            你是一个专业的 AI 评估者。请对比标准答案和 Agent 回答进行评分。

            ## 问题
            %s

            ## 标准答案
            %s

            ## Agent 回答
            %s

            ## 评分维度（每项 0-10 分）
            1. 准确性 (accuracy): 回答事实是否正确
            2. 完整性 (completeness): 是否覆盖标准答案的所有要点
            3. 相关性 (relevance): 回答是否切题
            4. 幻觉 (hallucination): 是否存在与事实不符的编造内容（0=严重幻觉，10=无幻觉）

            ## 输出 JSON
            {"accuracy": N, "completeness": N, "relevance": N, "hallucination": N, "reasoning": "..."}
            """.formatted(question, expected, actual);
    }

    private Map<String, Object> buildMetrics(List<ItemScore> scores) {
        double avgAccuracy = scores.stream().mapToDouble(ItemScore::getAccuracy).average().orElse(0);
        double avgCompleteness = scores.stream().mapToDouble(ItemScore::getCompleteness).average().orElse(0);
        double avgRelevance = scores.stream().mapToDouble(ItemScore::getRelevance).average().orElse(0);
        double avgHallucination = scores.stream().mapToDouble(ItemScore::getHallucination).average().orElse(0);
        double hallucinationRate = scores.stream().filter(s -> s.getHallucination() < 5).count() / (double) scores.size();

        return Map.of(
            "accuracy", String.format("%.2f", avgAccuracy),
            "completeness", String.format("%.2f", avgCompleteness),
            "relevance", String.format("%.2f", avgRelevance),
            "hallucination", String.format("%.2f", avgHallucination),
            "hallucinationRate", String.format("%.2f%%", hallucinationRate * 100),
            "sampleCount", scores.size()
        );
    }
}
```

### 3. 评测指标计算公式

| 指标 | 公式 | 说明 |
|------|------|------|
| 准确率 | `avg(accuracy_score)` | 回答事实正确性 |
| 完整率 | `avg(completeness_score)` | 要点覆盖度 |
| 幻觉率 | `count(hallucination<5) / total` | 编造内容占比 |
| 综合分 | `0.4*accuracy + 0.3*completeness + 0.2*relevance + 0.1*hallucination` | 加权总分 |
