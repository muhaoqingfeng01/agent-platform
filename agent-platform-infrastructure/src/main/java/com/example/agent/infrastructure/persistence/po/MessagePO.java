package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息持久化对象 — 映射 t_message 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePO {

    private Long id;
    private String conversationId;
    private String messageId;
    private String role;
    private String content;
    private Integer tokenCount;
    private String metadataJson;
    private String feedback;
    private LocalDateTime createdAt;
}
