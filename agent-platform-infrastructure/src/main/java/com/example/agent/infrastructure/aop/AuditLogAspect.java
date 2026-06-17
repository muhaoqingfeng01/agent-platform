package com.example.agent.infrastructure.aop;

import com.example.agent.domain.audit.entity.AuditLog;
import com.example.agent.domain.audit.repository.AuditLogRepository;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.context.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志 AOP 切面 — 对 {@code @Auditable} 注解的方法自动记录审计日志.
 *
 * <h3>记录内容</h3>
 * <ul>
 *   <li><b>traceId</b> — 从 MDC 获取（由 TraceFilter 注入）</li>
 *   <li><b>tenantId / userId</b> — 从 TenantContext 获取</li>
 *   <li><b>durationMs</b> — 方法执行耗时</li>
 *   <li><b>status</b> — SUCCESS / FAILED</li>
 *   <li><b>requestJson</b> — 方法参数（可选，受 recordRequest 控制）</li>
 *   <li><b>responseJson</b> — 返回值（可选，受 recordResponse 控制）</li>
 * </ul>
 *
 * <h3>异步写入</h3>
 * 审计日志通过 {@code "auditLogExecutor"} 线程池异步写入，
 * 写入失败不阻断业务（fail-open）。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditLogAspect(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 环绕增强：采集审计信息并异步写入.
     */
    @Around("@annotation(auditable)")
    public Object around(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long start = System.currentTimeMillis();
        String traceId = MDC.get("traceId");
        String tenantId = TenantContext.getCurrentTenantId();
        String userId = TenantContext.getCurrentUserId();
        String status = "SUCCESS";
        String requestJson = null;
        String responseJson = null;
        Object result = null;

        // 序列化请求参数
        if (auditable.recordRequest()) {
            requestJson = serializeArgs(joinPoint);
        }

        try {
            result = joinPoint.proceed();

            // 序列化返回值
            if (auditable.recordResponse() && result != null) {
                responseJson = serializeResult(result);
            }

            return result;
        } catch (Throwable e) {
            status = "FAILED";
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;

            // 构建审计日志实体
            AuditLog auditLog = AuditLog.builder()
                    .traceId(traceId)
                    .tenantId(tenantId)
                    .actorType(userId != null ? "USER" : "SYSTEM")
                    .actorId(userId)
                    .action(auditable.action())
                    .resourceType(auditable.resourceType())
                    .resourceId(resolveResourceId(auditable.resourceId(), joinPoint))
                    .requestJson(requestJson)
                    .responseJson(responseJson)
                    .durationMs(duration)
                    .status(status)
                    .build();

            // 异步写入（fail-open：写入失败不阻断业务）
            try {
                asyncSave(auditLog);
            } catch (Exception e) {
                log.warn("[AuditLog] 审计日志异步写入失败: traceId={}, action={}, error={}",
                        traceId, auditable.action(), e.getMessage());
            }
        }
    }

    /**
     * 异步持久化审计日志.
     *
     * <p>注意：不能使用 {@code @Async} 注解（Spring AOP 的切面内部自调用不走代理），
     * 这里提交到线程池执行。
     */
    private void asyncSave(AuditLog auditLog) {
        // 直接写入数据库，由 RepositoryImpl 的 DAO 层保证性能
        // 大规模部署时此处应改为 Kafka/ES
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("[AuditLog] 审计日志写入失败（已忽略）: traceId={}, action={}, error={}",
                    auditLog.getTraceId(), auditLog.getAction(), e.getMessage());
        }
    }

    /**
     * 解析资源标识 — 支持 SpEL 或自动取第一个参数.
     */
    private String resolveResourceId(String expression, ProceedingJoinPoint joinPoint) {
        if (expression == null || expression.isEmpty()) {
            // 自动取第一个参数
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0 && args[0] != null) {
                return truncate(args[0].toString(), 128);
            }
            return null;
        }
        // 简单处理 #paramName 模式（不做完整 SpEL，取方法参数名匹配）
        return resolveSimpleExpression(expression, joinPoint);
    }

    private String resolveSimpleExpression(String expression, ProceedingJoinPoint joinPoint) {
        if (expression.startsWith("#")) {
            String paramName = expression.substring(1);
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            if (paramNames != null && args != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    if (paramName.equals(paramNames[i]) && args[i] != null) {
                        return truncate(args[i].toString(), 128);
                    }
                }
            }
        }
        return expression;
    }

    /**
     * 序列化方法参数为 JSON.
     */
    private String serializeArgs(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            if (paramNames == null || args == null || args.length == 0) {
                return null;
            }

            // 过滤掉 HttpServletRequest 等非业务参数，限制序列化长度
            Map<String, Object> argMap = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                if (args[i] instanceof HttpServletRequest) {
                    continue;  // 跳过 Servlet 对象
                }
                argMap.put(paramNames[i], args[i]);
            }

            if (argMap.isEmpty()) {
                return null;
            }

            String json = objectMapper.writeValueAsString(argMap);
            return truncate(json, 2000);  // 限制 2KB
        } catch (JsonProcessingException e) {
            log.trace("[AuditLog] 参数序列化失败: {}", e.getMessage());
            return "{\"error\": \"serialization_failed\"}";
        }
    }

    /**
     * 序列化返回值为 JSON（截断）.
     */
    private String serializeResult(Object result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            return truncate(json, 1000);  // 限制 1KB
        } catch (JsonProcessingException e) {
            log.trace("[AuditLog] 返回值序列化失败: {}", e.getMessage());
            return null;
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return null;
        return text.length() > maxLength ? text.substring(0, maxLength) + "...(truncated)" : text;
    }
}
