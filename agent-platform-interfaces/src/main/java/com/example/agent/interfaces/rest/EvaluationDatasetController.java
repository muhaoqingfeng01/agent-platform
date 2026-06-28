package com.example.agent.interfaces.rest;

import com.example.agent.application.evaluation.EvaluationDatasetService;
import com.example.agent.application.evaluation.dto.*;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.evaluation.EvaluationListRequest;
import com.example.agent.interfaces.dto.request.evaluation.DatasetGetRequest;
import com.example.agent.interfaces.dto.request.evaluation.DatasetAddItemRequest;
import com.example.agent.interfaces.dto.request.evaluation.DatasetDeleteItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping("/create")
    @Operation(summary = "创建评测数据集")
    public Result<DatasetResponse> create(@Valid @RequestBody DatasetCreateCommand request) {
        return Result.ok(datasetService.create(request));
    }

    @PostMapping("/list")
    @Operation(summary = "数据集列表")
    public Result<List<DatasetResponse>> list(@RequestBody EvaluationListRequest request) {
        return Result.ok(datasetService.list(request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @Operation(summary = "数据集详情")
    public Result<DatasetResponse> get(@Valid @RequestBody DatasetGetRequest request) {
        return Result.ok(datasetService.getByDatasetId(request.getDatasetId()));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除数据集")
    public Result<Void> delete(@Valid @RequestBody DatasetGetRequest request) {
        datasetService.delete(request.getDatasetId());
        return Result.ok(null);
    }

    @PostMapping("/items/add")
    @Operation(summary = "添加样本")
    public Result<ItemResponse> addItem(@Valid @RequestBody DatasetAddItemRequest request) {
        return Result.ok(datasetService.addItem(request.getDatasetId(), request.getItemRequest()));
    }

    @PostMapping("/items/list")
    @Operation(summary = "样本列表")
    public Result<List<ItemResponse>> listItems(@Valid @RequestBody DatasetGetRequest request) {
        return Result.ok(datasetService.listItems(request.getDatasetId()));
    }

    @PostMapping("/items/delete")
    @Operation(summary = "删除样本")
    public Result<Void> deleteItem(@Valid @RequestBody DatasetDeleteItemRequest request) {
        datasetService.deleteItem(request.getItemId());
        return Result.ok(null);
    }
}
