package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.interaction.InteractionApplicationService;
import com.example.agent.application.interaction.dto.InteractionModeListResponse;
import com.example.agent.application.interaction.dto.InteractionResponse;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.interaction.InteractionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 统一交互入口 Controller — 纯粹 HTTP 适配层.
 * <p>
 * 仅负责参数校验、DTO 映射、调用 {@link InteractionApplicationService}，不包含业务逻辑。
 *
 * <h3>端点说明</h3>
 * <ul>
 *   <li>{@code POST /api/v1/interactions/execute} — 同步执行（知识检索等非流式模式）</li>
 *   <li>{@code GET  /api/v1/interactions/modes} — 查询可用模式列表</li>
 * </ul>
 *
 * <h3>流式端点去哪了？</h3>
 * SSE 流式交互统一走 {@link MessageController#streamChat}
 * （{@code POST /api/v1/conversations/messages/stream}），
 * 该端点同样通过 {@link InteractionApplicationService} 策略工厂路由，
 * 且额外提供会话生命周期管理（消息持久化 + 长期记忆提取），更适合对话场景。
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/interactions")
@RequiredArgsConstructor
@Tag(name = "统一交互", description = "多模式交互统一入口（同步执行 + 模式发现）")
public class InteractionController {

    private final InteractionApplicationService interactionService;

    // ==================== 同步执行端点 ====================

    @PostMapping("/execute")
    @SaCheckPermission("interaction:execute")
    @Operation(summary = "同步执行交互（知识检索等非流式模式）")
    public Result<InteractionResponse> execute(@Valid @RequestBody InteractionRequest request) {
        return ResultRespHelper.responseInvoke("InteractionController.execute", request, (req) ->
                interactionService.executeSync(
                        req.getMode(),
                        req.getContent(),
                        req.getConversationId(),
                        req.getKnowledgeId(),
                        req.getSearchConfig()));
    }

    // ==================== 模式查询端点 ====================

    @GetMapping("/modes")
    @SaCheckPermission("interaction:read")
    @Operation(summary = "查询可用交互模式列表")
    public Result<InteractionModeListResponse> listModes() {
        return ResultRespHelper.responseInvoke("InteractionController.listModes", null, (req) ->
                InteractionModeListResponse.builder()
                        .modes(interactionService.getRegisteredModeCodes())
                        .build());
    }
}
