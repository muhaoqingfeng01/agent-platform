package com.example.agent.application.approval.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 创建审批命令.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
public class ApprovalCreateCommand {

    /** 工具 ID */
    private String toolId;

    /** 任务执行 ID */
    private String executionId;

    /** 会话 ID */
    private String conversationId;

    /** 工具调用参数 */
    private Map<String, Object> params;

    /** 风险等级: LOW/MEDIUM/HIGH/CRITICAL */
    private String riskLevel;
}
