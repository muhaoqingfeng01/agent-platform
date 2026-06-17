package com.example.agent.infrastructure.mcp;

import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.domain.tool.service.ToolDomainService;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP 心跳检测器.
 *
 * <p>每 30 秒对所有 ACTIVE 的 MCP 工具发送 list_tools 探测请求.
 * <ul>
 *   <li>连续失败 3 次 → 自动 DISABLED + 告警日志</li>
 *   <li>恢复探测成功 1 次 → 重置计数器（需人工确认恢复）</li>
 * </ul>
 *
 * <p>与 McpClientManager 定时刷新的关系：
 * <ul>
 *   <li>心跳检测（30s）：快速发现故障 → 自动禁用</li>
 *   <li>定时刷新（5min）：全量重建连接 → 发现新工具</li>
 * </ul>
 * <p>两者互补，共同保障 MCP 连接可靠性.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpHeartbeatDetector {

    private final ToolRegistryRepository toolRepository;
    private final ToolDomainService toolDomainService;
    private final McpClientManager mcpClientManager;

    /** 连续失败计数器: toolId → failureCount */
    private final Map<String, Integer> failureCounters = new ConcurrentHashMap<>();

    private static final int MAX_FAILURES = 3;
    private static final int HEARTBEAT_INTERVAL = 30_000;  // 30 秒

    /**
     * 定时心跳检测 — 每 30 秒执行.
     */
    @Scheduled(fixedDelay = HEARTBEAT_INTERVAL)
    public void detectHeartbeat() {
        try {
            List<ToolRegistry> activeMcpTools = toolRepository.findByTypeAndStatus(
                ToolType.MCP, ToolStatus.ACTIVE);

            log.debug("[MCP-Heartbeat] 检测 {} 个 ACTIVE MCP 工具", activeMcpTools.size());

            for (ToolRegistry tool : activeMcpTools) {
                try {
                    boolean alive = mcpClientManager.ping(tool.getToolId());

                    if (alive) {
                        // 成功 → 重置计数器
                        Integer previousFailures = failureCounters.remove(tool.getToolId());
                        if (previousFailures != null && previousFailures >= MAX_FAILURES) {
                            log.warn("[MCP-Heartbeat] MCP 工具已恢复: toolId={} (之前已自动禁用，需人工确认恢复)",
                                tool.getToolId());
                        }
                    } else {
                        recordFailure(tool);
                    }
                } catch (Exception e) {
                    log.error("[MCP-Heartbeat] 心跳检测异常: toolId={}, error={}",
                        tool.getToolId(), e.getMessage());
                    recordFailure(tool);
                }
            }

            // 清理已不存在或非 ACTIVE 工具的计数器
            List<String> activeIds = activeMcpTools.stream()
                .map(ToolRegistry::getToolId)
                .toList();
            failureCounters.keySet().removeIf(id -> !activeIds.contains(id));

        } catch (Exception e) {
            log.error("[MCP-Heartbeat] 心跳检测轮次异常", e);
        }
    }

    /**
     * 获取指定工具的当前连续失败次数.
     *
     * @param toolId 工具业务 ID
     * @return 失败次数（0 表示正常）
     */
    public int getFailureCount(String toolId) {
        return failureCounters.getOrDefault(toolId, 0);
    }

    // ==================== 私有方法 ====================

    /**
     * 记录一次心跳失败，达到阈值后自动禁用工具.
     */
    private void recordFailure(ToolRegistry tool) {
        int failures = failureCounters.merge(tool.getToolId(), 1, Integer::sum);
        log.warn("[MCP-Heartbeat] MCP 心跳失败: toolId={}, 连续失败次数={}/{}",
            tool.getToolId(), failures, MAX_FAILURES);

        if (failures >= MAX_FAILURES) {
            try {
                // 自动禁用
                toolDomainService.assertCanDisable(tool);
                tool.disable();
                toolRepository.updateStatus(tool.getToolId(), ToolStatus.DISABLED);

                // 清除 MCP Client 连接缓存
                mcpClientManager.removeClient(tool.getToolId());

                log.error("[MCP-Heartbeat] MCP 工具已自动禁用: toolId={}, name={}, 原因: 连续 {} 次心跳失败",
                    tool.getToolId(), tool.getName(), MAX_FAILURES);
                // TODO: 发送告警通知（飞书/钉钉/Telegram）

            } catch (Exception e) {
                log.error("[MCP-Heartbeat] 自动禁用工具失败: toolId={}, error={}",
                    tool.getToolId(), e.getMessage());
            }
        }
    }
}
