package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.ApprovalWorkflowPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批工单 MyBatis Mapper — 对应 t_approval_workflow 表.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface ApprovalWorkflowMapper {

    int insert(ApprovalWorkflowPO po);

    int update(ApprovalWorkflowPO po);

    ApprovalWorkflowPO selectByApprovalId(@Param("approvalId") String approvalId);

    List<ApprovalWorkflowPO> selectByTenant(@Param("tenantId") String tenantId,
                                             @Param("offset") int offset,
                                             @Param("size") int size);

    long countByTenant(@Param("tenantId") String tenantId);

    List<ApprovalWorkflowPO> selectByRequester(@Param("requesterId") String requesterId,
                                                @Param("offset") int offset,
                                                @Param("size") int size);

    List<ApprovalWorkflowPO> selectByApprover(@Param("approverId") String approverId,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    /** 查询超时的待审批工单 */
    List<ApprovalWorkflowPO> selectTimeoutPending(@Param("now") LocalDateTime now);

    long countByStatus(@Param("tenantId") String tenantId, @Param("status") String status);

    List<ApprovalWorkflowPO> selectByTenantAndStatus(@Param("tenantId") String tenantId,
                                                      @Param("status") String status,
                                                      @Param("offset") int offset,
                                                      @Param("size") int size);
}
