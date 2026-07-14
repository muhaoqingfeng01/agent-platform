package com.example.agent.infrastructure.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 安全参数 Nacos 动态配置 — 继承 {@link NacosConfig} 模板.
 *
 * <p>对应 Nacos DataId: {@code agent-platform-security.json}
 * <p>Group: {@code AGENT-PLATFORM-CONFIG_ENTITY}
 *
 * <p>覆盖安全过滤器、审批超时、Presidio 脱敏、CORS 跨域参数。
 * <p>所有字段提供 {@code getXxx()} 便捷方法，Nacos 不可用时回退到硬编码兜底值.
 * <p>🆕 P6 配置治理子方案03: 安全参数统一 Nacos 动态管理.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class SecurityConfig extends NacosConfig<SecurityConfig.SecurityProps> {

    // ========== 构造器 ==========

    public SecurityConfig(NacosConfigManager nacosConfigManager, ObjectMapper objectMapper) {
        super(nacosConfigManager, objectMapper);
    }

    // ========== 模板方法实现 ==========

    @Override
    protected String getDataId() {
        return "agent-platform-security.json";
    }

    @Override
    protected String getGroup() {
        return "AGENT-PLATFORM-CONFIG_ENTITY";
    }

    @Override
    protected String getConfigName() {
        return "SecurityConfig";
    }

    @Override
    protected Class<SecurityProps> getPropsClass() {
        return SecurityProps.class;
    }

    // ============================================================
    // 便捷取值方法（Nacos 不可用时硬编码兜底，Optional 链式判空）
    // ============================================================

    /** 输入最大长度限制（字符），默认 10000 */
    public int getMaxInputLength() {
        return Optional.ofNullable(getConfig()).map(p -> p.maxInputLength).orElse(10000);
    }

    /** 审批超时时间（分钟），默认 5 */
    public int getApprovalTimeoutMinutes() {
        return Optional.ofNullable(getConfig()).map(p -> p.approvalTimeoutMinutes).orElse(5);
    }

    /** Presidio 脱敏超时（毫秒），默认 5000 */
    public int getPresidioTimeoutMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.presidioTimeoutMs).orElse(5000);
    }

    /** Presidio 超时时是否降级到正则，默认 true */
    public boolean isPresidioFallbackToRegex() {
        return Optional.ofNullable(getConfig()).map(p -> p.presidioFallbackToRegex).orElse(true);
    }

    /** CORS 允许的来源模式列表 */
    public List<String> getCorsAllowedOrigins() {
        return Optional.ofNullable(getConfig())
                .map(p -> p.corsAllowedOrigins)
                .filter(origins -> !origins.isEmpty())
                .orElse(DEFAULT_CORS_ORIGINS);
    }

    /** CORS 预检缓存时间（秒），默认 3600 */
    public int getCorsMaxAgeSeconds() {
        return Optional.ofNullable(getConfig()).map(p -> p.corsMaxAgeSeconds).orElse(3600);
    }

    // ============================================================
    // 内部常量
    // ============================================================

    private static final List<String> DEFAULT_CORS_ORIGINS = List.of(
            "http://localhost:*",
            "https://*.agent-platform.local",
            "https://*.agent-platform.com"
    );

    // ============================================================
    // 内部 POJO（Jackson 反序列化）
    // ============================================================

    /**
     * 配置数据对象 — 可通过 {@link SecurityConfig#getConfig()} 获取完整快照.
     */
    public static class SecurityProps {
        public Integer maxInputLength;
        public Integer approvalTimeoutMinutes;
        public Integer presidioTimeoutMs;
        public Boolean presidioFallbackToRegex;
        public List<String> corsAllowedOrigins;
        public Integer corsMaxAgeSeconds;
    }
}
