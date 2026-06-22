package com.example.agent.application.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetResponse {
    private String datasetId;
    private String name;
    private String description;
    private Integer itemCount;
    private String source;
    private Long createdAt;
}
