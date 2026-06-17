package com.example.agent.domain.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 租户权限校验器
 * <p>
 * 用于校验当前登录用户是否属于指定租户，
 * 防止跨租户数据访问。
 * <p>
 * 使用示例：
 * <pre>{@code
 *   if (!tenantValidator.isSameTenant(request.getTenantId())) {
 *       throw new SaTokenException("无权操作该租户数据");
 *   }
 * }</pre>
 */
@Slf4j
@Component("tenantValidator")
public class TenantPermissionValidator {

    /**
     * 校验当前用户是否属于目标租户
     * <p>
     * 从 Sa-Token Session 中获取登录时存储的 tenantId，
     * 与目标 tenantId 进行比对。
     *
     * @param targetTenantId 目标租户 ID
     * @return true=属于该租户，false=不属于
     */
    public boolean isSameTenant(Long targetTenantId) {
        if (targetTenantId == null) {
            log.warn("[TenantValidator] 目标租户 ID 为 null");
            return false;
        }

        SaSession session = StpUtil.getSession();
        Long currentTenantId = session.getLong("tenantId");

        if (currentTenantId == null) {
            log.warn("[TenantValidator] 当前用户 Session 中无 tenantId，拒绝跨租户操作");
            return false;
        }

        boolean isSame = currentTenantId.equals(targetTenantId);
        if (!isSame) {
            log.warn("[TenantValidator] 跨租户操作被阻止: currentTenant={}, targetTenant={}",
                    currentTenantId, targetTenantId);
        }
        return isSame;
    }

    /**
     * 获取当前登录用户的租户 ID
     *
     * @return 当前租户 ID，未登录返回 null
     */
    public Long getCurrentTenantId() {
        try {
            return StpUtil.getSession().getLong("tenantId");
        } catch (Exception e) {
            log.debug("[TenantValidator] 获取当前租户 ID 失败（可能未登录）: {}", e.getMessage());
            return null;
        }
    }
}
