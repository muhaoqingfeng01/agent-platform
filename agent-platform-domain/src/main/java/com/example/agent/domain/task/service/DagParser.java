package com.example.agent.domain.task.service;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.task.valueobject.DagGraph;
import com.example.agent.domain.task.valueobject.TaskNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * DAG 解析领域服务 — 负责 JSON → DAG 图的解析、合法性校验、拓扑排序.
 *
 * <p>设计模式: <b>Strategy</b>（可替换不同的图算法）+ <b>Template Method</b>（parse → validate → sort）
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class DagParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解析 LLM 返回的 JSON 任务序列为 DAG 图.
     *
     * @param llmResponse LLM 输出的 JSON 字符串
     * @return 校验通过的 DAG 图
     * @throws BusinessException 如果 JSON 格式错误或存在循环依赖
     */
    public DagGraph parse(String llmResponse) {
        // 1. 解析 JSON → TaskNode 列表
        List<TaskNode> nodes = parseJson(llmResponse);
        if (nodes == null || nodes.isEmpty()) {
            throw new BusinessException(400, "LLM 未输出有效的任务规划");
        }

        // 2. 构建节点索引
        Map<String, TaskNode> nodeMap = new LinkedHashMap<>();
        for (TaskNode node : nodes) {
            if (nodeMap.containsKey(node.getId())) {
                throw new BusinessException(400, "DAG 节点 ID 重复: " + node.getId());
            }
            nodeMap.put(node.getId(), node);
        }

        // 3. 验证依赖引用完整性
        validateDependencies(nodes, nodeMap);

        // 4. 构造邻接表
        Map<String, Set<String>> adjacency = buildAdjacency(nodes, nodeMap);

        // 5. 循环依赖检测 (DFS 三色标记)
        if (hasCycle(adjacency)) {
            throw new BusinessException(400, "DAG 存在循环依赖，请检查任务规划");
        }

        // 6. 查找根节点
        List<TaskNode> rootNodes = findRootNodes(nodes);

        // 7. 拓扑分层
        List<List<TaskNode>> levels = topologicalLevels(nodes, adjacency, rootNodes);

        DagGraph graph = DagGraph.of(nodes, adjacency, rootNodes);
        graph.setTopologicalLevels(levels);

        log.info("[DagParser] 解析成功: nodes={}, levels={}, roots={}",
                nodes.size(), levels.size(), rootNodes.size());
        return graph;
    }

    // ==================== 解析 ====================

    private List<TaskNode> parseJson(String llmResponse) {
        try {
            String json = extractJsonBlock(llmResponse);
            return objectMapper.readValue(json, new TypeReference<List<TaskNode>>() {});
        } catch (JsonProcessingException e) {
            log.error("[DagParser] JSON 解析失败: {}", llmResponse, e);
            throw new BusinessException(400, "任务规划 JSON 格式错误: " + e.getMessage());
        }
    }

    private String extractJsonBlock(String text) {
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.indexOf("```", start);
            if (end > start) return text.substring(start, end).trim();
        }
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.indexOf("```", start);
            if (end > start) return text.substring(start, end).trim();
        }
        // 尝试找最外层 JSON 数组
        int arrStart = text.indexOf('[');
        int arrEnd = text.lastIndexOf(']');
        if (arrStart >= 0 && arrEnd > arrStart) {
            return text.substring(arrStart, arrEnd + 1);
        }
        return text.trim();
    }

    // ==================== 验证 ====================

    private void validateDependencies(List<TaskNode> nodes, Map<String, TaskNode> nodeMap) {
        for (TaskNode node : nodes) {
            for (String depId : node.getDep()) {
                if (!nodeMap.containsKey(depId)) {
                    throw new BusinessException(400,
                            String.format("步骤 '%s' 依赖的步骤 '%s' 不存在", node.getId(), depId));
                }
            }
        }
    }

    // ==================== 邻接表 ====================

    private Map<String, Set<String>> buildAdjacency(List<TaskNode> nodes, Map<String, TaskNode> nodeMap) {
        Map<String, Set<String>> adjacency = new LinkedHashMap<>();
        for (TaskNode node : nodes) {
            adjacency.put(node.getId(), new LinkedHashSet<>());
        }
        // 反向构建: dep → 被依赖的后继节点
        for (TaskNode node : nodes) {
            for (String depId : node.getDep()) {
                adjacency.get(depId).add(node.getId());
            }
        }
        return adjacency;
    }

    // ==================== 环检测 ====================

    /**
     * DFS 三色标记检测有向图环.
     * <ul>
     *   <li>WHITE (不在 visiting/visited) → 未访问</li>
     *   <li>GRAY (在 visiting 中) → 当前路径正在访问 → 发现环</li>
     *   <li>BLACK (在 visited 中) → 已完成，无需再访问</li>
     * </ul>
     */
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
        if (visiting.contains(node)) return true;   // 发现环
        if (visited.contains(node)) return false;    // 已处理完

        visiting.add(node);
        for (String successor : graph.getOrDefault(node, Set.of())) {
            if (dfs(successor, graph, visiting, visited)) return true;
        }
        visiting.remove(node);
        visited.add(node);
        return false;
    }

    // ==================== 拓扑排序 ====================

    private List<TaskNode> findRootNodes(List<TaskNode> nodes) {
        return nodes.stream()
                .filter(TaskNode::isRoot)
                .toList();
    }

    /**
     * BFS 拓扑分层 — Khan 算法.
     * <p>每层内的节点无相互依赖，可并行执行.
     */
    public List<List<TaskNode>> topologicalLevels(List<TaskNode> nodes,
                                                   Map<String, Set<String>> adjacency,
                                                   List<TaskNode> rootNodes) {
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        for (TaskNode node : nodes) {
            inDegree.put(node.getId(), node.dependencyCount());
        }

        List<List<TaskNode>> levels = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        for (TaskNode root : rootNodes) {
            queue.offer(root.getId());
        }

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<TaskNode> level = new ArrayList<>();
            for (int i = 0; i < levelSize; i++) {
                String nodeId = queue.poll();
                TaskNode node = nodes.stream()
                        .filter(n -> n.getId().equals(nodeId))
                        .findFirst().orElse(null);
                if (node != null) {
                    level.add(node);
                }

                // 减少后继节点入度
                for (String successorId : adjacency.getOrDefault(nodeId, Set.of())) {
                    inDegree.merge(successorId, -1, Integer::sum);
                    if (inDegree.get(successorId) == 0) {
                        queue.offer(successorId);
                    }
                }
            }
            if (!level.isEmpty()) {
                levels.add(level);
            }
        }

        // 检测: 如果分层节点数 < 总节点数，说明有孤立节点或未处理节点
        long processed = levels.stream().mapToLong(List::size).sum();
        if (processed < nodes.size()) {
            log.warn("[DagParser] 拓扑分层未覆盖所有节点: processed={}, total={}", processed, nodes.size());
        }

        return levels;
    }
}
