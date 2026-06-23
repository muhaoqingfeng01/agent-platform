package com.example.agent.common.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON 工具类 — 基于 Jackson 封装
 * <p>
 * 统一管理 {@link ObjectMapper} 实例，提供常用 JSON 序列化/反序列化方法，
 * 替代项目中散落的 {@code new ObjectMapper()} 调用。
 *
 * <h3>特性</h3>
 * <ul>
 *   <li>忽略未知属性（兼容 API 版本演进）</li>
 *   <li>支持 Java 8 时间类型（{@code LocalDateTime} 等）</li>
 *   <li>空值不序列化</li>
 *   <li>异常统一转为 {@link JsonException}（受检 → 非受检）</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 对象 → JSON 字符串
 * String json = JsonHelper.toJson(doc);
 *
 * // JSON 字符串 → 对象
 * DocumentDTO doc = JsonHelper.fromJson(json, DocumentDTO.class);
 *
 * // JSON 字符串 → List
 * List<DocumentDTO> list = JsonHelper.fromJsonList(json, DocumentDTO.class);
 *
 * // JSON 字符串 → Map
 * Map<String, Object> map = JsonHelper.fromJsonMap(json);
 *
 * // JSON 字符串 → 泛型对象
 * Result<DocumentDTO> result = JsonHelper.fromJson(json, new TypeReference<Result<DocumentDTO>>() {});
 * }</pre>
 *
 * @author agent-platform
 * @since 2026-06-24
 */
@Slf4j
public final class JsonHelper {

    /** 共享 ObjectMapper（线程安全） */
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper()
                // 忽略 JSON 中存在的未知属性（服务端新增字段时旧客户端不报错）
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 空 Bean 不报错
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // 支持 Java 8 时间
                .registerModule(new JavaTimeModule())
                // 不将时间写为时间戳
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonHelper() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    // ==================== 获取 ObjectMapper ====================

    /** 获取共享的 ObjectMapper 实例（避免项目中 new ObjectMapper()） */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    // ==================== 对象 → JSON 字符串 ====================

