package com.example.agent.interfaces.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class ConversationCreateRequest {
    @NotBlank
    private String agentId;
    private String title;
    private Map<String, Object> metadata;
}
