package com.example.agent.application.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 动作处理器列表响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "动作处理器列表")
public class ActionHandlerListResponse {

    @Schema(description = "动作处理器列表")
    private List<ActionHandlerResponse> records;
}
