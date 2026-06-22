package com.example.agent.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 时间转换工具 — 将 LocalDateTime 转换为前端时间戳.
 *
 * <p>统一使用 Asia/Shanghai (UTC+8) 时区.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
public final class TimeConverters {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private TimeConverters() {}

    /**
     * LocalDateTime → epoch 毫秒时间戳.
     *
     * @param ldt 本地时间，可为 null
     * @return epoch 毫秒，null 返回 null
     */
    public static Long toEpochMilli(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.atZone(ZONE).toInstant().toEpochMilli();
    }
}
