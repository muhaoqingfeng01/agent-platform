package com.example.agent.infrastructure.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 会话 & 记忆参数 Nacos 动态配置 — 继承 {@link NacosConfig} 模板.
 *
 * <p>对应 Nacos DataId: {@code agent-platform-session.json}
 * <p>Group: {@code AGENT-PLATFORM-CONFIG_ENTITY}
 *
 * <p>覆盖短期记忆、长期记忆、SSE 心跳、缓存 TTL 参数。
 * <p>所有字段提供 {@code getXxx()} 便捷方法，Nacos 不可用时回退到硬编码兜底值.
 * <p>🆕 P6 配置治理子方案03: 会话/记忆/缓存参数统一管理，消除 HEARTBEAT_INTERVAL_MS 重复定义.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class SessionConfig extends NacosConfig<SessionConfig.SessionProps> {

    // ========== 构造器 ==========

    public SessionConfig(NacosConfigManager nacosConfigManager, ObjectMapper objectMapper) {
        super(nacosConfigManager, objectMapper);
    }

    // ========== 模板方法实现 ==========

    @Override
    protected String getDataId() {
        return "agent-platform-session.json";
    }

    @Override
    protected String getGroup() {
        return "AGENT-PLATFORM-CONFIG_ENTITY";
    }

    @Override
    protected String getConfigName() {
        return "SessionConfig";
    }

    @Override
    protected Class<SessionProps> getPropsClass() {
        return SessionProps.class;
    }

    // ============================================================
    // 便捷取值方法（Nacos 不可用时硬编码兜底，Optional 链式判空）
    // ============================================================

    /** 短期记忆最大轮数，默认 20 */
    public int getShortTermMemoryMaxRounds() {
        return Optional.ofNullable(getConfig()).map(p -> p.shortTermMemoryMaxRounds).orElse(20);
    }

    /** 短期记忆 TTL（分钟），默认 30 */
    public int getShortTermMemoryTtlMinutes() {
        return Optional.ofNullable(getConfig()).map(p -> p.shortTermMemoryTtlMinutes).orElse(30);
    }

    /** 长期记忆最大轮数，默认 20 */
    public int getLongTermMemoryMaxRounds() {
        return Optional.ofNullable(getConfig()).map(p -> p.longTermMemoryMaxRounds).orElse(20);
    }

    /** SSE 心跳间隔（毫秒），默认 15000（15s） */
    public long getSseHeartbeatIntervalMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.sseHeartbeatIntervalMs).orElse(15_000L);
    }

    /** 上下文引用轮数（用于 RAG 检索增强），默认 5 */
    public int getContextRounds() {
        return Optional.ofNullable(getConfig()).map(p -> p.contextRounds).orElse(5);
    }

    /** 意图缓存 TTL（分钟），默认 30 */
    public int getIntentCacheTtlMinutes() {
        return Optional.ofNullable(getConfig()).map(p -> p.intentCacheTtlMinutes).orElse(30);
    }

    /** 工具缓存 TTL（分钟），默认 60 */
    public int getToolCacheTtlMinutes() {
        return Optional.ofNullable(getConfig()).map(p -> p.toolCacheTtlMinutes).orElse(60);
    }

    // ============================================================
    // 内部 POJO（Jackson 反序列化）
    // ============================================================

    /**
     * 配置数据对象 — 可通过 {@link SessionConfig#getConfig()} 获取完整快照.
     */
    public static class SessionProps {
        public Integer shortTermMemoryMaxRounds;
        public Integer shortTermMemoryTtlMinutes;
        public Integer longTermMemoryMaxRounds;
        public Long sseHeartbeatIntervalMs;
        public Integer contextRounds;
        public Integer intentCacheTtlMinutes;
        public Integer toolCacheTtlMinutes;
    }
}
