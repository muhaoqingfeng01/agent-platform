package com.example.agent.interfaces.rest;

import com.example.agent.application.optimization.OptimizationTicketService;
import com.example.agent.application.optimization.dto.*;
import com.example.agent.common.result.Result;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.interfaces.dto.request.optimization.TicketListRequest;
import com.example.agent.interfaces.dto.request.optimization.TicketGetRequest;
import com.example.agent.interfaces.dto.request.optimization.TicketAssignRequest;
import com.example.agent.interfaces.dto.request.optimization.TicketUpdateStatusRequest;
import com.example.agent.interfaces.dto.request.optimization.TicketResolveRequest;
import com.example.agent.interfaces.dto.request.optimization.TicketFeedbackStatsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping("/list")
    @Operation(summary = "工单列表")
    public Result<List<OptimizationTicketResponse>> list(@RequestBody TicketListRequest request) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return Result.ok(ticketService.list(tenantId, request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @Operation(summary = "工单详情")
    public Result<OptimizationTicketResponse> get(@Valid @RequestBody TicketGetRequest request) {
        return Result.ok(ticketService.getByTicketId(request.getTicketId()));
    }

    @PostMapping("/assign")
    @Operation(summary = "指派处理人")
    public Result<OptimizationTicketResponse> assign(@Valid @RequestBody TicketAssignRequest request) {
        return Result.ok(ticketService.assign(request.getTicketId(), request.getAssignee()));
    }

    @PostMapping("/update-status")
    @Operation(summary = "更新工单状态")
    public Result<OptimizationTicketResponse> updateStatus(@Valid @RequestBody TicketUpdateStatusRequest request) {
        return Result.ok(ticketService.updateStatus(request.getTicketId(), request.getStatus()));
    }

    @PostMapping("/resolve")
    @Operation(summary = "提交解决方案")
    public Result<OptimizationTicketResponse> resolve(@Valid @RequestBody TicketResolveRequest request) {
        return Result.ok(ticketService.resolve(request.getTicketId(), request.getResolution(), request.getResolutionType()));
    }

    @PostMapping("/feedback/stats")
    @Operation(summary = "反馈统计面板")
    public Result<FeedbackStatsResponse> feedbackStats(@RequestBody TicketFeedbackStatsRequest request) {
        return Result.ok(ticketService.getFeedbackStats(request.getDays()));
    }
}
