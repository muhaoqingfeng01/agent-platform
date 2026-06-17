package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDatasetItemPO {
    private Long id;
    private String datasetId;
    private String question;
    private String expectedAnswer;
    private String retrievalContext;
    private String metadataJson;
    private LocalDateTime createdAt;
}
