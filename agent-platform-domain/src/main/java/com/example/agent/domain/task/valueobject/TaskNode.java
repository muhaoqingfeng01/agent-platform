package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务节点值对象 — Command 模式的命令封装.
 *
 * <p>对应 LLM 输出的单个任务步骤.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNode {

    /** 步骤唯一标识 */
    private String id;

    /** 动作类型（如 retrieve_orders、send_email） */
    private String action;

    /** 步骤描述 */
    private String description;

    /** 参数对象 */
    private Map<String, Object> params;

    /** 依赖的步骤 ID 列表（无依赖则为空数组） */
    private List<String> dep;

    // ==================== 工厂方法 ====================

    /** 创建无依赖的根节点 */
    public static TaskNode root(String id, String action, String description, Map<String, Object> params) {
        return TaskNode.builder()
                .id(id)
                .action(action)
                .description(description)
                .params(params != null ? params : new HashMap<>())
                .dep(new ArrayList<>())
                .build();
    }

    /** 创建有依赖的节点 */
    public static TaskNode dependent(String id, String action, String description,
                                      Map<String, Object> params, List<String> dep) {
        return TaskNode.builder()
                .id(id)
                .action(action)
                .description(description)
                .params(params != null ? params : new HashMap<>())
                .dep(dep != null ? dep : new ArrayList<>())
                .build();
    }

    /** 是否为根节点（无依赖） */
    public boolean isRoot() {
        return dep == null || dep.isEmpty();
    }

    /** 获取依赖数量 */
    public int dependencyCount() {
        return dep != null ? dep.size() : 0;
    }
}
