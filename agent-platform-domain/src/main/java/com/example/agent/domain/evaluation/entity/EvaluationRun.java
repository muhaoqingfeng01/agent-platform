package com.example.agent.domain.evaluation.entity;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评测执行记录实体.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class EvaluationRun {

    private Long id;
    private String tenantId;
    private String evaluationId;
    private String agentId;
    private String datasetId;
    private String status;
    private BigDecimal overallScore;
    private String metricsJson;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;

    public void complete(BigDecimal score, String metrics) {
        this.status = "COMPLETED";
        this.overallScore = score;
        this.metricsJson = metrics;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = "FAILED";
        this.finishedAt = LocalDateTime.now();
    }
}
