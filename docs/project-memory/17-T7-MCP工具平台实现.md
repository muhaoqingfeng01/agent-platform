# T7 MCP 工具平台 — 完整实现

> **日期**: 2026-06-17
> **分支**: master
> **触发**: 代码生成（33 文件）
> **设计文档**: `docs/P2-增强能力/T7-MCP工具平台/01-工具注册与MCP-Client管理.md`

---

## 做了什么

- **Phase 0 - 数据库**: 创建 `V1.2.2__add_tool_invocation_business_id.sql`，为 `t_tool_invocation_log` 添加 `invocation_id` 业务 ID 列
- **Phase 1 - Domain 层（10 文件）**: 3 枚举（ToolType/ToolStatus/InvocationStatus）+ 2 值对象（ToolSchema/AuthConfig）+ 2 实体（ToolRegistry/ToolInvocationLog）+ 2 仓储接口 + 1 领域服务（ToolDomainService）
- **Phase 2 - Infrastructure 层（10 文件）**: 2 PO + 2 Mapper 接口 + 2 XML Mapper + 2 RepositoryImpl + McpClientManager（MCP 连接生命周期管理）+ HttpToolAdapter（HTTP API 统一包装）
- **Phase 3 - Application 层（7 文件）**: 5 DTO + ToolApplicationService（7 方法：CRUD + toggleStatus + test + listInvocations）
- **Phase 4 - Interfaces 层（5 文件）**: 4 请求 DTO + ToolController（7 端点 + SaCheckPermission 鉴权）

## 关键决策

1. **ToolSchema/AuthConfig 存为 POJO**: Domain 层用值对象表达，仅在 RepositoryImpl 中用 ObjectMapper 做 JSON ↔ POJO 转换，保持域层技术无关
2. **McpClientManager 使用反射**: 因为 Spring AI 1.1.7 的 MCP Client API 签名可能因版本而异，使用 `Class.forName()` 反射方式兼容不同版本的 API
3. **HttpToolAdapter 使用 RestClient.Builder**: Spring Boot 3.3.7 自动配置的 RestClient，非 WebClient，同步调用模式
4. **test() 方法同步阻塞**: 记录耗时、参数、结果到 `t_tool_invocation_log`，失败不丢日志
5. **TenantContext 在 infrastructure 层**: Application 层 import `com.example.agent.infrastructure.context.TenantContext` — 这是项目已有的约定（所有 AppService 都这么做）
6. **调用日志只追加**: ToolInvocationLogRepository 只有 save + select，无 update/delete

## 踩坑记录

1. **TenantContext 导入路径错误**: 初始写了 `com.example.agent.common.context.TenantContext`，正确路径是 `com.example.agent.infrastructure.context.TenantContext`。项目所有 AppService 都从 infrastructure 层 import
2. **Spring MVC 路由歧义**: `GET /api/v1/tools/invocations` 必须在 `GET /api/v1/tools/{id}` 之前定义，否则 Spring 会将 "invocations" 当作 `{id}` 的值
3. **编译已存在的知识模块错误**: `HybridSearchApplicationService.java` 和 `HitRecordDTO.java` 有预存的编译错误（缺少 VectorSearchProvider 等），与 T7 无关，需下次修复

## 实现清单（33 个新文件）

### Domain 层
- `domain/tool/valueobject/ToolType.java`
- `domain/tool/valueobject/ToolStatus.java`
- `domain/tool/valueobject/InvocationStatus.java`
- `domain/tool/valueobject/ToolSchema.java`
- `domain/tool/valueobject/AuthConfig.java`
- `domain/tool/entity/ToolRegistry.java`
- `domain/tool/entity/ToolInvocationLog.java`
- `domain/tool/repository/ToolRegistryRepository.java`
- `domain/tool/repository/ToolInvocationLogRepository.java`
- `domain/tool/service/ToolDomainService.java`

### Infrastructure 层
- `infrastructure/persistence/po/ToolRegistryPO.java`
- `infrastructure/persistence/po/ToolInvocationLogPO.java`
- `infrastructure/persistence/mapper/ToolRegistryMapper.java`
- `infrastructure/persistence/mapper/ToolInvocationLogMapper.java`
- `infrastructure/persistence/impl/ToolRegistryRepositoryImpl.java`
- `infrastructure/persistence/impl/ToolInvocationLogRepositoryImpl.java`
- `infrastructure/mcp/McpClientManager.java`
- `infrastructure/mcp/HttpToolAdapter.java`
- `resources/mapper/ToolRegistryMapper.xml`
- `resources/mapper/ToolInvocationLogMapper.xml`

### Application 层
- `application/tool/ToolApplicationService.java`
- `application/tool/dto/CreateToolRequest.java`
- `application/tool/dto/UpdateToolRequest.java`
- `application/tool/dto/ToolTestRequest.java`
- `application/tool/dto/ToolTestResponse.java`
- `application/tool/dto/ToolResponse.java`
- `application/tool/dto/ToolInvocationLogResponse.java`

### Interfaces 层
- `interfaces/rest/ToolController.java`
- `interfaces/dto/request/tool/CreateToolRequest.java`
- `interfaces/dto/request/tool/UpdateToolRequest.java`
- `interfaces/dto/request/tool/ToggleToolStatusRequest.java`
- `interfaces/dto/request/tool/ToolTestRequest.java`

### 数据迁移
- `bootstrap/resources/db/migration/V1.2.2__add_tool_invocation_business_id.sql`

## 编译状态

✅ agent-platform-domain BUILD SUCCESS (85 源文件)
✅ agent-platform-infrastructure BUILD SUCCESS (86 源文件)
✅ agent-platform-application BUILD SUCCESS（T7 代码编译通过）
✅ agent-platform-interfaces BUILD SUCCESS

⚠️ `HybridSearchApplicationService` + `HitRecordDTO` 有预存的编译错误，与 T7 无关

## 设计模式应用

| 设计模式 | 应用场景 |
|----------|----------|
| **Facade** | ToolApplicationService — 封装 DomainService + Repository + McpClientManager + HttpToolAdapter |
| **Builder** | 所有实体/值对象/DTO 的构建（`@Builder(toBuilder = true)`） |
| **Strategy** | 4 种工具类型（MCP/HTTP/BUILTIN/CUSTOM）通过 switch 分发不同调用策略 |
| **Adapter** | HttpToolAdapter — 将 HTTP API 包装为统一工具调用格式 |
| **Factory** | ToolResponse.from() / ToolInvocationLogResponse.from() 工厂方法 |
| **Observer** | McpClientManager @Scheduled 定时刷新，监听工具状态变更 |
| **Value Object** | ToolSchema / AuthConfig — 不可变值对象 |

## 下一步

- P2-T7 增强：Redis 工具缓存、工具版本化、MCP 心跳健康检查
- P3-T10：安全围栏（对接 T7 工具调用前后的输入/输出安全过滤）
- P3-T11：人机协同审批（对接 T7 的 `requireApproval` 字段）
- P4-T9：全链路观测（对接 T7 的 `t_tool_invocation_log`）
