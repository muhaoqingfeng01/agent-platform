package com.example.agent.domain.optimization.repository;

import com.example.agent.domain.optimization.entity.OptimizationTicket;

import java.util.List;

/**
 * 优化工单仓储接口.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface OptimizationTicketRepository {

    void save(OptimizationTicket ticket);

    void updateById(OptimizationTicket ticket);

    OptimizationTicket findByTicketId(String ticketId);

    List<OptimizationTicket> findByTenant(String tenantId, int page, int size);

    List<OptimizationTicket> findByAssignee(String tenantId, String assignee, int page, int size);

    List<OptimizationTicket> findByStatus(String tenantId, String status, int page, int size);

    long countByStatusAndSince(String tenantId, String status, java.time.LocalDateTime since);

    long countCreatedSince(String tenantId, java.time.LocalDateTime since);
}
