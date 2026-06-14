package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话持久化对象 — 映射 t_conversation 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationPO {

    private Long id;
    private String tenantId;
    private String conversationId;
    private String agentId;
    private String userId;
    private String title;
    private String status;
    private Integer messageCount;
    private Long totalTokens;
    private String metadataJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
