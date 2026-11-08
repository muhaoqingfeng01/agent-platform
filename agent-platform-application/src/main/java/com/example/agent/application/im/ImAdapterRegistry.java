package com.example.agent.application.im;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.im.service.ImAdapter;
import com.example.agent.domain.im.valueobject.ImChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * IM 适配器注册表。
 */
@Component
public class ImAdapterRegistry {

    private final Map<ImChannel, ImAdapter> adapters = new EnumMap<>(ImChannel.class);

    public ImAdapterRegistry(List<ImAdapter> adapterList) {
        for (ImAdapter adapter : adapterList) {
            adapters.put(adapter.channel(), adapter);
        }
    }

    public ImAdapter require(ImChannel channel) {
        ImAdapter adapter = adapters.get(channel);
        if (adapter == null) {
            throw new BusinessException(400, "不支持的 IM 渠道: " + channel.getCode());
        }
        return adapter;
    }
}
