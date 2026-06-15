package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.DocumentApplicationService;
import com.example.agent.application.knowledge.KnowledgeBaseApplicationService;
import com.example.agent.application.knowledge.dto.DocumentDTO;
import com.example.agent.application.knowledge.dto.KnowledgeBaseDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{knowledgeId}/files")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文件管理列表（含状态标签、操作权限）")
    public Result<Map<String, Object>> listFiles(
            @PathVariable String knowledgeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        KnowledgeBaseDTO kb = kbService.getByKnowledgeId(knowledgeId);
        PageResponse<DocumentDTO> docs = docService.listByKnowledgeId(knowledgeId, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kbInfo", Map.of(
                "knowledgeId", kb.getKnowledgeId(),
                "name", kb.getName(),
                "status", kb.getStatus(),
                "documentCount", kb.getDocumentCount()
        ));
        result.put("documents", docs.getRecords());
        result.put("total", docs.getTotal());
        result.put("page", docs.getPage());
        result.put("size", docs.getSize());

        return Result.ok(result);
    }

    @GetMapping("/{knowledgeId}/files/summary")
    @SaCheckPermission("doc:read")
    @Operation(summary = "文件状态汇总")
    public Result<Map<String, Long>> summary(@PathVariable String knowledgeId) {
        return Result.ok(kbService.getStats(knowledgeId));
    }
}
