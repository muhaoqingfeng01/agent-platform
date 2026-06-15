package com.example.agent.domain.knowledge.repository;

import com.example.agent.domain.knowledge.entity.KnowledgeHitRecord;

import java.util.List;

/**
 * 知识命中记录仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface KnowledgeHitRecordRepository {

    void save(KnowledgeHitRecord record);

    void batchSave(List<KnowledgeHitRecord> records);

    List<KnowledgeHitRecord> findByConversationId(String conversationId, int page, int size);

    long countByConversationId(String conversationId);

    List<KnowledgeHitRecord> findByChunkId(Long chunkId, int page, int size);

    void updateFeedback(Long id, String feedback, String note);

    List<KnowledgeHitRecord> findPositiveFeedbackByTenant(String tenantId, int limit);

    long countByTenantAndDateRange(String tenantId, String startDate, String endDate);
}
