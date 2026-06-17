package com.example.agent.application.tool.dto;

import com.example.agent.domain.tool.entity.ToolRegistryVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工具版本历史响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
@Data
public class VersionResponse {

    @Schema(description = "版本 ID")
    private Long id;

    @Schema(description = "工具 ID")
    private String toolId;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "工具名称")
    private String toolName;

    @Schema(description = "工具类型")
    private String toolType;

    @Schema(description = "连接端点")
    private String endpointUrl;

    @Schema(description = "输入 Schema")
    private String inputSchema;

    @Schema(description = "输出 Schema")
    private String outputSchema;

    @Schema(description = "认证配置 JSON")
    private String authConfigJson;

    @Schema(description = "是否需要审批")
    private Boolean requireApproval;

    @Schema(description = "功能描述")
    private String description;

    @Schema(description = "变更原因")
    private String changeReason;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    public static VersionResponse from(ToolRegistryVersion v) {
        VersionResponse r = new VersionResponse();
        r.setId(v.getId());
        r.setToolId(v.getToolId());
        r.setVersion(v.getVersion());
        r.setToolName(v.getToolName());
        r.setToolType(v.getToolType() != null ? v.getToolType().name() : null);
        r.setEndpointUrl(v.getEndpointUrl());
        r.setInputSchema(v.getInputSchema());
        r.setOutputSchema(v.getOutputSchema());
        r.setAuthConfigJson(v.getAuthConfigJson());
        r.setRequireApproval(v.getRequireApproval());
        r.setDescription(v.getDescription());
        r.setChangeReason(v.getChangeReason());
        r.setCreatedAt(v.getCreatedAt());
        return r;
    }
}
