package com.example.agent.application.prompt;

import com.example.agent.application.prompt.resolve.VariableResolver;
import com.example.agent.domain.prompt.valueobject.VariableDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词渲染服务 — Strategy + Chain of Responsibility 模式.
 * <p>
 * 核心职责: 将模板文本中的 {{variable}} 占位符替换为实际值。
 * <p>
 * 渲染流程:
 * <ol>
 *   <li>遍历模板文本中的所有 {{variable}} 占位符</li>
 *   <li>对每个变量，按优先级调用 VariableResolver 链</li>
 *   <li>第一个能处理的 Resolver 返回结果</li>
 *   <li>处理完所有变量后，检查是否有未填充的必填变量</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class PromptRenderService {

    private final List<VariableResolver> resolvers;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    /**
     * 构造函数注入所有 VariableResolver 实现，按优先级排序.
     */
    public PromptRenderService(List<VariableResolver> resolvers) {
        this.resolvers = resolvers.stream()
                .sorted(Comparator.comparingInt(VariableResolver::priority))
                .toList();
        log.info("[PromptRender] 加载 {} 个变量解析器: {}",
                this.resolvers.size(),
                this.resolvers.stream().map(r -> r.getClass().getSimpleName()).toList());
    }

    /**
     * 渲染模板 — 将模板文本中的变量替换为实际值.
     *
     * @param templateText 含 {{variable}} 占位符的模板文本
     * @param context      运行时上下文（包含所有变量值）
     * @return 渲染后的文本
     */
    public String render(String templateText, Map<String, Object> context) {
        if (templateText == null || templateText.isBlank()) {
            return "";
        }

        String result = templateText;

        // 提取所有唯一变量名
        List<String> varNames = VARIABLE_PATTERN.matcher(templateText).results()
                .map(m -> m.group(1))
                .distinct()
                .toList();

        // 按顺序替换每个变量
        for (String varName : varNames) {
            String resolved = resolveVariable(varName, context);
            String placeholder = "{{" + varName + "}}";
            // 使用 replace 而非 replaceAll 避免正则特殊字符问题
            result = result.replace(placeholder, resolved != null ? resolved : "");
        }

        return result;
    }

    /**
     * 渲染模板并校验必填变量.
     *
     * @param templateText 模板文本
     * @param context      上下文变量
     * @param variableDefs 变量定义列表（用于必填校验）
     * @return 渲染后的文本
     * @throws IllegalStateException 如果必填变量未填充
     */
    public String renderWithValidation(String templateText, Map<String, Object> context,
                                        List<VariableDef> variableDefs) {
        String rendered = render(templateText, context);

        // 校验未填充的占位符
        Matcher m = VARIABLE_PATTERN.matcher(rendered);
        if (m.find()) {
            throw new IllegalStateException("存在未填充的变量: {{" + m.group(1) + "}}");
        }

        // 校验必填变量
        if (variableDefs != null) {
            for (VariableDef def : variableDefs) {
                if (def.isRequired()) {
                    Object value = context.get(def.getName());
                    if (value == null || (value instanceof String s && s.isBlank())) {
                        throw new IllegalStateException(
                                "必填变量未提供: " + def.getName() + " (" + def.getDescription() + ")");
                    }
                }
            }
        }

        return rendered;
    }

    /**
     * 使用解析器链解析单个变量.
     * <p>
     * 遍历所有解析器（按优先级排序），返回第一个非 null 结果。
     */
    private String resolveVariable(String variableName, Map<String, Object> context) {
        for (VariableResolver resolver : resolvers) {
            if (resolver.supports(variableName)) {
                String result = resolver.resolve(variableName, context);
                if (result != null) {
                    log.debug("[PromptRender] 变量 {} 由 {} 解析", variableName,
                            resolver.getClass().getSimpleName());
                    return result;
                }
            }
        }
        log.debug("[PromptRender] 变量 {} 无解析器可处理，返回空字符串", variableName);
        return "";
    }

    /**
     * 提取模板中的所有变量名（用于前端编辑器提示）.
     */
    public List<String> extractVariables(String templateText) {
        return VARIABLE_PATTERN.matcher(templateText).results()
                .map(m -> m.group(1))
                .distinct()
                .toList();
    }
}
