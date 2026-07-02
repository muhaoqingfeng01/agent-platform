package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.HybridSearchApplicationService;
import com.example.agent.application.knowledge.PrecisionConfigApplicationService;
import com.example.agent.application.knowledge.dto.HitRecordDTO;
import com.example.agent.application.knowledge.dto.SearchResultDTO;
import com.example.agent.application.knowledge.dto.HitRecordListResponse;
import com.example.agent.application.knowledge.dto.StrategyPresetListResponse;
import com.example.agent.application.knowledge.dto.StrategyPresetResponse;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeHitFeedbackRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeSearchRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeListHitsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<SearchResultDTO> search(@RequestBody KnowledgeSearchRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeSearchController.search", request, (req) ->
                searchService.search(req.getQuery(), req.getKnowledgeId(), req.getSearchConfig()));
    }

    @PostMapping("/hits/list")
    @SaCheckPermission("kb:read")
    @Operation(summary = "命中记录列表")
    public Result<HitRecordListResponse> listHits(@RequestBody KnowledgeListHitsRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeSearchController.listHits", request, (req) ->
                HitRecordListResponse.builder()
                        .records(searchService.listHits(req.getConversationId(), req.getPage(), req.getSize()))
                        .build());
    }

    @PostMapping("/hits/feedback")
    @SaCheckPermission("kb:update")
    @Operation(summary = "人工标注（EXCELLENT/NEEDS_FIX/SUPPLEMENT）")
    public Result<Void> feedback(@Valid @RequestBody KnowledgeHitFeedbackRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeSearchController.feedback", request, (req) -> {
            searchService.recordFeedback(req.getId(), req.getFeedback(), req.getNote());
            return null;
        });
    }

    @PostMapping("/precision-strategies")
    @SaCheckPermission("kb:read")
    @Operation(summary = "查询可用检索策略预设列表")
    public Result<StrategyPresetListResponse> listStrategies() {
        return ResultRespHelper.responseInvoke("KnowledgeSearchController.listStrategies", null, (req) ->
                StrategyPresetListResponse.builder()
                        .records(precisionService.listStrategyPresets())
                        .build());
    }
}
