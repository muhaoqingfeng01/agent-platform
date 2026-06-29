package com.example.agent.interfaces.rest;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.approval.ApprovalListRequest;
import com.example.agent.interfaces.dto.request.approval.ApprovalGetRequest;
import com.example.agent.interfaces.dto.request.approval.ApprovalApproveRequest;
import com.example.agent.interfaces.dto.request.approval.ApprovalRejectRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 审批工单管理控制器 — 人机协同审批的 REST API.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
@Tag(name = "人机协同审批", description = "高风险工具调用的审批工单管理")
public class ApprovalController {

    private final ApprovalWorkflowApplicationService approvalService;

    @PostMapping("/list")
    @Operation(summary = "审批列表（我的待审批/我已审批/我发起的）")
    public Result<List<ApprovalWorkflowResponse>> list(@RequestBody ApprovalListRequest request) {
        return ResultRespHelper.responseInvoke("ApprovalController.list", request, (req) -> {
            return switch (req.getFilter()) {
                case "my-pending" -> approvalService.listPendingByApprover(req.getApproverId(), req.getPage(), req.getSize());
                case "my-resolved" -> approvalService.listResolvedByApprover(req.getApproverId(), req.getPage(), req.getSize());
                case "my-requested" -> approvalService.listByRequester(req.getRequesterId(), req.getPage(), req.getSize());
                case "by-status" -> approvalService.listByStatus(req.getStatus(), req.getPage(), req.getSize());
                default -> approvalService.listByTenant(req.getPage(), req.getSize());
            };
        });
    }

    @PostMapping("/get")
    @Operation(summary = "审批详情")
    public Result<ApprovalWorkflowResponse> getById(@Valid @RequestBody ApprovalGetRequest request) {
        return ResultRespHelper.responseInvoke("ApprovalController.getById", request, (req) ->
                approvalService.getByApprovalId(req.getApprovalId()));
    }

    @PostMapping("/approve")
    @Operation(summary = "同意审批")
    public Result<ApprovalWorkflowResponse> approve(@Valid @RequestBody ApprovalApproveRequest request) {
        return ResultRespHelper.responseInvoke("ApprovalController.approve", request, (req) ->
                approvalService.approve(req.getApprovalId(), req.getComment()));
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝审批")
    public Result<ApprovalWorkflowResponse> reject(@Valid @RequestBody ApprovalRejectRequest request) {
        return ResultRespHelper.responseInvoke("ApprovalController.reject", request, (req) ->
                approvalService.reject(req.getApprovalId(), req.getReason()));
    }

    @PostMapping("/stats")
    @Operation(summary = "审批统计")
    public Result<Map<String, Object>> stats() {
        return ResultRespHelper.responseInvoke("ApprovalController.stats", null, (req) ->
                approvalService.stats());
    }
}
