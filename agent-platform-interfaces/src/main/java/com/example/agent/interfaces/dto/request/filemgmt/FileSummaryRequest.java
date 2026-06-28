package com.example.agent.interfaces.dto.request.filemgmt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "文件状态汇总请求")
public class FileSummaryRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;
}
