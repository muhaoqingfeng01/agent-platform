package com.example.agent.common.util;

/**
 * 业务 ID 前缀常量 — 集中管理所有实体 ID 前缀，避免散落在各 Service 中的硬编码字符串.
 *
 * <p>格式：{@code <prefix>_<snowflake_id>}，如 {@code conv_1234567890}
 *
 * @author Agent Platform Team
 * @since 1.5.0
 */
public final class IdPrefixConstants {

    private IdPrefixConstants() {}

    /** 会话 */
    public static final String CONVERSATION = "conv";
    /** 消息 */
    public static final String MESSAGE = "msg";
    /** 意图 */
    public static final String INTENT = "int";
    /** 工具 */
    public static final String TOOL = "tool";
    /** 工具调用日志 */
    public static final String TOOL_INVOCATION = "tlinv";
    /** 用户 */
    public static final String USER = "user";
    /** 文档 */
    public static final String DOCUMENT = "doc";
    /** 知识库 */
    public static final String KNOWLEDGE_BASE = "kb";
    /** 任务执行 */
    public static final String EXECUTION = "exec";
    /** 评估 */
    public static final String EVALUATION = "eval";
    /** 数据集 */
    public static final String DATASET = "ds";
    /** 审批 */
    public static final String APPROVAL = "appr";
    /** 提示词模板 */
    public static final String PROMPT = "prompt";
    /** 优化工单 */
    public static final String TICKET = "ticket";
}
