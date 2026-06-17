package com.example.agent.application.optimization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveRequest {
    @NotBlank
    private String resolution;
    @NotBlank
    private String resolutionType;
}
