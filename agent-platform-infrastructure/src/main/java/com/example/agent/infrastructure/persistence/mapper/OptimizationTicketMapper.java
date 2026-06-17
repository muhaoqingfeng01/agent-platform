package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.OptimizationTicketPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OptimizationTicketMapper {

    int insert(OptimizationTicketPO po);

    int updateById(OptimizationTicketPO po);

    OptimizationTicketPO selectByTicketId(@Param("ticketId") String ticketId);

    List<OptimizationTicketPO> selectByTenant(@Param("tenantId") String tenantId,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    List<OptimizationTicketPO> selectByAssignee(@Param("tenantId") String tenantId,
                                                 @Param("assignee") String assignee,
                                                 @Param("offset") int offset,
                                                 @Param("size") int size);

    List<OptimizationTicketPO> selectByStatus(@Param("tenantId") String tenantId,
                                               @Param("status") String status,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    long countByStatusAndResolvedSince(@Param("tenantId") String tenantId,
                                        @Param("status") String status,
                                        @Param("since") LocalDateTime since);

    long countCreatedSince(@Param("tenantId") String tenantId,
                            @Param("since") LocalDateTime since);
}
