package com.example.agent.domain.evaluation.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 评测数据集实体.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class EvaluationDataset {

    private Long id;
    private String tenantId;
    private String datasetId;
    private String name;
    private String description;
    private Integer itemCount;
    private String source;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void incrementItemCount() {
        this.itemCount = (this.itemCount == null ? 0 : this.itemCount) + 1;
    }

    public void decrementItemCount() {
        if (this.itemCount != null && this.itemCount > 0) {
            this.itemCount--;
        }
    }
}
