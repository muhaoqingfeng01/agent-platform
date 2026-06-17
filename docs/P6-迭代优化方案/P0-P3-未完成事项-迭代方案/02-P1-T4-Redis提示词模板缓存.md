# P1-T4 Redis 提示词模板缓存

## 所属阶段
**P1 核心能力 → T4 提示词管理与版本控制**

## 使用技术
- Spring Cache（`@Cacheable` / `@CacheEvict` / `@CachePut`）
- Redis（缓存存储）
- Jackson（序列化 PromptTemplate 对象）

---

## 1. 当前状态

| 功能 | 状态 | 说明 |
|------|:--:|------|
| 模板 CRUD | ✅ | Controller → AppService → DomainService → Repository |
| 版本发布/回滚 | ✅ | Memento 快照模式 |
| 运行时渲染 | ✅ | `PromptRenderService` 每次从 MySQL 查询模板 |
| **Redis 热点缓存** | ❌ | 原计划 P1-T4 第 7 项，实现时遗漏 |

**影响**：高并发下每次渲染都查 MySQL（`PromptRenderService.render()` → `PromptTemplateRepository.findPublishedByName()`），热点模板（如系统默认 prompt）会产生不必要的 DB 查询。

---

## 2. 设计方案

### 2.1 缓存策略

```
                            请求渲染模板
                                 │
                    ┌────────────┴────────────┐
                    ▼                         ▼
            Cache Hit? ──Yes──→ 直接返回       Cache Miss?
                    │                         │
                    │                    查 MySQL
                    │                         │
                    │                    写入 Redis
                    │                    (TTL 30 min)
                    │                         │
                    └──────────┬──────────────┘
                               ▼
                          返回模板对象
```

### 2.2 缓存 Key 设计

```
# 模板缓存 Key 格式
prompt:template:{tenantId}:{templateName}:{version}

# 版本列表缓存 Key 格式
prompt:versions:{tenantId}:{templateId}

# 示例
prompt:template:tenant_001:system-chat-prompt:3
prompt:versions:tenant_001:prompt_a1b2c3d4
```

### 2.3 缓存失效时机

| 操作 | 失效策略 | 说明 |
|------|------|------|
| 发布新版本 | `@CachePut` 更新 `prompt:template:*:latest` | 新版本直接覆盖 latest 缓存 |
| 回滚到旧版本 | `@CachePut` 更新 `prompt:template:*:latest` | 回滚后 latest 指向旧版本 |
| 编辑 DRAFT 模板 | 不失效缓存 | DRAFT 不对外渲染 |
| 删除模板 | `@CacheEvict` 清除该模板所有缓存 | 防止返回已删除模板 |
| 模板归档 | `@CacheEvict` 清除该模板所有缓存 | 归档后不可渲染 |

---

## 3. 实现方案

### 3.1 新增 CachedPromptRepository

```java
package com.example.agent.infrastructure.persistence.cache;

import com.example.agent.domain.prompt.PromptTemplate;
import com.example.agent.domain.prompt.PromptTemplateRepository;
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
```

### 3.2 修改 PromptDomainService（集成缓存）

```java
// PromptDomainService.java 修改 publish() 方法

public PromptTemplate publish(String templateId) {
    PromptTemplate template = repository.findById(templateId)
        .orElseThrow(() -> new ResourceNotFoundException("模板", templateId));

    // 1. 领域校验 + 版本号 +1 + 快照（现有逻辑不变）
    template.publish();
    PromptTemplateVersion snapshot = template.createSnapshot();
    versionRepository.save(snapshot);
    repository.save(template);

    // 2. 🆕 发布后更新 Redis 缓存
    cachedPromptRepository.updateLatestCache(template);

    return template;
}

public PromptTemplate rollback(String templateId, int targetVersion) {
    // ... 现有回滚逻辑 ...

    // 🆕 回滚后更新 Redis 缓存
    cachedPromptRepository.updateLatestCache(restoredTemplate);

    return restoredTemplate;
}
```

### 3.3 修改 PromptRenderService（使用缓存）

```java
// 修改前：
Optional<PromptTemplate> templateOpt = repository.findLatestPublished(tenantId, name);

// 修改后：
Optional<PromptTemplate> templateOpt = cachedPromptRepository.findLatestPublished(tenantId, name);
```

### 3.4 Redis 配置

```java
// infrastructure/config/CacheConfig.java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheManagerBuilderCustomizer() {
        return builder -> builder
            .withCacheConfiguration("prompt:template:latest",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30))   // 热点模板 30 分钟
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(new GenericJackson2JsonRedisSerializer())))
            .withCacheConfiguration("prompt:template:version",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofHours(2))       // 历史版本 2 小时
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(new GenericJackson2JsonRedisSerializer())));
    }
}
```

---

## 4. 影响范围

| 涉及文件 | 操作 | 说明 |
|------|:--:|------|
| `CachedPromptRepository.java` | 新建 | 缓存装饰器 |
| `CacheConfig.java` | 新建 | Redis Cache 配置 |
| `PromptDomainService.java` | 修改 | publish/rollback/archive 后调用缓存更新 |
| `PromptRenderService.java` | 修改 | findLatestPublished → cachedRepository |
| `application.yml` | 修改 | 确认 `spring.cache.type=redis` |

---

## 5. 测试验证

```
1. 调用 POST /prompts/{id}/render → 第一次查 MySQL（Cache MISS）
2. 再次调用 render → 直接 Redis 返回（Cache HIT，日志可见）
3. 调用 POST /prompts/{id}/publish → 发布新版本 → 缓存自动更新
4. 再次 render → 返回新版本内容（Cache UPDATED）
5. 删除模板 → 缓存自动清除
```

---

## 6. 工作量

| 活动 | 时间 |
|------|:--:|
| `CachedPromptRepository` + `CacheConfig` | 1h |
| 修改 `PromptDomainService` + `PromptRenderService` | 1h |
| 单元测试 + 集成测试 | 1.5h |
| **合计** | **0.5天** |
