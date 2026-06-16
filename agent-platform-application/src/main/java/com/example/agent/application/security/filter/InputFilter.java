package com.example.agent.application.security.filter;

/**
 * 输入过滤器接口 — Chain of Responsibility 模式.
 *
 * <p>每个实现类负责一种安全检查（注入检测、越狱检测、敏感词匹配、长度限制）。
 * <p>实现类标注 {@code @Component} 即可由 {@code SecurityFenceApplicationService} 自动收集。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public interface InputFilter {

    /**
     * 过滤检查.
     *
     * @param content 用户输入内容
     * @param context 过滤上下文（会话 ID、消息 ID、租户 ID 等）
     * @return 过滤结果，blocked=true 表示阻断
     */
    FilterResult filter(String content, FilterContext context);

    /**
     * 优先级 — 越小越先执行.
     * <p>建议范围：1=注入检测, 2=越狱检测, 3=敏感词, 4=长度限制
     */
    int order();
}
