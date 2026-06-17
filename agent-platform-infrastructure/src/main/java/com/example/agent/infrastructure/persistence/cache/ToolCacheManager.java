package com.example.agent.infrastructure.persistence.cache;

import com.example.agent.domain.tool.entity.ToolRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 工具定义二级缓存（L2: Redis）.
 *
 * <p>在现有 McpClientManager ConcurrentHashMap (L1) 基础上增加 Redis 二级缓存：
 * <ul>
 *   <li>L1 缓存（内存）：McpClientManager 的 ConcurrentHashMap，极速访问</li>
 *   <li>L2 缓存（Redis）：工具定义 JSON 序列化存储，跨实例共享、重启不丢失</li>
 * </ul>
 *
 * <p>Key 格式: tool:def:{tenantId}:{toolId}
 * <p>TTL: 1 小时（比 L1 的 5min 刷新周期更长，因为 Redis 可跨实例共享）
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolCacheManager {

    private final RedisTemplate<String, ToolRegistry> redisTemplate;
    private static final String KEY_PREFIX = "tool:def:";
    private static final Duration TTL = Duration.ofHours(1);

    /**
     * 从 Redis 获取工具定义.
     *
     * @param tenantId 租户 ID
     * @param toolId   工具业务 ID
     * @return 工具定义（缓存命中）或 empty（缓存未命中）
     */
    public Optional<ToolRegistry> get(String tenantId, String toolId) {
        String key = KEY_PREFIX + tenantId + ":" + toolId;
        ToolRegistry tool = redisTemplate.opsForValue().get(key);
        if (tool != null) {
            log.debug("Redis cache HIT for tool: {}", toolId);
            return Optional.of(tool);
        }
        log.debug("Redis cache MISS for tool: {}", toolId);
        return Optional.empty();
    }

    /**
     * 将工具定义写入 Redis 缓存.
     *
     * @param tool 工具注册实体
     */
    public void put(ToolRegistry tool) {
        String key = KEY_PREFIX + tool.getTenantId() + ":" + tool.getToolId();
        redisTemplate.opsForValue().set(key, tool, TTL);
        log.debug("Redis cache PUT for tool: {}", tool.getToolId());
    }

    /**
     * 从 Redis 缓存中删除指定工具.
     *
     * @param tenantId 租户 ID
     * @param toolId   工具业务 ID
     */
    public void evict(String tenantId, String toolId) {
        String key = KEY_PREFIX + tenantId + ":" + toolId;
        redisTemplate.delete(key);
        log.info("Redis cache EVICT for tool: {}", toolId);
    }

    /**
     * 工具启停或配置变更时，清除 L1+L2 缓存.
     * <p>McpClientManager 下次 get() 时 L1 miss → L2 miss → MySQL → 回填.
     *
     * @param tenantId 租户 ID
     * @param toolId   工具业务 ID
     */
    public void refresh(String tenantId, String toolId) {
        evict(tenantId, toolId);
        log.info("Cache REFRESH triggered for tool: {}", toolId);
    }
}
