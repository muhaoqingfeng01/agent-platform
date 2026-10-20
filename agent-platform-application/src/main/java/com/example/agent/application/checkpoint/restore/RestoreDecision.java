package com.example.agent.application.checkpoint.restore;

/**
 * 检查点恢复决策 — 便于后续扩展 SKIP_LLM / REROUTE 等动作.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
public enum RestoreDecision {
    /** 本轮 SSE 已由处理器结束（例如继续追问槽位） */
    HANDLED,
    /** 已恢复上下文，继续默认策略管线 */
    CONTINUE
}
