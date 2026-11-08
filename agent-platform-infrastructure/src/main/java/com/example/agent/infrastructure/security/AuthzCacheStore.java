package com.example.agent.infrastructure.security;

import com.example.agent.common.constant.ProjectConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 用户权限/角色 Redis 缓存存储.
 * <p>
 * 登录写入、鉴权读取、权限变更刷新、登出删除；在线集合供定时刷新扫描。
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthzCacheStore {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private Duration ttl() {
        return Duration.ofHours(ProjectConstants.Authz.CACHE_TTL_HOURS);
    }

    public Optional<List<String>> getPermissions(String userId) {
        return readList(ProjectConstants.Authz.REDIS_PERM_PREFIX + userId);
    }

    public void putPermissions(String userId, List<String> permissions) {
        writeList(ProjectConstants.Authz.REDIS_PERM_PREFIX + userId, permissions);
    }

    public Optional<List<String>> getRoles(String userId) {
        return readList(ProjectConstants.Authz.REDIS_ROLE_PREFIX + userId);
    }

    public void putRoles(String userId, List<String> roles) {
        writeList(ProjectConstants.Authz.REDIS_ROLE_PREFIX + userId, roles);
    }

    /** 标记用户在线（登录成功） */
    public void markOnline(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        stringRedisTemplate.opsForSet().add(ProjectConstants.Authz.REDIS_ONLINE_SET, userId);
    }

    /** 清除在线标记与权限/角色缓存（登出） */
    public void clearUser(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        stringRedisTemplate.opsForSet().remove(ProjectConstants.Authz.REDIS_ONLINE_SET, userId);
        stringRedisTemplate.delete(ProjectConstants.Authz.REDIS_PERM_PREFIX + userId);
        stringRedisTemplate.delete(ProjectConstants.Authz.REDIS_ROLE_PREFIX + userId);
        log.debug("[AuthzCache] 已清除用户缓存: userId={}", userId);
    }

    /** 仅删除权限/角色缓存（随后由业务层重新写入最新值） */
    public void invalidateAuthz(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        stringRedisTemplate.delete(ProjectConstants.Authz.REDIS_PERM_PREFIX + userId);
        stringRedisTemplate.delete(ProjectConstants.Authz.REDIS_ROLE_PREFIX + userId);
    }

    public Set<String> listOnlineUserIds() {
        Set<String> members = stringRedisTemplate.opsForSet()
                .members(ProjectConstants.Authz.REDIS_ONLINE_SET);
        return members != null ? members : Collections.emptySet();
    }

    private Optional<List<String>> readList(String key) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isBlank()) {
                return Optional.empty();
            }
            List<String> list = objectMapper.readValue(json, new TypeReference<>() {});
            return Optional.ofNullable(list);
        } catch (Exception e) {
            log.warn("[AuthzCache] 读取失败 key={}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    private void writeList(String key, List<String> values) {
        try {
            List<String> safe = values != null ? values : List.of();
            String json = objectMapper.writeValueAsString(safe);
            stringRedisTemplate.opsForValue().set(key, json, ttl());
        } catch (Exception e) {
            log.warn("[AuthzCache] 写入失败 key={}: {}", key, e.getMessage());
        }
    }
}
