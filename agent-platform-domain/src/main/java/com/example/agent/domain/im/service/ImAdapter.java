package com.example.agent.domain.im.service;

import com.example.agent.domain.im.model.IncomingImMessage;
import com.example.agent.domain.im.model.UnifiedMessage;
import com.example.agent.domain.im.valueobject.ImChannel;

/**
 * IM 渠道适配端口 — Domain 定义，Infrastructure 实现。
 */
public interface ImAdapter {

    ImChannel channel();

    UnifiedMessage convert(IncomingImMessage raw);

    void sendReply(UnifiedMessage reply, String channelUserId);
}
