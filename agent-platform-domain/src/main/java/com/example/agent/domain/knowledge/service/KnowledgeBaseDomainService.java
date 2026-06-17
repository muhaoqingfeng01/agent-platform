package com.example.agent.domain.knowledge.service;

import com.example.agent.domain.knowledge.entity.KnowledgeBase;
import com.example.agent.domain.knowledge.valueobject.ChunkStrategy;
import com.example.agent.domain.knowledge.valueobject.SearchStrategy;
import org.springframework.stereotype.Component;

/**
 * 知识库领域服务 — 业务不变量校验.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Component
public class KnowledgeBaseDomainService {

    private static final int MAX_DOCUMENT_COUNT = 1000;
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    /** 校验新建知识库不变量 */
    public void validateNewKnowledgeBase(KnowledgeBase kb) {
        if (kb == null) throw new IllegalArgumentException("知识库不能为空");
        if (kb.getName() == null || kb.getName().isBlank()) throw new IllegalArgumentException("知识库名称不能为空");
        if (kb.getTenantId() == null) throw new IllegalArgumentException("租户ID不能为空");
    }

    /** 校验租户隔离 */
    public void assertTenantAccess(KnowledgeBase kb, Long currentTenantId) {
        if (!kb.getTenantId().equals(currentTenantId)) {
            throw new SecurityException("无权访问其他租户的知识库");
        }
    }

    /** 校验文档数上限 */
    public void assertDocumentLimit(int currentCount) {
        if (currentCount >= MAX_DOCUMENT_COUNT) {
            throw new IllegalStateException("知识库文档数已达上限: " + MAX_DOCUMENT_COUNT);
        }
    }

    /** 校验文件大小 */
    public void assertFileSize(long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过上限 50MB: " + (fileSize / 1024 / 1024) + "MB");
        }
    }

    /** 根据文件类型兜底选择切片策略 */
    public ChunkStrategy resolveFallbackStrategy(String fileType) {
        if (fileType == null) return ChunkStrategy.RECURSIVE_CHAR_SPLIT;
        return switch (fileType.toUpperCase()) {
            case "PDF", "DOCX" -> ChunkStrategy.PARAGRAPH_SLIDING_WINDOW;
            case "MD" -> ChunkStrategy.MARKDOWN_HEADER_AWARE;
            case "HTML" -> ChunkStrategy.MARKDOWN_HEADER_AWARE;
            case "CSV" -> ChunkStrategy.FIXED_SIZE;
            case "TXT" -> ChunkStrategy.RECURSIVE_CHAR_SPLIT;
            default -> ChunkStrategy.RECURSIVE_CHAR_SPLIT;
        };
    }

    /** 根据文件类型兜底检索策略 */
    public SearchStrategy resolveFallbackSearchStrategy(KnowledgeBase kb) {
        String strategyCode = kb.getSearchStrategy();
        if (strategyCode == null || strategyCode.isBlank()) return SearchStrategy.BALANCED;
        return SearchStrategy.fromCode(strategyCode);
    }

    /** 校验知识库是否可启用（已删除的知识库不可启用） */
    public void assertCanEnable(KnowledgeBase kb) {
        if (kb.isDeleted()) {
            throw new IllegalStateException("已删除的知识库不可启用");
        }
    }

    /** ★ V1.4.0: 只有 DISABLED 状态可启用 */
    public void assertCanDisable(KnowledgeBase kb) {
        if (!kb.isEnabled()) {
            throw new IllegalStateException("仅已启用的知识库可弃用");
        }
    }

    /** ★ V1.4.0: 只有 DISABLED 状态可删除 */
    public void assertCanDelete(KnowledgeBase kb) {
        if (kb.isEnabled()) {
            throw new IllegalStateException("已启用的知识库不可删除，请先弃用");
        }
        if (kb.isDeleted()) {
            throw new IllegalStateException("知识库已删除");
        }
    }

    /** ★ V1.4.0: KB 必须是 ENABLED 才能上传/解析 */
    public void assertKbEnabled(KnowledgeBase kb) {
        if (!kb.isEnabled()) {
            throw new IllegalStateException("知识库已弃用/删除，不可操作");
        }
    }

    /** ★ V1.4.0: KB 不可为已删除 */
    public void assertKbNotDeleted(KnowledgeBase kb) {
        if (kb.isDeleted()) {
            throw new IllegalStateException("知识库已删除");
        }
    }

    /** 校验操作者是否为知识库创建者 */
    public void assertCreatorAccess(KnowledgeBase kb, String currentUserId) {
        if (!kb.isCreatedBy(currentUserId)) {
            throw new SecurityException("仅知识库创建者可执行此操作");
        }
    }

    public int getMaxDocumentCount() { return MAX_DOCUMENT_COUNT; }
    public long getMaxFileSize() { return MAX_FILE_SIZE; }
}
