# 全链路追踪与 TraceId 传播

## 所属阶段
**P4 观测优化 → T9 全链路可观测性**

## 使用技术
- SLF4J MDC（Mapped Diagnostic Context）
- Spring `Filter` / `HandlerInterceptor`
- Langfuse Java SDK（LLM 调用追踪）

## 涉及数据库表
- `t_audit_log`

## 实现方案

### 1. TraceId 生成与传播

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws IOException, ServletException {
        // 优先使用上游传入的 traceId，否则生成新 ID
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }

        // 注入 MDC（日志中自动包含）
        MDC.put("traceId", traceId);
        MDC.put("tenantId", TenantContext.getCurrentTenantId());
        MDC.put("userId", TenantContext.getCurrentUserId());

        // 响应头返回 traceId
        response.setHeader(TRACE_ID_HEADER, traceId);
        response.setHeader("X-Request-Id", UUID.randomUUID().toString());

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();  // 防止内存泄漏
        }
    }

    private String generateTraceId() {
        // 格式: {timestamp}-{random}
        return String.format("%s-%s",
                Long.toHexString(System.currentTimeMillis()),
                UUID.randomUUID().toString().substring(0, 8));
    }
}
```

### 2. 日志格式（含 traceId）

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] [%X{tenantId}] %logger{36} - %msg%n"
```

日志输出示例：
```
2026-06-11 10:30:15.123 [http-nio-8080-exec-1] INFO  [18f3a2b1-a1b2c3d4] [tenant_001] c.e.a.i.r.c.ConversationController - 创建会话: conv_abc123
```

### 3. Langfuse LLM 追踪集成

```java
@Configuration
public class LangfuseConfig {

    @Bean
    public Langfuse langfuse(@Value("${langfuse.public-key}") String publicKey,
                              @Value("${langfuse.secret-key}") String secretKey,
                              @Value("${langfuse.host}") String host) {
        return Langfuse.builder()
                .publicKey(publicKey)
                .secretKey(secretKey)
                .host(host)
                .build();
    }
}

@Component
public class LangfuseTraceService {

    private final Langfuse langfuse;

    /**
     * 为每次对话创建 Langfuse Trace
     */
    public TraceHandle startTrace(String conversationId) {
        return langfuse.trace()
                .id(MDC.get("traceId"))
                .name("t_conversation:" + conversationId)
                .userId(TenantContext.getCurrentUserId())
                .metadata(Map.of(
                    "tenantId", TenantContext.getCurrentTenantId(),
                    "conversationId", conversationId
                ))
                .start();
    }

    /**
     * 记录 LLM 调用 Generation
     */
    public void logLLMCall(String traceId, String model, String prompt,
                            String completion, long durationMs, int tokens) {
        langfuse.generation()
                .traceId(traceId)
                .name("llm_call")
                .model(model)
                .input(prompt)
                .output(completion)
                .usage(new Usage(tokens))
                .startTime(Instant.now().minusMillis(durationMs))
                .endTime(Instant.now())
                .publish();
    }
}
```

### 4. 审计日志切面

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String action();            // 动作类型
    String resourceType();      // 资源类型
    String resourceId() default "";  // SpEL 表达式
}

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object around(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        long start = System.currentTimeMillis();
        String traceId = MDC.get("traceId");
        Object result = null;
        String status = "SUCCESS";

        try {
            result = pjp.proceed();
            return result;
        } catch (Exception e) {
            status = "FAILED";
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            AuditLog log = AuditLog.builder()
                    .traceId(traceId)
                    .tenantId(TenantContext.getCurrentTenantId())
                    .actorType("USER")
                    .actorId(TenantContext.getCurrentUserId())
                    .action(auditable.action())
                    .resourceType(auditable.resourceType())
                    .resourceId(resolveSpEL(auditable.resourceId(), pjp))
                    .durationMs(duration)
                    .status(status)
                    .build();
            auditLogRepository.save(log);
        }
    }
}
```
