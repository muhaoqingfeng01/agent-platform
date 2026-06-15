package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.DocumentApplicationService;
import com.example.agent.application.knowledge.dto.DocumentDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.domain.knowledge.entity.DocumentChunk;
import com.example.agent.interfaces.dto.request.knowledge.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 文档管理 Controller — 上传/查询/下载/删除.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "文档管理", description = "文档上传/查询/状态/下载/删除")
public class DocumentController {

    private final DocumentApplicationService docService;

    @PostMapping("/knowledge-bases/{knowledgeId}/documents")
    @SaCheckPermission("doc:upload")
    @Operation(summary = "上传文档（multipart/form-data，自动存 MinIO）")
    public Result<DocumentDTO> upload(
            @PathVariable String knowledgeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "chunk_strategy", required = false) String chunkStrategy,
            @RequestParam(value = "chunk_size", required = false) Integer chunkSize,
            @RequestParam(value = "chunk_overlap", required = false) Integer chunkOverlap,
            @RequestParam(value = "chunk_config", required = false) String chunkConfig) {

        DocumentDTO doc = docService.uploadFile(knowledgeId, file, chunkStrategy, chunkConfig);
        return Result.ok(doc);
    }

    @GetMapping("/documents")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文档列表（按知识库）")
    public Result<PageResponse<DocumentDTO>> list(
            @RequestParam String knowledgeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(docService.listByKnowledgeId(knowledgeId, page, size));
    }

    @GetMapping("/documents/{documentId}")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文档详情")
    public Result<DocumentDTO> getById(@PathVariable String documentId) {
        return Result.ok(docService.getDocument(documentId));
    }

    @GetMapping("/documents/{documentId}/status")
    @SaCheckPermission("doc:read")
    @Operation(summary = "查询文档处理状态")
    public Result<String> getStatus(@PathVariable String documentId) {
        return Result.ok(docService.getDocumentStatus(documentId).name());
    }

    @GetMapping("/documents/{documentId}/download")
    @SaCheckPermission("doc:read")
    @Operation(summary = "下载原始文档（MinIO 流式代理）")
    public void download(@PathVariable String documentId, HttpServletResponse response) throws IOException {
        DocumentDTO doc = docService.getDocument(documentId);

        response.setContentType("application/octet-stream");
        String encodedFilename = URLEncoder.encode(doc.getFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFilename);

        try (InputStream is = docService.downloadDocumentFile(documentId)) {
            is.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }

    @GetMapping("/documents/{documentId}/chunks")
    @SaCheckPermission("doc:read")
    @Operation(summary = "查询文档切片列表")
    public Result<Map<String, Object>> listChunks(
            @PathVariable String documentId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit) {
        List<DocumentChunk> chunks = docService.listChunks(documentId, offset, limit);
        int total = docService.countChunks(documentId);
        return Result.ok(Map.of("total", total, "chunks", chunks));
    }

    @PutMapping("/documents/{documentId}/precision-override")
    @SaCheckPermission("doc:update")
    @Operation(summary = "设置文档级精度参数覆盖")
    public Result<Void> setPrecisionOverride(@PathVariable String documentId,
                                              @RequestBody SetPrecisionOverrideRequest request) {
        docService.setPrecisionOverride(documentId,
                request.getSearchParamsOverrideJson(), request.getMultiStageOverrideJson());
        return Result.ok();
    }

    @DeleteMapping("/documents/{documentId}")
    @SaCheckPermission("doc:delete")
    @Operation(summary = "删除文档（含 MinIO + Milvus + MySQL，KB 创建者权限）")
    public Result<Void> delete(@PathVariable String documentId) {
        docService.deleteDocument(documentId);
        return Result.ok();
    }

    // ========== V1.4.0 新增: 解析触发 + 弃用 ==========

    @PostMapping("/documents/{documentId}/parse")
    @SaCheckPermission("doc:update")
    @Operation(summary = "手动触发文档异步解析（PENDING_PARSE/FAILED → 异步管线）")
    public Result<DocumentDTO> triggerParse(@PathVariable String documentId) {
        return Result.ok(docService.triggerParse(documentId));
    }

    @PostMapping("/knowledge-bases/{knowledgeId}/documents/batch-parse")
    @SaCheckPermission("doc:update")
    @Operation(summary = "批量触发文档解析")
    public Result<Map<String, Object>> batchParse(@PathVariable String knowledgeId,
                                                   @RequestBody Map<String, List<String>> body) {
        List<String> documentIds = body.getOrDefault("documentIds", List.of());
        int triggered = docService.batchTriggerParse(knowledgeId, documentIds);
        return Result.ok(Map.of("triggered", triggered, "total", documentIds.size()));
    }

    @PostMapping("/documents/{documentId}/deprecate")
    @SaCheckPermission("doc:update")
    @Operation(summary = "弃用文档（删除 Milvus 向量，保留 MinIO 文件和元数据）")
    public Result<DocumentDTO> deprecate(@PathVariable String documentId) {
        return Result.ok(docService.deprecateDocument(documentId));
    }
}
