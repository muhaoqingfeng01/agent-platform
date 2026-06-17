package com.example.agent.application.tenant;

import com.example.agent.domain.tenant.Tenant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantResponse {
    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "租户唯一标识")
    private Long tenantId;
    @Schema(description = "租户名称")
    private String name;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "套餐等级")
    private String tier;
    @Schema(description = "配置 JSON")
    private String configJson;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    public static TenantResponse from(Tenant t) {
        TenantResponse r = new TenantResponse();
        r.setId(t.getId());
        r.setTenantId(t.getTenantId());
        r.setName(t.getName());
        r.setStatus(t.getStatus());
        r.setTier(t.getTier());
        r.setConfigJson(t.getConfigJson());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}
