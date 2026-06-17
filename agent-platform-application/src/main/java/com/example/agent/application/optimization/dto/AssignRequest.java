package com.example.agent.application.optimization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRequest {
    @NotBlank
    private String assignee;
}
