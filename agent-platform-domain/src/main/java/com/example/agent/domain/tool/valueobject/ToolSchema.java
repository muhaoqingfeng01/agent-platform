package com.example.agent.domain.tool.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具 Schema 值对象 — 描述工具的输入参数和输出格式.
 *
 * <p>inputSchema 和 outputSchema 均为标准 JSON Schema 格式字符串，
 * 供 LLM 理解工具的调用契约，也用于前端动态生成参数表单.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolSchema {

    /** 输入参数 JSON Schema — 定义工具调用时需要传入的参数类型、必填项、约束等 */
    private String inputSchema;

    /** 输出格式 JSON Schema — 定义工具返回结果的数据结构，供 LLM 理解如何解读返回值 */
    private String outputSchema;
}
