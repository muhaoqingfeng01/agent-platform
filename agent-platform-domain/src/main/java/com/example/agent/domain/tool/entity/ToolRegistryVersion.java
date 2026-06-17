package com.example.agent.domain.tool.entity;

import com.example.agent.domain.tool.valueobject.AuthConfig;
import com.example.agent.domain.tool.valueobject.ToolSchema;
import com.example.agent.domain.tool.valueobject.ToolType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 工具注册版本快照 — Memento 模式.
 *
 * <p>每次工具配置变更时自动创建版本快照，保留完整历史记录.
 * 回滚操作本质上是用历史版本内容覆盖当前配置并创建新版本.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Getter
@Builder(toBuilder = true)
public class ToolRegistryVersion {

    /** 主键 */
    private Long id;

    /** 租户 ID */
    private Long tenantId;

    /** 关联的工具业务 ID */
    private String toolId;

    /** 版本号 */
    private Integer version;

    /** 工具名称 */
    private String toolName;

    /** 工具类型 */
    private ToolType toolType;

    /** 连接端点 */
    private String endpointUrl;

    /** 输入 JSON Schema */
    private String inputSchema;

    /** 输出 JSON Schema */
    private String outputSchema;

    /** 认证配置 JSON */
    private String authConfigJson;

    /** 是否需要审批 */
    private Boolean requireApproval;

    /** 功能描述 */
    private String description;

    /** 变更原因 */
    private String changeReason;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 从当前工具注册实体创建版本快照.
     *
     * @param tool         当前工具实体
     * @param changeReason 变更原因
     * @return 版本快照
     */
    public static ToolRegistryVersion from(ToolRegistry tool, String changeReason) {
        return ToolRegistryVersion.builder()
                .tenantId(tool.getTenantId())
                .toolId(tool.getToolId())
                .version(tool.getVersion() != null ? tool.getVersion() : 1)
                .toolName(tool.getName())
                .toolType(tool.getToolType())
                .endpointUrl(tool.getEndpoint())
                .inputSchema(tool.getSchema() != null ? tool.getSchema().getInputSchema() : null)
                .outputSchema(tool.getSchema() != null ? tool.getSchema().getOutputSchema() : null)
                .authConfigJson(tool.getAuthConfig() != null ? toJson(tool.getAuthConfig()) : null)
                .requireApproval(tool.isRequireApproval())
                .description(tool.getDescription())
                .changeReason(changeReason)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static String toJson(AuthConfig config) {
        // 简单 JSON 序列化
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"authType\":\"").append(config.getAuthType()).append("\"");
        if (config.getApiKey() != null) sb.append(",\"apiKey\":\"").append(config.getApiKey()).append("\"");
        if (config.getToken() != null) sb.append(",\"token\":\"").append(config.getToken()).append("\"");
        sb.append("}");
        return sb.toString();
    }
}
