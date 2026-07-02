package com.example.agent.interfaces.rest;

import com.example.agent.application.evaluation.EvaluationDatasetService;
import com.example.agent.application.evaluation.dto.*;
import com.example.agent.common.helper.ResultRespHelper;
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
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.create", request, (req) ->
                datasetService.create(req));
    }

    @PostMapping("/list")
    @Operation(summary = "数据集列表")
    public Result<DatasetListResponse> list(@RequestBody EvaluationListRequest request) {
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.list", request, (req) ->
                DatasetListResponse.builder()
                        .records(datasetService.list(req.getPage(), req.getSize()))
                        .build());
    }

    @PostMapping("/get")
    @Operation(summary = "数据集详情")
    public Result<DatasetResponse> get(@Valid @RequestBody DatasetGetRequest request) {
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.get", request, (req) ->
                datasetService.getByDatasetId(req.getDatasetId()));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除数据集")
    public Result<Void> delete(@Valid @RequestBody DatasetGetRequest request) {
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.delete", request, (req) -> {
            datasetService.delete(req.getDatasetId());
            return null;
        });
    }

    @PostMapping("/items/add")
    @Operation(summary = "添加样本")
    public Result<ItemResponse> addItem(@Valid @RequestBody DatasetAddItemRequest request) {
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.addItem", request, (req) ->
                datasetService.addItem(req.getDatasetId(), req.getItemRequest()));
    }

    @PostMapping("/items/list")
    @Operation(summary = "样本列表")
    public Result<ItemListResponse> listItems(@Valid @RequestBody DatasetGetRequest request) {
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.listItems", request, (req) ->
                ItemListResponse.builder()
                        .records(datasetService.listItems(req.getDatasetId()))
                        .build());
    }

    @PostMapping("/items/delete")
    @Operation(summary = "删除样本")
    public Result<Void> deleteItem(@Valid @RequestBody DatasetDeleteItemRequest request) {
        return ResultRespHelper.responseInvoke("EvaluationDatasetController.deleteItem", request, (req) -> {
            datasetService.deleteItem(req.getItemId());
            return null;
        });
    }
}
