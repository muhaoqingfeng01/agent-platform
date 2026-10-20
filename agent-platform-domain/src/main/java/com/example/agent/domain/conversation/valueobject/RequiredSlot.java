package com.example.agent.domain.conversation.valueobject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 意图必需槽位规格 — 与已填值分离，便于检查点只存结构 + 当前值.
 *
 * @author Agent Platform Team
 * @since 1.9.0
 */
@Getter
@Builder
@EqualsAndHashCode
public class RequiredSlot {

    private final String name;
    private final String description;

    public static RequiredSlot of(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("槽位 name 不能为空");
        }
        String desc = (description == null || description.isBlank()) ? name : description;
        return RequiredSlot.builder().name(name.trim()).description(desc.trim()).build();
    }

    /**
     * 从意图 {@code requiredParams} 条目解析，兼容 name/key/param 与 description/desc/label.
     */
    public static RequiredSlot fromParamMap(Map<String, Object> param) {
        if (param == null || param.isEmpty()) {
            return null;
        }
        String name = firstText(param, "name", "key", "param");
        if (name == null) {
            return null;
        }
        String description = firstText(param, "description", "desc", "label");
        return of(name, description);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("description", description);
        return map;
    }

    private static String firstText(Map<String, Object> param, String... keys) {
        for (String key : keys) {
            Object val = param.get(key);
            if (val != null) {
                String text = String.valueOf(val).trim();
                if (!text.isEmpty() && !"null".equalsIgnoreCase(text)) {
                    return text;
                }
            }
        }
        return null;
    }
}
