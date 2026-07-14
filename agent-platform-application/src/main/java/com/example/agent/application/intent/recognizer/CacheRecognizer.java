package com.example.agent.application.intent.recognizer;

import com.example.agent.application.intent.model.IntentResult;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * 缓存识别器 — Decorator 模式为 LLM 识别器添加缓存层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheRecognizer {

    private static final String CACHE_NAMESPACE = "intent:cache";

    private final RedisTemplate<String, String> redisTemplate;
    /** 🆕 P6 配置治理子方案03: 缓存 TTL 从 Nacos 动态读取 */
    private final SessionConfig sessionConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Duration cacheTtl() { return Duration.ofMinutes(sessionConfig.getIntentCacheTtlMinutes()); }

    public Optional<IntentResult> recognize(Long tenantId, String userInput) {
        String cacheKey = buildCacheKey(tenantId, userInput);
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                IntentResult result = objectMapper.readValue(cached, IntentResult.class);
                log.debug("[Intent] 缓存命中: key={}", cacheKey);
                return Optional.of(result);
            } catch (JsonProcessingException e) {
                log.warn("[Intent] 缓存反序列化失败", e);
            }
        }
        return Optional.empty();
    }

    public void cacheResult(Long tenantId, String userInput, IntentResult result) {
        try {
            String cacheKey = buildCacheKey(tenantId, userInput);
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(cacheKey, json, cacheTtl());
            log.debug("[Intent] 结果缓存: key={}, intent={}", cacheKey, result.getIntentCode());
        } catch (JsonProcessingException e) {
            log.warn("[Intent] 缓存序列化失败", e);
        }
    }

    public void evictCache(Long tenantId) {
        String pattern = CACHE_NAMESPACE + ":" + tenantId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("[Intent] 缓存失效: tenantId={}, count={}", tenantId, keys.size());
        }
    }

    private String buildCacheKey(Long tenantId, String userInput) {
        String inputHash = Integer.toHexString(userInput.trim().toLowerCase().hashCode());
        return CACHE_NAMESPACE + ":" + tenantId + ":" + inputHash;
    }
}
