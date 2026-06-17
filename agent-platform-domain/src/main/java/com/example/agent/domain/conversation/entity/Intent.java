package com.example.agent.domain.conversation.entity;

import com.example.agent.domain.conversation.valueobject.IntentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 意图领域实体 — 聚合根.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class Intent {

    private String intentId;
    private Long tenantId;
    private String intentCode;
    private String intentName;
    private String category;
    private List<String> patterns;
    private List<String> examples;
    private String llmPrompt;
    private List<Map<String, Object>> requiredParams;
    private String riskLevel;
    private IntentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 更新意图定义 — 仅更新非 null 字段 */
    public void updateDefinition(String name, List<String> newPatterns, List<String> newExamples,
                                  String newLlmPrompt, List<Map<String, Object>> newRequiredParams,
                                  String newRiskLevel) {
        if (name != null && !name.isBlank()) this.intentName = name;
        if (newPatterns != null) this.patterns = newPatterns;
        if (newExamples != null) this.examples = newExamples;
        if (newLlmPrompt != null) this.llmPrompt = newLlmPrompt;
        if (newRequiredParams != null) this.requiredParams = newRequiredParams;
        if (newRiskLevel != null) this.riskLevel = newRiskLevel;
    }

    public void setStatus(IntentStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == IntentStatus.ACTIVE;
    }
}
