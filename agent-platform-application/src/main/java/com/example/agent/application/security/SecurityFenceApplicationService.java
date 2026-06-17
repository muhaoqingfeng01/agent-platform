package com.example.agent.application.security;

import com.example.agent.application.security.dto.CreateSensitiveWordRequest;
import com.example.agent.application.security.dto.SecurityEventResponse;
import com.example.agent.application.security.dto.SensitiveWordResponse;
import com.example.agent.application.security.dto.UpdateSensitiveWordRequest;
import com.example.agent.application.security.filter.FilterContext;
import com.example.agent.application.security.filter.FilterResult;
import com.example.agent.application.security.filter.InputFilter;
import com.example.agent.common.exception.SecurityBlockedException;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.domain.security.entity.SecurityEvent;
import com.example.agent.domain.security.entity.SensitiveWord;
import com.example.agent.domain.security.repository.SecurityEventRepository;
import com.example.agent.domain.security.repository.SensitiveWordRepository;
import com.example.agent.domain.security.valueobject.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 安全围栏应用服务 — Facade 模式，编排过滤器链和脱敏流程.
 *
 * <p>核心职责：
 * <ul>
 *   <li>{@link #filterInput}: 遍历所有 InputFilter（按 order 排序），遇阻断抛 SecurityBlockedException</li>
 *   <li>{@link #desensitizeOutput}: 调用 PiiDesensitizer 脱敏 LLM 响应</li>
 *   <li>敏感词 CRUD 管理</li>
 *   <li>安全事件查询</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityFenceApplicationService {

    private final List<InputFilter> filters;
    private final PiiDesensitizer piiDesensitizer;
    private final SecurityEventRecorder eventRecorder;
    private final SensitiveWordRepository sensitiveWordRepository;
    private final SecurityEventRepository securityEventRepository;

    /**
     * 输入过滤 — 按过滤器优先级顺序执行，任一阻断即抛出异常.
     *
     * @param content 用户输入内容
     * @param context 过滤上下文
     * @throws SecurityBlockedException 当任一过滤器判定阻断时
     */
    public void filterInput(String content, FilterContext context) {
        if (content == null || content.isEmpty()) {
            return;
        }

        // 按 order 排序后依次执行
        List<InputFilter> sortedFilters = new ArrayList<>(filters);
        sortedFilters.sort(Comparator.comparingInt(InputFilter::order));

        for (InputFilter filter : sortedFilters) {
            FilterResult result;
            try {
                result = filter.filter(content, context);
            } catch (Exception e) {
                log.error("[SecurityFence] 过滤器异常: filter={}, conversationId={}",
                        filter.getClass().getSimpleName(), context.getConversationId(), e);
                continue; // 过滤器异常不应阻断业务
            }

            if (result.isBlocked()) {
                // 记录安全事件
                eventRecorder.record(result, context);
                // 抛出阻断异常
                throw new SecurityBlockedException(
                        result.getEventType(),
                        result.getReason(),
                        result.getMatchedPattern()
                );
            }

            // 即使通过，若携带事件信息也记录（LOG/WARN 动作）
            if (result.getEventType() != null && result.getMatchedPattern() != null) {
                eventRecorder.record(result, context);
            }
        }

        log.debug("[SecurityFence] 输入过滤通过: conversationId={}, length={}",
                context.getConversationId(), content.length());
    }

    /**
     * 输出脱敏 — LLM 响应文本在返回用户前调用.
     *
     * @param content        LLM 原始响应
     * @param conversationId 会话 ID
     * @param messageId      消息 ID
     * @return 脱敏后的文本（如无 PII 则原样返回）
     */
    public String desensitizeOutput(String content, String conversationId, String messageId) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        // 检测 PII
        java.util.List<String> piiTypes = piiDesensitizer.detectPiiTypes(content);
        if (piiTypes.isEmpty()) {
            return content;
        }

        // 脱敏处理
        String desensitized = piiDesensitizer.desensitize(content);

        // 记录事件
        Long tenantId = TenantContext.getCurrentTenantId();
        eventRecorder.recordDesensitize(tenantId, conversationId, messageId,
                content, desensitized, piiTypes);

        log.info("[SecurityFence] PII 脱敏完成: piiTypes={}, conversationId={}, originalLength={}, desensitizedLength={}",
                piiTypes, conversationId, content.length(), desensitized.length());

        return desensitized;
    }

    // ==================== 敏感词管理 ====================

    @Transactional
    public SensitiveWordResponse createSensitiveWord(CreateSensitiveWordRequest request) {
        SensitiveWord word = SensitiveWord.builder()
                .tenantId(TenantContext.getCurrentTenantId())
                .word(request.getWord())
                .matchType(MatchType.fromCode(request.getMatchType()))
                .category(SensitiveCategory.fromCode(request.getCategory()))
                .severity(SeverityLevel.fromCode(request.getSeverity()))
                .action(ActionType.fromCode(request.getAction()))
                .status(SensitiveWordStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        sensitiveWordRepository.save(word);

        log.info("[SecurityFence] 敏感词规则已创建: word={}, category={}, severity={}",
                request.getWord(), request.getCategory(), request.getSeverity());

        return SensitiveWordResponse.from(word);
    }

    @Transactional
    public SensitiveWordResponse updateSensitiveWord(Long id, UpdateSensitiveWordRequest request) {
        SensitiveWord word = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new com.example.agent.common.exception.ResourceNotFoundException("敏感词规则不存在: " + id));

        SensitiveWord updated = word.toBuilder()
                .word(request.getWord())
                .matchType(MatchType.fromCode(request.getMatchType()))
                .category(SensitiveCategory.fromCode(request.getCategory()))
                .severity(SeverityLevel.fromCode(request.getSeverity()))
                .action(ActionType.fromCode(request.getAction()))
                .updatedAt(LocalDateTime.now())
                .build();
        sensitiveWordRepository.update(updated);

        return SensitiveWordResponse.from(updated);
    }

    @Transactional
    public void toggleSensitiveWordStatus(Long id) {
        SensitiveWord word = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new com.example.agent.common.exception.ResourceNotFoundException("敏感词规则不存在: " + id));

        if (word.isActive()) {
            word.disable();
        } else {
            word.enable();
        }
        sensitiveWordRepository.updateStatus(id, word.getStatus());
    }

    @Transactional
    public void deleteSensitiveWord(Long id) {
        sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new com.example.agent.common.exception.ResourceNotFoundException("敏感词规则不存在: " + id));
        sensitiveWordRepository.deleteById(id);
    }

    public SensitiveWordResponse getSensitiveWord(Long id) {
        SensitiveWord word = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new com.example.agent.common.exception.ResourceNotFoundException("敏感词规则不存在: " + id));
        return SensitiveWordResponse.from(word);
    }

    public List<SensitiveWordResponse> listSensitiveWords(int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return sensitiveWordRepository.findByTenant(tenantId, page, size).stream()
                .map(SensitiveWordResponse::from)
                .toList();
    }

    public long countSensitiveWords() {
        return sensitiveWordRepository.countByTenant(TenantContext.getCurrentTenantId());
    }

    // ==================== 安全事件查询 ====================

    public List<SecurityEventResponse> listSecurityEvents(int page, int size) {
        Long tenantId = TenantContext.getCurrentTenantId();
        return securityEventRepository.findByTenant(tenantId, page, size).stream()
                .map(SecurityEventResponse::from)
                .toList();
    }

    public long countSecurityEvents() {
        return securityEventRepository.countByTenant(TenantContext.getCurrentTenantId());
    }

    public List<SecurityEventResponse> listSecurityEventsByConversation(String conversationId, int page, int size) {
        return securityEventRepository.findByConversation(conversationId, page, size).stream()
                .map(SecurityEventResponse::from)
                .toList();
    }
}
