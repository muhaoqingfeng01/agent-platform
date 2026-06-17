package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.evaluation.GridSearchOptimizer;
import com.example.agent.application.evaluation.RetrievalPrecisionService;
import com.example.agent.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 检索精度监控 Controller — 纯 HTTP 适配层.
 *
 * <p>提供检索质量可度量的 API：
 * <ul>
 *   <li>运行精度评估（recall@5 / MRR / NDCG@5 / Hit Rate）</li>
 *   <li>Grid Search 自动调优</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge-bases/{kbId}/precision")
@RequiredArgsConstructor
@Tag(name = "精度监控", description = "检索质量评估与自动调优")
public class PrecisionController {

    private final RetrievalPrecisionService precisionService;
    private final GridSearchOptimizer gridSearchOptimizer;

    /**
     * 运行精度评估.
     */
    @PostMapping("/evaluate")
    @SaCheckPermission("kb:read")
    @Operation(summary = "运行精度评估")
    public Result<RetrievalPrecisionService.PrecisionReport> evaluate(
            @Parameter(description = "知识库 ID") @PathVariable String kbId,
            @Parameter(description = "评测数据集 ID") @RequestParam String datasetId) {
        return Result.ok(precisionService.evaluate(kbId, datasetId));
    }

    /**
     * 触发 Grid Search 自动调优.
     */
    @PostMapping("/optimize")
    @SaCheckPermission("kb:read")
    @Operation(summary = "Grid Search 自动调优")
    public Result<GridSearchOptimizer.OptimizationResult> optimize(
            @Parameter(description = "知识库 ID") @PathVariable String kbId,
            @Parameter(description = "评测数据集 ID") @RequestParam String datasetId) {
        return Result.ok(gridSearchOptimizer.optimize(kbId, datasetId));
    }
}
