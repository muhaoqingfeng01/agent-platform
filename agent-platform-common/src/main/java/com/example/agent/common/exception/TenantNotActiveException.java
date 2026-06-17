package com.example.agent.common.exception;

/**
 * 租户已停用异常（403）
 * <p>
 * 操作已停用（SUSPENDED）租户下的数据时抛出此异常。
 */
public class TenantNotActiveException extends BusinessException {

    public TenantNotActiveException(Long tenantId) {
        super(403, "租户已停用: " + tenantId);
    }
}
