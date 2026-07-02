package com.example.agent.application.knowledge.task;

import com.example.agent.common.exception.TaskTimeoutException;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.service.DocumentLifecycleDomainService;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import com.example.agent.domain.task.service.TaskHandler;
import com.example.agent.domain.task.valueobject.TaskResult;
import com.example.agent.application.knowledge.pipeline.DocumentPipelineOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 文档解析 TaskHandler — 接入通用任务中心的桥梁.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentParseTaskHandler implements TaskHandler<DocumentParsePayload> {

    public static final String TASK_TYPE = "DOCUMENT_PARSE";
    /** 默认超时时间（秒） */
    private static final int TIMEOUT_SECONDS = 120;

    private final DocumentPipelineOrchestrator pipelineOrchestrator;
    private final DocumentRepository documentRepository;
    private final PlatformTransactionManager transactionManager;

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public int getTimeoutSeconds() {
        return TIMEOUT_SECONDS;
    }

    @Override
    public TaskResult execute(DocumentParsePayload payload, Long deadlineMs) {
        log.info("[DocParseHandler] 开始解析: docId={}, deadlineMs={}",
                payload.getDocumentId(), deadlineMs);

        // 先立即标记 PARSING（新事务，立即提交）
        updateDocumentStatusImmediate(payload.getDocumentId(), DocumentStatus.PARSING);

        // 委托给管线（带 deadline）
        pipelineOrchestrator.doProcessWithDeadline(payload.getDocumentId(), deadlineMs);

        log.info("[DocParseHandler] 解析完成: docId={}", payload.getDocumentId());
        return TaskResult.ok();
    }

    @Override
    public void onTimeout(DocumentParsePayload payload) {
        log.warn("[DocParseHandler] 解析超时: docId={}", payload.getDocumentId());
        updateDocumentStatusImmediate(payload.getDocumentId(), DocumentStatus.FAILED);
        updateDocumentErrorImmediate(payload.getDocumentId(), "解析超时（2分钟）");
    }

    @Override
    public void onSuccess(DocumentParsePayload payload, TaskResult result) {
        // pipeline 内部已完成状态更新，无需额外操作
    }

    @Override
    public void onFailure(DocumentParsePayload payload, Throwable error) {
        log.error("[DocParseHandler] 解析失败: docId={}", payload.getDocumentId(), error);
        updateDocumentStatusImmediate(payload.getDocumentId(), DocumentStatus.FAILED);
        updateDocumentErrorImmediate(payload.getDocumentId(),
                error.getMessage() != null ? error.getMessage() : "未知错误");
    }

    @Override
    public Class<DocumentParsePayload> getPayloadType() {
        return DocumentParsePayload.class;
    }

    // ==================== 内部工具 ====================

    private void updateDocumentStatusImmediate(String documentId, DocumentStatus status) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        var txStatus = transactionManager.getTransaction(def);
        try {
            documentRepository.updateStatus(documentId, status);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    private void updateDocumentErrorImmediate(String documentId, String errorMessage) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        var txStatus = transactionManager.getTransaction(def);
        try {
            documentRepository.updateErrorMessage(documentId, errorMessage);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }
}
