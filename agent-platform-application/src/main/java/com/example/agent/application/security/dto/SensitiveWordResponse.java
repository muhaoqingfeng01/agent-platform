package com.example.agent.application.security.dto;

import com.example.agent.domain.security.entity.SensitiveWord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 敏感词规则响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "敏感词规则")
public class SensitiveWordResponse {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "所属租户 ID")
    private String tenantId;

    @Schema(description = "敏感词或正则表达式")
    private String word;

    @Schema(description = "匹配方式: EXACT/REGEX/SEMANTIC")
    private String matchType;

    @Schema(description = "匹配方式标签")
    private String matchTypeLabel;

    @Schema(description = "分类: INJECTION/JAILBREAK/PII/CUSTOM")
    private String category;

    @Schema(description = "分类标签")
    private String categoryLabel;

    @Schema(description = "严重程度: LOW/MEDIUM/HIGH/BLOCK")
    private String severity;

    @Schema(description = "严重程度标签")
    private String severityLabel;

    @Schema(description = "动作: LOG/WARN/BLOCK")
    private String action;

    @Schema(description = "动作标签")
    private String actionLabel;

    @Schema(description = "状态: ACTIVE/DISABLED")
    private String status;

    @Schema(description = "状态标签")
    private String statusLabel;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;

    public static SensitiveWordResponse from(SensitiveWord entity) {
        return SensitiveWordResponse.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .word(entity.getWord())
                .matchType(entity.getMatchType().name())
                .matchTypeLabel(matchTypeLabel(entity.getMatchType().name()))
                .category(entity.getCategory().name())
                .categoryLabel(categoryLabel(entity.getCategory().name()))
                .severity(entity.getSeverity().name())
                .severityLabel(severityLabel(entity.getSeverity().name()))
                .action(entity.getAction().name())
                .actionLabel(actionLabel(entity.getAction().name()))
                .status(entity.getStatus().name())
                .statusLabel(entity.isActive() ? "启用" : "禁用")
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static String matchTypeLabel(String type) {
        return switch (type) {
            case "EXACT" -> "精确匹配";
            case "REGEX" -> "正则表达式";
            case "SEMANTIC" -> "语义匹配";
            default -> type;
        };
    }

    private static String categoryLabel(String category) {
        return switch (category) {
            case "INJECTION" -> "注入攻击";
            case "JAILBREAK" -> "越狱检测";
            case "PII" -> "个人隐私";
            case "CUSTOM" -> "自定义";
            default -> category;
        };
    }

    private static String severityLabel(String severity) {
        return switch (severity) {
            case "LOW" -> "低";
            case "MEDIUM" -> "中";
            case "HIGH" -> "高";
            case "BLOCK" -> "阻断";
            default -> severity;
        };
    }

    private static String actionLabel(String action) {
        return switch (action) {
            case "LOG" -> "仅记录";
            case "WARN" -> "告警";
            case "BLOCK" -> "阻断";
            default -> action;
        };
    }
}
