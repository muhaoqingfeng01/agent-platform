package com.example.agent.infrastructure.aop;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.result.Result;
import com.example.agent.infrastructure.annotation.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private MethodSignature methodSignature;

    private RateLimitAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new RateLimitAspect();
        FlowRuleManager.loadRules(Collections.emptyList());
    }

    @AfterEach
    void tearDown() {
        FlowRuleManager.loadRules(Collections.emptyList());
    }

    @Test
    void around_allowsWhenUnderLimit_returnsProceedResult() throws Throwable {
        Method method = StubController.class.getMethod("resultApi");
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        when(joinPoint.proceed()).thenReturn(Result.ok("ok"));

        Object out = aspect.around(joinPoint, rateLimit);
        assertInstanceOf(Result.class, out);
        assertEquals(200, ((Result<?>) out).getCode());
    }

    @Test
    void around_sseReturnType_whenBlocked_throwsBusinessException() throws Throwable {
        Method method = StubController.class.getMethod("sseApi");
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);

        FlowRule rule = new FlowRule();
        rule.setResource("test:sse:tenant:unknown");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(0);
        rule.setLimitApp("default");
        FlowRuleManager.loadRules(List.of(rule));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> aspect.around(joinPoint, rateLimit));
        assertEquals(429, ex.getCode());
    }

    @Test
    void around_resultReturnType_whenBlocked_returnsResult429() throws Throwable {
        Method method = StubController.class.getMethod("resultApi");
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);

        FlowRule rule = new FlowRule();
        rule.setResource("test:result:tenant:unknown");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(0);
        rule.setLimitApp("default");
        FlowRuleManager.loadRules(List.of(rule));

        Object out = aspect.around(joinPoint, rateLimit);
        assertInstanceOf(Result.class, out);
        assertEquals(429, ((Result<?>) out).getCode());
    }

    static class StubController {
        @RateLimit(resource = "test:result", qps = 100)
        public Result<String> resultApi() {
            return Result.ok("x");
        }

        @RateLimit(resource = "test:sse", qps = 100)
        public Object sseApi() {
            return new Object();
        }
    }
}
