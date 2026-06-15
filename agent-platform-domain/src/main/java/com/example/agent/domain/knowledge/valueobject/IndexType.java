package com.example.agent.domain.knowledge.valueobject;

/**
 * Milvus 向量索引类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
public enum IndexType {
    IVF_FLAT,
    IVF_SQ8,
    IVF_PQ,
    HNSW,
    DISKANN,
    AUTOINDEX;

    public static IndexType fromCode(String code) {
        if (code == null || code.isBlank()) return IVF_FLAT;
        for (IndexType t : values()) {
            if (t.name().equalsIgnoreCase(code)) return t;
        }
        throw new IllegalArgumentException("未知索引类型: " + code);
    }
}
