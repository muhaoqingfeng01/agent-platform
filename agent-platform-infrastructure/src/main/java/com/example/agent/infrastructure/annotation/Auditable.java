package com.example.agent.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解 — 标注在需要自动记录审计日志的方法上.
 *
 * <p>由 {@code AuditLogAspect} 通过 AOP 环绕增强自动采集：
 * <ul>
 *   <li>traceId（从 MDC 获取）</li>
 *   <li>tenantId / userId（从 TenantContext 获取）</li>
 *   <li>方法耗时（durationMs）</li>
 *   <li>执行状态（SUCCESS / FAILED）</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 *   @Auditable(action = "LLM_CALL", resourceType = "CONVERSATION")
 *   public String processMessage(String conversationId, String content) { ... }
 *
 *   @Auditable(action = "TOOL_INVOKE", resourceType = "TOOL", resourceId = "#toolName")
 *   public ToolResult execute(String toolName, Map<String, Object> params) { ... }
 * }</pre>
 *
 * @see com.example.agent.infrastructure.aop.AuditLogAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /** 动作类型（如 LLM_CALL、TOOL_INVOKE、RAG_RETRIEVE、INTENT_RECOGNITION） */
    String action();

    /** 资源类型（如 CONVERSATION、TOOL、KNOWLEDGE_BASE） */
    String resourceType();

    /** 资源标识 — 支持 SpEL 表达式（如 "#toolName"、 "#conversationId"），留空则自动使用第一个方法参数 */
    String resourceId() default "";

    /** 是否记录请求参数到 requestJson（默认 true） */
    boolean recordRequest() default true;

    /** 是否记录返回值到 responseJson（默认 false，避免大对象膨胀） */
    boolean recordResponse() default false;
}
