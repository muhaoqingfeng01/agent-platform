package com.example.agent.application.tool;

import com.example.agent.application.tool.dto.*;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.tool.entity.ToolInvocationLog;
import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.entity.ToolRegistryVersion;
import com.example.agent.domain.tool.repository.ToolInvocationLogRepository;
import com.example.agent.domain.tool.repository.ToolRegistryRepository;
import com.example.agent.domain.tool.repository.ToolRegistryVersionRepository;
import com.example.agent.domain.tool.service.ToolDomainService;
import com.example.agent.domain.tool.valueobject.*;
import com.example.agent.infrastructure.mcp.HttpToolAdapter;
import com.example.agent.infrastructure.mcp.McpClientManager;
import com.example.agent.infrastructure.persistence.cache.ToolCacheManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 工具应用服务 — 工具注册与调用的核心编排层.
 *
 * <p>负责协调领域服务、仓储和基础设施组件（MCP Client、HTTP Adapter），
 * 对外提供面向用例的粗粒度 API（CRUD + 测试 + 调用日志查询）.
 *
 * <p>设计模式：Facade（门面模式）— 将复杂的子系统交互封装为简单接口.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolApplicationService {

    /** JSON 序列化工具 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** 工具注册仓储 */
    private final ToolRegistryRepository toolRepository;

    /** 工具调用日志仓储 */
    private final ToolInvocationLogRepository invocationLogRepository;

    /** 工具领域服务 — 业务规则校验 */
    private final ToolDomainService domainService;

    /** MCP Client 连接管理器 */
    private final McpClientManager mcpClientManager;

    /** HTTP 工具适配器 */
    private final HttpToolAdapter httpToolAdapter;

    /** 工具 Redis 二级缓存管理器 */
    private final ToolCacheManager toolCacheManager;

    /** 工具版本历史仓储 */
    private final ToolRegistryVersionRepository versionRepository;

    // ==================== 工具 CRUD ====================

    /**
     * 注册新工具 — 创建工具注册记录.
     *
     * <p>流程：
     * <ol>
     *   <li>生成工具业务 ID（IdGenerator）</li>
     *   <li>组装领域实体（默认状态 ACTIVE）</li>
     *   <li>调用领域服务校验业务不变量</li>
     *   <li>持久化到数据库</li>
     * </ol>
     *
     * @param request 创建工具请求
     * @return 创建后的工具详情
     */
    @Transactional
    public ToolResponse create(CreateToolRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.info("[Tool] 创建工具: name={}, type={}, tenantId={}", request.getName(), request.getToolType(), tenantId);

        // 组装 ToolSchema
        ToolSchema schema = ToolSchema.builder()
                .inputSchema(request.getInputSchema())
                .outputSchema(request.getOutputSchema())
                .build();

        // 组装 AuthConfig
        AuthConfig authConfig = buildAuthConfig(request);

        // 组装领域实体
        ToolRegistry tool = ToolRegistry.builder()
                .toolId(IdGenerator.generate("tool"))
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .toolType(ToolType.fromCode(request.getToolType()))
                .schema(schema)
                .endpoint(request.getEndpoint())
                .authConfig(authConfig)
                .requireApproval(request.isRequireApproval())
                .status(ToolStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 领域校验
        domainService.validateNewTool(tool);

        // 持久化
        toolRepository.save(tool);

        log.info("[Tool] 工具创建成功: toolId={}, name={}", tool.getToolId(), tool.getName());
        return ToolResponse.from(tool);
    }

    /**
     * 获取工具详情.
     *
     * @param toolId 工具业务 ID
     * @return 工具详情
     * @throws ResourceNotFoundException 如果工具不存在
     */
    public ToolResponse getByToolId(String toolId) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.debug("[Tool] 查询工具详情: toolId={}", toolId);

        // L2 缓存（Redis）→ 回源 MySQL
        ToolRegistry tool = toolCacheManager.get(tenantId, toolId)
                .orElseGet(() -> {
                    ToolRegistry t = toolRepository.findByToolId(toolId)
                            .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
                    toolCacheManager.put(t);  // 回填 Redis
                    return t;
                });

        domainService.assertTenantAccess(tool, tenantId);

        return ToolResponse.from(tool);
    }

    /**
     * 工具列表 — 支持按类型筛选，分页返回.
     *
     * @param page     页码（从 0 开始）
     * @param size     每页数量
     * @param toolType 工具类型筛选（可选，null 或空字符串表示全部）
     * @return 分页结果
     */
    public PageResponse<ToolResponse> list(int page, int size, String toolType) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.debug("[Tool] 查询工具列表: page={}, size={}, type={}", page, size, toolType);

        List<ToolRegistry> tools;
        long total;

        if (toolType != null && !toolType.isBlank()) {
            ToolType type = ToolType.fromCode(toolType);
            tools = toolRepository.findByTenantAndType(tenantId, type, page, size);
            total = toolRepository.countByTenantAndType(tenantId, type);
        } else {
            tools = toolRepository.findByTenant(tenantId, page, size);
            total = toolRepository.countByTenant(tenantId);
        }

        List<ToolResponse> records = tools.stream()
                .map(ToolResponse::from)
                .toList();

        return PageResponse.of(records, total, page, size);
    }

    /**
     * 编辑工具配置 — 仅更新非 null 字段.
     *
     * <p>使用 Builder 的 toBuilder 模式进行不可变更新：
     * 先复制现有字段，再用请求中的非 null 值覆盖.
     *
     * @param toolId  工具业务 ID
     * @param request 更新请求（全可选字段）
     * @return 更新后的工具详情
     */
    @Transactional
    public ToolResponse update(String toolId, UpdateToolRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.info("[Tool] 编辑工具: toolId={}", toolId);

        ToolRegistry existing = toolRepository.findByToolId(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
        domainService.assertTenantAccess(existing, tenantId);

        // 使用 toBuilder 模式进行不可变更新
        ToolRegistry.ToolRegistryBuilder builder = existing.toBuilder();

        if (request.getName() != null) builder.name(request.getName());
        if (request.getDescription() != null) builder.description(request.getDescription());
        if (request.getToolType() != null) builder.toolType(ToolType.fromCode(request.getToolType()));
        if (request.getEndpoint() != null) builder.endpoint(request.getEndpoint());
        if (request.getRequireApproval() != null) builder.requireApproval(request.getRequireApproval());

        // Schema 部分更新
        if (request.getInputSchema() != null || request.getOutputSchema() != null) {
            ToolSchema.ToolSchemaBuilder schemaBuilder = ToolSchema.builder();
            if (existing.getSchema() != null) {
                schemaBuilder.inputSchema(existing.getSchema().getInputSchema())
                             .outputSchema(existing.getSchema().getOutputSchema());
            }
            if (request.getInputSchema() != null) schemaBuilder.inputSchema(request.getInputSchema());
            if (request.getOutputSchema() != null) schemaBuilder.outputSchema(request.getOutputSchema());
            builder.schema(schemaBuilder.build());
        }

        // AuthConfig 部分更新
        if (hasAuthUpdate(request)) {
            AuthConfig.AuthConfigBuilder authBuilder = AuthConfig.builder();
            if (existing.getAuthConfig() != null) {
                AuthConfig existingAuth = existing.getAuthConfig();
                authBuilder.authType(existingAuth.getAuthType())
                           .apiKey(existingAuth.getApiKey())
                           .token(existingAuth.getToken())
                           .username(existingAuth.getUsername())
                           .password(existingAuth.getPassword())
                           .headers(existingAuth.getHeaders());
            }
            if (request.getAuthType() != null) authBuilder.authType(request.getAuthType());
            if (request.getApiKey() != null) authBuilder.apiKey(request.getApiKey());
            if (request.getToken() != null) authBuilder.token(request.getToken());
            builder.authConfig(authBuilder.build());
        }

        builder.updatedAt(LocalDateTime.now());
        ToolRegistry updated = builder.build();

        toolRepository.update(updated);

        // 清除缓存
        toolCacheManager.refresh(tenantId, toolId);

        log.info("[Tool] 工具更新成功: toolId={}", toolId);
        return ToolResponse.from(updated);
    }

    /**
     * 启停工具 — 切换 ACTIVE / DISABLED 状态.
     *
     * @param toolId    工具业务 ID
     * @param newStatus 目标状态（ACTIVE 或 DISABLED）
     * @return 更新后的工具详情
     */
    @Transactional
    public ToolResponse toggleStatus(String toolId, String newStatus) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.info("[Tool] 启停工具: toolId={}, targetStatus={}", toolId, newStatus);

        ToolRegistry tool = toolRepository.findByToolId(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
        domainService.assertTenantAccess(tool, tenantId);

        ToolStatus targetStatus = ToolStatus.fromCode(newStatus);

        // 领域校验 + 状态切换
        if (targetStatus == ToolStatus.ACTIVE) {
            domainService.assertCanEnable(tool);
            tool.enable();
        } else {
            domainService.assertCanDisable(tool);
            tool.disable();
        }

        toolRepository.updateStatus(toolId, targetStatus);

        // 清除缓存
        toolCacheManager.refresh(tenantId, toolId);

        log.info("[Tool] 工具状态切换成功: toolId={}, status={}", toolId, targetStatus.toChinese());
        return ToolResponse.from(tool);
    }

    // ==================== 版本历史 ====================

    /**
     * 版本历史列表.
     */
    public List<VersionResponse> getVersionHistory(String toolId) {
        String tenantId = TenantContext.getCurrentTenantId();
        // 校验存在 + 租户隔离
        ToolRegistry tool = toolRepository.findByToolId(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
        domainService.assertTenantAccess(tool, tenantId);

        return versionRepository.findByToolId(toolId).stream()
                .map(VersionResponse::from)
                .toList();
    }

    /**
     * 版本详情.
     */
    public VersionResponse getVersionDetail(String toolId, int version) {
        String tenantId = TenantContext.getCurrentTenantId();
        ToolRegistry tool = toolRepository.findByToolId(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
        domainService.assertTenantAccess(tool, tenantId);

        ToolRegistryVersion v = versionRepository.findByToolIdAndVersion(toolId, version)
                .orElseThrow(() -> new ResourceNotFoundException("工具版本", toolId + " v" + version));
        return VersionResponse.from(v);
    }

    /**
     * 回滚到指定版本.
     */
    @Transactional
    public ToolResponse rollback(String toolId, int targetVersion) {
        String tenantId = TenantContext.getCurrentTenantId();
        String operator = TenantContext.getCurrentUserId();

        ToolRegistry tool = toolRepository.findByToolId(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
        domainService.assertTenantAccess(tool, tenantId);

        domainService.rollback(tool, targetVersion, operator);

        // 清除缓存
        toolCacheManager.refresh(tenantId, toolId);

        log.info("[Tool] 回滚成功: toolId={}, targetVersion={}", toolId, targetVersion);
        return ToolResponse.from(tool);
    }

    // ==================== 工具测试 ====================

    /**
     * 测试工具调用 — 支持 MCP 和 HTTP 两种类型.
     *
     * <p>流程：
     * <ol>
     *   <li>查找工具并校验租户权限</li>
     *   <li>根据 toolType 选择调用方式（MCP Client / HTTP Adapter）</li>
     *   <li>记录调用耗时和结果</li>
     *   <li>持久化调用日志到 t_tool_invocation_log</li>
     * </ol>
     *
     * <p>BUILTIN 和 CUSTOM 类型暂不支持在线测试（需通过 T5 任务引擎）.
     *
     * @param toolId 工具业务 ID
     * @param params 调用参数
     * @return 测试结果（成功/失败 + 返回值 + 耗时）
     */
    @Auditable(action = "TOOL_TEST", resourceType = "TOOL", resourceId = "#toolId", recordResponse = true)
    @Transactional
    public ToolTestResponse test(String toolId, Map<String, Object> params) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.info("[Tool] 测试工具调用: toolId={}", toolId);

        ToolRegistry tool = toolRepository.findByToolId(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("工具", toolId));
        domainService.assertTenantAccess(tool, tenantId);

        long startTime = System.currentTimeMillis();
        String paramsJson = toJson(params);
        String invocationId = IdGenerator.generate("tlinv");

        try {
            Object result;
            switch (tool.getToolType()) {
                case MCP:
                    // 通过 McpClientManager 获取缓存的 MCP 客户端并调用
                    Object mcpClient = mcpClientManager.getClient(toolId);
                    result = invokeMcpTool(mcpClient, tool, params);
                    break;

                case HTTP:
                    // 通过 HttpToolAdapter 发起 HTTP 调用
                    result = httpToolAdapter.invokeHttpTool(tool, params);
                    break;

                case BUILTIN:
                case CUSTOM:
                    // 内置和自定义工具需通过 T5 任务引擎测试
                    throw new BusinessException(400,
                            tool.getToolType().toChinese() + " 暂不支持在线测试，请通过任务引擎触发");

                default:
                    throw new BusinessException(400, "未知的工具类型: " + tool.getToolType());
            }

            long durationMs = System.currentTimeMillis() - startTime;
            String resultJson = toJson(result);

            // 记录成功日志
            saveInvocationLog(invocationId, tenantId, toolId, null, null, null,
                    paramsJson, resultJson, InvocationStatus.SUCCESS, durationMs, null);

            log.info("[Tool] 工具测试成功: toolId={}, durationMs={}", toolId, durationMs);

            return ToolTestResponse.builder()
                    .success(true)
                    .result(result)
                    .durationMs(durationMs)
                    .invocationId(invocationId)
                    .build();

        } catch (BusinessException e) {
            long durationMs = System.currentTimeMillis() - startTime;
            saveInvocationLog(invocationId, tenantId, toolId, null, null, null,
                    paramsJson, null,
                    e.getMessage().contains("审批驳回") ? InvocationStatus.REJECTED : InvocationStatus.FAILED,
                    durationMs, e.getMessage());

            log.error("[Tool] 工具测试失败: toolId={}, error={}", toolId, e.getMessage());

            return ToolTestResponse.builder()
                    .success(false)
                    .durationMs(durationMs)
                    .errorMessage(e.getMessage())
                    .invocationId(invocationId)
                    .build();

        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - startTime;
            saveInvocationLog(invocationId, tenantId, toolId, null, null, null,
                    paramsJson, null, InvocationStatus.FAILED, durationMs, e.getMessage());

            log.error("[Tool] 工具测试异常: toolId={}", toolId, e);

            return ToolTestResponse.builder()
                    .success(false)
                    .durationMs(durationMs)
                    .errorMessage("工具调用异常: " + e.getMessage())
                    .invocationId(invocationId)
                    .build();
        }
    }

    // ==================== 调用日志 ====================

    /**
     * 调用日志列表 — 支持按工具筛选，分页返回.
     *
     * <p>每条日志附带工具名称（toolName），前端无需二次查询.
     *
     * @param toolId 工具 ID（可选，null 表示全部）
     * @param page   页码
     * @param size   每页数量
     * @return 分页的调用日志列表
     */
    public PageResponse<ToolInvocationLogResponse> listInvocations(String toolId, int page, int size) {
        String tenantId = TenantContext.getCurrentTenantId();
        log.debug("[Tool] 查询调用日志: toolId={}, page={}, size={}", toolId, page, size);

        List<ToolInvocationLog> logs;
        long total;

        if (toolId != null && !toolId.isBlank()) {
            logs = invocationLogRepository.findByToolId(toolId, page, size);
            total = invocationLogRepository.countByToolId(toolId);
        } else {
            logs = invocationLogRepository.findByTenant(tenantId, page, size);
            total = invocationLogRepository.countByTenant(tenantId);
        }

        List<ToolInvocationLogResponse> records = logs.stream()
                .map(log -> {
                    String toolName = resolveToolName(log.getToolId());
                    return ToolInvocationLogResponse.from(log, toolName);
                })
                .toList();

        return PageResponse.of(records, total, page, size);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从请求中构建 AuthConfig 值对象.
     *
     * @param request 创建工具请求
     * @return AuthConfig 值对象（可能为 null）
     */
    private AuthConfig buildAuthConfig(CreateToolRequest request) {
        if (request.getAuthType() == null || request.getAuthType().isBlank()
                || "NONE".equalsIgnoreCase(request.getAuthType())) {
            return null;
        }
        return AuthConfig.builder()
                .authType(request.getAuthType().toUpperCase())
                .apiKey(request.getApiKey())
                .token(request.getToken())
                .build();
    }

    /**
     * 判断更新请求中是否包含认证信息变更.
     *
     * @param request 更新请求
     * @return true 表示有认证字段需要更新
     */
    private boolean hasAuthUpdate(UpdateToolRequest request) {
        return request.getAuthType() != null
                || request.getApiKey() != null
                || request.getToken() != null;
    }

    /**
     * 使用 MCP 客户端同步调用工具.
     *
     * <p>通过反射调用 McpSyncClient 的方法（兼容不同版本的 Spring AI MCP API）.
     *
     * @param mcpClient MCP 同步客户端实例
     * @param tool      工具注册实体
     * @param params    调用参数
     * @return 工具返回结果
     */
    private Object invokeMcpTool(Object mcpClient, ToolRegistry tool, Map<String, Object> params) {
        try {
            // McpSyncClient.callTool(toolName, arguments) 方法
            // toolName 映射为工具的唯一标识
            String toolName = tool.getToolId();
            String arguments = toJson(params);

            Object result = mcpClient.getClass()
                    .getMethod("callTool", String.class, String.class)
                    .invoke(mcpClient, toolName, arguments);

            // McpSyncClient 返回 ToolCallResult，需要提取内容
            // 尝试获取 content 字段（List<Content>）
            try {
                Object content = result.getClass().getMethod("content").invoke(result);
                if (content instanceof List<?> list && !list.isEmpty()) {
                    Object firstContent = list.get(0);
                    return firstContent.getClass().getMethod("text").invoke(firstContent);
                }
            } catch (Exception ignored) {
                // 无法提取 content，返回原始 toString
            }
            return result != null ? result.toString() : null;

        } catch (Exception e) {
            log.error("[Tool] MCP 工具调用失败: toolId={}", tool.getToolId(), e);
            throw new BusinessException(500, "MCP 工具调用失败: " + e.getMessage());
        }
    }

    /**
     * 保存调用日志 — 仅供内部 test() 方法使用.
     *
     * @param invocationId   调用记录业务 ID
     * @param tenantId       租户 ID
     * @param toolId         工具 ID
     * @param conversationId 会话 ID（测试时可为 null）
     * @param messageId      消息 ID（测试时可为 null）
     * @param executionId    执行 ID（测试时可为 null）
     * @param inputJson      输入参数 JSON
     * @param outputJson     输出结果 JSON
     * @param status         调用状态
     * @param durationMs     耗时
     * @param errorMessage   错误信息
     */
    private void saveInvocationLog(String invocationId, String tenantId, String toolId,
                                    String conversationId, String messageId, String executionId,
                                    String inputJson, String outputJson,
                                    InvocationStatus status, Long durationMs, String errorMessage) {
        try {
            ToolInvocationLog log = ToolInvocationLog.builder()
                    .invocationId(invocationId)
                    .tenantId(tenantId)
                    .toolId(toolId)
                    .conversationId(conversationId)
                    .messageId(messageId)
                    .executionId(executionId)
                    .inputJson(inputJson)
                    .outputJson(outputJson)
                    .status(status)
                    .durationMs(durationMs)
                    .errorMessage(errorMessage)
                    .createdAt(LocalDateTime.now())
                    .build();
            invocationLogRepository.save(log);
        } catch (Exception e) {
            // 日志记录失败不应影响测试结果返回
            log.error("[Tool] 保存调用日志失败: invocationId={}", invocationId, e);
        }
    }

    /**
     * 根据工具 ID 解析工具名称 — 用于日志展示.
     *
     * <p>先查缓存再查库，失败时返回 toolId 本身作为降级.
     *
     * @param toolId 工具 ID
     * @return 工具名称
     */
    private String resolveToolName(String toolId) {
        try {
            Optional<ToolRegistry> tool = toolRepository.findByToolId(toolId);
            return tool.map(ToolRegistry::getName).orElse(toolId);
        } catch (Exception e) {
            return toolId;
        }
    }

    /**
     * 将对象序列化为 JSON 字符串 — 用于调用日志记录.
     *
     * @param obj 待序列化对象
     * @return JSON 字符串，null 输入返回 null
     */
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("[Tool] JSON 序列化失败: class={}", obj.getClass().getName());
            return obj.toString();
        }
    }
}
