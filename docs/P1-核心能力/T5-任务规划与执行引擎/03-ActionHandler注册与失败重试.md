# ActionHandler 注册与失败重试

## 所属阶段
**P1 核心能力 → T5 任务规划与执行引擎**

## 使用技术
- Spring `InitializingBean` 自动注册
- 指数退避重试算法

## 涉及数据库表
- `t_task_step_execution`

## 实现方案

### 1. ActionHandler 接口

```java
public interface ActionHandler {

    /** 动作类型标识（如 "retrieve_orders"、"send_email"） */
    String getActionType();

    /** 执行动作 */
    Object execute(Map<String, Object> params) throws Exception;

    /** 参数校验 */
    void validateParams(Map<String, Object> params);

    /** 是否支持并行执行 */
    default boolean supportsParallel() { return true; }

    /** 最大重试次数 */
    default int maxRetries() { return 3; }

    /** 超时时间（毫秒） */
    default long timeoutMs() { return 30_000; }

    /** 是否高风险（需要审批） */
    default boolean isHighRisk() { return false; }
}
```

### 2. 内置 Handler 示例

```java
@Component
public class RetrieveOrdersHandler implements ActionHandler {

    @Override
    public String getActionType() { return "retrieve_orders"; }

    @Override
    public void validateParams(Map<String, Object> params) {
        if (!params.containsKey("period")) {
            throw new IllegalArgumentException("缺少必填参数: period");
        }
    }

    @Override
    public Object execute(Map<String, Object> params) throws Exception {
        String period = (String) params.get("period");
        // 实际调用订单服务 API
        return orderServiceClient.queryOrders(period);
    }
}

@Component
public class SendEmailHandler implements ActionHandler {

    @Override
    public String getActionType() { return "send_email"; }

    @Override
    public boolean isHighRisk() { return true; }  // 发送邮件需审批

    @Override
    public Object execute(Map<String, Object> params) throws Exception {
        return emailService.send(
            (String) params.get("to"),
            (String) params.get("subject"),
            (String) params.get("body")
        );
    }
}
```

### 3. 自动注册

```java
@Component
public class ActionHandlerRegistry implements InitializingBean {

    private final Map<String, ActionHandler> handlers = new ConcurrentHashMap<>();
    private final List<ActionHandler> handlerList;

    public ActionHandlerRegistry(List<ActionHandler> handlers) {
        this.handlerList = handlers;
    }

    @Override
    public void afterPropertiesSet() {
        for (ActionHandler handler : handlerList) {
            if (handlers.containsKey(handler.getActionType())) {
                throw new IllegalStateException("重复的 ActionHandler: " + handler.getActionType());
            }
            handlers.put(handler.getActionType(), handler);
            log.info("已注册 ActionHandler: {} → {}", handler.getActionType(), handler.getClass().getSimpleName());
        }
    }

    public ActionHandler getHandler(String actionType) {
        ActionHandler handler = handlers.get(actionType);
        if (handler == null) {
            throw new BusinessException(400, "未知的动作类型: " + actionType);
        }
        return handler;
    }

    public Set<String> getAvailableActions() {
        return handlers.keySet();
    }
}
```

### 4. 失败重试策略

```java
public class RetryPolicy {

    /**
     * 指数退避重试
     * 第1次重试: 1s 后
     * 第2次重试: 2s 后
     * 第3次重试: 4s 后
     */
    public static StepResult retry(ActionHandler handler, Map<String, Object> params, int maxRetries) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                long backoffMs = (long) Math.pow(2, attempt - 1) * 1000;
                log.info("重试 {}/{}，等待 {}ms", attempt, maxRetries, backoffMs);
                Thread.sleep(backoffMs);

                Object result = handler.execute(params);
                return new StepResult("SUCCESS", result, "重试 " + attempt + " 次后成功");
            } catch (Exception e) {
                lastException = e;
                log.warn("重试 {}/{} 失败: {}", attempt, maxRetries, e.getMessage());
            }
        }

        return new StepResult("FAILED", null, "重试 " + maxRetries + " 次后仍失败: " + lastException.getMessage());
    }
}
```

### 5. 超时控制

```java
public StepResult executeWithTimeout(ActionHandler handler, Map<String, Object> params) {
    ExecutorService timeoutExecutor = Executors.newSingleThreadExecutor();
    Future<Object> future = timeoutExecutor.submit(() -> handler.execute(params));

    try {
        Object result = future.get(handler.timeoutMs(), TimeUnit.MILLISECONDS);
        return new StepResult("SUCCESS", result, null);
    } catch (TimeoutException e) {
        future.cancel(true);
        return new StepResult("TIMEOUT", null, "执行超时 (" + handler.timeoutMs() + "ms)");
    } catch (Exception e) {
        return new StepResult("FAILED", null, e.getMessage());
    } finally {
        timeoutExecutor.shutdownNow();
    }
}
```
