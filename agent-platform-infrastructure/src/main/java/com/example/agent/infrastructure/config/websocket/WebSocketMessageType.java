package com.example.agent.infrastructure.config.websocket;

/**
 * WebSocket 消息类型常量 — 集中管理所有 WebSocket 推送消息的类型标识.
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public final class WebSocketMessageType {

    private WebSocketMessageType() {}

    /** DAG 任务执行进度推送 */
    public static final String TASK_PROGRESS = "task_progress";

    /** DAG 任务取消通知 */
    public static final String TASK_CANCELLED = "task_cancelled";

    /** 审批工单卡片推送 */
    public static final String APPROVAL_CARD = "approval_card";

    /** 审批结果通知 */
    public static final String APPROVAL_RESULT = "approval_result";
}
