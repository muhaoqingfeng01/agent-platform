package com.example.agent.application.interaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交互响应 DTO — 统一的模式无关响应结构.
 * <p>
 * 所有交互模式均使用此响应格式，前端可根据 {@link #mode} 字段区分不同模式的 {@link #data} 结构。
 *
 * <pre>
 * CONVERSATION / TASK_EXECUTION 模式: 走 SSE 流式通道，不使用此 DTO
 * KNOWLEDGE_SEARCH 模式: data = {@link com.example.agent.application.knowledge.dto.SearchResultDTO}
 * </pre>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "交互响应")
public class InteractionResponse {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "交互模式编码", example = "KNOWLEDGE_SEARCH")
    private String mode;

    @Schema(description = "业务数据（结构因模式而异）")
    private Object data;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMessage;

    // ==================== 工厂方法 ====================

    /** 成功响应 */
    public static InteractionResponse success(String mode, Object data) {
        return InteractionResponse.builder()
                .success(true)
                .mode(mode)
                .data(data)
                .build();
    }

    /** 失败响应 */
    public static InteractionResponse error(String mode, String errorCode, String errorMessage) {
        return InteractionResponse.builder()
                .success(false)
                .mode(mode)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
