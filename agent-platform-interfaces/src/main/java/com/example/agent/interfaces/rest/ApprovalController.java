package com.example.agent.interfaces.rest;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
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
        List<ApprovalWorkflowResponse> list = switch (request.getFilter()) {
            case "my-pending" -> approvalService.listPendingByApprover(request.getApproverId(), request.getPage(), request.getSize());
            case "my-resolved" -> approvalService.listResolvedByApprover(request.getApproverId(), request.getPage(), request.getSize());
            case "my-requested" -> approvalService.listByRequester(request.getRequesterId(), request.getPage(), request.getSize());
            case "by-status" -> approvalService.listByStatus(request.getStatus(), request.getPage(), request.getSize());
            default -> approvalService.listByTenant(request.getPage(), request.getSize());
        };
        return Result.ok(list);
    }

    @PostMapping("/get")
    @Operation(summary = "审批详情")
    public Result<ApprovalWorkflowResponse> getById(@Valid @RequestBody ApprovalGetRequest request) {
        return Result.ok(approvalService.getByApprovalId(request.getApprovalId()));
    }

    @PostMapping("/approve")
    @Operation(summary = "同意审批")
    public Result<ApprovalWorkflowResponse> approve(@Valid @RequestBody ApprovalApproveRequest request) {
        return Result.ok(approvalService.approve(request.getApprovalId(), request.getComment()));
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝审批")
    public Result<ApprovalWorkflowResponse> reject(@Valid @RequestBody ApprovalRejectRequest request) {
        return Result.ok(approvalService.reject(request.getApprovalId(), request.getReason()));
    }

    @PostMapping("/stats")
    @Operation(summary = "审批统计")
    public Result<Map<String, Object>> stats() {
        return Result.ok(approvalService.stats());
    }
}
