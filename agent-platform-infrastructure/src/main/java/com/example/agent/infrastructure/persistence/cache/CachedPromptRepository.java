package com.example.agent.infrastructure.persistence.cache;

import com.example.agent.domain.prompt.entity.PromptTemplate;
import com.example.agent.domain.prompt.repository.PromptTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Redis 缓存的提示词模板仓储装饰器.
 *
 * 热点模板走 Redis 缓存（TTL 30min），缓存未命中回源 MySQL.
 * 发布/回滚/删除时主动失效缓存.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CachedPromptRepository {

    private final PromptTemplateRepository delegate;  // MyBatis 实现

    /**
     * 按名称获取最新 PUBLISHED 版本（缓存热点）.
     */
    @Cacheable(value = "prompt:template:latest",
               key = "#tenantId + ':' + #name",
               unless = "#result == null || #result.isEmpty()")
    public Optional<PromptTemplate> findLatestPublished(String tenantId, String name) {
        log.debug("Cache MISS for prompt template: {}:{}", tenantId, name);
        return delegate.findLatestPublished(tenantId, name);
    }

    /**
     * 按名称+版本号获取（缓存历史版本，访问频率低但仍有价值）.
     */
    @Cacheable(value = "prompt:template:version",
               key = "#tenantId + ':' + #name + ':' + #version",
               unless = "#result == null || #result.isEmpty()")
    public Optional<PromptTemplate> findByVersion(String tenantId, String name, int version) {
        return delegate.findByVersion(tenantId, name, version);
    }

    /**
     * 发布或回滚后更新 latest 缓存.
     */
    @CachePut(value = "prompt:template:latest",
              key = "#template.tenantId + ':' + #template.name")
    public PromptTemplate updateLatestCache(PromptTemplate template) {
        log.info("Cache UPDATE for prompt template: {}:{} v{}",
                 template.getTenantId(), template.getName(), template.getVersion());
        return template;
    }

    /**
     * 删除或归档时清除该模板所有缓存.
     */
    @CacheEvict(value = {"prompt:template:latest", "prompt:template:version"},
                key = "#tenantId + ':' + #name")
    public void evictCache(String tenantId, String name) {
        log.info("Cache EVICT for prompt template: {}:{}", tenantId, name);
    }
}
