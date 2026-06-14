package com.example.agent.common.util;

import cn.hutool.core.util.IdUtil;

/**
 * 分布式 ID 生成器 — Factory Method 模式.
 * <p>
 * 基于 Hutool 雪花算法，统一项目内所有 ID 生成格式.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public final class IdGenerator {

    private IdGenerator() { /* 工具类禁止实例化 */ }

    /**
     * 生成带前缀的业务 ID.
     *
     * @param prefix 业务前缀（如 conv, msg, int, mem）
     * @return "{prefix}_{snowflakeId}"
     */
    public static String generate(String prefix) {
        return prefix + "_" + IdUtil.getSnowflake().nextIdStr();
    }

    /**
     * 生成不带前缀的纯数字 ID.
     */
    public static String generateSimple() {
        return IdUtil.getSnowflake().nextIdStr();
    }
}
