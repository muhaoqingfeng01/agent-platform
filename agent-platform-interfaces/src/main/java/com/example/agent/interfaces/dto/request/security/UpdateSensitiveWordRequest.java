package com.example.agent.interfaces.dto.request.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新敏感词请求 DTO — Interfaces 层.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "更新敏感词规则请求")
public class UpdateSensitiveWordRequest {

    @NotBlank(message = "敏感词不能为空")
    @Schema(description = "敏感词或正则表达式", example = "暴力")
    private String word;

    @NotBlank(message = "匹配方式不能为空")
    @Schema(description = "匹配方式: EXACT/REGEX/SEMANTIC", example = "EXACT")
    private String matchType;

    @NotBlank(message = "分类不能为空")
    @Schema(description = "分类: INJECTION/JAILBREAK/PII/CUSTOM", example = "CUSTOM")
    private String category;

    @NotBlank(message = "严重程度不能为空")
    @Schema(description = "严重程度: LOW/MEDIUM/HIGH/BLOCK", example = "MEDIUM")
    private String severity;

    @NotBlank(message = "动作不能为空")
    @Schema(description = "命中动作: LOG/WARN/BLOCK", example = "BLOCK")
    private String action;
}
