package com.example.agent.interfaces.dto.request.message;

import com.example.agent.domain.conversation.valueobject.FeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageFeedbackRequest {
    @NotNull
    private FeedbackType feedback;
}
