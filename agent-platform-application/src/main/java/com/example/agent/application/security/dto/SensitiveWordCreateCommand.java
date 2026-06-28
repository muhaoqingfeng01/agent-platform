package com.example.agent.application.security.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 创建敏感词命令.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
public class SensitiveWordCreateCommand {

    /** 敏感词或正则表达式 */
    private String word;

    /** 匹配方式: EXACT/REGEX/SEMANTIC */
    private String matchType;

    /** 分类: INJECTION/JAILBREAK/PII/CUSTOM */
    private String category;

    /** 严重程度: LOW/MEDIUM/HIGH/BLOCK */
    private String severity;

    /** 动作: LOG/WARN/BLOCK */
    private String action;
}
