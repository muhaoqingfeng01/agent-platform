package com.example.agent.application.knowledge.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 混合检索结果 DTO — 含文档溯源信息.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Builder
public class SearchResultDTO {
    private String query;
    private List<HitItem> hits;
    /** ★ 新增: 去重后的文档引用列表，前端可用此渲染"参考文档"侧栏 */
    private List<DocumentRef> documents;

    @Data
    @Builder
    public static class HitItem {
        private Long chunkId;
        private String documentId;
        private String content;
        private int chunkIndex;
        private BigDecimal rrfScore;
        private BigDecimal vectorScore;
        private BigDecimal keywordScore;
        private int rankPosition;
        private String chunkMetadata;
        // ★ 新增: 文档溯源字段
        private String documentFilename;
        private String documentFileType;
        private String documentAccessUrl;
        private LocalDateTime documentUploadedAt;
    }

    @Data
    @Builder
    public static class DocumentRef {
        private String documentId;
        private String filename;
        private String fileType;
        private long fileSize;
        private String accessUrl;
        private LocalDateTime uploadedAt;
    }
}
