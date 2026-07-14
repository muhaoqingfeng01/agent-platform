package com.example.agent.infrastructure.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 定时任务调度参数 Nacos 动态配置 — 继承 {@link NacosConfig} 模板.
 *
 * <p>对应 Nacos DataId: {@code agent-platform-scheduler.json}
 * <p>Group: {@code AGENT-PLATFORM-CONFIG_ENTITY}
 *
 * <p>所有字段提供 {@code getXxxOrDefault()} 便捷方法，Nacos 不可用时回退到硬编码兜底值.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Slf4j
@Component
public class SchedulerConfig extends NacosConfig<SchedulerConfig.SchedulerProps> {

    // ========== 构造器 ==========

    public SchedulerConfig(NacosConfigManager nacosConfigManager, ObjectMapper objectMapper) {
        super(nacosConfigManager, objectMapper);
    }

    // ========== 模板方法实现 ==========

    @Override
    protected String getDataId() {
        return "agent-platform-scheduler.json";
    }

    @Override
    protected String getGroup() {
        return "AGENT-PLATFORM-CONFIG_ENTITY";
    }

    @Override
    protected String getConfigName() {
        return "SchedulerConfig";
    }

    @Override
    protected Class<SchedulerProps> getPropsClass() {
        return SchedulerProps.class;
    }

    // ========== 便捷取值方法（Nacos 不可用时硬编码兜底，Optional 链式判空） ==========

    /** 超时任务扫描间隔（毫秒），默认 15s */
    public long getTimeoutScanIntervalMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.timeoutScanIntervalMs).orElse(15_000L);
    }

    /** 僵尸任务扫描间隔（毫秒），默认 60s */
    public long getStaleScanIntervalMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.staleScanIntervalMs).orElse(60_000L);
    }

    /** 僵尸 SUBMITTED 任务判定阈值（分钟），默认 5 */
    public int getStaleSubmittedMinutes() {
        return Optional.ofNullable(getConfig()).map(p -> p.staleSubmittedMinutes).orElse(5);
    }

    /** 每次扫描最大处理数，默认 100 */
    public int getTimeoutScanBatchSize() {
        return Optional.ofNullable(getConfig()).map(p -> p.timeoutScanBatchSize).orElse(100);
    }

    /** 审批超时扫描间隔（毫秒），默认 30s */
    public long getApprovalTimeoutScanMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.approvalTimeoutScanMs).orElse(30_000L);
    }

    /** MCP 心跳检测间隔（毫秒），默认 30s */
    public long getMcpHeartbeatIntervalMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.mcpHeartbeatIntervalMs).orElse(30_000L);
    }

    /** MCP 心跳连续失败阈值，默认 3 次 */
    public int getMcpMaxFailures() {
        return Optional.ofNullable(getConfig()).map(p -> p.mcpMaxFailures).orElse(3);
    }

    /** MCP Client 连接刷新间隔（毫秒），默认 5min */
    public long getMcpClientRefreshMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.mcpClientRefreshMs).orElse(300_000L);
    }

    /** 敏感词缓存刷新间隔（毫秒），默认 5min */
    public long getSensitiveWordRefreshMs() {
        return Optional.ofNullable(getConfig()).map(p -> p.sensitiveWordRefreshMs).orElse(300_000L);
    }

    // ========== 内部 POJO（Jackson 反序列化） ==========

    /**
     * 配置数据对象 — 可通过 {@link SchedulerConfig#getConfig()} 获取完整快照.
     */
    public static class SchedulerProps {
        /** 超时任务扫描间隔（毫秒） */
        public Long timeoutScanIntervalMs;
        /** 僵尸任务扫描间隔（毫秒） */
        public Long staleScanIntervalMs;
        /** 僵尸 SUBMITTED 任务判定阈值（分钟） */
        public Integer staleSubmittedMinutes;
        /** 每次扫描最大处理数 */
        public Integer timeoutScanBatchSize;
        /** 审批超时扫描间隔（毫秒） */
        public Long approvalTimeoutScanMs;
        /** MCP 心跳检测间隔（毫秒） */
        public Long mcpHeartbeatIntervalMs;
        /** MCP 心跳连续失败阈值 */
        public Integer mcpMaxFailures;
        /** MCP Client 连接刷新间隔（毫秒） */
        public Long mcpClientRefreshMs;
        /** 敏感词缓存刷新间隔（毫秒） */
        public Long sensitiveWordRefreshMs;
    }
}
