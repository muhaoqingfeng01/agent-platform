package com.example.agent.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 为 @Scheduled 定时任务自动生成 traceId 并注入 MDC.
 *
 * <p><b>背景</b>：@Scheduled 任务运行在 {@code scheduling-*} 线程池上，不经过 HTTP Filter 链，
 * 因此没有 traceId。本切面为每次定时任务调用生成独立的 traceId + spanId，
 * 使日志中的 {@code %X{traceId}} 有值可查。
 *
 * <p>优先级设为最高，确保在其他切面之前执行。
 *
 * @see org.springframework.scheduling.annotation.Scheduled
 * @see com.example.agent.infrastructure.filter.TraceFilter
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScheduledTaskMdcAspect {

    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_SPAN_ID = "spanId";

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        String spanId = traceId.substring(0, 16);

        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put(MDC_SPAN_ID, spanId);

        try {
            return pjp.proceed();
        } finally {
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_SPAN_ID);
        }
    }
}
