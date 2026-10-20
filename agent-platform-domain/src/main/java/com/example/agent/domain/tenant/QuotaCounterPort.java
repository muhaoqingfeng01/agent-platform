package com.example.agent.domain.tenant;

import java.time.Duration;

/**
 * 日配额计数端口 — Infra 用 Redis INCR 实现，Domain 不依赖 Redis API.
 * <p>
 * 读取/递增失败必须抛业务异常，由调用方 fail-close。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
public interface QuotaCounterPort {

    /**
     * 读取当前计数；key 不存在返回 0.
     */
    long current(String key);

    /**
     * 递增并在 key 新建时设置 TTL（到下一个 UTC 日切）.
     *
     * @param key       Redis key
     * @param delta     增量（LLM 为 1，上传为字节数）
     * @param ttlIfNew  仅当本次递增创建了新 key 时设置
     * @return 递增后的值
     */
    long increment(String key, long delta, Duration ttlIfNew);
}
