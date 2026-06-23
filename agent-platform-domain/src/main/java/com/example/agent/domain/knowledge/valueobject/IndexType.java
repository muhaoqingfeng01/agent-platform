package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Milvus 向量索引类型枚举.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@AllArgsConstructor
public enum IndexType {
    IVF_FLAT("IVF_FLAT", "IVF平坦"),
    IVF_SQ8("IVF_SQ8", "IVF量化"),
    IVF_PQ("IVF_PQ", "IVF乘积量化"),
    HNSW("HNSW", "分层可导航小世界"),
    DISKANN("DISKANN", "磁盘ANN"),
    AUTOINDEX("AUTOINDEX", "自动索引");

    private final String code;
    private final String desc;

    public static IndexType fromCode(String code) {
        if (code == null || code.isBlank()) return IVF_FLAT;
        for (IndexType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知索引类型: " + code);
    }
}
