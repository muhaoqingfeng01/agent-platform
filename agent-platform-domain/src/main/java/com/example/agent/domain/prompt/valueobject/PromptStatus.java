package com.example.agent.domain.prompt.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * 提示词模板状态值对象 — State 模式.
 * <p>
 * 每个状态枚举自带合法的状态转移集合，禁止非法转移。
 * 状态机: DRAFT → PUBLISHED → ARCHIVED（单向不可逆），PUBLISHED → DRAFT（回滚场景）
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum PromptStatus {

    /** 草稿 — 可编辑，可发布 */
    DRAFT("草稿") {
        @Override
        public Set<PromptStatus> allowedTransitions() {
            return EnumSet.of(PUBLISHED, ARCHIVED);
        }
    },

    /** 已发布 — 运行时使用，可归档 */
    PUBLISHED("已发布") {
        @Override
        public Set<PromptStatus> allowedTransitions() {
            return EnumSet.of(ARCHIVED);
        }
    },

    /** 已归档 — 只读，不可逆 */
    ARCHIVED("已归档") {
        @Override
        public Set<PromptStatus> allowedTransitions() {
            return EnumSet.noneOf(PromptStatus.class);
        }
    };

    private final String label;

    /** 当前状态允许转移到的目标状态集合 */
    public abstract Set<PromptStatus> allowedTransitions();

    /** 校验状态转移是否合法 */
    public boolean canTransitionTo(PromptStatus target) {
        return allowedTransitions().contains(target);
    }
}
