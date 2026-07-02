package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.DocumentApplicationService;
import com.example.agent.application.knowledge.KnowledgeBaseApplicationService;
import com.example.agent.application.knowledge.dto.DocumentDTO;
import com.example.agent.application.knowledge.dto.KbFileListResponse;
import com.example.agent.application.knowledge.dto.KnowledgeBaseDTO;
import com.example.agent.application.knowledge.dto.KnowledgeBaseStatsResponse;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.filemgmt.FileListRequest;
import com.example.agent.interfaces.dto.request.filemgmt.FileSummaryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文件管理 Controller — 知识库文件管理视图.
 *
 * @author Agent Platform Team
 * @since 1.4.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge-bases")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "知识库文件管理列表 + 状态汇总")
public class FileManagementController {

    private final DocumentApplicationService docService;
    private final KnowledgeBaseApplicationService kbService;

    @PostMapping("/files/list")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文件管理列表（含状态标签、操作权限）")
    public Result<KbFileListResponse> listFiles(@Valid @RequestBody FileListRequest request) {
        return ResultRespHelper.responseInvoke("FileManagementController.listFiles", request, (req) -> {
            KnowledgeBaseDTO kb = kbService.getByKnowledgeId(req.getKnowledgeId());
            PageResponse<DocumentDTO> docs = docService.listByKnowledgeId(req.getKnowledgeId(), req.getPage(), req.getSize());

            return KbFileListResponse.builder()
                    .kbInfo(KbFileListResponse.KbInfo.builder()
                            .knowledgeId(kb.getKnowledgeId())
                            .name(kb.getName())
                            .status(kb.getStatus())
                            .documentCount(kb.getDocumentCount())
                            .build())
                    .documents(docs.getRecords())
                    .total(docs.getTotal())
                    .page(docs.getPage())
                    .size(docs.getSize())
                    .build();
        });
    }

    @PostMapping("/files/summary")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文件状态汇总")
    public Result<KnowledgeBaseStatsResponse> summary(@Valid @RequestBody FileSummaryRequest request) {
        return ResultRespHelper.responseInvoke("FileManagementController.summary", request, (req) ->
                kbService.getStats(req.getKnowledgeId()));
    }
}
