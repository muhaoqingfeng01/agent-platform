package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAG 有向无环图值对象 — Composite + Builder 模式.
 *
 * <p>封装 LLM 任务规划解析后的图结构，包含节点、邻接表、根节点、拓扑层级。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DagGraph {

    /** 所有任务节点 */
    private List<TaskNode> nodes;

    /** 邻接表: nodeId → 依赖它的后继节点 */
    private Map<String, Set<String>> adjacency;

    /** 无依赖的根节点（可并行执行） */
    private List<TaskNode> rootNodes;

    /** 拓扑分层结果（BFS 计算后填充） */
    private List<List<TaskNode>> topologicalLevels;

    // ==================== 工厂方法 ====================

    /**
     * 从 TaskNode 列表构建 DagGraph.
     *
     * @param nodes 任务节点列表
     * @param adjacency 邻接表
     * @param rootNodes 根节点列表
     * @return DagGraph 实例
     */
    public static DagGraph of(List<TaskNode> nodes, Map<String, Set<String>> adjacency, List<TaskNode> rootNodes) {
        return DagGraph.builder()
                .nodes(nodes)
                .adjacency(adjacency)
                .rootNodes(rootNodes)
                .topologicalLevels(new ArrayList<>())
                .build();
    }

    /** 获取节点索引（供快速查找） */
    public Map<String, TaskNode> getNodeMap() {
        Map<String, TaskNode> map = new HashMap<>();
        for (TaskNode node : nodes) {
            map.put(node.getId(), node);
        }
        return map;
    }

    /** 获取某节点的后继节点 */
    public List<TaskNode> getSuccessors(String nodeId) {
        Map<String, TaskNode> nodeMap = getNodeMap();
        Set<String> successorIds = adjacency.getOrDefault(nodeId, Set.of());
        List<TaskNode> successors = new ArrayList<>();
        for (String sid : successorIds) {
            TaskNode node = nodeMap.get(sid);
            if (node != null) {
                successors.add(node);
            }
        }
        return successors;
    }

    /** 获取总节点数 */
    public int size() {
        return nodes != null ? nodes.size() : 0;
    }

    /** 判断图是否为空 */
    public boolean isEmpty() {
        return nodes == null || nodes.isEmpty();
    }
}
