package com.example.agent.common.exception;

/**
 * 重复资源异常（409 Conflict）
 * <p>
 * 创建资源时发现同名/同键资源已存在则抛出此异常。
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resource, Object key) {
        super(409, resource + " 已存在: " + key);
    }

    public DuplicateResourceException(String message) {
        super(409, message);
    }
}
