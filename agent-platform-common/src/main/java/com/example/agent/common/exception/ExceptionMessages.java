package com.example.agent.common.exception;

/**
 * 异常消息常量 — 集中管理业务异常消息，避免各 Service 中重复的硬编码字符串.
 *
 * <p>使用方式：{@code throw new BusinessException(404, ExceptionMessages.USER_NOT_FOUND + userId);}
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public final class ExceptionMessages {

    private ExceptionMessages() {}

    // ==================== 用户 ====================
    public static final String USER_NOT_FOUND = "用户不存在: ";
    public static final String USER_ALREADY_EXISTS = "用户已存在: ";

    // ==================== 租户 ====================
    public static final String TENANT_NOT_FOUND = "租户不存在: ";
    public static final String TENANT_ID_ALREADY_EXISTS = "租户标识已存在: ";

    // ==================== 角色 ====================
    public static final String ROLE_NOT_FOUND = "角色不存在: ";

    // ==================== 审批 ====================
    public static final String APPROVAL_NOT_FOUND = "审批工单不存在: ";
    public static final String APPROVAL_STATUS_NOT_PENDING = "工单状态不是 PENDING";

    // ==================== 任务执行 ====================
    public static final String EXECUTION_NOT_FOUND = "执行记录不存在: ";
    public static final String TASK_CANNOT_CANCEL = "任务无法取消，当前状态: ";
    public static final String TASK_NOT_WAITING_APPROVAL = "只有 WAITING_APPROVAL 状态的任务才能恢复，当前: ";

    // ==================== 知识库 / 文档 ====================
    public static final String KNOWLEDGE_BASE_NOT_FOUND = "知识库不存在: ";
    public static final String DOCUMENT_NOT_FOUND = "文档不存在: ";

    // ==================== 通用 ====================
    public static final String INTERNAL_ERROR = "系统内部错误";
    public static final String VALIDATION_ERROR = "参数校验失败: ";
}
