package com.example.agent.domain.conversation.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 槽位集合 — 不可变值对象，封装「规格 + 已填值」与补全判定.
 * <p>
 * 新识别器/抽取器只需产出下一槽的值，调用 {@link #fillNext(Object)} 即可，
 * 不必感知 Redis 检查点结构。
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Getter
@EqualsAndHashCode
public class SlotCollection {

    private static final String CONTINUE_PREFIX = "请继续补充";

    private final List<RequiredSlot> required;
    private final Map<String, Object> filled;

    private SlotCollection(List<RequiredSlot> required, Map<String, Object> filled) {
        this.required = List.copyOf(required == null ? List.of() : required);
        this.filled = Collections.unmodifiableMap(new LinkedHashMap<>(filled == null ? Map.of() : filled));
    }

    public static SlotCollection empty() {
        return new SlotCollection(List.of(), Map.of());
    }

    public static SlotCollection of(List<RequiredSlot> required, Map<String, Object> filled) {
        return new SlotCollection(required, filled);
    }

    /**
     * 从意图 requiredParams 构建空填值集合.
     */
    public static SlotCollection fromRequiredParamMaps(List<Map<String, Object>> requiredParams) {
        if (requiredParams == null || requiredParams.isEmpty()) {
            return empty();
        }
        List<RequiredSlot> slots = new ArrayList<>();
        for (Map<String, Object> param : requiredParams) {
            RequiredSlot slot = RequiredSlot.fromParamMap(param);
            if (slot != null) {
                slots.add(slot);
            }
        }
        return new SlotCollection(slots, Map.of());
    }

    @SuppressWarnings("unchecked")
    public static SlotCollection fromPayload(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return empty();
        }
        List<RequiredSlot> requiredSlots = new ArrayList<>();
        Object requiredRaw = payload.get("required");
        if (requiredRaw instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    RequiredSlot slot = RequiredSlot.fromParamMap((Map<String, Object>) map);
                    if (slot != null) {
                        requiredSlots.add(slot);
                    }
                }
            }
        }
        Map<String, Object> filledSlots = new LinkedHashMap<>();
        Object filledRaw = payload.get("filled");
        if (filledRaw instanceof Map<?, ?> map) {
            map.forEach((k, v) -> {
                if (k != null) {
                    filledSlots.put(String.valueOf(k), v);
                }
            });
        }
        return new SlotCollection(requiredSlots, filledSlots);
    }

    public Map<String, Object> toPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        List<Map<String, Object>> requiredMaps = new ArrayList<>();
        for (RequiredSlot slot : required) {
            requiredMaps.add(slot.toMap());
        }
        payload.put("required", requiredMaps);
        payload.put("filled", new LinkedHashMap<>(filled));
        return payload;
    }

    public boolean isComplete() {
        return firstMissing().isEmpty();
    }

    public Optional<RequiredSlot> firstMissing() {
        for (RequiredSlot slot : required) {
            if (!hasValue(slot.getName())) {
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }

    /**
     * 将用户本轮输入填入第一个缺失槽位，返回新集合（原对象不变）.
     */
    public SlotCollection fillNext(Object value) {
        Optional<RequiredSlot> missing = firstMissing();
        if (missing.isEmpty()) {
            return this;
        }
        if (value == null) {
            return this;
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return this;
        }
        Map<String, Object> nextFilled = new LinkedHashMap<>(filled);
        nextFilled.put(missing.get().getName(), text);
        return new SlotCollection(required, nextFilled);
    }

    public String continuePrompt() {
        return firstMissing()
                .map(slot -> CONTINUE_PREFIX + slot.getDescription())
                .orElse(CONTINUE_PREFIX + "必要信息");
    }

    public boolean hasRequired() {
        return !required.isEmpty();
    }

    private boolean hasValue(String name) {
        Object val = filled.get(name);
        if (val == null) {
            return false;
        }
        if (val instanceof String s) {
            return !s.isBlank();
        }
        return true;
    }
}
