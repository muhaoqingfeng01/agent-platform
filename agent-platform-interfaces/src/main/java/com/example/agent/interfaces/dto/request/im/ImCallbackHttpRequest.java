package com.example.agent.interfaces.dto.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * IM 回调 HTTP 请求。
 */
@Data
@Schema(description = "IM 渠道回调")
public class ImCallbackHttpRequest {

    @NotBlank
    @Schema(description = "回调共享令牌")
    private String token;

    @NotNull
    @Schema(description = "租户 ID")
    private Long tenantId;

    @Schema(description = "内部用户 ID，可空")
    private String userId;

    @NotBlank
    @Schema(description = "渠道侧用户 ID")
    private String channelUserId;

    @NotBlank
    @Schema(description = "消息正文")
    private String content;

    @Schema(description = "消息类型，默认 text")
    private String msgType;

    @Schema(description = "渠道消息 ID")
    private String msgId;
}
