package com.example.agent.application.prompt.resolve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上下文变量解析器 — 处理复杂类型的上下文变量.
 * <p>
 * 处理以下运行时上下文字段:
 * <ul>
 *   <li>{{conversation_history}} — 对话历史（List 格式化为文本）</li>
 *   <li>{{knowledge_context}} — 知识库检索结果</li>
 *   <li>{{available_tools}} — 可用工具清单（List 格式化为文本）</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ContextVariableResolver implements VariableResolver {

    private static final String CONVERSATION_HISTORY = "conversation_history";
    private static final String KNOWLEDGE_CONTEXT = "knowledge_context";
    private static final String AVAILABLE_TOOLS = "available_tools";

    @Override
    public String resolve(String variableName, Map<String, Object> context) {
        return switch (variableName) {
            case CONVERSATION_HISTORY -> formatConversationHistory(context);
            case KNOWLEDGE_CONTEXT -> formatKnowledgeContext(context);
            case AVAILABLE_TOOLS -> formatAvailableTools(context);
            default -> null;
        };
    }

    @Override
    public boolean supports(String variableName) {
        return CONVERSATION_HISTORY.equals(variableName)
                || KNOWLEDGE_CONTEXT.equals(variableName)
                || AVAILABLE_TOOLS.equals(variableName);
    }

    @Override
    public int priority() {
        return 20;
    }

    // ==================== 格式化方法 ====================

    @SuppressWarnings("unchecked")
    private String formatConversationHistory(Map<String, Object> context) {
        Object history = context.get(CONVERSATION_HISTORY);
        if (history == null) return "";
        if (history instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
        }
        return history.toString();
    }

    private String formatKnowledgeContext(Map<String, Object> context) {
        Object knowledge = context.get(KNOWLEDGE_CONTEXT);
        if (knowledge == null) return "";
        if (knowledge instanceof List<?> list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append("【参考资料 ").append(i + 1).append("】\n");
                sb.append(list.get(i).toString()).append("\n\n");
            }
            return sb.toString();
        }
        return knowledge.toString();
    }

    @SuppressWarnings("unchecked")
    private String formatAvailableTools(Map<String, Object> context) {
        Object tools = context.get(AVAILABLE_TOOLS);
        if (tools == null) return "无可用工具";
        if (tools instanceof List<?> list) {
            if (list.isEmpty()) return "无可用工具";
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n- ", "- ", ""));
        }
        return tools.toString();
    }
}
