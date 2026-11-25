# Sentinel 限流熔断方案

## 所属阶段
**P0 基础底座 → T2 统一网关与安全流控**

## 使用技术
- Alibaba Sentinel 1.8.x
- Sentinel Dashboard（可选，可视化控制台）
- Spring Cloud Alibaba Sentinel Starter

## 实现方案

### 1. 限流维度

| 维度 | 规则示例 | 说明 |
|------|----------|------|
| 租户级 | 每租户每秒最大 50 请求 | 防止单租户打爆系统 |
| API 级 | `/api/v1/chat` 每秒最大 200 请求 | 保护 LLM 调用成本 |
| IP 级 | 单 IP 每秒最大 20 请求 | 防止爬虫/恶意调用 |
| 用户级 | 单用户每分钟最大 30 请求 | 防止滥用 |

### 2. 自定义限流注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String resource() default "";     // 资源名（默认取类名+方法名）
    int qps() default 50;             // 每秒允许的请求数
    int warmUpPeriod() default 0;     // 预热时间（秒）
    String fallback() default "";     // 降级方法名
}
```

### 3. AOP 切面实现

```java
@Aspect
@Component
public class RateLimitAspect {

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit rateLimit) {
        String resource = rateLimit.resource();
        if (resource.isEmpty()) {
            resource = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
        }

        // 动态拼接租户维度
        String tenantId = TenantContext.getCurrentTenantId();
        String fullResource = resource + ":tenant:" + tenantId;

        Entry entry = null;
        try {
            entry = SphU.entry(fullResource);
            return pjp.proceed();
        } catch (BlockException e) {
            return handleBlock(pjp, rateLimit, e);
        } finally {
            if (entry != null) entry.exit();
        }
    }

    private Object handleBlock(ProceedingJoinPoint pjp, RateLimit rl, BlockException e) {
        // 返回统一限流响应
        return Result.fail(429, "请求过于频繁，请稍后再试。限制: " + rl.qps() + " QPS");
    }
}
```

### 4. Sentinel 配置

```java
@Configuration
public class SentinelConfig {

    @PostConstruct
    public void initRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 全局聊天接口限流
        FlowRule chatRule = new FlowRule();
        chatRule.setResource("com.example.agent.interfaces.rest.conversation.ConversationController.streamChat");
        chatRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        chatRule.setCount(200);
        rules.add(chatRule);

        // 租户限流
        FlowRule tenantRule = new FlowRule();
        tenantRule.setResource("com.example.agent.interfaces.rest.conversation.ConversationController.sendMessage:tenant:*");
        tenantRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        tenantRule.setCount(50);
        rules.add(tenantRule);

        FlowRuleManager.loadRules(rules);
    }
}
```

### 5. 熔断降级策略

| 策略 | 条件 | 动作 |
|------|------|------|
| 慢调用比例 | 1秒内慢调用（>200ms）比例 > 50% | 熔断 30 秒 |
| 异常比例 | 1秒内异常比例 > 50% | 熔断 30 秒 |
| 异常数 | 1分钟内异常数 > 100 | 熔断 30 秒 |

```java
List<DegradeRule> rules = new ArrayList<>();
DegradeRule rule = new DegradeRule();
rule.setResource("com.example.agent.llm.call");
rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
rule.setCount(0.5);     // 50%
rule.setTimeWindow(30); // 30秒恢复
rule.setMinRequestAmount(10);
rule.setStatIntervalMs(1000); // 1秒统计窗口
rules.add(rule);
DegradeRuleManager.loadRules(rules);
```
