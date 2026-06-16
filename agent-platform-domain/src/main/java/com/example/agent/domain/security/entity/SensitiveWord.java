package com.example.agent.domain.security.entity;

import com.example.agent.domain.security.valueobject.ActionType;
import com.example.agent.domain.security.valueobject.MatchType;
import com.example.agent.domain.security.valueobject.SensitiveCategory;
import com.example.agent.domain.security.valueobject.SensitiveWordStatus;
import com.example.agent.domain.security.valueobject.SeverityLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 敏感词聚合根 — 安全围栏的规则来源.
 *
 * <p>每条敏感词记录定义一个匹配规则，用于输入过滤链中的
 * {@code SensitiveWordFilter} 进行多模匹配。
 * <p>支持 EXACT（精确）、REGEX（正则）、SEMANTIC（语义）三种匹配方式。
 *
 * <p>领域不变量：
 * <ul>
 *   <li>word 字段不能为空</li>
 *   <li>只有 ACTIVE 状态的规则参与过滤</li>
 *   <li>tenantId 为 null 的规则为全局规则，所有租户共享</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@Builder(toBuilder = true)
public class SensitiveWord {

    /** 自增主键 */
    private Long id;

    /** 所属租户 ID — NULL 表示全局规则 */
    private String tenantId;

    /** 敏感词或正则表达式 */
    private String word;

    /** 匹配方式: EXACT / REGEX / SEMANTIC */
    private MatchType matchType;

    /** 分类: INJECTION / JAILBREAK / PII / CUSTOM */
    private SensitiveCategory category;

    /** 严重程度: LOW / MEDIUM / HIGH / BLOCK */
    private SeverityLevel severity;

    /** 动作: LOG / WARN / BLOCK */
    private ActionType action;

    /** 状态: ACTIVE / DISABLED */
    private SensitiveWordStatus status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;

    // ==================== 领域行为方法 ====================

    /** 启用规则 */
    public void enable() {
        if (this.status == SensitiveWordStatus.ACTIVE) {
            throw new IllegalStateException("规则已是启用状态: id=" + this.id);
        }
        this.status = SensitiveWordStatus.ACTIVE;
    }

    /** 禁用规则 */
    public void disable() {
        if (this.status == SensitiveWordStatus.DISABLED) {
            throw new IllegalStateException("规则已是禁用状态: id=" + this.id);
        }
        this.status = SensitiveWordStatus.DISABLED;
    }

    /** 规则是否生效中 */
    public boolean isActive() {
        return this.status == SensitiveWordStatus.ACTIVE;
    }

    /** 是否全局规则（无租户限制） */
    public boolean isGlobal() {
        return this.tenantId == null;
    }

    /** 是否阻断级别 */
    public boolean isBlockAction() {
        return this.action == ActionType.BLOCK;
    }
}
