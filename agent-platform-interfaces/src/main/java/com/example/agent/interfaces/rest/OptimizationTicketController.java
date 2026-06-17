package com.example.agent.interfaces.rest;

import com.example.agent.application.optimization.OptimizationTicketService;
import com.example.agent.application.optimization.dto.*;
import com.example.agent.common.result.Result;
import com.example.agent.infrastructure.context.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/optimization-tickets")
@RequiredArgsConstructor
@Tag(name = "优化工单", description = "BadCase 闭环与优化工单管理")
public class OptimizationTicketController {

    private final OptimizationTicketService ticketService;

    @GetMapping
    @Operation(summary = "工单列表")
    public Result<List<OptimizationTicketResponse>> list(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return Result.ok(ticketService.list(tenantId, page, size));
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "工单详情")
    public Result<OptimizationTicketResponse> get(@PathVariable String ticketId) {
        return Result.ok(ticketService.getByTicketId(ticketId));
    }

    @PatchMapping("/{ticketId}/assign")
    @Operation(summary = "指派处理人")
    public Result<OptimizationTicketResponse> assign(@PathVariable String ticketId,
                                                      @RequestBody @jakarta.validation.Valid AssignRequest request) {
        return Result.ok(ticketService.assign(ticketId, request.getAssignee()));
    }

    @PatchMapping("/{ticketId}/status")
    @Operation(summary = "更新工单状态")
    public Result<OptimizationTicketResponse> updateStatus(@PathVariable String ticketId,
                                                            @RequestParam String status) {
        return Result.ok(ticketService.updateStatus(ticketId, status));
    }

    @PostMapping("/{ticketId}/resolve")
    @Operation(summary = "提交解决方案")
    public Result<OptimizationTicketResponse> resolve(@PathVariable String ticketId,
                                                       @RequestBody @jakarta.validation.Valid ResolveRequest request) {
        return Result.ok(ticketService.resolve(ticketId, request.getResolution(), request.getResolutionType()));
    }

    @GetMapping("/feedback/stats")
    @Operation(summary = "反馈统计面板")
    public Result<FeedbackStatsResponse> feedbackStats(@RequestParam(defaultValue = "7") int days) {
        return Result.ok(ticketService.getFeedbackStats(days));
    }
}