    /** 对象 → JSON 字符串 */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonException("序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /** 对象 → 格式化 JSON 字符串（带缩进） */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonException("序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /** 对象 → 字节数组 */
    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new JsonException("序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    // ==================== JSON 字符串 → 对象 ====================

    /** JSON 字符串 → 普通对象 */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] 反序列化失败: target={}, json={}", clazz.getSimpleName(), truncate(json), e);
            throw new JsonException("反序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /** JSON 字符串 → 泛型对象（如 {@code Result<DocumentDTO>}） */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] 反序列化失败: type={}, json={}", typeReference.getType(), truncate(json), e);
            throw new JsonException("反序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /** JSON 字符串 → 泛型对象（通过 JavaType） */
    public static <T> T fromJson(String json, JavaType javaType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] 反序列化失败: type={}, json={}", javaType, truncate(json), e);
            throw new JsonException("反序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /** JSON 字节数组 → 普通对象 */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("[JsonHelper] 反序列化失败: target={}", clazz.getSimpleName(), e);
            throw new JsonException("反序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    /** InputStream → 普通对象 */
    public static <T> T fromStream(InputStream inputStream, Class<T> clazz) {
        if (inputStream == null) {
            return null;
        }
        try {
            return MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            log.error("[JsonHelper] 反序列化失败: target={}", clazz.getSimpleName(), e);
            throw new JsonException("反序列化 JSON 失败: " + e.getMessage(), e);
        }
    }

    // ==================== JSON 字符串 → 集合 ====================

    /** JSON 字符串 → {@code List<T>} */
    public static <T> List<T> fromJsonList(String json, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] 反序列化 List 失败: element={}, json={}", elementClass.getSimpleName(), truncate(json), e);
            throw new JsonException("反序列化 JSON List 失败: " + e.getMessage(), e);
        }
    }

    /** JSON 字符串 → {@code Map<String, T>} */
    public static <T> Map<String, T> fromJsonMap(String json, Class<T> valueClass) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, String.class, valueClass);
            return MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] 反序列化 Map 失败: value={}, json={}", valueClass.getSimpleName(), truncate(json), e);
            throw new JsonException("反序列化 JSON Map 失败: " + e.getMessage(), e);
        }
    }

    /** JSON 字符串 → {@code Map<String, Object>} */
    public static Map<String, Object> fromJsonMap(String json) {
        return fromJsonMap(json, Object.class);
    }

    /** JSON 字符串 → {@code Map<String, String>} */
    public static Map<String, String> fromJsonStringMap(String json) {
        return fromJsonMap(json, String.class);
    }

    // ==================== 对象转换 ====================

    /** 对象 → 对象（类型转换，如 Map → PO） */
    public static <T> T convert(Object from, Class<T> toClass) {
        if (from == null) {
            return null;
        }
        return MAPPER.convertValue(from, toClass);
    }

    /** 对象 → 泛型对象（类型转换） */
    public static <T> T convert(Object from, TypeReference<T> typeReference) {
        if (from == null) {
            return null;
        }
        return MAPPER.convertValue(from, typeReference);
    }

    /** 对象 → 泛型对象（类型转换，通过 JavaType） */
    public static <T> T convert(Object from, JavaType javaType) {
        if (from == null) {
            return null;
        }
        return MAPPER.convertValue(from, javaType);
    }

    /** 对象 → {@code List<T>}（类型转换） */
    public static <T> List<T> convertToList(Object from, Class<T> elementClass) {
        if (from == null) {
            return new ArrayList<>();
        }
        JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
        return MAPPER.convertValue(from, javaType);
    }

    /** 对象 → {@code Map<String, T>}（类型转换） */
    public static <T> Map<String, T> convertToMap(Object from, Class<T> valueClass) {
        if (from == null) {
            return new HashMap<>();
        }
        JavaType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, String.class, valueClass);
        return MAPPER.convertValue(from, javaType);
    }

    // ==================== JsonNode ====================

    /** JSON 字符串 → JsonNode（树模型，用于按路径提取） */
    public static JsonNode toNode(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] 解析 JSON 树失败: json={}", truncate(json), e);
            throw new JsonException("解析 JSON 树失败: " + e.getMessage(), e);
        }
    }

    /** JsonNode → 普通对象 */
    public static <T> T fromNode(JsonNode node, Class<T> clazz) {
        if (node == null) {
            return null;
        }
        try {
            return MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            log.error("[JsonHelper] JsonNode 转换失败: target={}", clazz.getSimpleName(), e);
            throw new JsonException("JsonNode 转换失败: " + e.getMessage(), e);
        }
    }

    // ==================== 便捷判断 ====================

    /** 判断字符串是否为合法 JSON */
    public static boolean isValid(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            MAPPER.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** 判断字符串是否为 JSON 对象（以 { 开头） */
    public static boolean isJsonObject(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        String trimmed = json.trim();
        return trimmed.startsWith("{") && trimmed.endsWith("}");
    }

    /** 判断字符串是否为 JSON 数组（以 [ 开头） */
    public static boolean isJsonArray(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        String trimmed = json.trim();
        return trimmed.startsWith("[") && trimmed.endsWith("]");
    }

    // ==================== private ====================

    /** 截断过长 JSON 用于日志 */
    private static String truncate(String s) {
        return s.length() > 200 ? s.substring(0, 200) + "..." : s;
    }

    // ==================== 内部异常类 ====================

    /**
     * JSON 处理异常（非受检）
     * <p>
     * 将 Jackson 受检异常统一转为非受检，避免调用方到处 try-catch。
     */
    public static class JsonException extends RuntimeException {
        public JsonException(String message) {
            super(message);
        }

        public JsonException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
