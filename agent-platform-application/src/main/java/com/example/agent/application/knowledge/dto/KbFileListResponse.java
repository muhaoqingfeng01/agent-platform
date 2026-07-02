package com.example.agent.application.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库文件列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.4.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件管理列表")
public class KbFileListResponse {

    @Schema(description = "知识库基本信息")
    private KbInfo kbInfo;

    @Schema(description = "文档列表")
    private java.util.List<DocumentDTO> documents;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "当前页码")
    private int page;

    @Schema(description = "每页大小")
    private int size;

    /**
     * 知识库简要信息.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "知识库简要信息")
    public static class KbInfo {

        @Schema(description = "知识库业务 ID")
        private String knowledgeId;

        @Schema(description = "知识库名称")
        private String name;

        @Schema(description = "知识库状态")
        private String status;

        @Schema(description = "文档数量")
        private Integer documentCount;
    }
}
