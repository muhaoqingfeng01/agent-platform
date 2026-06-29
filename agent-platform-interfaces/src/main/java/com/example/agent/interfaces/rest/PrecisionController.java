package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.evaluation.GridSearchOptimizer;
import com.example.agent.application.evaluation.RetrievalPrecisionService;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.precision.PrecisionEvaluateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 检索精度监控 Controller — 纯 HTTP 适配层.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge-bases/precision")
@RequiredArgsConstructor
@Tag(name = "精度监控", description = "检索质量评估与自动调优")
public class PrecisionController {

    private final RetrievalPrecisionService precisionService;
    private final GridSearchOptimizer gridSearchOptimizer;

    @PostMapping("/evaluate")
    @SaCheckPermission("kb:read")
    @Operation(summary = "运行精度评估")
    public Result<RetrievalPrecisionService.PrecisionReport> evaluate(
            @Valid @RequestBody PrecisionEvaluateRequest request) {
        return ResultRespHelper.responseInvoke("PrecisionController.evaluate", request, (req) ->
                precisionService.evaluate(req.getKbId(), req.getDatasetId()));
    }

    @PostMapping("/optimize")
    @SaCheckPermission("kb:read")
    @Operation(summary = "Grid Search 自动调优")
    public Result<GridSearchOptimizer.OptimizationResult> optimize(
            @Valid @RequestBody PrecisionEvaluateRequest request) {
        return ResultRespHelper.responseInvoke("PrecisionController.optimize", request, (req) ->
                gridSearchOptimizer.optimize(req.getKbId(), req.getDatasetId()));
    }
}
