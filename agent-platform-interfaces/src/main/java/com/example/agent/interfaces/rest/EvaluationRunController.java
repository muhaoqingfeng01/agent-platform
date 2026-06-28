package com.example.agent.interfaces.rest;

import com.example.agent.application.evaluation.EvaluationRunService;
import com.example.agent.application.evaluation.dto.EvaluationRunResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.evaluation.DatasetGetRequest;
import com.example.agent.interfaces.dto.request.evaluation.EvaluationGetRequest;
import com.example.agent.interfaces.dto.request.evaluation.EvaluationListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping("/run")
    @Operation(summary = "执行评测（LLM-as-Judge）")
    public Result<EvaluationRunResponse> execute(@Valid @RequestBody DatasetGetRequest request) {
        return Result.ok(runService.execute(request.getDatasetId()));
    }

    @PostMapping("/get")
    @Operation(summary = "评测结果详情")
    public Result<EvaluationRunResponse> get(@Valid @RequestBody EvaluationGetRequest request) {
        return Result.ok(runService.getByEvaluationId(request.getEvaluationId()));
    }

    @PostMapping("/list")
    @Operation(summary = "评测历史列表")
    public Result<List<EvaluationRunResponse>> list(@RequestBody EvaluationListRequest request) {
        return Result.ok(runService.list(request.getPage(), request.getSize()));
    }
}
