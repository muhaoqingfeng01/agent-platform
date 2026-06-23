package com.example.agent.domain.knowledge.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档生命周期状态枚举.
 * <p>
 * V1.4.0 重写：旧值 UPLOADING→PENDING_PARSE, READY→PARSED, 新增 DEPRECATED.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Getter
@AllArgsConstructor
public enum DocumentStatus {
    /** 待解析：上传完成，等待手动触发解析 */
    PENDING_PARSE("PENDING_PARSE", "待解析"),
    /** 解析中：Tika 异步解析中 */
    PARSING("PARSING", "解析中"),
    /** 切分中：异步切片中 */
    CHUNKING("CHUNKING", "切分中"),
    /** 向量化中：异步 Embedding + Milvus 写入中 */
    EMBEDDING("EMBEDDING", "向量化中"),
    /** 已解析：向量可用，可检索 */
    PARSED("PARSED", "已解析"),
    /** 已弃用：向量已删除，元数据保留 */
    DEPRECATED("DEPRECATED", "已弃用"),
    /** 失败：处理出错，可重试 */
    FAILED("FAILED", "失败");

    private final String code;
    private final String desc;

    public static DocumentStatus fromCode(String code) {
        if (code == null || code.isBlank()) return PENDING_PARSE;
        for (DocumentStatus e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知文档状态: " + code);
    }
}
