package com.example.agent.application.intent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 意图识别结果值对象 — Builder 模式构建.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentResult {

    private String intentCode;
    private String intentName;
    private String category;
    private double confidence;
    private String reasoning;
    private String riskLevel;
    private List<Map<String, Object>> requiredParams;
    private String matchedLayer;

    /** 规则命中 */
    public static IntentResult matched(String intentCode, String intentName, String category,
                                        double confidence, String reasoning, String riskLevel,
                                        List<Map<String, Object>> requiredParams) {
        return IntentResult.builder()
                .intentCode(intentCode).intentName(intentName).category(category)
                .confidence(confidence).reasoning(reasoning)
                .riskLevel(riskLevel).requiredParams(requiredParams)
                .matchedLayer("RULE")
                .build();
    }

    /** LLM/Cache 命中 */
    public static IntentResult matched(String intentCode, String intentName, String category,
                                        double confidence, String reasoning, String riskLevel,
                                        List<Map<String, Object>> requiredParams, String layer) {
        return IntentResult.builder()
                .intentCode(intentCode).intentName(intentName).category(category)
                .confidence(confidence).reasoning(reasoning)
                .riskLevel(riskLevel).requiredParams(requiredParams)
                .matchedLayer(layer)
                .build();
    }

    /** 未匹配 */
    public static IntentResult unknown(String reason) {
        return IntentResult.builder()
                .intentCode("UNKNOWN").intentName("未知意图").category("UNKNOWN")
                .confidence(0.0).reasoning(reason).riskLevel("LOW")
                .matchedLayer("NONE")
                .build();
    }

    public boolean isMatched() {
        return !"UNKNOWN".equals(intentCode);
    }

    public boolean isHighRisk() {
        return "HIGH".equals(riskLevel) || "CRITICAL".equals(riskLevel);
    }
}
