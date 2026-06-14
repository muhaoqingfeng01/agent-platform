package com.example.agent.application.intent.recognizer;

import com.example.agent.application.intent.model.IntentResult;
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
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<IntentResult> recognize(String tenantId, String userInput) {
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

    public void cacheResult(String tenantId, String userInput, IntentResult result) {
        try {
            String cacheKey = buildCacheKey(tenantId, userInput);
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
            log.debug("[Intent] 结果缓存: key={}, intent={}", cacheKey, result.getIntentCode());
        } catch (JsonProcessingException e) {
            log.warn("[Intent] 缓存序列化失败", e);
        }
    }

    public void evictCache(String tenantId) {
        String pattern = CACHE_NAMESPACE + ":" + tenantId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("[Intent] 缓存失效: tenantId={}, count={}", tenantId, keys.size());
        }
    }

    private String buildCacheKey(String tenantId, String userInput) {
        String inputHash = Integer.toHexString(userInput.trim().toLowerCase().hashCode());
        return CACHE_NAMESPACE + ":" + tenantId + ":" + inputHash;
    }
}
