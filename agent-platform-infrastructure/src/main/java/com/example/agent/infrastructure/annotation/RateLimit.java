package com.example.agent.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * <p>
 * 标注在 Controller 方法上，通过 AOP 自动进行 Sentinel 限流校验。
 * 支持按租户维度动态拼接资源名，实现精细化限流。
 * <p>
 * 使用示例：
 * <pre>{@code
 *   @RateLimit(resource = "chat", qps = 200)
 *   @PostMapping("/chat")
 *   public Result<String> chat(@RequestBody ChatRequest request) { ... }
 *
 *   @RateLimit(resource = "knowledge:search", qps = 100, warmUpPeriod = 5)
 *   @GetMapping("/search")
 *   public Result<Page<Document>> search(@RequestParam String q) { ... }
 * }</pre>
 *
 * @see com.example.agent.infrastructure.aop.RateLimitAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 资源名称
     * <p>
     * 留空则自动使用 "类全限定名.方法名" 作为资源名。
     * 实际限流时会拼接租户维度："{resource}:tenant:{tenantId}"
     */
    String resource() default "";

    /**
     * 每秒允许的请求数（QPS）
     * <p>
     * 默认 50 QPS，建议根据 API 重要程度调整。
     */
    int qps() default 50;

    /**
     * 预热时间（秒）
     * <p>
     * Sentinel WarmUp 模式：在指定时间内从 qps/3 线性增加到 qps。
     * 0 表示不启用预热（直接限流）。
     */
    int warmUpPeriod() default 0;

    /**
     * 降级处理方法的 Bean 名称
     * <p>
     * 被限流时调用该方法进行降级处理，返回兜底数据。
     * 留空则返回统一的 429 限流响应。
     */
    String fallback() default "";
}
