package com.example.agent.domain.tenant.service;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tenant.QuotaCounterPort;
import com.example.agent.domain.tenant.TenantModeQuota;
import com.example.agent.domain.tenant.TenantModeQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 租户配额守卫 — 模式开关与日限额校验.
 * <p>
 * <b>时区</b>：日配额按 <b>UTC</b> 自然日切分（key 中 yyyyMMdd），不使用租户本地时区。<br>
 * <b>计数策略</b>：一期在调用<b>成功后再 INCR</b>（可被并发刷高，二期改预扣）。
 * 入口只做 GET 比较，超限 429；模式关闭 403。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantQuotaDomainService {

    public static final ZoneOffset QUOTA_ZONE = ZoneOffset.UTC;
    private static final DateTimeFormatter DAY = DateTimeFormatter.BASIC_ISO_DATE;

    private final TenantModeQuotaRepository quotaRepository;
    private final QuotaCounterPort quotaCounterPort;

    /**
     * 模式关闭则 403；无配置行视为开通.
     */
    public void assertModeEnabled(Long tenantId, String modeCode) {
        TenantModeQuota quota = loadOrDefault(tenantId, modeCode);
        if (!quota.isEnabled()) {
            throw new BusinessException(403, "租户未开通该交互模式: " + modeCode);
        }
    }

    public boolean isModeEnabled(Long tenantId, String modeCode) {
        return loadOrDefault(tenantId, modeCode).isEnabled();
    }

    /**
     * 校验当日 LLM 次数未超限（不递增）.
     */
    public void assertLlmQuota(Long tenantId, String modeCode) {
        TenantModeQuota quota = loadOrDefault(tenantId, modeCode);
        Integer limit = quota.getDailyLlmLimit();
        if (limit == null) {
            return;
        }
        if (limit <= 0) {
            throw new BusinessException(429, "该模式每日 LLM 配额为 0，禁止调用: " + modeCode);
        }
        long used = quotaCounterPort.current(llmKey(tenantId, modeCode));
        if (used >= limit) {
            throw new BusinessException(429,
                    "该模式今日 LLM 调用已达上限 " + limit + " 次（UTC 日切）: " + modeCode);
        }
    }

    /**
     * 成功调用后再计数（一期宽松策略）.
     */
    public void recordLlmSuccess(Long tenantId, String modeCode) {
        try {
            quotaCounterPort.increment(llmKey(tenantId, modeCode), 1, ttlUntilNextUtcMidnight());
        } catch (Exception e) {
            log.warn("[Quota] 记录 LLM 成功次数失败（不影响本次结果）: tenant={}, mode={}",
                    tenantId, modeCode, e);
        }
    }

    /**
     * 校验当日上传字节未超限（不递增）.
     * <p>
     * 限额取该租户所有模式行 {@code daily_upload_bytes} 的最小值（更严）；全为 NULL 则不限制。
     */
    public void assertUploadQuota(Long tenantId, long incomingBytes) {
        Long limit = resolveUploadLimit(tenantId);
        if (limit == null) {
            return;
        }
        if (limit <= 0) {
            throw new BusinessException(429, "租户每日上传配额为 0，禁止上传");
        }
        if (incomingBytes <= 0) {
            return;
        }
        long used = quotaCounterPort.current(uploadKey(tenantId));
        if (used + incomingBytes > limit) {
            throw new BusinessException(429,
                    "今日上传将超过配额上限 " + limit + " 字节（UTC 日切，已用 " + used + "）");
        }
    }

    public void recordUploadSuccess(Long tenantId, long bytes) {
        if (bytes <= 0) {
            return;
        }
        try {
            quotaCounterPort.increment(uploadKey(tenantId), bytes, ttlUntilNextUtcMidnight());
        } catch (Exception e) {
            log.warn("[Quota] 记录上传字节失败（不影响本次结果）: tenant={}, bytes={}",
                    tenantId, bytes, e);
        }
    }

    public TenantModeQuota loadOrDefault(Long tenantId, String modeCode) {
        return quotaRepository.findByTenantIdAndModeCode(tenantId, modeCode)
                .orElseGet(() -> TenantModeQuota.defaultEnabled(tenantId, modeCode));
    }

    public Long resolveUploadLimit(Long tenantId) {
        List<TenantModeQuota> rows = quotaRepository.findByTenantId(tenantId);
        return rows.stream()
                .map(TenantModeQuota::getDailyUploadBytes)
                .filter(Objects::nonNull)
                .min(Long::compareTo)
                .orElse(null);
    }

    public long currentLlmUsed(Long tenantId, String modeCode) {
        try {
            return quotaCounterPort.current(llmKey(tenantId, modeCode));
        } catch (Exception e) {
            log.warn("[Quota] 读取 LLM 计数失败: tenant={}, mode={}", tenantId, modeCode, e);
            return 0L;
        }
    }

    public long currentUploadUsed(Long tenantId) {
        try {
            return quotaCounterPort.current(uploadKey(tenantId));
        } catch (Exception e) {
            log.warn("[Quota] 读取上传计数失败: tenant={}", tenantId, e);
            return 0L;
        }
    }

    public static String utcDay() {
        return LocalDate.now(QUOTA_ZONE).format(DAY);
    }

    public static Duration ttlUntilNextUtcMidnight() {
        var now = java.time.Instant.now();
        var next = LocalDate.now(QUOTA_ZONE).plusDays(1).atStartOfDay().toInstant(QUOTA_ZONE);
        Duration ttl = Duration.between(now, next);
        return ttl.isNegative() || ttl.isZero() ? Duration.ofDays(1) : ttl;
    }

    public static String llmKey(Long tenantId, String modeCode) {
        return "quota:llm:" + tenantId + ":" + modeCode + ":" + utcDay();
    }

    public static String uploadKey(Long tenantId) {
        return "quota:upload:" + tenantId + ":" + utcDay();
    }
}
