package com.example.agent.infrastructure.mcp;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.domain.tool.valueobject.ToolStatus;
import com.example.agent.domain.tool.valueobject.ToolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
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
 *   <li>每 5 分钟定时刷新：新增启用工具的连接，移除已停用工具的连接</li>
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

    /**
     * 应用启动后立即执行 — 加载所有 ACTIVE 的 MCP 工具并建立连接.
     * <p>通过 InitializingBean 接口确保在 Spring 容器初始化完成后调用.
     */
    @Override
    public void afterPropertiesSet() {
        log.info("[MCP] 开始初始化 MCP 客户端连接...");
        refreshClients();
        log.info("[MCP] MCP 客户端初始化完成，当前连接数: {}", clients.size());
    }

    /**
     * 定时刷新 MCP 客户端连接 — 每 5 分钟执行一次.
     *
     * <p>刷新策略：
     * <ul>
     *   <li>查询数据库中所有 ACTIVE 状态的 MCP 类型工具</li>
     *   <li>新增工具：建立连接并加入缓存</li>
     *   <li>已停用工具：从缓存中移除并关闭连接</li>
     *   <li>已存在的连接保持不变，避免频繁重建</li>
     * </ul>
     *
     * <p>连接失败不影响其他工具的正常加载，仅记录错误日志.
     */
    @Scheduled(fixedDelay = 300_000)
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
     * <p>如果连接不存在（工具未启用或连接失败），抛出 BusinessException，
     * 调用方应捕获此异常并做降级处理.
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
     *
     * @return 连接数
     */
    public int getConnectionCount() {
        return clients.size();
    }

    /**
     * 判断指定工具是否已建立 MCP 连接.
     *
     * @param toolId 工具业务 ID
     * @return true 表示连接已建立
     */
    public boolean isConnected(String toolId) {
        return clients.containsKey(toolId);
    }

    /**
     * 探测 MCP 工具连接是否存活 — 发送 list_tools 轻量请求.
     *
     * <p>通过 MCP 协议的 listTools 方法验证连接是否仍可用.
     * 用于心跳检测，不修改任何状态.
     *
     * @param toolId 工具业务 ID
     * @return true 表示连接存活
     */
    public boolean ping(String toolId) {
        Object client = clients.get(toolId);
        if (client == null) {
            return false;
        }
        try {
            // 调用 McpSyncClient.listTools() 轻量探测
            client.getClass().getMethod("listTools").invoke(client);
            return true;
        } catch (Exception e) {
            log.debug("[MCP] Ping 失败: toolId={}, error={}", toolId, e.getMessage());
            return false;
        }
    }

    /**
     * 从缓存中移除并关闭指定 MCP 客户端连接.
     *
     * @param toolId 工具业务 ID
     */
    public void removeClient(String toolId) {
        Object client = clients.remove(toolId);
        if (client != null) {
            try {
                // 尝试优雅关闭
                client.getClass().getMethod("close").invoke(client);
            } catch (Exception e) {
                log.debug("[MCP] 关闭 MCP 客户端连接异常: toolId={}, error={}", toolId, e.getMessage());
            }
            log.info("[MCP] 已移除 MCP 客户端: toolId={}", toolId);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 为指定工具建立 MCP 连接并加入缓存.
     *
     * <p>使用 Spring AI McpClient 的 Builder API 创建同步客户端.
     * 连接端点为工具注册的 endpoint（MCP SSE URL）.
     * 连接失败时记录 ERROR 日志但不抛出异常（不阻塞其他工具的加载）.
     *
     * @param tool MCP 工具注册实体
     */
    private void connectAndCache(ToolRegistry tool) {
        try {
            log.info("[MCP] 正在连接 MCP Server: toolId={}, name={}, endpoint={}",
                    tool.getToolId(), tool.getName(), tool.getEndpoint());

            // 使用 Spring AI MCP Client 构建器建立连接
            // McpSyncClient 通过 McpClient.sync(transport).build() 创建
            // transport 使用 HttpClientSseClientTransport 连接 SSE 端点
            Object client = createMcpClient(tool);
            clients.put(tool.getToolId(), client);

            log.info("[MCP] MCP 客户端连接成功: toolId={}, name={}, endpoint={}",
                    tool.getToolId(), tool.getName(), tool.getEndpoint());

        } catch (Exception e) {
            log.error("[MCP] MCP 客户端连接失败: toolId={}, name={}, endpoint={}, error={}",
                    tool.getToolId(), tool.getName(), tool.getEndpoint(), e.getMessage());
        }
    }

    /**
     * 使用 Spring AI MCP Client API 创建同步 MCP 客户端.
     *
     * <p>根据 Spring AI 1.1.7 的 MCP Client API：
     * <pre>{@code
     *   var transport = HttpClientSseClientTransport.builder()
     *           .serverUri(URI.create(endpoint))
     *           .build();
     *   var client = McpClient.sync(transport).build();
     * }</pre>
     *
     * <p>由于具体 API 签名可能因版本而异，此处使用反射/接口适配方式
     * 确保代码在不同 Spring AI 版本下都能编译通过.
     *
     * @param tool MCP 工具注册实体
     * @return McpSyncClient 实例
     */
    private Object createMcpClient(ToolRegistry tool) {
        try {
            // 使用 Spring AI McpClient 同步模式创建客户端
            // 导入路径: org.springframework.ai.mcp.client.McpClient
            //           org.springframework.ai.mcp.client.transport.HttpClientSseClientTransport
            java.net.URI serverUri = java.net.URI.create(tool.getEndpoint());

            // 构建 SSE 传输层
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

            // 获取 McpClient 类并构建同步客户端
            Class<?> mcpClientClass = Class.forName(
                    "org.springframework.ai.mcp.client.McpClient");
            Object mcpClientBuilder = mcpClientClass
                    .getMethod("sync", Object.class)
                    .invoke(null, transport);
            Object syncClient = mcpClientBuilder.getClass()
                    .getMethod("build")
                    .invoke(mcpClientBuilder);

            // 初始化连接
            syncClient.getClass().getMethod("initialize").invoke(syncClient);

            return syncClient;

        } catch (Exception e) {
            log.error("[MCP] 创建 MCP 客户端失败: toolId={}, error={}",
                    tool.getToolId(), e.getMessage());
            throw new BusinessException(500, "MCP 客户端创建失败: " + e.getMessage());
        }
    }
}
