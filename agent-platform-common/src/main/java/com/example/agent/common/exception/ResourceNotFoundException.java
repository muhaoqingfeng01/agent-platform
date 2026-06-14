package com.example.agent.common.exception;

/**
 * 资源不存在异常（404）
 * <p>
 * 查询/更新/删除指定资源时，资源不存在则抛出此异常。
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object id) {
        super(404, resource + " 不存在: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}
