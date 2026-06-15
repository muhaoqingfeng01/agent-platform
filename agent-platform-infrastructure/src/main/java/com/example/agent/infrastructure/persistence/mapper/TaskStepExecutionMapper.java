package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.TaskStepExecutionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 任务步骤执行 MyBatis Mapper — 单表查询，禁止 JOIN.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Mapper
public interface TaskStepExecutionMapper {

    int insert(TaskStepExecutionPO po);

    int batchInsert(@Param("list") List<TaskStepExecutionPO> list);

    int updateStatus(@Param("executionId") String executionId,
                     @Param("stepId") String stepId,
                     @Param("status") String status);

    int updateResult(@Param("executionId") String executionId,
                     @Param("stepId") String stepId,
                     @Param("status") String status,
                     @Param("outputJson") String outputJson,
                     @Param("errorMessage") String errorMessage,
                     @Param("durationMs") long durationMs);

    int updateRetry(@Param("executionId") String executionId,
                    @Param("stepId") String stepId,
                    @Param("retryCount") int retryCount,
                    @Param("status") String status);

    Optional<TaskStepExecutionPO> selectByExecIdAndStepId(@Param("executionId") String executionId,
                                                           @Param("stepId") String stepId);

    List<TaskStepExecutionPO> selectByExecutionId(@Param("executionId") String executionId);

    int batchUpdateStatusByExecutionId(@Param("executionId") String executionId,
                                       @Param("status") String status);
}
