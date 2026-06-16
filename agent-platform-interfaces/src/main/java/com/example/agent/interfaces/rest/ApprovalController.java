package com.example.agent.interfaces.rest;

import com.example.agent.application.approval.ApprovalWorkflowApplicationService;
import com.example.agent.application.approval.dto.ApprovalWorkflowResponse;
import com.example.agent.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
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

    @GetMapping
    @Operation(summary = "审批列表（我的待审批/我已审批/我发起的）")
    public Result<List<ApprovalWorkflowResponse>> list(
            @RequestParam(defaultValue = "my-pending") String filter,
            @RequestParam(required = false) String approverId,
            @RequestParam(required = false) String requesterId,
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ApprovalWorkflowResponse> list = switch (filter) {
            case "my-pending" -> approvalService.listPendingByApprover(approverId, page, size);
            case "my-resolved" -> approvalService.listResolvedByApprover(approverId, page, size);
            case "my-requested" -> approvalService.listByRequester(requesterId, page, size);
            case "by-status" -> approvalService.listByStatus(status, page, size);
            default -> approvalService.listByTenant(page, size);
        };
        return Result.ok(list);
    }

    @GetMapping("/{approvalId}")
    @Operation(summary = "审批详情")
    public Result<ApprovalWorkflowResponse> getById(@PathVariable String approvalId) {
        return Result.ok(approvalService.getByApprovalId(approvalId));
    }

    @PostMapping("/{approvalId}/approve")
    @Operation(summary = "同意审批")
    public Result<ApprovalWorkflowResponse> approve(
            @PathVariable String approvalId,
            @Valid @RequestBody ApproveRequest request) {
        return Result.ok(approvalService.approve(approvalId, request.getComment()));
    }

    @PostMapping("/{approvalId}/reject")
    @Operation(summary = "拒绝审批")
    public Result<ApprovalWorkflowResponse> reject(
            @PathVariable String approvalId,
            @Valid @RequestBody RejectRequest request) {
        return Result.ok(approvalService.reject(approvalId, request.getReason()));
    }

    @GetMapping("/stats")
    @Operation(summary = "审批统计")
    public Result<Map<String, Object>> stats() {
        return Result.ok(approvalService.stats());
    }

    // ==================== 内嵌请求 DTO ====================

    @Data
    static class ApproveRequest {
        @Schema(description = "审批意见", example = "同意执行")
        private String comment;
    }

    @Data
    static class RejectRequest {
        @NotBlank(message = "拒绝原因不能为空")
        @Schema(description = "拒绝原因", example = "参数异常，拒绝执行")
        private String reason;
    }
}
