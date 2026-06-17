package com.example.agent.domain.knowledge.entity;

import com.example.agent.domain.knowledge.valueobject.IndexType;
import com.example.agent.domain.knowledge.valueobject.KnowledgeBaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 知识库聚合根 — 管理文档集合与检索配置.
 * <p>
 * V1.4.0: status 改为 KnowledgeBaseStatus 枚举; 新增 createdBy 字段.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@Builder(toBuilder = true)
public class KnowledgeBase {

    private String knowledgeId;
    private Long tenantId;
    private String name;
    private String description;
    private String embeddingModel;
    private int chunkSize;
    private int chunkOverlap;
    private int documentCount;
    private KnowledgeBaseStatus status;

    // V1.4.0 新增
    private String createdBy;

    // V1.3.0: 切片策略
    private String defaultChunkStrategy;
    private String chunkConfigJson;

    // V1.3.0: 索引参数
    private IndexType indexType;
    private String indexParamsJson;

    // V1.3.0: 检索策略
    private String searchStrategy;
    private String searchParamsJson;
    private String multiStageParamsJson;
    private String monitoringParamsJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ========== 领域行为 ==========

    /** 文档数+1 */
    public void incrementDocumentCount() {
        this.documentCount++;
    }

    /** 文档数-1 */
    public void decrementDocumentCount() {
        if (this.documentCount > 0) this.documentCount--;
    }

    /** 更新文档计数 */
    public void updateDocumentCount(int count) {
        this.documentCount = count;
    }

    /** 更新默认切片策略 */
    public void updateDefaultChunkStrategy(String strategy, String configJson) {
        this.defaultChunkStrategy = strategy;
        if (configJson != null) {
            this.chunkConfigJson = configJson;
        }
    }

    /** 更新索引配置 */
    public void updateIndexConfig(IndexType type, String paramsJson) {
        this.indexType = type;
        if (paramsJson != null) {
            this.indexParamsJson = paramsJson;
        }
    }

    /** 更新检索策略 */
    public void updateSearchConfig(String strategy, String searchParams, String multiStageParams, String monitoringParams) {
        if (strategy != null) this.searchStrategy = strategy;
        if (searchParams != null) this.searchParamsJson = searchParams;
        if (multiStageParams != null) this.multiStageParamsJson = multiStageParams;
        if (monitoringParams != null) this.monitoringParamsJson = monitoringParams;
    }

    // ========== V1.4.0 新增领域行为 ==========

    /** 启用知识库 */
    public void enable() {
        this.status = KnowledgeBaseStatus.ENABLED;
    }

    /** 弃用知识库 */
    public void disable() {
        this.status = KnowledgeBaseStatus.DISABLED;
    }

    /** 标记删除 */
    public void markDeleted() {
        this.status = KnowledgeBaseStatus.DELETED;
    }

    public boolean isEnabled() {
        return this.status == KnowledgeBaseStatus.ENABLED;
    }

    public boolean isDisabled() {
        return this.status == KnowledgeBaseStatus.DISABLED;
    }

    public boolean isDeleted() {
        return this.status == KnowledgeBaseStatus.DELETED;
    }

    /** 权限判断：是否由指定用户创建 */
    public boolean isCreatedBy(String userId) {
        return this.createdBy != null && this.createdBy.equals(userId);
    }
}
