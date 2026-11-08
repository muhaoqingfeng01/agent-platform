package com.example.agent.domain.im.model;

import com.example.agent.domain.im.valueobject.ImChannel;
import lombok.Builder;
import lombok.Getter;

/**
 * 内部统一 IM 消息。
 */
@Getter
@Builder
public class UnifiedMessage {

    private final ImChannel channel;
    private final String channelUserId;
    private final String content;
    private final String msgType;
    private final String externalMsgId;
}
