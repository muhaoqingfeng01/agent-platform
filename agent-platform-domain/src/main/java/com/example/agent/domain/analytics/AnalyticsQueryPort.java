package com.example.agent.domain.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分析查询端口 — 只读查询本平台可观测/评估指标，禁止策略层拼 SQL.
 * <p>
 * 覆盖：知识库命中、消息反馈分布、评估 run 总体表现。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
public interface AnalyticsQueryPort {

    /**
     * 知识库命中率/命中量（按日趋势 + 区间汇总）.
     *
     * @param tenantId 租户 ID
     * @param from     区间起点（含）
     * @param to       区间终点（含）
     */
    HitRateResult hitRate(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * 消息反馈分布（LIKE / DISLIKE 等）.
     */
    FeedbackDistResult feedbackDist(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * 评估 run 总体表现（次数、平均分、状态分布）.
     */
    EvalOverallResult evalOverall(Long tenantId, LocalDateTime from, LocalDateTime to);

    // ==================== 结果类型 ====================

    /** 单日命中统计 */
    record DailyHitStat(String day, long hitCount, long usedInPromptCount) {}

    /**
     * 命中率结果.
     * <p>
     * {@code usedRate} = usedInPromptCount / hitCount（无命中时为 null，避免编造 0%）.
     */
    record HitRateResult(long totalHits, long usedInPromptCount, BigDecimal usedRate,
                         List<DailyHitStat> daily) {
        public boolean isEmpty() {
            return totalHits <= 0;
        }
    }

    /** 单类反馈计数 */
    record FeedbackBucket(String feedback, long count) {}

    /** 反馈分布结果 */
    record FeedbackDistResult(long total, List<FeedbackBucket> buckets) {
        public boolean isEmpty() {
            return total <= 0;
        }
    }

    /** 评估总体结果 */
    record EvalOverallResult(long runCount, long completedCount, long failedCount,
                             BigDecimal avgOverallScore) {
        public boolean isEmpty() {
            return runCount <= 0;
        }
    }
}
