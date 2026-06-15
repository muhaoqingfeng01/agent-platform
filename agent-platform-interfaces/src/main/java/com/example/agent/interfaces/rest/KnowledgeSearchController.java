package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.HybridSearchApplicationService;
import com.example.agent.application.knowledge.PrecisionConfigApplicationService;
import com.example.agent.application.knowledge.dto.HitRecordDTO;
import com.example.agent.application.knowledge.dto.SearchResultDTO;
import com.example.agent.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识检索 Controller — 混合检索 + 命中记录 + 人工标注.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识检索", description = "混合检索 + 命中记录 + 人工标注")
public class KnowledgeSearchController {

    private final HybridSearchApplicationService searchService;
    private final PrecisionConfigApplicationService precisionService;

    @PostMapping("/search")
    @SaCheckPermission("kb:search")
    @Operation(summary = "混合检索（向量 + 关键词 + RRF 融合）")
    public Result<SearchResultDTO> search(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        String knowledgeId = (String) request.get("knowledgeId");
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) request.get("searchConfig");
        return Result.ok(searchService.search(query, knowledgeId, config));
    }

    @GetMapping("/hits")
    @SaCheckPermission("kb:read")
    @Operation(summary = "命中记录列表")
    public Result<List<HitRecordDTO>> listHits(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(searchService.listHits(conversationId, page, size));
    }

    @PostMapping("/hits/{id}/feedback")
    @SaCheckPermission("kb:update")
    @Operation(summary = "人工标注（EXCELLENT/NEEDS_FIX/SUPPLEMENT）")
    public Result<Void> feedback(@PathVariable Long id, @RequestBody Map<String, String> request) {
        searchService.recordFeedback(id, request.get("feedback"), request.get("note"));
        return Result.ok();
    }

    @GetMapping("/precision-strategies")
    @SaCheckPermission("kb:read")
    @Operation(summary = "查询可用检索策略预设列表")
    public Result<List<Map<String, Object>>> listStrategies() {
        return Result.ok(precisionService.listStrategyPresets());
    }
}
