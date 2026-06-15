package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.TaskExecutionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 任务执行 MyBatis Mapper — 单表查询，禁止 JOIN.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface TaskExecutionMapper {

    int insert(TaskExecutionPO po);

    int update(TaskExecutionPO po);

    int updateStatus(@Param("executionId") String executionId, @Param("status") String status);

    int updateProgress(@Param("executionId") String executionId,
                       @Param("completedSteps") int completedSteps);

    int markFailed(@Param("executionId") String executionId,
                   @Param("failedStepId") String failedStepId,
                   @Param("errorMessage") String errorMessage);

    Optional<TaskExecutionPO> selectByExecutionId(@Param("executionId") String executionId);

    List<TaskExecutionPO> selectByConversationId(@Param("conversationId") String conversationId);

    List<TaskExecutionPO> selectByAgentId(@Param("agentId") String agentId,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    long countByAgentId(@Param("agentId") String agentId);

    List<TaskExecutionPO> selectActiveByTenant(@Param("tenantId") String tenantId);
}
