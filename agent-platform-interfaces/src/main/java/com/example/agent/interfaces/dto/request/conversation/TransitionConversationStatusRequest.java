package com.example.agent.interfaces.dto.request.conversation;

import com.example.agent.domain.conversation.valueobject.ConversationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransitionConversationStatusRequest {
    @NotNull
    private ConversationStatus targetStatus;
}
