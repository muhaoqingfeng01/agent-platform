package com.example.agent.application.im.dto;

import lombok.Builder;
import lombok.Data;

/**
 * IM 回调命令。
 */
@Data
@Builder
public class ImCallbackCommand {

    private String channel;
    private String token;
    private Long tenantId;
    private String userId;
    private String channelUserId;
    private String content;
    private String msgType;
    private String msgId;
}
