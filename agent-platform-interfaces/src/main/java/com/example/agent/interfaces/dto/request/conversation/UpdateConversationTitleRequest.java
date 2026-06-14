package com.example.agent.interfaces.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateConversationTitleRequest {
    @NotBlank
    private String title;
}
