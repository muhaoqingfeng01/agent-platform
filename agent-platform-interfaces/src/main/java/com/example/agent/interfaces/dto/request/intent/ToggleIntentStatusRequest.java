package com.example.agent.interfaces.dto.request.intent;

import com.example.agent.domain.conversation.valueobject.IntentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToggleIntentStatusRequest {
    @NotBlank
    private IntentStatus status;
}
