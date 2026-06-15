package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.knowledge.entity.KnowledgeHitRecord;
import com.example.agent.domain.knowledge.repository.KnowledgeHitRecordRepository;
import com.example.agent.infrastructure.persistence.mapper.KnowledgeHitRecordMapper;
import com.example.agent.infrastructure.persistence.po.KnowledgeHitRecordPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识命中记录仓储 MyBatis 实现.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class KnowledgeHitRecordRepositoryImpl implements KnowledgeHitRecordRepository {

    private final KnowledgeHitRecordMapper mapper;

    @Override
    public void save(KnowledgeHitRecord record) { mapper.insert(toPO(record)); }

    @Override
    public void batchSave(List<KnowledgeHitRecord> records) {
        if (records == null || records.isEmpty()) return;
        mapper.batchInsert(records.stream().map(this::toPO).toList());
    }

    @Override
    public List<KnowledgeHitRecord> findByConversationId(String conversationId, int page, int size) {
        int offset = page * size;
        return mapper.selectByConversationId(conversationId, offset, size)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByConversationId(String conversationId) {
        return mapper.countByConversationId(conversationId);
    }

    @Override
    public List<KnowledgeHitRecord> findByChunkId(Long chunkId, int page, int size) {
        int offset = page * size;
        return mapper.selectByChunkId(chunkId, offset, size).stream().map(this::toDomain).toList();
    }

    @Override
    public void updateFeedback(Long id, String feedback, String note) {
        mapper.updateFeedback(id, feedback, note);
    }

    @Override
    public List<KnowledgeHitRecord> findPositiveFeedbackByTenant(String tenantId, int limit) {
        return mapper.selectPositiveFeedbackByTenant(tenantId, limit)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByTenantAndDateRange(String tenantId, String startDate, String endDate) {
        return mapper.countByTenantAndDateRange(tenantId, startDate, endDate);
    }

    // ==================== 映射方法 ====================

    private KnowledgeHitRecord toDomain(KnowledgeHitRecordPO po) {
        return KnowledgeHitRecord.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .conversationId(po.getConversationId())
                .messageId(po.getMessageId())
                .chunkId(po.getChunkId())
                .queryText(po.getQueryText())
                .relevanceScore(po.getRelevanceScore())
                .rankPosition(po.getRankPosition() != null ? po.getRankPosition() : 0)
                .usedInPrompt(po.getUsedInPrompt() != null && po.getUsedInPrompt())
                .humanFeedback(po.getHumanFeedback())
                .feedbackNote(po.getFeedbackNote())
                .createdAt(po.getCreatedAt())
                .build();
    }

    private KnowledgeHitRecordPO toPO(KnowledgeHitRecord record) {
        return KnowledgeHitRecordPO.builder()
                .tenantId(record.getTenantId())
                .conversationId(record.getConversationId())
                .messageId(record.getMessageId())
                .chunkId(record.getChunkId())
                .queryText(record.getQueryText())
                .relevanceScore(record.getRelevanceScore())
                .rankPosition(record.getRankPosition())
                .usedInPrompt(record.isUsedInPrompt())
                .humanFeedback(record.getHumanFeedback())
                .feedbackNote(record.getFeedbackNote())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
