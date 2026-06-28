package com.example.agent.interfaces.dto.request.intent;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IntentCreateRequest {
    @NotBlank private String name;
    private String category;
    private List<String> patterns;
    private List<String> examples;
    private String llmPrompt;
    private List<Map<String, Object>> requiredParams;
    private String riskLevel;
}
