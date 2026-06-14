# DAG 执行器与并行调度

## 所属阶段
**P1 核心能力 → T5 任务规划与执行引擎**

## 使用技术
- Java `CompletableFuture` / `ExecutorService`
- Redis（状态同步）
- WebSocket（进度推送）

## 涉及数据库表
- `t_task_execution`, `t_task_step_execution`

## 实现方案

### 1. DAG 执行器核心

```java
@Service
@Slf4j
public class DagExecutor {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();  // Java 21+
    private final ActionHandlerRegistry handlerRegistry;
    private final TaskExecutionRepository executionRepository;
    private final ConversationWebSocketHandler wsHandler;

    public void execute(DagGraph graph, String executionId, String conversationId) {
        Map<String, CompletableFuture<StepResult>> futures = new ConcurrentHashMap<>();

        // BFS 层级执行：每层内的节点可并行
        List<List<TaskNode>> levels = topologicalLevels(graph);

        for (List<TaskNode> level : levels) {
            // 当前层的节点等待其依赖完成
            List<CompletableFuture<StepResult>> levelFutures = level.stream()
                .map(node -> {
                    // 等待所有依赖完成
                    CompletableFuture<Void> depsFuture = CompletableFuture.allOf(
                        node.getDep().stream()
                            .map(futures::get)
                            .toArray(CompletableFuture[]::new)
                    );

                    return depsFuture.thenComposeAsync(v ->
                        executeNode(node, executionId, conversationId), executor);
                })
                .toList();

            // 并行执行当前层所有节点
            CompletableFuture.allOf(levelFutures.toArray(new CompletableFuture[0])).join();

            // 记录结果供下一层使用
            for (int i = 0; i < level.size(); i++) {
                TaskNode node = level.get(i);
                try {
                    futures.put(node.getId(), levelFutures.get(i));
                } catch (Exception e) {
                    futures.put(node.getId(), CompletableFuture.failedFuture(e));
                }
            }
        }
    }

    private CompletableFuture<StepResult> executeNode(TaskNode node, String executionId, String convId) {
        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            updateStepStatus(executionId, node.getId(), "RUNNING");

            try {
                ActionHandler handler = handlerRegistry.getHandler(node.getAction());
                Object result = handler.execute(node.getParams());

                long duration = System.currentTimeMillis() - start;
                updateStepStatus(executionId, node.getId(), "SUCCESS");
                pushProgress(convId, executionId, node.getId(), "SUCCESS");

                return new StepResult(node.getId(), "SUCCESS", result, duration);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - start;
                StepResult failResult = new StepResult(node.getId(), "FAILED", null, duration);

                // 尝试重试
                if (shouldRetry(node)) {
                    return retryNode(node, executionId, convId);
                }

                updateStepStatus(executionId, node.getId(), "FAILED");
                pushProgress(convId, executionId, node.getId(), "FAILED");
                return failResult;
            }
        }, executor);
    }
}
```

### 2. 拓扑分层

```java
private List<List<TaskNode>> topologicalLevels(DagGraph graph) {
    Map<String, Integer> inDegree = new HashMap<>();
    for (TaskNode node : graph.getNodes()) {
        inDegree.put(node.getId(), node.getDep().size());
    }

    List<List<TaskNode>> levels = new ArrayList<>();
    Queue<String> queue = new LinkedList<>(graph.getRootNodes().stream().map(TaskNode::getId).toList());

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<TaskNode> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String nodeId = queue.poll();
            TaskNode node = graph.getNodeMap().get(nodeId);
            level.add(node);

            // 减少后继节点的入度
            for (TaskNode successor : graph.getSuccessors(nodeId)) {
                inDegree.merge(successor.getId(), -1, Integer::sum);
                if (inDegree.get(successor.getId()) == 0) {
                    queue.offer(successor.getId());
                }
            }
        }
        levels.add(level);
    }
    return levels;
}
```

### 3. WebSocket 进度推送

```java
private void pushProgress(String conversationId, String executionId, String stepId, String status) {
    TaskExecution execution = executionRepository.findByExecutionId(executionId);

    WebSocketMessage msg = WebSocketMessage.builder()
            .type("progress")
            .payload(Map.of(
                "executionId", executionId,
                "stepId", stepId,
                "status", status,
                "completedSteps", execution.getCompletedSteps(),
                "totalSteps", execution.getTotalSteps()
            ))
            .timestamp(System.currentTimeMillis())
            .build();

    wsHandler.pushToConversation(conversationId, msg);
}
```
