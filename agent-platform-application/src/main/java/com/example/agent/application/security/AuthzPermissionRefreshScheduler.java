package com.example.agent.application.security;

import com.example.agent.infrastructure.config.nacos.SchedulerConfig;
import com.example.agent.infrastructure.config.scheduler.DynamicScheduledTaskManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在线用户权限定时刷新 — 间隔从 Nacos {@code SchedulerConfig#getAuthzPermissionRefreshMs} 读取，
 * 未配置时默认 1 小时；执行时重载 DB 覆盖缓存，不踢下线.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthzPermissionRefreshScheduler {

    private final DynamicScheduledTaskManager dynamicScheduler;
    private final AuthzSessionService authzSessionService;
    private final SchedulerConfig schedulerConfig;

    @PostConstruct
    public void registerTasks() {
        dynamicScheduler.register(
                "authzPermissionRefresh",
                authzSessionService::scheduledRefreshOnlineUsers,
                schedulerConfig::getAuthzPermissionRefreshMs);
        log.info("[AuthzRefresh] 动态定时任务已注册: authzPermissionRefresh, intervalMs={}（Nacos 可热更新，缺省 1h）",
                schedulerConfig.getAuthzPermissionRefreshMs());
    }
}
