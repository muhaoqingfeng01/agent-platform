package com.example.agent.application.prompt.resolve;

import com.example.agent.infrastructure.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

/**
 * 系统变量解析器 — 自动注入系统级变量.
 * <p>
 * 处理以下系统保留变量（无需用户在模板中声明）:
 * <ul>
 *   <li>{{user_name}} — 当前登录用户名</li>
 *   <li>{{tenant_name}} — 当前租户名称</li>
 *   <li>{{current_time}} — 当前时间（yyyy-MM-dd HH:mm:ss）</li>
 *   <li>{{current_date}} — 当前日期（yyyy-MM-dd）</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class SystemVariableResolver implements VariableResolver {

    private static final Set<String> SYSTEM_VARIABLES = Set.of(
            "user_name", "tenant_name", "current_time", "current_date"
    );

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String resolve(String variableName, Map<String, Object> context) {
        if (!SYSTEM_VARIABLES.contains(variableName)) {
            return null;
        }

        return switch (variableName) {
            case "user_name" -> TenantContext.getCurrentUserId() != null
                    ? TenantContext.getCurrentUserId() : "未知用户";
            case "tenant_name" -> TenantContext.getCurrentTenantId() != null
                    ? TenantContext.getCurrentTenantId() : "默认租户";
            case "current_time" -> LocalDateTime.now().format(DATETIME_FMT);
            case "current_date" -> LocalDateTime.now().format(DATE_FMT);
            default -> null;
        };
    }

    @Override
    public boolean supports(String variableName) {
        return SYSTEM_VARIABLES.contains(variableName);
    }

    @Override
    public int priority() {
        return 10; // 系统变量最高优先级
    }
}
