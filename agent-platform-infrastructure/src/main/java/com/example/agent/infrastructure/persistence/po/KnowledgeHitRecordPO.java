package com.example.agent.infrastructure.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 知识命中记录持久化对象 — 映射 t_knowledge_hit_record 表.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeHitRecordPO {
    private Long id;
    private Long tenantId;
    private String conversationId;
    private String messageId;
    private Long chunkId;
    private String queryText;
    private BigDecimal relevanceScore;
    private Integer rankPosition;
    private Boolean usedInPrompt;
    private String humanFeedback;
    private String feedbackNote;
    private LocalDateTime createdAt;
}
