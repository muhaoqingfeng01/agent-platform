package com.example.agent.infrastructure.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 全链路追踪过滤器 — 为每个请求生成 traceId、spanId、requestId 并注入 MDC
 * <p>
 * 优先级设为最高（{@link Ordered#HIGHEST_PRECEDENCE}），确保在所有其他组件之前执行。
 * <p>
 * MDC 注入责任分离：
 * <ul>
 *   <li><b>TraceFilter</b>（此处）：注入 traceId + spanId（请求到达时立即可用）</li>
 *   <li><b>TenantInterceptor</b>：注入 tenantId + userId（需 Sa-Token 认证后才能获取，详见
 *       {@link com.example.agent.infrastructure.interceptor.TenantInterceptor}）</li>
 * </ul>
 * <p>
 * 行为：
 * <ul>
 *   <li>从请求头 {@code X-Trace-Id} 提取上游 traceId（存在则复用，不存在则生成）</li>
 *   <li>为当前请求生成新的 spanId</li>
 *   <li>生成 requestId 写入响应头 {@code X-Request-Id}（供客户端定位请求）</li>
 *   <li>将 traceId + spanId 注入 SLF4J {@link MDC}</li>
 *   <li>将 traceId 写入响应头 {@code X-Trace-Id}，方便前端/客户端关联</li>
 *   <li>请求完成后清理 MDC，防止 ThreadLocal 内存泄漏</li>
 * </ul>
 *
 * @see MDC
 * @see com.example.agent.infrastructure.interceptor.TenantInterceptor
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter implements Filter {

    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String HEADER_SPAN_ID = "X-Span-Id";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. traceId — 优先复用上游传入的值，否则生成新值
        String traceId = httpRequest.getHeader(HEADER_TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = generateTraceId();
        }

        // 2. spanId — 每次调用都生成新值
        String spanId = generateSpanId();

        // 3. requestId — 每次调用生成新值（用于客户端定位请求）
        String requestId = UUID.randomUUID().toString();

        // 4. 注入 MDC
        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put(MDC_SPAN_ID, spanId);

        // 5. 响应头回写
        httpResponse.setHeader(HEADER_TRACE_ID, traceId);
        httpResponse.setHeader(HEADER_REQUEST_ID, requestId);

        try {
            log.trace("[TraceFilter] 请求进入: method={}, uri={}, traceId={}, spanId={}, requestId={}",
                    httpRequest.getMethod(), httpRequest.getRequestURI(), traceId, spanId, requestId);
            chain.doFilter(request, response);
        } finally {
            // 6. 清理 MDC（必须执行，否则 ThreadLocal 污染后续请求）
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_SPAN_ID);
        }
    }

    /**
     * 生成 32 位无连字符的 traceId（便于日志检索）
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成 16 位 spanId（取 UUID 前半部分，足够在单次 trace 内唯一）
     */
    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
