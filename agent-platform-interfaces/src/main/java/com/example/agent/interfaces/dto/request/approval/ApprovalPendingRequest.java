package com.example.agent.interfaces.dto.request.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 待审批列表请求 — 断线重连后补聊天卡片。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Schema(description = "待审批列表请求")
public class ApprovalPendingRequest {

    @Schema(description = "会话 ID；传入时只返回该会话下的 PENDING 工单")
    private String conversationId;
}
