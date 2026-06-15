package com.example.agent.application.knowledge;

import com.example.agent.application.knowledge.dto.*;
import com.example.agent.application.knowledge.pipeline.DocumentPipelineOrchestrator;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.entity.DocumentChunk;
import com.example.agent.domain.knowledge.repository.DocumentChunkRepository;
import com.example.agent.domain.knowledge.repository.DocumentRepository;
import com.example.agent.domain.knowledge.repository.KnowledgeBaseRepository;
import com.example.agent.domain.knowledge.service.DocumentLifecycleDomainService;
import com.example.agent.domain.knowledge.service.KnowledgeBaseDomainService;
import com.example.agent.domain.knowledge.service.MilvusStoreClient;
import com.example.agent.domain.knowledge.valueobject.DocumentStatus;
import com.example.agent.infrastructure.context.TenantContext;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

/**
 * 文档应用服务 — 上传/查询/删除（含 MinIO 对象存储集成）.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentApplicationService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final KnowledgeBaseRepository kbRepository;
    private final KnowledgeBaseDomainService domainService;
    private final DocumentLifecycleDomainService lifecycleService;
    private final DocumentPipelineOrchestrator pipelineOrchestrator;
    private final MilvusStoreClient milvusStore;
    private final MinioClient minioClient;

    private static final String MINIO_BUCKET = "knowledge-docs";

    /**
     * 上传文档（含 MinIO 存储）— Controller 传入 MultipartFile，此处处理完整流程.
     */
    @Transactional
    public DocumentDTO uploadFile(String knowledgeId, MultipartFile file,
                                  String chunkStrategy, String chunkConfigJson) {
        // 校验知识库
        var kb = kbRepository.findByKnowledgeId(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库", knowledgeId));
        domainService.assertTenantAccess(kb, TenantContext.getCurrentTenantId());
        domainService.assertDocumentLimit(kb.getDocumentCount());

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        String fileType = getFileType(filename);
        long fileSize = file.getSize();
        domainService.assertFileSize(fileSize);

        String documentId = IdGenerator.generate("doc");
        String minioPath = knowledgeId + "/" + documentId + "/" + filename;

        // 1. 上传到 MinIO
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(MINIO_BUCKET)
                    .object(minioPath)
                    .stream(file.getInputStream(), fileSize, -1)
                    .contentType(resolveMimeType(fileType))
                    .build());
            log.info("[MinIO] 文件上传成功: bucket={}, path={}, size={}", MINIO_BUCKET, minioPath, fileSize);
        } catch (Exception e) {
            throw new BusinessException(500, "文件上传到 MinIO 失败: " + filename, e);
        }

        // 2. 计算 contentHash
        String contentHash = computeSha256(file);

        // 3. 写入 t_document
        Document doc = Document.builder()
                .documentId(documentId)
                .tenantId(TenantContext.getCurrentTenantId())
                .knowledgeId(knowledgeId)
                .filename(filename)
                .fileType(fileType)
                .fileSize(fileSize)
                .minioPath(minioPath)
                .contentHash(contentHash)
                .chunkCount(0)
                .status(DocumentStatus.PENDING_PARSE)
                .uploadedBy(TenantContext.getCurrentUserId())
                .uploadedAt(LocalDateTime.now())
                .chunkStrategy(chunkStrategy)
                .chunkConfigJson(chunkConfigJson)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        documentRepository.save(doc);

        // ★ V1.4.0: 不再自动触发解析，用户需手动调用 POST /documents/{id}/parse
        // pipelineOrchestrator.processAsync(documentId);

        log.info("[Document] 文档上传完成（待手动触发解析）: docId={}, kbId={}, filename={}, size={}",
                documentId, knowledgeId, filename, fileSize);
        return DocumentDTO.from(doc);
    }

    /**
     * 下载文档 — 返回 MinIO InputStream.
     */
    public InputStream downloadDocumentFile(String documentId) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));

        // V1.4.0: 允许 PENDING_PARSE/PARSED/DEPRECATED 状态下载（文件在 MinIO 中）
        if (doc.getStatus() == DocumentStatus.PARSING
            || doc.getStatus() == DocumentStatus.CHUNKING
            || doc.getStatus() == DocumentStatus.EMBEDDING) {
            throw new IllegalStateException("文档正在处理中，请稍后下载，当前状态: " + doc.getStatus());
        }

        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(MINIO_BUCKET)
                    .object(doc.getMinioPath())
                    .build());
        } catch (Exception e) {
            throw new BusinessException(500, "从 MinIO 下载文件失败: " + doc.getFilename(), e);
        }
    }

    public DocumentDTO getDocument(String documentId) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));
        return DocumentDTO.from(doc);
    }

    public PageResponse<DocumentDTO> listByKnowledgeId(String knowledgeId, int page, int size) {
        List<Document> docs = documentRepository.findByKnowledgeId(knowledgeId, page, size);
        long total = documentRepository.countByKnowledgeId(knowledgeId);
        return PageResponse.of(docs.stream().map(DocumentDTO::from).toList(), total, page, size);
    }

    public DocumentStatus getDocumentStatus(String documentId) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));
        return doc.getStatus();
    }

    public List<DocumentChunk> listChunks(String documentId, int offset, int limit) {
        return chunkRepository.findByDocumentId(documentId, offset, limit);
    }

    public int countChunks(String documentId) {
        return chunkRepository.countByDocumentId(documentId);
    }

    // ========== V1.4.0 新增: 解析触发 + 弃用 + 级联删除 ==========

    /**
     * 手动触发异步解析.
     */
    @Transactional
    public DocumentDTO triggerParse(String documentId) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));
        lifecycleService.assertCanParse(doc);

        // 校验 KB 状态
        var kb = kbRepository.findByKnowledgeId(doc.getKnowledgeId())
                .orElseThrow(() -> new ResourceNotFoundException("知识库", doc.getKnowledgeId()));
        domainService.assertKbEnabled(kb);

        // 重置为 PENDING_PARSE 然后触发
        doc.updateStatus(DocumentStatus.PENDING_PARSE);
        documentRepository.update(doc);

        pipelineOrchestrator.processAsync(documentId);
        log.info("[Document] 手动触发解析: docId={}", documentId);
        return DocumentDTO.from(doc);
    }

    /**
     * 批量触发异步解析.
     */
    @Transactional
    public int batchTriggerParse(String knowledgeId, List<String> documentIds) {
        int triggered = 0;
        for (String docId : documentIds) {
            try {
                Document doc = documentRepository.findByDocumentId(docId).orElse(null);
                if (doc != null && doc.getKnowledgeId().equals(knowledgeId) && doc.isParseable()) {
                    doc.updateStatus(DocumentStatus.PENDING_PARSE);
                    documentRepository.update(doc);
                    pipelineOrchestrator.processAsync(docId);
                    triggered++;
                }
            } catch (Exception e) {
                log.warn("[Document] 批量解析跳过: docId={}, reason={}", docId, e.getMessage());
            }
        }
        log.info("[Document] 批量触发解析完成: kbId={}, triggered={}/{}", knowledgeId, triggered, documentIds.size());
        return triggered;
    }

    /**
     * 弃用文档 — 删除 Milvus 向量，保留元数据和 MinIO 文件.
     */
    @Transactional
    public DocumentDTO deprecateDocument(String documentId) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));
        lifecycleService.assertCanDeprecate(doc);

        String tenantId = TenantContext.getCurrentTenantId();
        String collectionName = "kb_" + tenantId;

        // 删除 Milvus 向量
        if (doc.isSearchable()) {
            milvusStore.deleteByDocumentId(collectionName, documentId);
        }

        // 软删除 MySQL 切片
        chunkRepository.softDeleteByDocumentId(documentId);

        // 更新 KB 文档计数
        if (doc.isSearchable()) {
            kbRepository.decrementDocumentCount(doc.getKnowledgeId());
        }

        // 标记为弃用
        doc.markDeprecated();
        documentRepository.update(doc);

        log.info("[Document] 文档已弃用: docId={}, milvus cleared", documentId);
        return DocumentDTO.from(doc);
    }

    @Transactional
    public void deleteDocument(String documentId) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));

        // ★ V1.4.0: KB 级创建者权限
        var kb = kbRepository.findByKnowledgeId(doc.getKnowledgeId())
                .orElseThrow(() -> new ResourceNotFoundException("知识库", doc.getKnowledgeId()));
        domainService.assertCreatorAccess(kb, TenantContext.getCurrentUserId());

        // PARSED 必须先弃用
        lifecycleService.assertCanDelete(doc);

        String tenantId = TenantContext.getCurrentTenantId();
        String collectionName = "kb_" + tenantId;

        // 若已弃用，向量已删除；若 PARSED 则需删除（正常流程不应到此处）
        if (doc.isSearchable()) {
            milvusStore.deleteByDocumentId(collectionName, documentId);
            kbRepository.decrementDocumentCount(doc.getKnowledgeId());
        }

        // 删除 MinIO 文件
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(MINIO_BUCKET)
                    .object(doc.getMinioPath())
                    .build());
        } catch (Exception e) {
            log.warn("[MinIO] 文件删除失败: path={}", doc.getMinioPath(), e);
        }

        // 删除切片 + 文档
        chunkRepository.deleteByDocumentId(documentId);
        documentRepository.softDelete(documentId);

        log.info("[Document] 文档已删除: docId={}, kbId={}", documentId, doc.getKnowledgeId());
    }

    @Transactional
    public void setPrecisionOverride(String documentId, String searchParamsOverrideJson,
                                     String multiStageOverrideJson) {
        Document doc = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("文档", documentId));
        Document updated = doc.toBuilder()
                .searchParamsOverrideJson(searchParamsOverrideJson)
                .multiStageOverrideJson(multiStageOverrideJson)
                .updatedAt(LocalDateTime.now())
                .build();
        documentRepository.update(updated);
    }

    // ========== 内部工具方法 ==========

    private String getFileType(String filename) {
        if (filename == null) return "UNKNOWN";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".docx") || lower.endsWith(".doc")) return "DOCX";
        if (lower.endsWith(".txt")) return "TXT";
        if (lower.endsWith(".md")) return "MD";
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "HTML";
        if (lower.endsWith(".csv")) return "CSV";
        return "UNKNOWN";
    }

    private String resolveMimeType(String fileType) {
        return switch (fileType) {
            case "PDF" -> "application/pdf";
            case "DOCX" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "TXT" -> "text/plain";
            case "MD" -> "text/markdown";
            case "HTML" -> "text/html";
            case "CSV" -> "text/csv";
            default -> "application/octet-stream";
        };
    }

    private String computeSha256(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.warn("[Document] SHA256 计算失败: {}", e.getMessage());
            return "sha256_error_" + System.currentTimeMillis();
        }
    }
}
