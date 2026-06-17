package com.example.agent.infrastructure.aop;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.agent.common.result.Result;
import com.example.agent.infrastructure.annotation.RateLimit;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Sentinel 限流 AOP 切面
 * <p>
 * 拦截带有 {@link RateLimit} 注解的方法，自动进行 Sentinel 限流校验。
 * 支持动态拼接租户维度，实现多租户精细化限流。
 * <p>
 * 限流逻辑：
 * <ol>
 *   <li>解析资源名（注解指定 → 自动生成 "类名.方法名"）</li>
 *   <li>拼接租户维度："resource:tenant:tenantId"</li>
 *   <li>通过 Sentinel SphU.entry() 申请通行</li>
 *   <li>被限流时返回统一 429 响应</li>
 * </ol>
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    /**
     * 环绕增强：限流校验
     * <p>
     * 被 {@link BlockException} 捕获时返回限流响应。
     *
     * @param joinPoint 切入点
     * @param rateLimit 限流注解配置
     * @return 方法返回值（正常执行） 或 429 限流响应（被限流）
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 1. 解析资源名
        String resource = resolveResource(joinPoint, rateLimit);

        // 2. 拼接租户维度（尝试获取，失败则使用默认维度）
        String fullResource = appendTenantDimension(resource);

        log.debug("[Sentinel] 限流校验: resource={}, qps={}, warmUp={}s",
                fullResource, rateLimit.qps(), rateLimit.warmUpPeriod());

        Entry entry = null;
        try {
            // 3. 申请 Sentinel 通行许可
            entry = SphU.entry(fullResource);
            // 4. 正常执行目标方法
            return joinPoint.proceed();

        } catch (BlockException e) {
            // 5. 被限流 — 记录日志，返回统一响应
            log.warn("[Sentinel] 请求被限流: resource={}, qpsLimit={}, blockType={}",
                    fullResource, rateLimit.qps(), e.getRuleLimitApp());
            return handleBlock(rateLimit, e);

        } finally {
            // 6. 释放 Sentinel Entry（务必在 finally 中执行）
            if (entry != null) {
                entry.exit();
            }
        }
    }

    /**
     * 解析资源名称
     * <p>
     * 注解指定则使用注解值，否则自动生成 "类全限定名.方法名"。
     */
    private String resolveResource(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String resource = rateLimit.resource();
        if (resource == null || resource.isEmpty()) {
            // 自动生成：com.example.agent.interfaces.rest.XXXController.methodName
            resource = joinPoint.getSignature().getDeclaringTypeName()
                    + "." + joinPoint.getSignature().getName();
        }
        return resource;
    }

    /**
     * 拼接租户维度
     * <p>
     * 格式：{resource}:tenant:{tenantId}
     * 从 {@link TenantContext} 获取当前租户 ID（由 TenantInterceptor 在请求进入时设置），
     * 无法获取时使用 "unknown" 兜底。
     */
    private String appendTenantDimension(String resource) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String tenantStr;
        if (tenantId == null) {
            tenantStr = "unknown";
        } else {
            tenantStr = String.valueOf(tenantId);
        }
        return resource + ":tenant:" + tenantStr;
    }

    /**
     * 限流降级处理
     * <p>
     * 返回统一的 HTTP 429 响应，提示用户稍后再试。
     */
    private Object handleBlock(RateLimit rateLimit, BlockException e) {
        String message = "请求过于频繁，请稍后再试。限制: " + rateLimit.qps() + " QPS";
        log.info("[Sentinel] 返回限流响应: message={}", message);
        return Result.fail(429, message);
    }
}
