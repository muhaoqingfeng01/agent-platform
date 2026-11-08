package com.example.agent.infrastructure.config.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.agent.infrastructure.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 启动后扫描 {@link RateLimit}，将注解 QPS 合并为 Sentinel FlowRule.
 *
 * <p>仅当对应 resource 尚无规则时写入，避免覆盖 Nacos 已下发规则.
 * 资源名为注解 {@code resource}（或类.方法），不含租户后缀；
 * 租户维度规则由 {@link com.example.agent.infrastructure.aop.RateLimitAspect} 按需补齐.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitRuleRegistrar {

    private final ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void registerAnnotationRules() {
        Map<String, Integer> discovered = scanRateLimitResources();
        if (discovered.isEmpty()) {
            log.info("[Sentinel] 未发现 @RateLimit 注解，跳过注解规则注册");
            return;
        }

        List<FlowRule> existing = FlowRuleManager.getRules();
        Set<String> existingResources = existing.stream()
                .map(FlowRule::getResource)
                .collect(Collectors.toSet());

        List<FlowRule> merged = new ArrayList<>(existing);
        int added = 0;
        for (Map.Entry<String, Integer> entry : discovered.entrySet()) {
            String resource = entry.getKey();
            if (existingResources.contains(resource)) {
                continue;
            }
            FlowRule rule = new FlowRule();
            rule.setResource(resource);
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(entry.getValue());
            rule.setLimitApp("default");
            merged.add(rule);
            existingResources.add(resource);
            added++;
            log.info("[Sentinel] 注解规则注册: resource={}, qps={}", resource, entry.getValue());
        }

        if (added > 0) {
            FlowRuleManager.loadRules(merged);
        }
        log.info("[Sentinel] @RateLimit 扫描完成: discovered={}, newlyRegistered={}",
                discovered.size(), added);
    }

    private Map<String, Integer> scanRateLimitResources() {
        Map<String, Integer> resources = new HashMap<>();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean;
            try {
                bean = applicationContext.getBean(beanName);
            } catch (Exception e) {
                continue;
            }
            Class<?> rawTarget = AopUtils.getTargetClass(bean);
            final Class<?> targetClass = rawTarget != null ? rawTarget : ClassUtils.getUserClass(bean);
            ReflectionUtils.doWithMethods(targetClass, method -> {
                RateLimit rateLimit = AnnotationUtils.findAnnotation(method, RateLimit.class);
                if (rateLimit == null) {
                    return;
                }
                String resource = resolveResource(targetClass, method, rateLimit);
                resources.merge(resource, rateLimit.qps(), Math::min);
            });
        }
        return resources;
    }

    private String resolveResource(Class<?> targetClass, Method method, RateLimit rateLimit) {
        if (rateLimit.resource() != null && !rateLimit.resource().isEmpty()) {
            return rateLimit.resource();
        }
        return targetClass.getName() + "." + method.getName();
    }
}
