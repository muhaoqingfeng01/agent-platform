package com.example.agent.infrastructure.aop;

import com.example.agent.domain.audit.entity.AuditLog;
import com.example.agent.domain.audit.repository.AuditLogRepository;
import com.example.agent.domain.audit.valueobject.ActorTypeEnums;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.config.threadpool.AuditLogThreadPoolConfig;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 审计日志 AOP 切面 — 对 {@code @Auditable} 注解的方法自动记录审计日志.
 *
 * <h3>记录内容</h3>
 * <ul>
 *   <li><b>traceId</b> — 从 MDC 获取；缺失时生成兜底值（满足 DB NOT NULL）</li>
 *   <li><b>tenantId / userId</b> — 优先 TenantContext，回退方法参数</li>
 *   <li><b>conversationId</b> — 方法参数 conversationId（若有）</li>
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
    private final ThreadPoolTaskExecutor auditLogExecutor;

    public AuditLogAspect(AuditLogRepository auditLogRepository,
                          ObjectMapper objectMapper,
                          @Qualifier(AuditLogThreadPoolConfig.AUDIT_LOG_EXECUTOR)
                          ThreadPoolTaskExecutor auditLogExecutor) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
        this.auditLogExecutor = auditLogExecutor;
    }

    /**
     * 环绕增强：采集审计信息并异步写入.
     */
    @Around("@annotation(auditable)")
    public Object around(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long start = System.currentTimeMillis();
        // 预捕获：HTTP 线程场景下上下文在进入方法前已就绪；
        // 流式线程场景下方法体内才会 setTenantId，需在 finally 再解析一次。
        String traceId = MDC.get("traceId");
        Long tenantIdBefore = TenantContext.getCurrentTenantId();
        String userIdBefore = TenantContext.getCurrentUserId();
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

            // 方法执行后再次解析：覆盖 stream-pool 等延迟注入 TenantContext 的场景
            Long tenantId = resolveTenantId(joinPoint, tenantIdBefore);
            String userId = resolveUserId(joinPoint, userIdBefore);
            traceId = resolveTraceId(traceId);
            String conversationId = resolveConversationId(joinPoint);

            if (tenantId == null) {
                log.warn("[AuditLog] 跳过写入：tenantId 为空, action={}, method={}",
                        auditable.action(), joinPoint.getSignature().toShortString());
            } else {
                AuditLog auditLog = AuditLog.builder()
                        .traceId(traceId)
                        .tenantId(tenantId)
                        .conversationId(conversationId)
                        .actorType(userId != null ? ActorTypeEnums.USER.getCode() : ActorTypeEnums.SYSTEM.getCode())
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
                    log.warn("[AuditLog] 审计日志异步提交失败: traceId={}, action={}, error={}",
                            traceId, auditable.action(), e.getMessage());
                }
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
        auditLogExecutor.execute(() -> {
            try {
                auditLogRepository.save(auditLog);
            } catch (Exception e) {
                log.warn("[AuditLog] 审计日志写入失败（已忽略）: traceId={}, action={}, error={}",
                        auditLog.getTraceId(), auditLog.getAction(), e.getMessage());
            }
        });
    }

    /**
     * 解析 traceId：预捕获 → MDC → 生成兜底 UUID（DB 列为 NOT NULL）.
     */
    private String resolveTraceId(String traceIdBefore) {
        if (traceIdBefore != null && !traceIdBefore.isBlank()) {
            return traceIdBefore;
        }
        String fromMdc = MDC.get("traceId");
        if (fromMdc != null && !fromMdc.isBlank()) {
            return fromMdc;
        }
        String generated = UUID.randomUUID().toString().replace("-", "");
        log.debug("[AuditLog] MDC 无 traceId，已生成兜底值: {}", generated);
        return generated;
    }

    private String resolveConversationId(ProceedingJoinPoint joinPoint) {
        Object arg = findArgByName(joinPoint, "conversationId");
        return arg != null ? truncate(arg.toString(), 64) : null;
    }

    /**
     * 解析租户 ID：TenantContext（方法后）→ 预捕获值 → 方法参数 tenantId.
     */
    private Long resolveTenantId(ProceedingJoinPoint joinPoint, Long tenantIdBefore) {
        Long fromContext = TenantContext.getCurrentTenantId();
        if (fromContext != null) {
            return fromContext;
        }
        if (tenantIdBefore != null) {
            return tenantIdBefore;
        }
        Object arg = findArgByName(joinPoint, "tenantId");
        if (arg instanceof Long longVal) {
            return longVal;
        }
        if (arg instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    /**
     * 解析用户 ID：TenantContext（方法后）→ 预捕获值 → 方法参数 userId.
     */
    private String resolveUserId(ProceedingJoinPoint joinPoint, String userIdBefore) {
        String fromContext = TenantContext.getCurrentUserId();
        if (fromContext != null) {
            return fromContext;
        }
        if (userIdBefore != null) {
            return userIdBefore;
        }
        Object arg = findArgByName(joinPoint, "userId");
        return arg != null ? arg.toString() : null;
    }

    private Object findArgByName(ProceedingJoinPoint joinPoint, String name) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        if (paramNames == null || args == null) {
            return null;
        }
        for (int i = 0; i < paramNames.length && i < args.length; i++) {
            if (name.equals(paramNames[i])) {
                return args[i];
            }
        }
        return null;
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
