package com.example.agent.application.optimization;

import com.example.agent.application.optimization.dto.*;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.common.util.TimeConverters;
import com.example.agent.domain.optimization.entity.OptimizationTicket;
import com.example.agent.domain.optimization.repository.OptimizationTicketRepository;
import com.example.agent.domain.optimization.valueobject.TicketStatus;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationTicketService {

    private final OptimizationTicketRepository ticketRepository;

    public OptimizationTicketResponse getByTicketId(String ticketId) {
        OptimizationTicket ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket == null) throw new RuntimeException("工单不存在: " + ticketId);
        return toResponse(ticket);
    }

    public List<OptimizationTicketResponse> list(Long tenantId, int page, int size) {
        return ticketRepository.findByTenant(tenantId, page, size).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OptimizationTicketResponse> listByStatus(Long tenantId, String status, int page, int size) {
        return ticketRepository.findByStatus(tenantId, status, page, size).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public OptimizationTicketResponse assign(String ticketId, String assignee) {
        OptimizationTicket ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket == null) throw new RuntimeException("工单不存在: " + ticketId);
        ticket.assign(assignee);
        ticketRepository.updateById(ticket);
        log.info("[Optimization] 工单已指派: ticketId={}, assignee={}", ticketId, assignee);
        return toResponse(ticket);
    }

    @Transactional
    public OptimizationTicketResponse updateStatus(String ticketId, String newStatus) {
        OptimizationTicket ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket == null) throw new RuntimeException("工单不存在: " + ticketId);

        TicketStatus current = TicketStatus.fromCode(ticket.getStatus());
        TicketStatus target = TicketStatus.fromCode(newStatus);
        if (!current.canTransitionTo(target)) {
            throw new IllegalArgumentException("状态流转不允许: " + current + " → " + target);
        }

        ticket = ticket.toBuilder().status(target.name()).build();
        ticketRepository.updateById(ticket);
        log.info("[Optimization] 工单状态更新: ticketId={}, {} → {}", ticketId, current, target);
        return toResponse(ticket);
    }

    @Transactional
    public OptimizationTicketResponse resolve(String ticketId, String resolution, String resolutionType) {
        OptimizationTicket ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket == null) throw new RuntimeException("工单不存在: " + ticketId);

        ticket.resolve(resolution, resolutionType);
        ticketRepository.updateById(ticket);
        log.info("[Optimization] 工单已解决: ticketId={}, type={}", ticketId, resolutionType);
        return toResponse(ticket);
    }

    public FeedbackStatsResponse getFeedbackStats(int days) {
        Long tenantId = TenantContext.getCurrentTenantId();
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        long total = ticketRepository.countCreatedSince(tenantId, since);
        long resolved = ticketRepository.countByStatusAndSince(tenantId, "RESOLVED", since);
        double resolveRate = total > 0 ? (double) resolved / total * 100 : 0;

        return FeedbackStatsResponse.builder()
                .totalTickets(total).resolvedTickets(resolved)
                .resolveRate(String.format("%.1f%%", resolveRate))
                .periodDays(days).build();
    }

    private OptimizationTicketResponse toResponse(OptimizationTicket ticket) {
        return OptimizationTicketResponse.builder()
                .ticketId(ticket.getTicketId()).conversationId(ticket.getConversationId())
                .messageId(ticket.getMessageId()).issueType(ticket.getIssueType())
                .severity(ticket.getSeverity()).description(ticket.getDescription())
                .assignee(ticket.getAssignee()).status(ticket.getStatus())
                .resolution(ticket.getResolution()).resolutionType(ticket.getResolutionType())
                .createdAt(TimeConverters.toEpochMilli(ticket.getCreatedAt())).updatedAt(TimeConverters.toEpochMilli(ticket.getUpdatedAt())).build();
    }
}
