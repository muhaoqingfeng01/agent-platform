package com.example.agent.application.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRunResponse {
    private String evaluationId;
    private String agentId;
    private String datasetId;
    private String status;
    private BigDecimal overallScore;
    private String metricsJson;
    private Long createdAt;
    private Long finishedAt;
}
