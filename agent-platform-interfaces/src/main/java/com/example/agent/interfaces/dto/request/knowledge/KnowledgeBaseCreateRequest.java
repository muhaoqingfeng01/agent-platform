package com.example.agent.interfaces.dto.request.knowledge;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgeBaseCreateRequest {
    @NotBlank private String name;
    private String description;
    private String embeddingModel;
}
