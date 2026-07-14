package com.example.agent.infrastructure.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Nacos 配置抽象模板类 — 子类只需定义 dataId/group/configName/propsClass + 字段 + applyConfig().
 *
 * <pre>
 * 使用示例:
 * {@code @Component public class XxxConfig extends NacosConfig<XxxConfig.Props> {
 *     @Getter private volatile String someField;
 *     {@code @Override} protected String getDataId() { return "xxx.json"; }
 *     {@code @Override} protected String getGroup() { return "GROUP"; }
 *     {@code @Override} protected String getConfigName() { return "XxxConfig"; }
 *     {@code @Override} protected Class<Props> getPropsClass() { return Props.class; }
 *     {@code @Override} protected void applyConfig(Props p) { this.someField = p.someField; }
 *     private static class Props { public String someField; }
 * }}</pre>
 *
 * @param <T> 配置属性 POJO 类型（用于 Jackson 反序列化）
 */
@Slf4j
public abstract class NacosConfig<T> {

    protected final NacosConfigManager nacosConfigManager;
    protected final ObjectMapper objectMapper;

    /** 当前配置快照（volatile 保证多线程可见性） */
    private volatile T configData;

    // ========== 子类必须实现 ==========

    /** Nacos 配置 dataId */
    protected abstract String getDataId();

    /** Nacos 配置 group */
    protected abstract String getGroup();

    /** 配置名称（用于日志标记，如 "AiModelConfig"） */
    protected abstract String getConfigName();

    /** 配置属性 POJO 的 Class，用于 Jackson 反序列化 */
    protected abstract Class<T> getPropsClass();


    // ========== 公共 API ==========

    /**
     * 返回当前配置的完整快照对象.
     * <p>返回的是内部数据的引用，调用方不应修改返回值。</p>
     *
     * @return 配置快照，首次加载前可能为 {@code null}
     */
    public T getConfig() {
        return configData;
    }

    // ========== 模板方法 ==========

    protected NacosConfig(NacosConfigManager nacosConfigManager, ObjectMapper objectMapper) {
        this.nacosConfigManager = nacosConfigManager;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        refresh();
        registerListener();
    }

    private void refresh() {
        try {
            String config = nacosConfigManager.getConfigService()
                    .getConfig(getDataId(), getGroup(), 5000);
            if (config != null && !config.isEmpty()) {
                parseConfig(config);
                log.info("[{}] 配置加载成功", getConfigName());
            } else {
                log.warn("[{}] Nacos 中不存在配置: dataId={}, group={}", getConfigName(), getDataId(), getGroup());
            }
        } catch (NacosException e) {
            log.error("[{}] 加载配置失败: {}", getConfigName(), e.getMessage());
        }
    }

    private void parseConfig(String json) {
        try {
            this.configData = objectMapper.readValue(json, getPropsClass());
        } catch (Exception e) {
            log.error("[{}] JSON 解析失败: {}", getConfigName(), e.getMessage());
        }
    }

    private void registerListener() {
        try {
            nacosConfigManager.getConfigService().addListener(getDataId(), getGroup(), new Listener() {
                @Override
                public Executor getExecutor() {
                    return Executors.newSingleThreadExecutor();
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("[{}] 配置已变更，重新加载...", getConfigName());
                    parseConfig(configInfo);
                }
            });
        } catch (NacosException e) {
            log.error("[{}] 注册监听器失败: {}", getConfigName(), e.getMessage());
        }
    }
}
