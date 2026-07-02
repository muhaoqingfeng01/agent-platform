package com.example.agent.infrastructure.persistence.mapper;

import com.example.agent.infrastructure.persistence.po.AsyncTaskPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 异步任务 MyBatis Mapper.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Mapper
public interface AsyncTaskMapper {

    int insert(AsyncTaskPO po);

    int update(AsyncTaskPO po);

    int updateStatusIfExpected(@Param("taskId") String taskId,
                               @Param("expected") String expected,
                               @Param("target") String target,
                               @Param("errorMessage") String errorMessage,
                               @Param("resultJson") String resultJson);

    int incrementRetryAndReset(@Param("taskId") String taskId,
                                @Param("submittedCode") String submittedCode);

    AsyncTaskPO selectByTaskId(@Param("taskId") String taskId);

    List<AsyncTaskPO> selectTimeoutRunning(@Param("now") LocalDateTime now,
                                            @Param("runningCode") String runningCode,
                                            @Param("limit") int limit);

    List<AsyncTaskPO> selectStaleSubmitted(@Param("before") LocalDateTime before,
                                            @Param("submittedCode") String submittedCode,
                                            @Param("limit") int limit);

    int countActiveByTypeAndBiz(@Param("taskType") String taskType,
                                 @Param("bizId") String bizId,
                                 @Param("activeCodes") List<String> activeCodes);
}
