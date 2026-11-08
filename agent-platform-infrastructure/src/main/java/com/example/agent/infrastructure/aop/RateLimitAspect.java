package com.example.agent.infrastructure.aop;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.result.Result;
import com.example.agent.infrastructure.annotation.RateLimit;
import com.example.agent.infrastructure.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
 *   <li>若该完整资源尚无 FlowRule，按注解 QPS 合并注册（不覆盖已有规则）</li>
 *   <li>通过 Sentinel SphU.entry() 申请通行</li>
 *   <li>被限流时：返回类型为 Result 则返回 429 Result，否则抛 BusinessException(429)</li>
 * </ol>
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    /** 本切面已确保存在规则的资源名，避免每次请求扫描全量规则 */
    private final Set<String> ensuredResources = ConcurrentHashMap.newKeySet();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String resource = resolveResource(joinPoint, rateLimit);
        String fullResource = appendTenantDimension(resource);

        log.debug("[Sentinel] 限流校验: resource={}, qps={}, warmUp={}s",
                fullResource, rateLimit.qps(), rateLimit.warmUpPeriod());

        ensureFlowRule(fullResource, rateLimit.qps());

        Entry entry = null;
        try {
            entry = SphU.entry(fullResource);
            return joinPoint.proceed();
        } catch (BlockException e) {
            log.warn("[Sentinel] 请求被限流: resource={}, qpsLimit={}, blockType={}",
                    fullResource, rateLimit.qps(), e.getRuleLimitApp());
            return handleBlock(joinPoint, rateLimit);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    private String resolveResource(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String resource = rateLimit.resource();
        if (resource == null || resource.isEmpty()) {
            resource = joinPoint.getSignature().getDeclaringTypeName()
                    + "." + joinPoint.getSignature().getName();
        }
        return resource;
    }

    private String appendTenantDimension(String resource) {
        Long tenantId = TenantContext.getCurrentTenantId();
        String tenantStr = tenantId == null ? "unknown" : String.valueOf(tenantId);
        return resource + ":tenant:" + tenantStr;
    }

    /**
     * 仅为缺失资源注册注解 QPS，避免覆盖 Nacos / Fallback 已下发规则.
     */
    private void ensureFlowRule(String fullResource, int qps) {
        if (ensuredResources.contains(fullResource)) {
            return;
        }
        synchronized (this) {
            if (ensuredResources.contains(fullResource)) {
                return;
            }
            List<FlowRule> existing = FlowRuleManager.getRules();
            boolean present = existing.stream().anyMatch(r -> fullResource.equals(r.getResource()));
            if (!present) {
                List<FlowRule> merged = new ArrayList<>(existing);
                FlowRule rule = new FlowRule();
                rule.setResource(fullResource);
                rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
                rule.setCount(qps);
                rule.setLimitApp("default");
                merged.add(rule);
                FlowRuleManager.loadRules(merged);
                log.info("[Sentinel] 按注解注册限流规则: resource={}, qps={}", fullResource, qps);
            }
            ensuredResources.add(fullResource);
        }
    }

    /**
     * Result 返回类型 → 429 Result；SSE 等非 Result → 抛 BusinessException(429).
     */
    private Object handleBlock(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String message = "请求过于频繁，请稍后再试。限制: " + rateLimit.qps() + " QPS";
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();
        if (Result.class.isAssignableFrom(returnType)) {
            log.info("[Sentinel] 返回限流 Result: message={}", message);
            return Result.fail(429, message);
        }
        log.info("[Sentinel] 抛出限流异常: message={}", message);
        throw new BusinessException(429, message);
    }
}
