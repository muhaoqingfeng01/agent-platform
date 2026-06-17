package com.example.agent.domain.knowledge.entity;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 知识命中记录实体 — 用于检索追溯与人工标注.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@Builder(toBuilder = true)
public class KnowledgeHitRecord {

    private Long id;
    private Long tenantId;
    private String conversationId;
    private String messageId;
    private Long chunkId;
    private String queryText;
    private BigDecimal relevanceScore;
    private int rankPosition;
    private boolean usedInPrompt;
    private String humanFeedback;
    private String feedbackNote;
    private LocalDateTime createdAt;
}
