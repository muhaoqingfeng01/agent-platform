package com.example.agent.interfaces.dto.request.knowledge;

import com.example.agent.common.constant.ProjectConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "命中记录列表请求")
public class KnowledgeListHitsRequest {
    @Schema(description = "会话ID")
    private String conversationId;
    @Schema(description = "页码", example = "0")
    private int page = ProjectConstants.Page.DEFAULT_PAGE_NUM;
    @Schema(description = "每页大小", example = "20")
    private int size = ProjectConstants.Page.DEFAULT_PAGE_SIZE;
}
