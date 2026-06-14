package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 意图持久化对象 — 映射 t_intent 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentPO {

    private Long id;
    private String intentId;
    private String tenantId;
    private String intentCode;
    private String intentName;
    private String category;
    private String patterns;
    private String examples;
    private String llmPrompt;
    private String requiredParams;
    private String riskLevel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
