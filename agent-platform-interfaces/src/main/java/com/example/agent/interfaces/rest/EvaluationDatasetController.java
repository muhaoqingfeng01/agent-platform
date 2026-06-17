package com.example.agent.interfaces.rest;

import com.example.agent.application.evaluation.EvaluationDatasetService;
import com.example.agent.application.evaluation.dto.*;
import com.example.agent.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/evaluation/datasets")
@RequiredArgsConstructor
@Tag(name = "评测数据集", description = "评测数据集管理")
public class EvaluationDatasetController {

    private final EvaluationDatasetService datasetService;

    @PostMapping
    @Operation(summary = "创建评测数据集")
    public Result<DatasetResponse> create(@RequestBody @jakarta.validation.Valid CreateDatasetRequest request) {
        return Result.ok(datasetService.create(request));
    }

    @GetMapping
    @Operation(summary = "数据集列表")
    public Result<List<DatasetResponse>> list(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return Result.ok(datasetService.list(page, size));
    }

    @GetMapping("/{datasetId}")
    @Operation(summary = "数据集详情")
    public Result<DatasetResponse> get(@PathVariable String datasetId) {
        return Result.ok(datasetService.getByDatasetId(datasetId));
    }

    @DeleteMapping("/{datasetId}")
    @Operation(summary = "删除数据集")
    public Result<Void> delete(@PathVariable String datasetId) {
        datasetService.delete(datasetId);
        return Result.ok(null);
    }

    @PostMapping("/{datasetId}/items")
    @Operation(summary = "添加样本")
    public Result<ItemResponse> addItem(@PathVariable String datasetId,
                                         @RequestBody @jakarta.validation.Valid AddItemRequest request) {
        return Result.ok(datasetService.addItem(datasetId, request));
    }

    @GetMapping("/{datasetId}/items")
    @Operation(summary = "样本列表")
    public Result<List<ItemResponse>> listItems(@PathVariable String datasetId) {
        return Result.ok(datasetService.listItems(datasetId));
    }

    @DeleteMapping("/{datasetId}/items/{itemId}")
    @Operation(summary = "删除样本")
    public Result<Void> deleteItem(@PathVariable String datasetId,
                                    @PathVariable Long itemId) {
        datasetService.deleteItem(itemId);
        return Result.ok(null);
    }
}
