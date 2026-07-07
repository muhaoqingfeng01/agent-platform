package com.example.agent.application.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 交互模式列表响应 DTO — 封装可用模式编码列表.
 * <p>
 * 遵循项目规范：Controller 禁止直接返回 {@code Result<List<String>>}。
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "交互模式列表响应")
public class InteractionModeListResponse {

    @Schema(description = "可用模式编码列表", example = "[\"CONVERSATION\", \"KNOWLEDGE_SEARCH\"]")
    private List<String> modes;
}
