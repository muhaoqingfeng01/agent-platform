package com.example.agent.interfaces.dto.request.conversation;

import lombok.Data;

import java.util.Map;

@Data
public class ConversationCreateRequest {
    /** 可选；为空时应用层使用 ProjectConstants.Conversation.DEFAULT_AGENT_ID */
    private String agentId;
    private String title;
    private Map<String, Object> metadata;
}
