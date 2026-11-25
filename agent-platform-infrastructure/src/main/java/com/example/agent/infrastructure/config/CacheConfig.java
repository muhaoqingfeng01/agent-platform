package com.example.agent.infrastructure.config;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Spring Cache 配置 — Redis 作为缓存存储.
 *
 * 配置不同 cache region 的 TTL 和序列化方式.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置 RedisTemplate，用于操作 Redis.
     *
     * @param connectionFactory Redis 连接工厂
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, ToolRegistry> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ToolRegistry> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 使用 String 序列化
        template.setKeySerializer(new StringRedisSerializer());

        // Value 使用支持 JavaTime 的 JSON 序列化（ToolRegistry.createdAt/updatedAt）
        GenericJackson2JsonRedisSerializer jsonSerializer = redisJsonSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheManagerBuilderCustomizer() {
        GenericJackson2JsonRedisSerializer jsonSerializer = redisJsonSerializer();
        return builder -> builder
            .withCacheConfiguration("prompt:template:latest",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30))          // 热点模板 30 分钟
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(jsonSerializer)))
            .withCacheConfiguration("prompt:template:version",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofHours(2))             // 历史版本 2 小时
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(jsonSerializer)))
            .withCacheConfiguration("tool:def",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofHours(1))             // 工具定义 1 小时
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(jsonSerializer)));
    }

    /**
     * Redis JSON 序列化器 — 注册 JavaTimeModule，避免 LocalDateTime 无法序列化.
     */
    private static GenericJackson2JsonRedisSerializer redisJsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 与 GenericJackson2JsonRedisSerializer 无参构造一致：写入 @class 以便反序列化回 ToolRegistry
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
