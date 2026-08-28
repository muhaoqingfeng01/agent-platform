package com.example.agent.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 分析指标只读查询 Mapper — 仅供 {@code AnalyticsQueryPort} 使用.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Mapper
public interface AnalyticsQueryMapper {

    /**
     * 命中汇总：totalHits / usedInPromptCount.
     *
     * @return Map keys: totalHits, usedInPromptCount
     */
    Map<String, Object> selectHitRateSummary(@Param("tenantId") Long tenantId,
                                             @Param("from") String from,
                                             @Param("to") String to);

    /**
     * 按日命中趋势.
     *
     * @return 每行含 day / hitCount / usedInPromptCount
     */
    List<Map<String, Object>> selectHitRateDaily(@Param("tenantId") Long tenantId,
                                                  @Param("from") String from,
                                                  @Param("to") String to);

    /**
     * 消息反馈分布（按 feedback 分组）.
     *
     * @return 每行含 feedback / cnt
     */
    List<Map<String, Object>> selectFeedbackDist(@Param("tenantId") Long tenantId,
                                                  @Param("from") String from,
                                                  @Param("to") String to);

    /**
     * 评估 run 总体.
     *
     * @return Map keys: runCount, completedCount, failedCount, avgOverallScore
     */
    Map<String, Object> selectEvalOverall(@Param("tenantId") Long tenantId,
                                          @Param("from") String from,
                                          @Param("to") String to);
}
