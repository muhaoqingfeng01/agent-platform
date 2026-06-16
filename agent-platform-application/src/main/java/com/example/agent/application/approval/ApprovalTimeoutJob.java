package com.example.agent.application.approval;

import com.example.agent.domain.security.entity.ApprovalWorkflow;
import com.example.agent.domain.security.repository.ApprovalWorkflowRepository;
import com.example.agent.infrastructure.config.websocket.ConversationWebSocketHandler;
import com.example.agent.infrastructure.config.websocket.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批超时扫描任务 — 每 30 秒扫描一次超时的待审批工单并自动拒绝.
 *
 * <p>使用 Spring {@code @Scheduled} 定时任务。
 * <p>需要 {@code @EnableScheduling} 已启用（通常在 bootstrap 启动类上）。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalTimeoutJob {

    private final ApprovalWorkflowRepository approvalRepository;
    private final ConversationWebSocketHandler wsHandler;

    /**
     * 每 30 秒扫描超时工单.
     */
    @Scheduled(fixedDelay = 30_000)
    public void scanTimeout() {
        try {
            List<ApprovalWorkflow> timeouts = approvalRepository.findTimeoutPending(LocalDateTime.now());

            if (timeouts.isEmpty()) {
                return;
            }

            log.info("[ApprovalTimeout] 扫描到 {} 个超时工单，开始自动拒绝", timeouts.size());

            for (ApprovalWorkflow approval : timeouts) {
                try {
                    approval.timeout();
                    approvalRepository.update(approval);

                    // 推送超时通知
                    pushTimeoutNotification(approval);

                    log.warn("[ApprovalTimeout] 工单已超时自动拒绝: approvalId={}, toolId={}",
                            approval.getApprovalId(), approval.getToolId());

                } catch (Exception e) {
                    log.error("[ApprovalTimeout] 处理超时工单失败: approvalId={}",
                            approval.getApprovalId(), e);
                }
            }

            log.info("[ApprovalTimeout] 超时扫描完成: 处理 {} 个工单", timeouts.size());

        } catch (Exception e) {
            log.error("[ApprovalTimeout] 超时扫描异常", e);
        }
    }

    /**
     * 推送超时通知给请求人和审批人.
     */
    private void pushTimeoutNotification(ApprovalWorkflow approval) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("approvalId", approval.getApprovalId());
            payload.put("result", "TIMEOUT");
            payload.put("reason", "审批超时，系统自动拒绝");
            payload.put("toolId", approval.getToolId());

            WebSocketMessage msg = WebSocketMessage.builder()
                    .type("approval_result")
                    .payload(payload)
                    .timestamp(System.currentTimeMillis())
                    .build();

            // 通知请求人
            if (approval.getRequesterId() != null) {
                wsHandler.pushMessage(approval.getRequesterId(), msg);
            }

            // 通知审批人
            if (approval.getApproverId() != null) {
                wsHandler.pushMessage(approval.getApproverId(), msg);
            }

        } catch (Exception e) {
            log.warn("[ApprovalTimeout] 推送超时通知失败: approvalId={}", approval.getApprovalId(), e);
        }
    }
}
