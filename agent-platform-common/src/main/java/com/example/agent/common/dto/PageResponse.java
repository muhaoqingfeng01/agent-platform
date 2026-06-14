package com.example.agent.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页响应包装 — 所有分页查询统一使用此类型.
 *
 * @param <T> 记录类型
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页记录 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private int page;

    /** 每页大小 */
    private int size;

    /** 总页数（计算字段） */
    public int getTotalPages() {
        return size > 0 ? (int) Math.ceil((double) total / size) : 0;
    }

    /**
     * 工厂方法.
     */
    public static <T> PageResponse<T> of(List<T> records, long total, int page, int size) {
        return PageResponse.<T>builder()
                .records(records)
                .total(total)
                .page(page)
                .size(size)
                .build();
    }
}
