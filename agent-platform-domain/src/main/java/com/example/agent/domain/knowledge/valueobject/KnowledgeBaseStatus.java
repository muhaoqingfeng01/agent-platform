package com.example.agent.domain.knowledge.valueobject;

/**
 * 知识库生命周期状态枚举.
 * <p>
 * ENABLED → DISABLED（可逆）→ DELETED（终态，不可逆）.
 *
 * @author Agent Platform Team
 * @since 1.4.0
 */
public enum KnowledgeBaseStatus {
    /** 启用：可上传、可检索、可解析 */
    ENABLED,
    /** 弃用：不可上传、不可检索，但数据保留，可恢复为 ENABLED */
    DISABLED,
    /** 删除：级联清理文档+向量，终态不可逆 */
    DELETED;

    public static KnowledgeBaseStatus fromCode(String code) {
        if (code == null || code.isBlank()) return ENABLED;
        for (KnowledgeBaseStatus s : values()) {
            if (s.name().equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("未知知识库状态: " + code);
    }

    public String toChinese() {
        return switch (this) {
            case ENABLED -> "已启用";
            case DISABLED -> "已弃用";
            case DELETED -> "已删除";
        };
    }
}
