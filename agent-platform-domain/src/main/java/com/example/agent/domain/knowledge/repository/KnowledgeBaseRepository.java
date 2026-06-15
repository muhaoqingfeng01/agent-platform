package com.example.agent.domain.knowledge.repository;

import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import com.example.agent.domain.knowledge.valueobject.IndexType;
import com.example.agent.domain.knowledge.valueobject.KnowledgeBaseStatus;
import com.example.agent.domain.knowledge.valueobject.SearchStrategy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 知识库仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public interface KnowledgeBaseRepository {

    void save(KnowledgeBase kb);

    void update(KnowledgeBase kb);

    Optional<KnowledgeBase> findByKnowledgeId(String knowledgeId);

    List<KnowledgeBase> findByTenant(String tenantId, int page, int size);

    long countByTenant(String tenantId);

    void updateDocumentCount(String knowledgeId, int documentCount);

    void incrementDocumentCount(String knowledgeId);

    void decrementDocumentCount(String knowledgeId);

    void updateChunkStrategy(String knowledgeId, ChunkStrategy strategy, String configJson);

    void updateIndexConfig(String knowledgeId, IndexType indexType, String indexParamsJson);

    void updateSearchConfig(String knowledgeId, SearchStrategy strategy,
                            String searchParamsJson, String multiStageParamsJson,
                            String monitoringParamsJson);

    void updateStatus(String knowledgeId, String status);

    void updateKnowledgeBaseStatus(String knowledgeId, KnowledgeBaseStatus status);

    List<KnowledgeBase> findByTenantAndStatus(String tenantId, KnowledgeBaseStatus status);

    /** ★ V1.4.0: 获取租户下所有 ENABLED 状态的知识库 ID（用于检索过滤） */
    Set<String> findEnabledKnowledgeIds(String tenantId);

    void softDelete(String knowledgeId);
}
