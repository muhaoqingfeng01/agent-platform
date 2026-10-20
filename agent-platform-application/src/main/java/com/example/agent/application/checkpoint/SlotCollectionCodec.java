package com.example.agent.application.checkpoint;

import com.example.agent.domain.conversation.valueobject.SlotCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 槽位集合与检查点 slotsJson 编解码，隔离 Jackson 于应用层.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlotCollectionCodec {

    private final ObjectMapper objectMapper;

    public String encode(SlotCollection slots) {
        SlotCollection safe = slots != null ? slots : SlotCollection.empty();
        try {
            return objectMapper.writeValueAsString(safe.toPayload());
        } catch (JsonProcessingException e) {
            log.warn("[Checkpoint] 槽位序列化失败", e);
            return "{}";
        }
    }

    public SlotCollection decode(String slotsJson) {
        if (slotsJson == null || slotsJson.isBlank()) {
            return SlotCollection.empty();
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(slotsJson, new TypeReference<>() {});
            return SlotCollection.fromPayload(payload);
        } catch (JsonProcessingException e) {
            log.warn("[Checkpoint] 槽位反序列化失败，按空槽位处理", e);
            return SlotCollection.empty();
        }
    }
}
