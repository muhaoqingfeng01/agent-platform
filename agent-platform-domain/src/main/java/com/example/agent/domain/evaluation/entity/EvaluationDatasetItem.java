package com.example.agent.domain.evaluation.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 评测样本实体（Q&A 对）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class EvaluationDatasetItem {

    private Long id;
    private String datasetId;
    private String question;
    private String expectedAnswer;
    private String retrievalContext;
    private String metadataJson;
    private LocalDateTime createdAt;
}
