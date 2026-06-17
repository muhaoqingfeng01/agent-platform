# P3-T10 Microsoft Presidio PII 增强脱敏

## 所属阶段
**P3 安全治理 → T10 安全围栏**

## 使用技术
- Microsoft Presidio（Python 服务，基于 NER 的 PII 检测）
- Presidio Analyzer（PII 检测引擎）+ Presidio Anonymizer（脱敏引擎）
- Spring RestClient（Java 端调用 Presidio HTTP API）
- 现有 `PiiDesensitizer` 保留（正则兜底）

---

## 1. 当前状态

| 功能 | 状态 | 说明 |
|------|:--:|------|
| 正则 PII 脱敏 | ✅ | 6 类 PII（身份证/手机/邮箱/银行卡/固话/IP） |
| 4 层输入过滤链 | ✅ | Injection → Jailbreak → SensitiveWord → Length |
| **Presidio 增强** | ❌ | 原方案标记「可选，增强脱敏能力」 |

**当前正则方案的局限性**：
- 依赖精确格式匹配，变体绕过（如手机号中间插空格 `138 0000 1234`）
- 无法识别上下文相关的 PII（如地址、人名、公司名）
- 新类型 PII 需要手动添加正则

---

## 2. Presidio 简介

Microsoft Presidio 基于 NER（命名实体识别）模型，可以识别：

| PII 类型 | 正则方案 | Presidio 增强 |
|------|:--:|:--:|
| 身份证号 | ✅ 18位格式 | ✅ + 校验码验证 |
| 手机号 | ✅ 11位格式 | ✅ + 变体识别（分隔符） |
| 邮箱 | ✅ | ✅ |
| 银行卡号 | ✅ | ✅ + Luhn 校验 |
| 人名 | ❌ 无法识别 | ✅ NER 人名实体 |
| 地址 | ❌ 无法识别 | ✅ NER 地址实体 |
| 公司名 | ❌ 无法识别 | ✅ NER 组织实体 |
| 车牌号 | ❌ 无法识别 | ✅ 自定义正则 |
| 密码/密钥 | ❌ 无法识别 | ✅ 高熵值检测 |

---

## 3. 部署架构

```
Agent Platform (Java)
    │
    │ HTTP POST /analyze (JSON)
    ▼
Presidio Analyzer (Python/Docker)
    │
    │ 返回: [{type: "PHONE_NUMBER", start: 15, end: 26, score: 0.95}, ...]
    ▼
Agent Platform (Java)
    │
    │ 脱敏决策（与正则结果合并去重）
    ▼
Presidio Anonymizer (Python/Docker)   ← 可选，也可 Java 端自己做脱敏
    │
    ▼
脱敏后的文本返回给用户
```

### Docker 部署

```bash
# 拉取 Presidio 镜像
docker pull mcr.microsoft.com/presidio-analyzer:latest
docker pull mcr.microsoft.com/presidio-anonymizer:latest

# docker-compose.yml
services:
  presidio-analyzer:
    image: mcr.microsoft.com/presidio-analyzer:latest
    ports:
      - "3000:3000"
    environment:
      - LOG_LEVEL=INFO

  presidio-anonymizer:
    image: mcr.microsoft.com/presidio-anonymizer:latest
    ports:
      - "3001:3001"
```

---

## 4. 实现方案

### 4.1 Presidio 适配器

```java
package com.example.agent.infrastructure.security.pii;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Microsoft Presidio PII 分析适配器.
 *
 * 通过 HTTP 调用 Presidio Analyzer 服务进行高级 PII 检测.
 * 条件装配: security.pii.presidio.enabled=true 时启用.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "security.pii.presidio.enabled", havingValue = "true")
public class PresidioPiiAdapter {

    private final RestClient restClient;

    /**
     * 调用 Presidio Analyzer 分析文本中的 PII.
     *
     * @param text 待检测文本
     * @param language 语言（zh/en）
     * @return PII 检测结果列表
     */
    public List<PiiDetection> analyze(String text, String language) {
        try {
            AnalyzeRequest request = AnalyzeRequest.builder()
                .text(text)
                .language(language)
                .build();

            ResponseEntity<List<PiiDetection>> response = restClient.post()
                .uri("/analyze")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            log.error("Presidio 调用失败，回退到正则脱敏: {}", e.getMessage());
            return List.of();  // 静默降级，不影响正则脱敏流程
        }
    }

    // --- DTO ---

    @Data
    @Builder
    public static class AnalyzeRequest {
        private String text;
        private String language;
    }

    @Data
    public static class PiiDetection {
        @JsonProperty("entity_type")
        private String entityType;      // PHONE_NUMBER / PERSON / LOCATION ...

        @JsonProperty("start")
        private int start;

        @JsonProperty("end")
        private int end;

        @JsonProperty("score")
        private float score;            // 置信度 0-1
    }
}
```

