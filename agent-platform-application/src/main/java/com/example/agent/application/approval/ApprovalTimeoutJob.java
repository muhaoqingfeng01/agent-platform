package com.example.agent.application.approval;

import com.example.agent.domain.security.entity.ApprovalWorkflow;
import com.example.agent.domain.security.repository.ApprovalWorkflowRepository;
import com.example.agent.infrastructure.config.nacos.SchedulerConfig;
import com.example.agent.infrastructure.config.scheduler.DynamicScheduledTaskManager;
import com.example.agent.infrastructure.config.websocket.ConversationWebSocketHandler;
import com.example.agent.infrastructure.config.websocket.WebSocketMessage;
import com.example.agent.infrastructure.config.websocket.WebSocketMessageType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批超时扫描任务 — 定期扫描超时的待审批工单并自动拒绝.
 *
 * <p>使用 {@link DynamicScheduledTaskManager} 替代 {@code @Scheduled} 注解，
 * 扫描间隔从 {@link SchedulerConfig}（Nacos 动态配置）读取，支持运行时免重启调优.
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
    private final DynamicScheduledTaskManager dynamicScheduler;
    private final SchedulerConfig schedulerConfig;

    @PostConstruct
    public void registerTasks() {
        dynamicScheduler.register(
                "approvalTimeoutScan",
                this::scanTimeout,
                schedulerConfig::getApprovalTimeoutScanMs);

        log.info("[ApprovalTimeoutJob] 动态定时任务已注册: approvalTimeoutScan");
    }

    /**
     * 扫描超时工单.
     */
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
                    .type(WebSocketMessageType.APPROVAL_RESULT)
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
