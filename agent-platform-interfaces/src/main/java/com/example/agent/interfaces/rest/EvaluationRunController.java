package com.example.agent.interfaces.rest;

import com.example.agent.application.evaluation.EvaluationRunService;
import com.example.agent.application.evaluation.dto.EvaluationRunResponse;
import com.example.agent.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/evaluation")
@RequiredArgsConstructor
@Tag(name = "评测执行", description = "离线评估与 LLM-as-Judge")
public class EvaluationRunController {

    private final EvaluationRunService runService;

    @PostMapping("/datasets/{datasetId}/run")
    @Operation(summary = "执行评测（LLM-as-Judge）")
    public Result<EvaluationRunResponse> execute(@PathVariable String datasetId) {
        return Result.ok(runService.execute(datasetId));
    }

    @GetMapping("/{evaluationId}")
    @Operation(summary = "评测结果详情")
    public Result<EvaluationRunResponse> get(@PathVariable String evaluationId) {
        return Result.ok(runService.getByEvaluationId(evaluationId));
    }

    @GetMapping
    @Operation(summary = "评测历史列表")
    public Result<List<EvaluationRunResponse>> list(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return Result.ok(runService.list(page, size));
    }
}
