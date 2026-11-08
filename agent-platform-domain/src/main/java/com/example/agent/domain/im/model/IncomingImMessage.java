package com.example.agent.domain.im.model;

import lombok.Builder;
import lombok.Getter;

/**
 * IM 平台原始入站消息。
 */
@Getter
@Builder
public class IncomingImMessage {

    private final String msgId;
    private final String channelUserId;
    private final String content;
    private final String msgType;
}
