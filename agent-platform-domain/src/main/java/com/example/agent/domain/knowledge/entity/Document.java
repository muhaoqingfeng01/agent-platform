package com.example.agent.domain.knowledge.entity;

import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 文档实体 — 所属 KnowledgeBase 聚合.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@Builder(toBuilder = true)
public class Document {

    private String documentId;
    private String tenantId;
    private String knowledgeId;
    private String filename;
    private String fileType;
    private long fileSize;
    private String minioPath;
    private String contentHash;
    private int chunkCount;
    private DocumentStatus status;
    private String errorMessage;
    private String uploadedBy;
    private LocalDateTime uploadedAt;

    // V1.3.0 新增：切片策略覆盖
    private String chunkStrategy;
    private String chunkConfigJson;

    // V1.3.0 新增：检索精度覆盖
    private String searchStrategyOverride;
    private String searchParamsOverrideJson;
    private String multiStageOverrideJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ========== 领域行为 ==========

    public void updateStatus(DocumentStatus newStatus) {
        this.status = newStatus;
    }

    public void markFailed(String errorMsg) {
        this.status = DocumentStatus.FAILED;
        this.errorMessage = errorMsg;
    }

    /** V1.4.0: 替代 markReady — 已解析，向量可用 */
    public void markParsed(int chunkCount) {
        this.status = DocumentStatus.PARSED;
        this.chunkCount = chunkCount;
    }

    /** V1.4.0: 初始状态 — 上传完成等待手动触发解析 */
    public void markPendingParse() {
        this.status = DocumentStatus.PENDING_PARSE;
    }

    /** V1.4.0: 弃用 — 向量已删除，元数据保留 */
    public void markDeprecated() {
        this.status = DocumentStatus.DEPRECATED;
    }

    public boolean isProcessing() {
        return this.status == DocumentStatus.PARSING
                || this.status == DocumentStatus.CHUNKING
                || this.status == DocumentStatus.EMBEDDING;
    }

    public boolean isParseable() {
        return this.status == DocumentStatus.PENDING_PARSE
                || this.status == DocumentStatus.FAILED;
    }

    public boolean isSearchable() {
        return this.status == DocumentStatus.PARSED;
    }

    public boolean isDeprecated() {
        return this.status == DocumentStatus.DEPRECATED;
    }

    public boolean isFailed() {
        return this.status == DocumentStatus.FAILED;
    }
}
