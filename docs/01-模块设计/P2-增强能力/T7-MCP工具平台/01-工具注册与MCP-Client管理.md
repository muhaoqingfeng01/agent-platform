# MCP 工具注册与 Client 管理

## 所属阶段
**P2 增强能力 → T7 MCP 工具平台**

## 使用技术
- Spring AI MCP Client
- Nacos（服务发现，可选）
- Redis（工具清单缓存）

## 涉及数据库表
- `t_tool_registry`

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/tools` | 注册新工具 |
| GET | `/api/v1/tools` | 工具列表（按类型筛选） |
| GET | `/api/v1/tools/{id}` | 工具详情 |
| PUT | `/api/v1/tools/{id}` | 编辑工具配置 |
| PATCH | `/api/v1/tools/{id}/status` | 启停工具 |
| POST | `/api/v1/tools/{id}/test` | 测试工具调用 |
| GET | `/api/v1/tools/invocations` | 调用日志列表 |

## 实现方案

### 1. 工具注册模型

```java
@Data
@TableName("t_tool_registry")
public class ToolRegistry {
    private Long id;
    private String toolId;          // 唯一标识
    private String tenantId;
    private String name;            // 工具名称
    private String description;     // 功能描述（供 LLM 理解）
    private String toolType;        // MCP / HTTP / BUILTIN / CUSTOM
    private ToolSchema schema;      // JSON → schema_json
    private String endpoint;        // MCP SSE 端点 / HTTP URL
    private AuthConfig authConfig;  // JSON → auth_config
    private boolean requireApproval; // 高风险需审批
    private String status;
}

@Data
public class ToolSchema {
    private JsonSchema input;       // 输入参数 JSON Schema
    private JsonSchema output;      // 输出格式 JSON Schema
}
```

### 2. MCP Client 管理器

```java
@Component
@Slf4j
public class McpClientManager implements InitializingBean {

    private final Map<String, McpClient> clients = new ConcurrentHashMap<>();
    private final ToolRegistryRepository toolRepo;

    @Override
    public void afterPropertiesSet() {
        refreshClients();
    }

    /**
     * 启动时加载所有 ACTIVE 的 MCP 工具，建立连接
     */
    @Scheduled(fixedDelay = 300_000)  // 每5分钟刷新
    public void refreshClients() {
        List<ToolRegistry> mcpTools = toolRepo.findByTypeAndStatus("MCP", "ACTIVE");
        for (ToolRegistry tool : mcpTools) {
            if (!clients.containsKey(tool.getToolId())) {
                try {
                    McpClient client = McpClient.create(tool.getEndpoint(), tool.getAuthConfig());
                    clients.put(tool.getToolId(), client);
                    log.info("MCP 客户端已连接: {} → {}", tool.getName(), tool.getEndpoint());
                } catch (Exception e) {
                    log.error("MCP 客户端连接失败: {} → {}", tool.getName(), e.getMessage());
                }
            }
        }

        // 移除已停用的
        Set<String> activeIds = mcpTools.stream().map(ToolRegistry::getToolId).collect(Collectors.toSet());
        clients.keySet().removeIf(id -> !activeIds.contains(id));
    }

    public McpClient getClient(String toolId) {
        McpClient client = clients.get(toolId);
        if (client == null) {
            throw new BusinessException(500, "MCP 工具未连接: " + toolId);
        }
        return client;
    }
}
```

### 3. HTTP 工具适配器

```java
@Component
public class HttpToolAdapter {

    private final RestClient restClient;

    /**
     * 将 HTTP API 包装为统一工具调用格式
     */
    public Object invokeHttpTool(ToolRegistry tool, Map<String, Object> params) {
        String url = tool.getEndpoint();
        String method = tool.getSchema().getInput().getMethod();  // GET / POST

        // 构建请求
        var request = restClient.method(HttpMethod.valueOf(method))
                .uri(url)
                .headers(h -> {
                    if (tool.getAuthConfig() != null) {
                        h.set("Authorization", "Bearer " + tool.getAuthConfig().getApiKey());
                    }
                });

        if ("POST".equalsIgnoreCase(method)) {
            request.body(JsonUtil.toJson(params));
        }

        // 执行
        var response = request.retrieve().toEntity(String.class);
        return JsonUtil.fromJson(response.getBody(), Object.class);
    }
}
```

### 4. 工具 Schema 示例（供 LLM 理解）

```json
{
  "name": "order_query",
  "description": "查询用户的订单信息，支持按时间范围和状态筛选",
  "input": {
    "type": "object",
    "properties": {
      "start_date": { "type": "string", "description": "开始日期，格式 YYYY-MM-DD" },
      "end_date": { "type": "string", "description": "结束日期，格式 YYYY-MM-DD" },
      "status": { "type": "string", "enum": ["pending", "shipped", "delivered", "cancelled"] }
    },
    "required": ["start_date", "end_date"]
  },
  "output": {
    "type": "array",
    "items": {
      "type": "object",
      "properties": {
        "order_id": { "type": "string" },
        "amount": { "type": "number" },
        "status": { "type": "string" }
      }
    }
  }
}
```
