package com.example.agent.infrastructure.mcp;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import com.example.agent.infrastructure.config.nacos.SchedulerConfig;
import com.example.agent.infrastructure.config.scheduler.DynamicScheduledTaskManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MCP Client 连接管理器 — 管理所有 ACTIVE 状态 MCP 工具的长连接生命周期.
 *
 * <p>核心职责：
 * <ul>
 *   <li>应用启动时自动加载所有 ACTIVE 的 MCP 工具并建立连接</li>
 *   <li>定时刷新间隔从 {@link SchedulerConfig}（Nacos 动态配置）读取</li>
 *   <li>为上层调用方提供按 toolId 获取缓存的 MCP 客户端实例</li>
 * </ul>
 *
 * <p>连接存储：使用 ConcurrentHashMap 保证线程安全，key 为 toolId，
 * value 为 McpSyncClient（Spring AI MCP 同步客户端）.
 * 连接建立失败不会阻塞应用启动，仅记录错误日志.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpClientManager implements InitializingBean {

    /** MCP 客户端缓存 — key=toolId, value=已连接的 McpSyncClient */
    private final Map<String, Object> clients = new ConcurrentHashMap<>();

    /** 工具注册仓储 — 用于查询 ACTIVE 的 MCP 工具 */
    private final ToolRegistryRepository toolRepository;

    private final DynamicScheduledTaskManager dynamicScheduler;
    private final SchedulerConfig schedulerConfig;

    /**
     * 应用启动后立即执行 — 加载所有 ACTIVE 的 MCP 工具并建立连接，
     * 并注册定时刷新任务.
     */
    @Override
    public void afterPropertiesSet() {
        log.info("[MCP] 开始初始化 MCP 客户端连接...");
        refreshClients();
        log.info("[MCP] MCP 客户端初始化完成，当前连接数: {}", clients.size());

        // 注册动态定时刷新任务（替代 @Scheduled）
        dynamicScheduler.register(
                "mcpClientRefresh",
                this::refreshClients,
                schedulerConfig::getMcpClientRefreshMs);

        log.info("[MCP] 动态定时刷新任务已注册: mcpClientRefresh");
    }

    /**
     * 定时刷新 MCP 客户端连接.
     *
     * <p>刷新策略：
     * <ul>
     *   <li>查询数据库中所有 ACTIVE 状态的 MCP 类型工具</li>
     *   <li>新增工具：建立连接并加入缓存</li>
     *   <li>已停用工具：从缓存中移除并关闭连接</li>
     *   <li>已存在的连接保持不变，避免频繁重建</li>
     * </ul>
     */
    public void refreshClients() {
        try {
            List<ToolRegistry> mcpTools = toolRepository.findByTypeAndStatus(ToolType.MCP, ToolStatus.ACTIVE);
            log.debug("[MCP] 定时刷新: 发现 {} 个 ACTIVE MCP 工具", mcpTools.size());

            // 建立新连接：对尚未缓存的工具创建连接
            for (ToolRegistry tool : mcpTools) {
                if (!clients.containsKey(tool.getToolId())) {
                    connectAndCache(tool);
                }
            }

            // 移除已停用的连接：缓存中有但数据库查询结果中没有的
            Set<String> activeIds = mcpTools.stream()
                    .map(ToolRegistry::getToolId)
                    .collect(Collectors.toSet());
            clients.keySet().removeIf(id -> {
                if (!activeIds.contains(id)) {
                    log.info("[MCP] 移除已停用的 MCP 客户端: toolId={}", id);
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
            log.error("[MCP] 刷新 MCP 客户端连接异常", e);
        }
    }

    /**
     * 获取指定工具的 MCP 客户端实例 — 用于实际调用 MCP 工具.
     *
     * @param toolId 工具业务 ID
     * @return McpSyncClient 实例（Object 类型，调用方自行转型）
     * @throws BusinessException 如果 MCP 客户端未连接
     */
    public Object getClient(String toolId) {
        Object client = clients.get(toolId);
        if (client == null) {
            throw new BusinessException(500, "MCP 工具未连接或不可用: " + toolId
                    + "，请检查工具状态是否为 ACTIVE 且 MCP Server 是否正常运行");
        }
        return client;
    }

    /**
     * 获取当前已连接的 MCP 客户端数量.
     */
    public int getConnectionCount() {
        return clients.size();
    }

    /**
     * 判断指定工具是否已建立 MCP 连接.
     */
    public boolean isConnected(String toolId) {
        return clients.containsKey(toolId);
    }

    /**
     * 探测 MCP 工具连接是否存活 — 发送 list_tools 轻量请求.
     */
    public boolean ping(String toolId) {
        Object client = clients.get(toolId);
        if (client == null) {
            return false;
        }
        try {
            client.getClass().getMethod("listTools").invoke(client);
            return true;
        } catch (Exception e) {
            log.debug("[MCP] Ping 失败: toolId={}, error={}", toolId, e.getMessage());
            return false;
        }
    }

    /**
     * 从缓存中移除并关闭指定 MCP 客户端连接.
     */
    public void removeClient(String toolId) {
        Object client = clients.remove(toolId);
        if (client != null) {
            try {
                client.getClass().getMethod("close").invoke(client);
            } catch (Exception e) {
                log.debug("[MCP] 关闭 MCP 客户端连接异常: toolId={}, error={}", toolId, e.getMessage());
            }
            log.info("[MCP] 已移除 MCP 客户端: toolId={}", toolId);
        }
    }

    // ==================== 私有辅助方法 ====================

    private void connectAndCache(ToolRegistry tool) {
        try {
            log.info("[MCP] 正在连接 MCP Server: toolId={}, name={}, endpoint={}",
                    tool.getToolId(), tool.getName(), tool.getEndpoint());

            Object client = createMcpClient(tool);
            clients.put(tool.getToolId(), client);

            log.info("[MCP] MCP 客户端连接成功: toolId={}, name={}, endpoint={}",
                    tool.getToolId(), tool.getName(), tool.getEndpoint());

        } catch (Exception e) {
            log.error("[MCP] MCP 客户端连接失败: toolId={}, name={}, endpoint={}, error={}",
                    tool.getToolId(), tool.getName(), tool.getEndpoint(), e.getMessage());
        }
    }

    private Object createMcpClient(ToolRegistry tool) {
        try {
            java.net.URI serverUri = java.net.URI.create(tool.getEndpoint());

            Object transport = Class.forName(
                    "org.springframework.ai.mcp.client.transport.HttpClientSseClientTransport")
                    .getMethod("builder")
                    .invoke(null);
            transport = transport.getClass()
                    .getMethod("serverUri", java.net.URI.class)
                    .invoke(transport, serverUri);
            transport = transport.getClass()
                    .getMethod("build")
                    .invoke(transport);

            Class<?> mcpClientClass = Class.forName(
                    "org.springframework.ai.mcp.client.McpClient");
            Object mcpClientBuilder = mcpClientClass
                    .getMethod("sync", Object.class)
                    .invoke(null, transport);
            Object syncClient = mcpClientBuilder.getClass()
                    .getMethod("build")
                    .invoke(mcpClientBuilder);

            syncClient.getClass().getMethod("initialize").invoke(syncClient);

            return syncClient;

        } catch (Exception e) {
            log.error("[MCP] 创建 MCP 客户端失败: toolId={}, error={}",
                    tool.getToolId(), e.getMessage());
            throw new BusinessException(500, "MCP 客户端创建失败: " + e.getMessage());
        }
    }
}
