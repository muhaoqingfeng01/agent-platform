package com.example.agent.application.knowledge.dto;

import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.knowledge.entity.KnowledgeHitRecord;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 命中记录 DTO.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
public class HitRecordDTO {
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
    private Long createdAt;

    public static HitRecordDTO from(KnowledgeHitRecord r) {
        return HitRecordDTO.builder()
                .id(r.getId())
                .tenantId(r.getTenantId())
                .conversationId(r.getConversationId())
                .messageId(r.getMessageId())
                .chunkId(r.getChunkId())
                .queryText(r.getQueryText())
                .relevanceScore(r.getRelevanceScore())
                .rankPosition(r.getRankPosition())
                .usedInPrompt(r.isUsedInPrompt())
                .humanFeedback(r.getHumanFeedback())
                .feedbackNote(r.getFeedbackNote())
                .createdAt(TimeConverters.toEpochMilli(r.getCreatedAt()))
                .build();
    }
}
