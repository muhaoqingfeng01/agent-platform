package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.optimization.entity.OptimizationTicket;
import com.example.agent.domain.optimization.repository.OptimizationTicketRepository;
import com.example.agent.infrastructure.persistence.mapper.OptimizationTicketMapper;
import com.example.agent.infrastructure.persistence.po.OptimizationTicketPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class OptimizationTicketRepositoryImpl implements OptimizationTicketRepository {

    private final OptimizationTicketMapper mapper;

    @Override
    public void save(OptimizationTicket ticket) {
        mapper.insert(toPO(ticket));
    }

    @Override
    public void updateById(OptimizationTicket ticket) {
        mapper.updateById(toPO(ticket));
    }

    @Override
    public OptimizationTicket findByTicketId(String ticketId) {
        OptimizationTicketPO po = mapper.selectByTicketId(ticketId);
        return po != null ? toDomain(po) : null;
    }

    @Override
    public List<OptimizationTicket> findByTenant(String tenantId, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.selectByTenant(tenantId, offset, size).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<OptimizationTicket> findByAssignee(String tenantId, String assignee, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.selectByAssignee(tenantId, assignee, offset, size).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<OptimizationTicket> findByStatus(String tenantId, String status, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.selectByStatus(tenantId, status, offset, size).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByStatusAndSince(String tenantId, String status, LocalDateTime since) {
        return mapper.countByStatusAndResolvedSince(tenantId, status, since);
    }

    @Override
    public long countCreatedSince(String tenantId, LocalDateTime since) {
        return mapper.countCreatedSince(tenantId, since);
    }

    private OptimizationTicket toDomain(OptimizationTicketPO po) {
        return OptimizationTicket.builder()
                .id(po.getId()).tenantId(po.getTenantId()).ticketId(po.getTicketId())
                .conversationId(po.getConversationId()).messageId(po.getMessageId())
                .issueType(po.getIssueType()).severity(po.getSeverity()).description(po.getDescription())
                .assignee(po.getAssignee()).status(po.getStatus())
                .resolution(po.getResolution()).resolutionType(po.getResolutionType())
                .createdAt(po.getCreatedAt()).updatedAt(po.getUpdatedAt()).build();
    }

    private OptimizationTicketPO toPO(OptimizationTicket ticket) {
        return OptimizationTicketPO.builder()
                .id(ticket.getId()).tenantId(ticket.getTenantId()).ticketId(ticket.getTicketId())
                .conversationId(ticket.getConversationId()).messageId(ticket.getMessageId())
                .issueType(ticket.getIssueType()).severity(ticket.getSeverity()).description(ticket.getDescription())
                .assignee(ticket.getAssignee()).status(ticket.getStatus())
                .resolution(ticket.getResolution()).resolutionType(ticket.getResolutionType())
                .createdAt(ticket.getCreatedAt()).updatedAt(ticket.getUpdatedAt()).build();
    }
}
