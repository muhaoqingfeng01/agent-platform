package com.example.agent.domain.tool.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工具类型枚举 — 定义平台支持的四种工具接入方式.
 *
 * <p>MCP 为标准 Model Context Protocol 工具，HTTP 为 REST API 包装，
 * BUILTIN 为平台内置 Java 实现，CUSTOM 为自定义扩展.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ToolType {

    /** MCP 协议工具 — 通过 MCP SSE 端点连接外部 MCP Server */
    MCP("MCP", "MCP协议工具"),

    /** HTTP 接口工具 — 将 REST API 包装为统一工具调用格式 */
    HTTP("HTTP", "HTTP接口工具"),

    /** 内置工具 — 平台内置的 Java ActionHandler 实现 */
    BUILTIN("BUILTIN", "内置工具"),

    /** 自定义工具 — 用户自定义脚本或扩展 */
    CUSTOM("CUSTOM", "自定义工具");

    private final String code;
    private final String desc;

    /**
     * 根据代码字符串查找对应的枚举值.
     *
     * @param code 工具类型代码（大小写不敏感）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果代码不匹配任何已知类型
     */
    public static ToolType fromCode(String code) {
        if (code == null || code.isBlank()) return MCP;
        for (ToolType e : values()) {
            if (e.code.equalsIgnoreCase(code)) return e;
        }
        throw new IllegalArgumentException("未知的工具类型: " + code + "，有效值: MCP, HTTP, BUILTIN, CUSTOM");
    }
}
