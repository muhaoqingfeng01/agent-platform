package com.example.agent.application.evaluation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DatasetCreateCommand {
    @NotBlank
    private String name;
    private String description;
    private String source;
}
