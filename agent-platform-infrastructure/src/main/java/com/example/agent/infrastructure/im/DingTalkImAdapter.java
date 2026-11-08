package com.example.agent.infrastructure.im;

import com.example.agent.domain.im.model.IncomingImMessage;
import com.example.agent.domain.im.model.UnifiedMessage;
import com.example.agent.domain.im.service.ImAdapter;
import com.example.agent.domain.im.valueobject.ImChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 钉钉适配器 — 文本消息转换与回复投递（Webhook 可后续接入）。
 */
@Slf4j
@Component
public class DingTalkImAdapter implements ImAdapter {

    @Override
    public ImChannel channel() {
        return ImChannel.DINGTALK;
    }

    @Override
    public UnifiedMessage convert(IncomingImMessage raw) {
        return UnifiedMessage.builder()
                .channel(ImChannel.DINGTALK)
                .channelUserId(raw.getChannelUserId())
                .content(raw.getContent())
                .msgType(raw.getMsgType() != null ? raw.getMsgType() : "text")
                .externalMsgId(raw.getMsgId())
                .build();
    }

    @Override
    public void sendReply(UnifiedMessage reply, String channelUserId) {
        log.info("[DingTalk] 发送回复: user={}, length={}",
                channelUserId, reply.getContent() != null ? reply.getContent().length() : 0);
    }
}
