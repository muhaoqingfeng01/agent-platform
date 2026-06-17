package com.example.agent.application.evaluation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddItemRequest {
    @NotBlank
    private String question;
    private String expectedAnswer;
    private String retrievalContext;
    private String metadataJson;
}
