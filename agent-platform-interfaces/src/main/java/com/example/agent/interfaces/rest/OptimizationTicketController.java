package com.example.agent.interfaces.rest;

import com.example.agent.application.optimization.OptimizationTicketService;
import com.example.agent.application.optimization.dto.*;
import com.example.agent.common.helper.ResultRespHelper;
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
        return ResultRespHelper.responseInvoke("OptimizationTicketController.list", request, (req) ->
                ticketService.list(tenantId, req.getPage(), req.getSize()));
    }

    @PostMapping("/get")
    @Operation(summary = "工单详情")
    public Result<OptimizationTicketResponse> get(@Valid @RequestBody TicketGetRequest request) {
        return ResultRespHelper.responseInvoke("OptimizationTicketController.get", request, (req) ->
                ticketService.getByTicketId(req.getTicketId()));
    }

    @PostMapping("/assign")
    @Operation(summary = "指派处理人")
    public Result<OptimizationTicketResponse> assign(@Valid @RequestBody TicketAssignRequest request) {
        return ResultRespHelper.responseInvoke("OptimizationTicketController.assign", request, (req) ->
                ticketService.assign(req.getTicketId(), req.getAssignee()));
    }

    @PostMapping("/update-status")
    @Operation(summary = "更新工单状态")
    public Result<OptimizationTicketResponse> updateStatus(@Valid @RequestBody TicketUpdateStatusRequest request) {
        return ResultRespHelper.responseInvoke("OptimizationTicketController.updateStatus", request, (req) ->
                ticketService.updateStatus(req.getTicketId(), req.getStatus()));
    }

    @PostMapping("/resolve")
    @Operation(summary = "提交解决方案")
    public Result<OptimizationTicketResponse> resolve(@Valid @RequestBody TicketResolveRequest request) {
        return ResultRespHelper.responseInvoke("OptimizationTicketController.resolve", request, (req) ->
                ticketService.resolve(req.getTicketId(), req.getResolution(), req.getResolutionType()));
    }

    @PostMapping("/feedback/stats")
    @Operation(summary = "反馈统计面板")
    public Result<FeedbackStatsResponse> feedbackStats(@RequestBody TicketFeedbackStatsRequest request) {
        return ResultRespHelper.responseInvoke("OptimizationTicketController.feedbackStats", request, (req) ->
                ticketService.getFeedbackStats(req.getDays()));
    }
}
