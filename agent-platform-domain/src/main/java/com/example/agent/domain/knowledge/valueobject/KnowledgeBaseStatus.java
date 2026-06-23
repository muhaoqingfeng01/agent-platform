package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 知识库生命周期状态枚举.
 * <p>
 * ENABLED → DISABLED（可逆）→ DELETED（终态，不可逆）.
 *
 * @author Agent Platform Team
 * @since 1.4.0
 */
@Getter
@AllArgsConstructor
public enum KnowledgeBaseStatus {
    /** 启用：可上传、可检索、可解析 */
    ENABLED("ENABLED", "已启用"),
    /** 弃用：不可上传、不可检索，但数据保留，可恢复为 ENABLED */
    DISABLED("DISABLED", "已弃用"),
    /** 删除：级联清理文档+向量，终态不可逆 */
    DELETED("DELETED", "已删除");

    private final String code;
    private final String desc;

    public static KnowledgeBaseStatus fromCode(String code) {
        if (code == null || code.isBlank()) return ENABLED;
        for (KnowledgeBaseStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知知识库状态: " + code);
    }
}
