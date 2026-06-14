package com.example.agent.domain.prompt.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 模板变量定义值对象.
 * <p>
 * 描述提示词模板中每个变量的元信息，包括类型、默认值、是否必填等。
 * 存储在 t_prompt_template.variables JSON 字段中。
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableDef implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 变量名（对应 {{name}} 中的 name） */
    private String name;

    /** 变量类型: string / number / boolean / array / object */
    private String type;

    /** 变量说明（给模板编辑者看的） */
    private String description;

    /** 默认值（可选） */
    private String defaultValue;

    /** 是否必填 */
    @Builder.Default
    private boolean required = false;

    // ==================== 工厂方法 ====================

    /** 创建一个必填的 string 类型变量 */
    public static VariableDef requiredString(String name, String description) {
        return VariableDef.builder()
                .name(name)
                .type("string")
                .description(description)
                .required(true)
                .build();
    }

    /** 创建一个可选的 string 类型变量（带默认值） */
    public static VariableDef optionalString(String name, String description, String defaultValue) {
        return VariableDef.builder()
                .name(name)
                .type("string")
                .description(description)
                .defaultValue(defaultValue)
                .required(false)
                .build();
    }

    /** 创建一个 array 类型变量 */
    public static VariableDef array(String name, String description) {
        return VariableDef.builder()
                .name(name)
                .type("array")
                .description(description)
                .required(false)
                .build();
    }
}
