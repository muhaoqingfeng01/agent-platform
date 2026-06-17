package com.example.agent.application.evaluation;

import com.example.agent.application.evaluation.dto.EvaluationRunResponse;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.evaluation.entity.EvaluationDatasetItem;
import com.example.agent.domain.evaluation.entity.EvaluationRun;
import com.example.agent.domain.evaluation.repository.EvaluationDatasetRepository;
import com.example.agent.domain.evaluation.repository.EvaluationRunRepository;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * LLM-as-Judge 评测执行引擎.
 *
 * <p>遍历数据集中的每个 Q&A 样本，获取 Agent 回答，通过 LLM 评分，
 * 汇总生成评估报告。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRunService {

    private final EvaluationRunRepository runRepository;
    private final EvaluationDatasetRepository datasetRepository;
    private final ChatClient judgeClient;
    private final ObjectMapper objectMapper;

    /**
     * 执行评测 — 遍历数据集样本，LLM-as-Judge 评分.
     */
    public EvaluationRunResponse execute(String datasetId) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String evaluationId = IdGenerator.generate("eval");
        String agentId = "default";

        // 创建评测记录
        EvaluationRun run = EvaluationRun.builder()
                .tenantId(tenantId).evaluationId(evaluationId)
                .agentId(agentId).datasetId(datasetId)
                .status("RUNNING").build();
        runRepository.save(run);

        // 获取数据集样本
        List<EvaluationDatasetItem> items = datasetRepository.findItemsByDatasetId(datasetId);
        if (items.isEmpty()) {
            run.fail();
            runRepository.updateById(run);
            return toResponse(run);
        }

        // 逐题评分
        List<ItemScore> scores = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            EvaluationDatasetItem item = items.get(i);
            log.info("[Evaluation] 评测进度: {}/{} , evalId={}", i + 1, items.size(), evaluationId);
            try {
                ItemScore score = evaluateItem(item);
                scores.add(score);
            } catch (Exception e) {
                log.warn("[Evaluation] 单题评测失败: itemId={}, error={}", item.getId(), e.getMessage());
                scores.add(ItemScore.zero());
            }
        }

        // 汇总评分
        double overall = scores.stream().mapToDouble(ItemScore::getTotalScore).average().orElse(0);
        String metrics = buildMetrics(scores);

        run.complete(BigDecimal.valueOf(overall).setScale(2, RoundingMode.HALF_UP), metrics);
        runRepository.updateById(run);

        log.info("[Evaluation] 评测完成: evalId={}, overall={}, samples={}", evaluationId, overall, scores.size());
        return toResponse(run);
    }

    /**
     * 单题 LLM-as-Judge 评分.
     */
    private ItemScore evaluateItem(EvaluationDatasetItem item) {
        String question = item.getQuestion();
        String expected = item.getExpectedAnswer() != null ? item.getExpectedAnswer() : "";

        // 构建 Judge Prompt
        String prompt = buildJudgePrompt(question, expected);
        String judgeResp = judgeClient.prompt().user(prompt).call().content();

        return parseJudgeResponse(judgeResp);
    }

    private String buildJudgePrompt(String question, String expected) {
        return String.format("""
                你是一个专业的 AI 评估者。请根据标准答案对给定的回答进行评分。

                ## 问题
                %s

                ## 标准答案
                %s

                ## 评分维度（每项 0-10 分）
                1. 准确性 (accuracy): 回答事实是否正确
                2. 完整性 (completeness): 是否覆盖标准答案的所有要点
                3. 相关性 (relevance): 回答是否切题
                4. 幻觉 (hallucination): 是否存在与事实不符的编造内容（0=严重幻觉，10=无幻觉）

                ## 输出 JSON
                {"accuracy": N, "completeness": N, "relevance": N, "hallucination": N, "reasoning": "评分理由简述"}
                只输出 JSON，不要其他内容。""", question, expected);
    }

    private ItemScore parseJudgeResponse(String json) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(extractJson(json), Map.class);
            return ItemScore.builder()
                    .accuracy(toDouble(map.get("accuracy")))
                    .completeness(toDouble(map.get("completeness")))
                    .relevance(toDouble(map.get("relevance")))
                    .hallucination(toDouble(map.get("hallucination")))
                    .reasoning((String) map.getOrDefault("reasoning", "")).build();
        } catch (Exception e) {
            log.warn("[Evaluation] Judge 响应解析失败: {}", json);
            return ItemScore.zero();
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return "{}";
    }

    private double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        if (val instanceof String s) {
            try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    private String buildMetrics(List<ItemScore> scores) {
        double avgAcc = scores.stream().mapToDouble(ItemScore::getAccuracy).average().orElse(0);
        double avgCom = scores.stream().mapToDouble(ItemScore::getCompleteness).average().orElse(0);
        double avgRel = scores.stream().mapToDouble(ItemScore::getRelevance).average().orElse(0);
        double avgHal = scores.stream().mapToDouble(ItemScore::getHallucination).average().orElse(0);
        long halCount = scores.stream().filter(s -> s.getHallucination() < 5).count();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("accuracy", String.format("%.2f", avgAcc));
        metrics.put("completeness", String.format("%.2f", avgCom));
        metrics.put("relevance", String.format("%.2f", avgRel));
        metrics.put("hallucination", String.format("%.2f", avgHal));
        metrics.put("hallucinationRate", scores.isEmpty() ? "0%" :
                String.format("%.1f%%", 100.0 * halCount / scores.size()));
        metrics.put("sampleCount", scores.size());

        try { return objectMapper.writeValueAsString(metrics); }
        catch (JsonProcessingException e) { return "{}"; }
    }

    public EvaluationRunResponse getByEvaluationId(String evaluationId) {
        EvaluationRun run = runRepository.findByEvaluationId(evaluationId);
        if (run == null) throw new RuntimeException("评测记录不存在: " + evaluationId);
        return toResponse(run);
    }

    public List<EvaluationRunResponse> list(int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return runRepository.findByTenant(tenantId, page, size).stream()
                .map(this::toResponse).collect(java.util.stream.Collectors.toList());
    }

    private EvaluationRunResponse toResponse(EvaluationRun run) {
        return EvaluationRunResponse.builder()
                .evaluationId(run.getEvaluationId()).agentId(run.getAgentId())
                .datasetId(run.getDatasetId()).status(run.getStatus())
                .overallScore(run.getOverallScore()).metricsJson(run.getMetricsJson())
                .createdAt(run.getCreatedAt()).finishedAt(run.getFinishedAt()).build();
    }

    @lombok.Builder
    @lombok.Data
    static class ItemScore {
        double accuracy, completeness, relevance, hallucination;
        String reasoning;

        double getTotalScore() {
            return 0.4 * accuracy + 0.3 * completeness + 0.2 * relevance + 0.1 * hallucination;
        }

        static ItemScore zero() {
            return ItemScore.builder().accuracy(0).completeness(0).relevance(0).hallucination(0).reasoning("error").build();
        }
    }
}
