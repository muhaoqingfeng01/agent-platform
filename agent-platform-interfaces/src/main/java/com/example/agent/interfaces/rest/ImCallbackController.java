package com.example.agent.interfaces.rest;

import com.example.agent.application.im.ImApplicationService;
import com.example.agent.application.im.dto.ImCallbackCommand;
import com.example.agent.application.im.dto.ImCallbackResponse;
import com.example.agent.common.helper.ResultRespHelper;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.im.ImCallbackHttpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * IM 渠道回调入口。
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "IM 接入", description = "企业微信 / 钉钉回调")
public class ImCallbackController {

    private final ImApplicationService imApplicationService;

    @PostMapping("/api/v1/im/callback/{channel}")
    @Operation(summary = "IM 渠道消息回调")
    public Result<ImCallbackResponse> callback(@PathVariable String channel,
                                               @Valid @RequestBody ImCallbackHttpRequest request) {
        return ResultRespHelper.responseInvoke("ImCallbackController.callback", request, req ->
                imApplicationService.handleCallback(ImCallbackCommand.builder()
                        .channel(channel)
                        .token(req.getToken())
                        .tenantId(req.getTenantId())
                        .userId(req.getUserId())
                        .channelUserId(req.getChannelUserId())
                        .content(req.getContent())
                        .msgType(req.getMsgType())
                        .msgId(req.getMsgId())
                        .build()));
    }
}
