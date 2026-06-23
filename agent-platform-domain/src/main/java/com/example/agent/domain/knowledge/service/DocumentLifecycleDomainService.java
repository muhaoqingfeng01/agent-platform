package com.example.agent.domain.knowledge.service;

import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import org.springframework.stereotype.Component;

/**
 * 文档生命周期领域服务 — 状态转换校验.
 *
 * @author Agent Platform Team
 * @since 1.4.0
 */
@Component
public class DocumentLifecycleDomainService {

    /** 仅 PENDING_PARSE / FAILED 可触发解析 */
    public void assertCanParse(Document doc) {
        if (doc == null) throw new IllegalArgumentException("文档不存在");
        if (doc.getStatus() != DocumentStatus.PENDING_PARSE && doc.getStatus() != DocumentStatus.FAILED) {
            throw new IllegalStateException(
                "仅待解析或失败的文档可触发解析，当前状态: " + doc.getStatus().getDesc());
        }
    }

    /** 仅 PARSED / FAILED 可弃用（PARSED 需先删除向量） */
    public void assertCanDeprecate(Document doc) {
        if (doc == null) throw new IllegalArgumentException("文档不存在");
        if (doc.getStatus() != DocumentStatus.PARSED && doc.getStatus() != DocumentStatus.FAILED) {
            throw new IllegalStateException(
                "仅已解析或失败的文档可弃用，当前状态: " + doc.getStatus().getDesc());
        }
    }

    /** 仅 PENDING_PARSE / FAILED / DEPRECATED 可删除（PARSED 必须先弃用再删除） */
    public void assertCanDelete(Document doc) {
        if (doc == null) throw new IllegalArgumentException("文档不存在");
        if (doc.getStatus() == DocumentStatus.PARSING
            || doc.getStatus() == DocumentStatus.CHUNKING
            || doc.getStatus() == DocumentStatus.EMBEDDING) {
            throw new IllegalStateException(
                "文档正在处理中，不可删除，当前状态: " + doc.getStatus().getDesc());
        }
        if (doc.getStatus() == DocumentStatus.PARSED) {
            throw new IllegalStateException(
                "已解析的文档必须先弃用（删除向量）再删除，当前状态: " + doc.getStatus().getDesc());
        }
    }

    /** 检查 KB 是否允许文档操作 */
    public void assertDocumentOperationAllowed(Document doc, boolean kbEnabled) {
        if (!kbEnabled) {
            throw new IllegalStateException("知识库已弃用/删除，不可操作文档");
        }
    }
}
