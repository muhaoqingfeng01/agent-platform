package com.example.agent.application.security;

import com.example.agent.infrastructure.security.pii.PresidioPiiAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * PII（个人身份信息）输出脱敏器 — 增强版.
 *
 * <p>脱敏策略:
 * <ol>
 *   <li>先跑 Presidio（NER 高精度，但依赖外部服务）</li>
 *   <li>再跑正则（兜底，覆盖 Presidio 遗漏的）</li>
 *   <li>合并去重（同位置实体以 Presidio 为准）</li>
 *   <li>按位置从后往前替换（避免索引偏移）</li>
 * </ol>
 *
 * <p>Presidio 不可用时静默降级到仅正则模式，不阻塞请求.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class PiiDesensitizer {

    /** Presidio 适配器 — 可能为 null（条件装配未启用时） */
    @Autowired(required = false)
    private PresidioPiiAdapter presidioAdapter;

    /**
     * PII 脱敏规则 — 保持插入顺序（LruCache 风格）.
     * Key: PII 类型名称, Value: [Pattern, Replacement]
     */
    private static final Map<String, PiiRule> PII_RULES = new LinkedHashMap<>();

    static {
        // 身份证号: 6位地区 + 8位生日 + 4位校验 → 6位地区****校验
        PII_RULES.put("身份证号", new PiiRule(
                Pattern.compile("(\\b\\d{6})\\d{8}(\\d{4}\\b|\\d{3}[Xx]\\b)"),
                "$1********$2"
        ));
        // 手机号: 1XX + 4位 + 4位 → 1XX****后4位
        PII_RULES.put("手机号", new PiiRule(
                Pattern.compile("(\\b1[3-9]\\d)\\d{4}(\\d{4}\\b)"),
                "$1****$2"
        ));
        // 邮箱: 前2字符 + 中间 + @域名 → 前2***@域名
        PII_RULES.put("邮箱", new PiiRule(
                Pattern.compile("(\\b[A-Za-z0-9._%+-]{2})[A-Za-z0-9._%+-]*(@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b)"),
                "$1***$2"
        ));
        // 银行卡: 前4位 + 中间 + 后4位 → 前4位****后4位
        PII_RULES.put("银行卡", new PiiRule(
                Pattern.compile("(\\b\\d{4})\\d{8,16}(\\d{4}\\b)"),
                "$1****$2"
        ));
        // 固定电话: 区号-号码 → 区号-****
        PII_RULES.put("固定电话", new PiiRule(
                Pattern.compile("(\\b0\\d{2,3}-?)\\d{7,8}\\b"),
                "$1****"
        ));
        // IPv4 地址
        PII_RULES.put("IP地址", new PiiRule(
                Pattern.compile("(\\b\\d{1,3})\\.\\d{1,3}\\.\\d{1,3}\\.(\\d{1,3}\\b)"),
                "$1.*.*.$2"
        ));
    }

    /**
     * 对文本中的 PII 进行脱敏处理 — 增强版（Presidio + 正则）.
     *
     * @param text 原始文本（可能包含 PII）
     * @return 脱敏后的文本
     */
    public String desensitize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        List<DetectionResult> allDetections = new ArrayList<>();

        // 1. Presidio 检测（如果启用）
        if (presidioAdapter != null) {
            try {
                List<PresidioPiiAdapter.PiiDetection> presidioResults =
                    presidioAdapter.analyze(text, "zh");
                allDetections.addAll(presidioResults.stream()
                    .filter(d -> d.getScore() > 0.6)   // 置信度阈值
                    .map(DetectionResult::fromPresidio)
                    .toList());
                log.debug("[PiiDesensitizer] Presidio 检测到 {} 个 PII 实体", allDetections.size());
            } catch (Exception e) {
                log.warn("[PiiDesensitizer] Presidio 不可用，仅使用正则脱敏: {}", e.getMessage());
            }
        }

        // 2. 正则检测（兜底 + 去重）
        List<DetectionResult> regexDetections = detectByRegex(text);
        allDetections = mergeAndDeduplicate(allDetections, regexDetections);

        // 3. 按位置从后往前替换（避免索引偏移）
        return applyDesensitization(text, allDetections);
    }

    /**
     * 检测文本中是否包含 PII.
     *
     * @param text 待检测文本
     * @return true 表示包含 PII
     */
    public boolean containsPii(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return PII_RULES.values().stream()
                .anyMatch(rule -> rule.pattern.matcher(text).find());
    }

    /**
     * 检测并返回命中的 PII 类型列表.
     *
     * @param text 待检测文本
     * @return 命中的 PII 类型列表，空列表表示无 PII
     */
    public java.util.List<String> detectPiiTypes(String text) {
        if (text == null || text.isEmpty()) {
            return java.util.List.of();
        }

        return PII_RULES.entrySet().stream()
                .filter(entry -> entry.getValue().pattern.matcher(text).find())
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * PII 规则 — Pattern + Replacement 的不可变组合.
     */
    private record PiiRule(Pattern pattern, String replacement) {}

    /**
     * 检测结果 — 统一的 PII 位置描述.
     */
    private record DetectionResult(String type, int start, int end, float confidence) {
        static DetectionResult fromPresidio(PresidioPiiAdapter.PiiDetection d) {
            return new DetectionResult(d.getEntityType(), d.getStart(), d.getEnd(), d.getScore());
        }
    }

    /**
     * 正则检测 — 返回所有匹配到的 PII 位置.
     */
    private List<DetectionResult> detectByRegex(String text) {
        List<DetectionResult> results = new ArrayList<>();
        for (Map.Entry<String, PiiRule> entry : PII_RULES.entrySet()) {
            var matcher = entry.getValue().pattern.matcher(text);
            while (matcher.find()) {
                results.add(new DetectionResult(
                    entry.getKey(), matcher.start(), matcher.end(), 0.9f));
            }
        }
        return results;
    }

    /**
     * 合并 Presidio 和正则结果，去除重叠区域.
     * <p>规则: 如果两个检测结果位置重叠 > 50%，优先保留 Presidio（置信度更高）.
     */
    private List<DetectionResult> mergeAndDeduplicate(
        List<DetectionResult> presidioResults,
        List<DetectionResult> regexResults) {

        Set<Integer> coveredPositions = new HashSet<>();
        List<DetectionResult> merged = new ArrayList<>();

        // Presidio 优先
        for (DetectionResult d : presidioResults) {
            merged.add(d);
            for (int i = d.start; i < d.end; i++) {
                coveredPositions.add(i);
            }
        }

        // 正则补充（跳过已被 Presidio 覆盖的位置）
        for (DetectionResult d : regexResults) {
            long overlapCount = IntStream.range(d.start, d.end)
                .filter(coveredPositions::contains)
                .count();
            double overlapRatio = (double) overlapCount / (d.end - d.start);

            if (overlapRatio < 0.5) {  // 重叠 < 50% 才加入
                merged.add(d);
            }
        }

        return merged;
    }

    /**
     * 按位置从后往前应用脱敏替换（避免索引偏移）.
     */
    private String applyDesensitization(String text, List<DetectionResult> detections) {
        if (detections.isEmpty()) return text;

        // 按起始位置降序（从后往前替换）
        List<DetectionResult> sorted = detections.stream()
            .sorted(Comparator.comparingInt(DetectionResult::start).reversed())
            .toList();

        StringBuilder sb = new StringBuilder(text);
        for (DetectionResult d : sorted) {
            String replacement = buildReplacement(d.end - d.start);
            sb.replace(d.start, Math.min(d.end, sb.length()), replacement);
        }
        return sb.toString();
    }

    /**
     * 根据长度生成脱敏替换字符串（如 "***"）.
     */
    private String buildReplacement(int length) {
        int stars = Math.min(length, 4);
        return "*".repeat(stars);
    }
}
