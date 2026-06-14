# 任务规划与 DAG 解析

## 所属阶段
**P1 核心能力 → T5 任务规划与执行引擎**

## 使用技术
- Spring AI（LLM 任务规划）
- 自研 DAG 解析器
- Jackson JSON

## 涉及数据库表
- `t_task_execution`

## 实现方案

### 1. 任务规划流程

```
复杂用户意图
    │
    ▼
┌────────────────────┐
│ LLM 任务规划 Prompt  │ ← 要求输出 JSON 任务序列
└──────┬─────────────┘
       │
       ▼
┌────────────────────┐
│ 解析 JSON → DAG 图  │ ← DagParser
└──────┬─────────────┘
       │
       ▼
┌────────────────────┐
│ 校验 DAG 合法性      │ ← 无循环依赖 / 节点 ID 唯一
└──────┬─────────────┘
       │
       ▼
┌────────────────────┐
│ 生成 execution_id   │ → 提交 DagExecutor
└────────────────────┘
```

### 2. LLM 任务规划提示词

```
你是一个任务规划器。根据用户意图，将复杂任务拆解为可执行的步骤序列。

## 可用动作
{available_actions}

## 输出格式
返回 JSON 数组，每个元素包含：
- "id": 步骤唯一标识（字符串）
- "action": 动作类型（从可用动作中选择）
- "description": 步骤描述
- "params": 参数对象
- "dep": 依赖的步骤 ID 列表（无依赖则为空数组）

## 示例
用户: "帮我查一下上周的订单总额并发送邮件给张三"
输出:
[
  {"id":"1","action":"retrieve_orders","description":"查询上周订单","params":{"period":"last_week"},"dep":[]},
  {"id":"2","action":"calculate_sum","description":"计算总额","params":{"field":"amount"},"dep":["1"]},
  {"id":"3","action":"send_email","description":"发送邮件","params":{"to":"zhangsan","subject":"订单总额"},"dep":["2"]}
]

## 用户意图
{user_intent}
```

### 3. DAG 解析器

```java
@Component
public class DagParser {

    public DagGraph parse(String llmResponse) {
        List<TaskNode> nodes = JsonUtil.fromJsonList(llmResponse, TaskNode.class);

        // 构建节点索引
        Map<String, TaskNode> nodeMap = nodes.stream()
                .collect(Collectors.toMap(TaskNode::getId, Function.identity()));

        // 构造邻接表
        Map<String, Set<String>> adjacency = new HashMap<>();
        for (TaskNode node : nodes) {
            adjacency.put(node.getId(), new HashSet<>(node.getDep()));
        }

        // 循环依赖检测 (DFS)
        if (hasCycle(adjacency)) {
            throw new BusinessException(400, "DAG 存在循环依赖，请检查任务规划");
        }

        return new DagGraph(nodes, adjacency, findRootNodes(nodes));
    }

    private boolean hasCycle(Map<String, Set<String>> graph) {
        Set<String> visiting = new HashSet<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            if (dfs(node, graph, visiting, visited)) return true;
        }
        return false;
    }

    private boolean dfs(String node, Map<String, Set<String>> graph,
                        Set<String> visiting, Set<String> visited) {
        if (visiting.contains(node)) return true;  // 发现环
        if (visited.contains(node)) return false;

        visiting.add(node);
        for (String dep : graph.getOrDefault(node, Set.of())) {
            if (dfs(dep, graph, visiting, visited)) return true;
        }
        visiting.remove(node);
        visited.add(node);
        return false;
    }

    private List<TaskNode> findRootNodes(List<TaskNode> nodes) {
        return nodes.stream()
                .filter(n -> n.getDep().isEmpty())
                .toList();
    }
}
```

### 4. 数据模型

```java
@Data
public class DagGraph {
    private List<TaskNode> nodes;
    private Map<String, Set<String>> adjacency;  // 邻接表
    private List<TaskNode> rootNodes;             // 无依赖的根节点
}

@Data
public class TaskNode {
    private String id;          // 步骤 ID
    private String action;      // 动作类型
    private String description; // 描述
    private Map<String, Object> params;  // 参数
    private List<String> dep;   // 依赖步骤 ID 列表
}
```
