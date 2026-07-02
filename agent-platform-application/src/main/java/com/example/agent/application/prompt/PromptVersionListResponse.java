package com.example.agent.application.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 提示词版本列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "提示词版本历史列表")
public class PromptVersionListResponse {

    @Schema(description = "版本历史列表")
    private List<PromptApplicationService.VersionResponse> records;
}
