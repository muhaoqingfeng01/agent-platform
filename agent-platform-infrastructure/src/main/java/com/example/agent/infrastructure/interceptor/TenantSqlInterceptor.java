package com.example.agent.infrastructure.interceptor;

import com.example.agent.infrastructure.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Set;

/**
 * MyBatis 租户 SQL 自动注入拦截器
 * <p>
 * 在执行 SELECT / UPDATE / DELETE 语句前自动拼接 {@code AND tenant_id = ?} 条件，
 * 实现应用层多租户数据隔离（无需在每处 SQL 手动写 tenant_id）。
 * <p>
 * 白名单表（不自动注入 tenant_id）：
 * <ul>
 *   <li>{@code t_tenant} — 租户表本身</li>
 *   <li>{@code t_permission} — 权限表（全局共享，无 tenant_id 字段）</li>
 * </ul>
 */
@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class TenantSqlInterceptor implements Interceptor {

    /** 不需要自动注入 tenant_id 的表（白名单） */
    private static final Set<String> SKIP_TABLES = Set.of("t_tenant", "t_permission");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 只拦截 SELECT / UPDATE / DELETE
        String sqlCommandType = mappedStatement.getSqlCommandType().name();
        if (!"SELECT".equals(sqlCommandType)
                && !"UPDATE".equals(sqlCommandType)
                && !"DELETE".equals(sqlCommandType)) {
            return invocation.proceed();
        }

        // 获取当前租户 ID
        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            log.trace("[TenantSQL] 当前无租户上下文，跳过 SQL 注入");
            return invocation.proceed();
        }

        // 检查是否需要跳过的表
        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();
        if (shouldSkipTenantFilter(originalSql)) {
            log.trace("[TenantSQL] 白名单表，跳过租户过滤: sql={}", trimSql(originalSql));
            return invocation.proceed();
        }

        // 注入 tenant_id 条件
        String newSql = injectTenantCondition(originalSql, tenantId);
        metaObject.setValue("delegate.boundSql.sql", newSql);

        log.debug("[TenantSQL] 租户过滤注入: tenantId={}, original={}, new={}",
                tenantId, trimSql(originalSql), trimSql(newSql));

        return invocation.proceed();
    }

    /**
     * 判断是否跳过租户过滤
     */
    private boolean shouldSkipTenantFilter(String sql) {
        String lowerSql = sql.toLowerCase();
        for (String table : SKIP_TABLES) {
            if (lowerSql.contains("from " + table) || lowerSql.contains("update " + table)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 向 SQL 注入 tenant_id 条件
     * <p>
     * 简单实现：在 WHERE 子句后追加 {@code AND tenant_id = 'xxx'}。
     * 生产环境建议使用 MyBatis-Plus 多租户插件替代此实现。
     */
    private String injectTenantCondition(String sql, String tenantId) {
        String condition = "tenant_id = '" + tenantId + "'";

        // 如果已有 WHERE 子句，追加 AND
        if (sql.toLowerCase().contains("where")) {
            return sql + " AND " + condition;
        }
        // 没有 WHERE 则追加 WHERE
        return sql + " WHERE " + condition;
    }

    /**
     * 修剪 SQL 用于日志输出（去除多余空白和换行）
     */
    private String trimSql(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }
}
