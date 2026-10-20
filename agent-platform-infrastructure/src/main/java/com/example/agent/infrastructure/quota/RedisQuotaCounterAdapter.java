package com.example.agent.infrastructure.quota;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tenant.QuotaCounterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis 日配额计数 — INCR + 新建 key 时设置 TTL.
 * <p>
 * 读写失败 fail-close：抛 429，禁止在计数不可用时放行调用。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisQuotaCounterAdapter implements QuotaCounterPort {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long current(String key) {
        try {
            String val = stringRedisTemplate.opsForValue().get(key);
            if (val == null || val.isBlank()) {
                return 0L;
            }
            return Long.parseLong(val);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Quota] Redis GET 失败（fail-close）: key={}", key, e);
            throw new BusinessException(429, "配额服务暂不可用，已拒绝请求");
        }
    }

    @Override
    public long increment(String key, long delta, Duration ttlIfNew) {
        try {
            Long after = stringRedisTemplate.opsForValue().increment(key, delta);
            if (after == null) {
                throw new BusinessException(429, "配额计数失败，已拒绝请求");
            }
            if (after == delta && ttlIfNew != null && !ttlIfNew.isNegative() && !ttlIfNew.isZero()) {
                stringRedisTemplate.expire(key, ttlIfNew);
            }
            return after;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Quota] Redis INCR 失败（fail-close）: key={}", key, e);
            throw new BusinessException(429, "配额服务暂不可用，已拒绝请求");
        }
    }
}
