package com.example.agent.interfaces.dto.request.intent;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IntentTestRequest {
    @NotBlank
    private String input;
}
