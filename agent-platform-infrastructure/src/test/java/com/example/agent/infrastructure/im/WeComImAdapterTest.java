package com.example.agent.infrastructure.im;

import com.example.agent.domain.im.model.IncomingImMessage;
import com.example.agent.domain.im.model.UnifiedMessage;
import com.example.agent.domain.im.valueobject.ImChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeComImAdapterTest {

    @Test
    void convert_mapsFields() {
        WeComImAdapter adapter = new WeComImAdapter();
        UnifiedMessage msg = adapter.convert(IncomingImMessage.builder()
                .msgId("m1")
                .channelUserId("wx-user")
                .content("你好")
                .msgType(null)
                .build());
        assertEquals(ImChannel.WECOM, adapter.channel());
        assertEquals(ImChannel.WECOM, msg.getChannel());
        assertEquals("wx-user", msg.getChannelUserId());
        assertEquals("你好", msg.getContent());
        assertEquals("text", msg.getMsgType());
        assertEquals("m1", msg.getExternalMsgId());
    }
}
