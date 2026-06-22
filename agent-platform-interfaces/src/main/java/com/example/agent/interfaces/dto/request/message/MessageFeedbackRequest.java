package com.example.agent.interfaces.dto.request.message;

import com.example.agent.domain.conversation.valueobject.FeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageFeedbackRequest {
    @NotNull(message = "feedback 不能为空，请传入 LIKE 或 DISLIKE")
    private FeedbackType feedback;
}
