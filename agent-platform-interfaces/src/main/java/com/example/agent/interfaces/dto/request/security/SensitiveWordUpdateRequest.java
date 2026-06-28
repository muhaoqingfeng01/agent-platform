package com.example.agent.interfaces.dto.request.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新敏感词请求（含ID）")
public class SensitiveWordUpdateRequest {
    @NotNull(message = "敏感词ID不能为空")
    @Schema(description = "敏感词ID")
    private Long id;
    @Schema(description = "敏感词")
    private String word;
    @Schema(description = "匹配类型")
    private String matchType;
    @Schema(description = "分类")
    private String category;
    @Schema(description = "严重级别")
    private String severity;
    @Schema(description = "动作")
    private String action;
}
