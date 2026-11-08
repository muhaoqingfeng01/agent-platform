package com.example.agent.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 演示用 HTTP 工具端点 — 供对话内工具调用联调（白名单，无需登录）.
 *
 * <p>对应种子工具见 docs/database 追加段与 scripts/seed-demo-tools.http
 */
@RestController
@RequestMapping("/api/v1/demo-tools")
@Tag(name = "演示工具", description = "本地联调用假工具，无需外部 MCP")
public class DemoHttpToolController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/echo")
    @Operation(summary = "回声工具 — 原样返回输入")
    public Map<String, Object> echo(@RequestBody EchoRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("echo", request.getMessage() != null ? request.getMessage() : "");
        result.put("length", request.getMessage() != null ? request.getMessage().length() : 0);
        result.put("ok", true);
        return result;
    }

    @PostMapping("/current-time")
    @Operation(summary = "当前时间 — 按时区返回本地时间")
    public Map<String, Object> currentTime(@RequestBody(required = false) TimeRequest request) {
        String zone = request != null && request.getTimezone() != null && !request.getTimezone().isBlank()
                ? request.getTimezone()
                : "Asia/Shanghai";
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(zone);
        } catch (Exception e) {
            zoneId = ZoneId.of("Asia/Shanghai");
            zone = "Asia/Shanghai";
        }
        LocalDateTime now = LocalDateTime.now(zoneId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("timezone", zone);
        result.put("datetime", now.format(FMT));
        result.put("dayOfWeek", now.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.CHINA));
        result.put("ok", true);
        return result;
    }

    @PostMapping("/city-weather")
    @Operation(summary = "城市天气（模拟）— 返回假天气数据便于联调")
    public Map<String, Object> cityWeather(@RequestBody WeatherRequest request) {
        String city = request.getCity() != null && !request.getCity().isBlank() ? request.getCity() : "上海";
        String[] conditions = {"晴", "多云", "小雨", "阴"};
        String condition = conditions[ThreadLocalRandom.current().nextInt(conditions.length)];
        int temp = 12 + ThreadLocalRandom.current().nextInt(18);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("city", city);
        result.put("condition", condition);
        result.put("temperatureC", temp);
        result.put("humidity", 40 + ThreadLocalRandom.current().nextInt(40));
        result.put("summary", city + "今日" + condition + "，气温约 " + temp + "℃");
        result.put("source", "demo-mock");
        result.put("ok", true);
        return result;
    }

    @PostMapping("/calculator")
    @Operation(summary = "简易计算器 — 支持 add/sub/mul/div")
    public Map<String, Object> calculator(@RequestBody CalcRequest request) {
        double a = request.getA();
        double b = request.getB();
        String op = request.getOp() != null ? request.getOp().toLowerCase(Locale.ROOT) : "add";
        double value;
        switch (op) {
            case "sub", "subtract" -> value = a - b;
            case "mul", "multiply" -> value = a * b;
            case "div", "divide" -> {
                if (b == 0) {
                    return Map.of("ok", false, "error", "除数不能为 0");
                }
                value = a / b;
            }
            default -> value = a + b;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("op", op);
        result.put("a", a);
        result.put("b", b);
        result.put("result", value);
        result.put("ok", true);
        return result;
    }

    @Data
    public static class EchoRequest {
        private String message;
    }

    @Data
    public static class TimeRequest {
        private String timezone;
    }

    @Data
    public static class WeatherRequest {
        private String city;
    }

    @Data
    public static class CalcRequest {
        private String op;
        private double a;
        private double b;
    }
}
