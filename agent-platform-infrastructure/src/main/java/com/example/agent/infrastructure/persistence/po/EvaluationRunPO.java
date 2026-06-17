package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRunPO {
    private Long id;
    private Long tenantId;
    private String evaluationId;
    private String agentId;
    private String datasetId;
    private String status;
    private BigDecimal overallScore;
    private String metricsJson;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
}
