package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.DocumentApplicationService;
import com.example.agent.application.knowledge.dto.DocumentDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.ResultCheckHelper;
import com.example.agent.common.result.Result;
import com.example.agent.domain.knowledge.entity.DocumentChunk;
import com.example.agent.interfaces.dto.request.document.DocumentListRequest;
import com.example.agent.interfaces.dto.request.document.DocumentGetRequest;
import com.example.agent.interfaces.dto.request.document.DocumentUploadRequest;
import com.example.agent.interfaces.dto.request.document.DocumentDownloadRequest;
import com.example.agent.interfaces.dto.request.document.DocumentListChunksRequest;
import com.example.agent.interfaces.dto.request.document.DocumentPrecisionOverrideRequest;
import com.example.agent.interfaces.dto.request.document.DocumentDeleteRequest;
import com.example.agent.interfaces.dto.request.document.DocumentParseRequest;
import com.example.agent.interfaces.dto.request.document.DocumentBatchParseRequest;
import com.example.agent.interfaces.dto.request.document.DocumentDeprecateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

    @PostMapping("/knowledge-bases/documents/upload")
    @SaCheckPermission("doc:upload")
    @Operation(summary = "上传文档（multipart/form-data，自动存 MinIO）")
    public Result<DocumentDTO> upload(
            @RequestPart("metadata") @Valid DocumentUploadRequest request,
            @RequestPart("file") MultipartFile file) {

        return ResultCheckHelper.wrap(() -> {
            DocumentDTO doc = docService.uploadFile(request.getKnowledgeId(), file, request.getChunkStrategy(), request.getChunkConfig());
            return Result.ok(doc);
        }, "文档上传", file.getOriginalFilename());
    }

    @PostMapping("/documents/list")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文档列表（按知识库）")
    public Result<PageResponse<DocumentDTO>> list(@RequestBody DocumentListRequest request) {
        return Result.ok(docService.listByKnowledgeId(request.getKnowledgeId(), request.getPage(), request.getSize()));
    }

    @PostMapping("/documents/get")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文档详情")
    public Result<DocumentDTO> getById(@Valid @RequestBody DocumentGetRequest request) {
        return Result.ok(docService.getDocument(request.getDocumentId()));
    }

    @PostMapping("/documents/status")
    @SaCheckPermission("doc:read")
    @Operation(summary = "查询文档处理状态")
    public Result<String> getStatus(@Valid @RequestBody DocumentGetRequest request) {
        return Result.ok(docService.getDocumentStatus(request.getDocumentId()).name());
    }

    @PostMapping("/documents/download")
    @SaCheckPermission("doc:read")
    @Operation(summary = "下载原始文档（MinIO 流式代理）")
    public void download(@Valid @RequestBody DocumentDownloadRequest request, HttpServletResponse response) throws IOException {
        DocumentDTO doc = docService.getDocument(request.getDocumentId());

        response.setContentType("application/octet-stream");
        String encodedFilename = URLEncoder.encode(doc.getFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFilename);

        try (InputStream is = docService.downloadDocumentFile(request.getDocumentId())) {
            is.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }

    @PostMapping("/documents/chunks")
    @SaCheckPermission("doc:read")
    @Operation(summary = "查询文档切片列表")
    public Result<Map<String, Object>> listChunks(@Valid @RequestBody DocumentListChunksRequest request) {
        List<DocumentChunk> chunks = docService.listChunks(request.getDocumentId(), request.getOffset(), request.getLimit());
        int total = docService.countChunks(request.getDocumentId());
        return Result.ok(Map.of("total", total, "chunks", chunks));
    }

    @PostMapping("/documents/precision-override")
    @SaCheckPermission("doc:update")
    @Operation(summary = "设置文档级精度参数覆盖")
    public Result<Void> setPrecisionOverride(@Valid @RequestBody DocumentPrecisionOverrideRequest request) {
        docService.setPrecisionOverride(request.getDocumentId(),
                request.getSearchParamsOverrideJson(), request.getMultiStageOverrideJson());
        return Result.ok();
    }

    @PostMapping("/documents/delete")
    @SaCheckPermission("doc:delete")
    @Operation(summary = "删除文档（含 MinIO + Milvus + MySQL，KB 创建者权限）")
    public Result<Void> delete(@Valid @RequestBody DocumentDeleteRequest request) {
        docService.deleteDocument(request.getDocumentId());
        return Result.ok();
    }

    @PostMapping("/documents/parse")
    @SaCheckPermission("doc:update")
    @Operation(summary = "手动触发文档异步解析（PENDING_PARSE/FAILED → 异步管线）")
    public Result<DocumentDTO> triggerParse(@Valid @RequestBody DocumentParseRequest request) {
        return Result.ok(docService.triggerParse(request.getDocumentId()));
    }

    @PostMapping("/documents/batch-parse")
    @SaCheckPermission("doc:update")
    @Operation(summary = "批量触发文档解析")
    public Result<Map<String, Object>> batchParse(@Valid @RequestBody DocumentBatchParseRequest request) {
        List<String> documentIds = request.getDocumentIds() != null ? request.getDocumentIds() : List.of();
        int triggered = docService.batchTriggerParse(request.getKnowledgeId(), documentIds);
        return Result.ok(Map.of("triggered", triggered, "total", documentIds.size()));
    }

    @PostMapping("/documents/deprecate")
    @SaCheckPermission("doc:update")
    @Operation(summary = "弃用文档（删除 Milvus 向量，保留 MinIO 文件和元数据）")
    public Result<DocumentDTO> deprecate(@Valid @RequestBody DocumentDeprecateRequest request) {
        return Result.ok(docService.deprecateDocument(request.getDocumentId()));
    }
}