### 4.2 与现有 PiiDesensitizer 的协作

```java
// PiiDesensitizer.java 增强

/**
 * 增强版 PII 脱敏器.
 *
 * 脱敏策略:
 * 1. 先跑 Presidio（NER 高精度，但依赖外部服务）
 * 2. 再跑正则（兜底，覆盖 Presidio 遗漏的）
 * 3. 合并去重（同位置实体以 Presidio 为准）
 * 4. 执行替换
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PiiDesensitizer {

    private final PresidioPiiAdapter presidioAdapter;  // 可能为 null（条件装配）

    /** 6 类正则模式（现有逻辑，不变） */
    private static final Map<String, Pattern> REGEX_PATTERNS = Map.of(
        "PHONE_NUMBER",  Pattern.compile("..."),
        "ID_CARD",       Pattern.compile("..."),
        "EMAIL",         Pattern.compile("..."),
        "BANK_CARD",     Pattern.compile("..."),
        "LANDLINE",      Pattern.compile("..."),
        "IP_ADDRESS",    Pattern.compile("...")
    );

    /**
     * 对输出文本执行完整脱敏.
     */
    public String desensitize(String text) {
        if (text == null || text.isEmpty()) return text;

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
                log.debug("Presidio 检测到 {} 个 PII 实体", allDetections.size());
            } catch (Exception e) {
                log.warn("Presidio 不可用，仅使用正则脱敏: {}", e.getMessage());
            }
        }

        // 2. 正则检测（兜底 + 去重）
        List<DetectionResult> regexDetections = detectByRegex(text);
        allDetections = mergeAndDeduplicate(allDetections, regexDetections);

        // 3. 按位置从后往前替换（避免索引偏移）
        return applyDesensitization(text, allDetections);
    }

    /**
     * 合并 Presidio 和正则结果，去除重叠区域.
     *
     * 规则: 如果两个检测结果位置重叠 > 50%，优先保留 Presidio（置信度更高）
     */
    private List<DetectionResult> mergeAndDeduplicate(
        List<DetectionResult> presidioResults,
        List<DetectionResult> regexResults) {

        Set<Integer> coveredPositions = new HashSet<>();
        List<DetectionResult> merged = new ArrayList<>();

        // Presidio 优先
        for (DetectionResult d : presidioResults) {
            merged.add(d);
            for (int i = d.getStart(); i < d.getEnd(); i++) {
                coveredPositions.add(i);
            }
        }

        // 正则补充（跳过已被 Presidio 覆盖的位置）
        for (DetectionResult d : regexResults) {
            long overlapCount = IntStream.range(d.getStart(), d.getEnd())
                .filter(coveredPositions::contains)
                .count();
            double overlapRatio = (double) overlapCount / (d.getEnd() - d.getStart());

            if (overlapRatio < 0.5) {  // 重叠 < 50% 才加入
                merged.add(d);
            }
        }

        return merged;
    }
}
```

### 4.3 配置

```yaml
# application.yml
security:
  pii:
    presidio:
      enabled: false           # 默认禁用，生产环境启用
      analyzer-url: http://localhost:3000
      anonymizer-url: http://localhost:3001
      timeout: 5000            # 超时 5 秒
      fallback-to-regex: true  # 超时时降级到正则

  filter:
    max-input-length: 10000
```

---

## 5. 降级策略

```
                      请求到达
                         │
                    Presidio 可用?
                    ┌─────┴─────┐
                    ▼ Yes        ▼ No / Timeout
            Presidio + 正则     仅用正则
            (高精度模式)        (降级模式)
                    │                │
                    └───────┬────────┘
                            ▼
                      PII 脱敏完成
                      (regardless of path)
```

**关键原则**：Presidio 不可用时**绝不阻塞请求**，静默降级到正则模式。Presidio 是增强，不是必需品。

---

## 6. 工作量

| 活动 | 时间 |
|------|:--:|
| Docker 部署 Presidio | 0.5h |
| PresidioPiiAdapter | 1.5h |
| PiiDesensitizer 增强（合并+降级） | 1h |
| 配置 + 条件装配 | 0.5h |
| 测试（Presidio 可用 + 不可用两种场景） | 1h |
| **合计** | **~1天** |

---

## 7. 实施建议

| 建议 | 说明 |
|------|------|
| **不是高优先级** | 当前 6 类正则 PII 脱敏已覆盖中国常见 PII 类型，Presidio 主要增强人名/地址等 NER 类型 |
| **随 Docker Compose 环境一起部署** | Presidio 需要额外 2 个容器，不值得单独起 |
| **等合规需求明确后再做** | 如果不需要脱敏人名/地址/公司名，当前正则方案已够用 |
