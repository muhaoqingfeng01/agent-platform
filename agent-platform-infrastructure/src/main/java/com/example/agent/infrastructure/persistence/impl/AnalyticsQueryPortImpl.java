package com.example.agent.infrastructure.persistence.impl;

import com.example.agent.domain.analytics.AnalyticsQueryPort;
import com.example.agent.infrastructure.persistence.mapper.AnalyticsQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分析查询端口实现 — MyBatis 只读查询，不在策略层拼 SQL.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Repository
@RequiredArgsConstructor
public class AnalyticsQueryPortImpl implements AnalyticsQueryPort {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AnalyticsQueryMapper mapper;

    @Override
    public HitRateResult hitRate(Long tenantId, LocalDateTime from, LocalDateTime to) {
        String fromStr = format(from);
        String toStr = format(to);

        Map<String, Object> summary = mapper.selectHitRateSummary(tenantId, fromStr, toStr);
        long totalHits = toLong(summary, "totalHits");
        long usedCount = toLong(summary, "usedInPromptCount");
        BigDecimal usedRate = totalHits > 0
                ? BigDecimal.valueOf(usedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalHits), 2, RoundingMode.HALF_UP)
                : null;

        List<Map<String, Object>> dailyRows = mapper.selectHitRateDaily(tenantId, fromStr, toStr);
        List<DailyHitStat> daily = new ArrayList<>();
        if (dailyRows != null) {
            for (Map<String, Object> row : dailyRows) {
                daily.add(new DailyHitStat(
                        String.valueOf(row.get("day")),
                        toLong(row, "hitCount"),
                        toLong(row, "usedInPromptCount")));
            }
        }
        return new HitRateResult(totalHits, usedCount, usedRate, daily);
    }

    @Override
    public FeedbackDistResult feedbackDist(Long tenantId, LocalDateTime from, LocalDateTime to) {
        List<Map<String, Object>> rows = mapper.selectFeedbackDist(
                tenantId, format(from), format(to));
        List<FeedbackBucket> buckets = new ArrayList<>();
        long total = 0;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Object fb = row.get("feedback");
                long cnt = toLong(row, "cnt");
                total += cnt;
                buckets.add(new FeedbackBucket(fb != null ? fb.toString() : "UNKNOWN", cnt));
            }
        }
        return new FeedbackDistResult(total, buckets);
    }

    @Override
    public EvalOverallResult evalOverall(Long tenantId, LocalDateTime from, LocalDateTime to) {
        Map<String, Object> row = mapper.selectEvalOverall(tenantId, format(from), format(to));
        long runCount = toLong(row, "runCount");
        long completed = toLong(row, "completedCount");
        long failed = toLong(row, "failedCount");
        BigDecimal avg = toBigDecimal(row, "avgOverallScore");
        if (avg != null) {
            avg = avg.setScale(4, RoundingMode.HALF_UP);
        }
        return new EvalOverallResult(runCount, completed, failed, avg);
    }

    private static String format(LocalDateTime dt) {
        return dt.format(TS);
    }

    private static long toLong(Map<String, Object> map, String key) {
        if (map == null) {
            return 0L;
        }
        Object v = map.get(key);
        if (v == null) {
            // MySQL 驱动可能返回小写 key
            v = map.get(key.toLowerCase());
        }
        if (v == null) {
            return 0L;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static BigDecimal toBigDecimal(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object v = map.get(key);
        if (v == null) {
            v = map.get(key.toLowerCase());
        }
        if (v == null) {
            return null;
        }
        if (v instanceof BigDecimal bd) {
            return bd;
        }
        if (v instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        try {
            return new BigDecimal(v.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
