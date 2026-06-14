package com.example.agent.interfaces.dto.request.intent;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateIntentRequest {
    private String intentName;
    private List<String> patterns;
    private List<String> examples;
    private String llmPrompt;
    private List<Map<String, Object>> requiredParams;
    private String riskLevel;
}
