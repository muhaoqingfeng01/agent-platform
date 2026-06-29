package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.DocumentApplicationService;
import com.example.agent.application.knowledge.dto.DocumentDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.ResultRespHelper;
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

        return ResultRespHelper.responseInvoke("DocumentController.upload", request, (req) ->
                docService.uploadFile(req.getKnowledgeId(), file, req.getChunkStrategy(), req.getChunkConfig()));
    }

    @PostMapping("/documents/list")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文档列表（按知识库）")
    public Result<PageResponse<DocumentDTO>> list(@RequestBody DocumentListRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.list", request, (req) ->
                docService.listByKnowledgeId(req.getKnowledgeId(), req.getPage(), req.getSize()));
    }

    @PostMapping("/documents/get")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文档详情")
    public Result<DocumentDTO> getById(@Valid @RequestBody DocumentGetRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.getById", request, (req) ->
                docService.getDocument(req.getDocumentId()));
    }

    @PostMapping("/documents/status")
    @SaCheckPermission("doc:read")
    @Operation(summary = "查询文档处理状态")
    public Result<String> getStatus(@Valid @RequestBody DocumentGetRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.getStatus", request, (req) ->
                docService.getDocumentStatus(req.getDocumentId()).name());
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
        return ResultRespHelper.responseInvoke("DocumentController.listChunks", request, (req) -> {
            List<DocumentChunk> chunks = docService.listChunks(req.getDocumentId(), req.getOffset(), req.getLimit());
            int total = docService.countChunks(req.getDocumentId());
            return Map.of("total", total, "chunks", chunks);
        });
    }

    @PostMapping("/documents/precision-override")
    @SaCheckPermission("doc:update")
    @Operation(summary = "设置文档级精度参数覆盖")
    public Result<Void> setPrecisionOverride(@Valid @RequestBody DocumentPrecisionOverrideRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.setPrecisionOverride", request, (req) -> {
            docService.setPrecisionOverride(req.getDocumentId(),
                    req.getSearchParamsOverrideJson(), req.getMultiStageOverrideJson());
            return null;
        });
    }

    @PostMapping("/documents/delete")
    @SaCheckPermission("doc:delete")
    @Operation(summary = "删除文档（含 MinIO + Milvus + MySQL，KB 创建者权限）")
    public Result<Void> delete(@Valid @RequestBody DocumentDeleteRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.delete", request, (req) -> {
            docService.deleteDocument(req.getDocumentId());
            return null;
        });
    }

    @PostMapping("/documents/parse")
    @SaCheckPermission("doc:update")
    @Operation(summary = "手动触发文档异步解析（PENDING_PARSE/FAILED → 异步管线）")
    public Result<DocumentDTO> triggerParse(@Valid @RequestBody DocumentParseRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.triggerParse", request, (req) ->
                docService.triggerParse(req.getDocumentId()));
    }

    @PostMapping("/documents/batch-parse")
    @SaCheckPermission("doc:update")
    @Operation(summary = "批量触发文档解析")
    public Result<Map<String, Object>> batchParse(@Valid @RequestBody DocumentBatchParseRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.batchParse", request, (req) -> {
            List<String> documentIds = req.getDocumentIds() != null ? req.getDocumentIds() : List.of();
            int triggered = docService.batchTriggerParse(req.getKnowledgeId(), documentIds);
            return Map.of("triggered", triggered, "total", documentIds.size());
        });
    }

    @PostMapping("/documents/deprecate")
    @SaCheckPermission("doc:update")
    @Operation(summary = "弃用文档（删除 Milvus 向量，保留 MinIO 文件和元数据）")
    public Result<DocumentDTO> deprecate(@Valid @RequestBody DocumentDeprecateRequest request) {
        return ResultRespHelper.responseInvoke("DocumentController.deprecate", request, (req) ->
                docService.deprecateDocument(req.getDocumentId()));
    }
}
