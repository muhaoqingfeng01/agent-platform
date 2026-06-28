package com.example.agent.interfaces.dto.request.filemgmt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "文件管理列表请求")
public class FileListRequest {
    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private String knowledgeId;
    @Schema(description = "页码", example = "0")
    private int page = 0;
    @Schema(description = "每页大小", example = "50")
    private int size = 50;
}
