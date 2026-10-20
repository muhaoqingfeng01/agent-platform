package com.example.agent.application.tenant;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.domain.tenant.TenantModeQuota;
import com.example.agent.domain.tenant.TenantModeQuotaRepository;
import com.example.agent.domain.tenant.TenantRepository;
import com.example.agent.domain.tenant.service.TenantQuotaDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * 租户模式配额应用服务 — 官方目录 CRUD（一期无第三方模式）.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantModeQuotaApplicationService {

    public static final String TIMEZONE = "UTC";
    public static final String COUNTING_POLICY = "SUCCESS_THEN_INCR";

    private final TenantRepository tenantRepository;
    private final TenantModeQuotaRepository quotaRepository;
    private final TenantQuotaDomainService quotaDomainService;

    public TenantModeQuotaListResponse list(Long tenantId) {
        ensureTenantExists(tenantId);
        var items = Arrays.stream(InteractionMode.values())
                .map(mode -> toItem(tenantId, mode))
                .toList();
        return TenantModeQuotaListResponse.builder()
                .tenantId(tenantId)
                .timezone(TIMEZONE)
                .countingPolicy(COUNTING_POLICY)
                .uploadLimit(quotaDomainService.resolveUploadLimit(tenantId))
                .uploadUsedToday(quotaDomainService.currentUploadUsed(tenantId))
                .items(items)
                .build();
    }

    @Transactional
    public TenantModeQuotaItemResponse upsert(TenantModeQuotaUpsertCommand command) {
        Long tenantId = command.getTenantId();
        ensureTenantExists(tenantId);
        InteractionMode mode = parseMode(command.getModeCode());
        boolean enabled = command.getEnabled() == null || command.getEnabled();
        LocalDateTime now = LocalDateTime.now();

        Optional<TenantModeQuota> existing =
                quotaRepository.findByTenantIdAndModeCodeIncludingDeleted(tenantId, mode.getCode());

        TenantModeQuota saved;
        if (existing.isPresent()) {
            TenantModeQuota old = existing.get();
            saved = TenantModeQuota.builder()
                    .id(old.getId())
                    .tenantId(tenantId)
                    .modeCode(mode.getCode())
                    .enabled(enabled)
                    .dailyLlmLimit(command.getDailyLlmLimit())
                    .dailyUploadBytes(command.getDailyUploadBytes())
                    .createdAt(old.getCreatedAt() != null ? old.getCreatedAt() : now)
                    .updatedAt(now)
                    .deleted(false)
                    .build();
            quotaRepository.update(saved);
            log.info("[Quota] 更新模式配额: tenant={}, mode={}, enabled={}, llmLimit={}, upload={}",
                    tenantId, mode.getCode(), enabled, command.getDailyLlmLimit(), command.getDailyUploadBytes());
        } else {
            saved = TenantModeQuota.builder()
                    .tenantId(tenantId)
                    .modeCode(mode.getCode())
                    .enabled(enabled)
                    .dailyLlmLimit(command.getDailyLlmLimit())
                    .dailyUploadBytes(command.getDailyUploadBytes())
                    .createdAt(now)
                    .updatedAt(now)
                    .deleted(false)
                    .build();
            quotaRepository.save(saved);
            log.info("[Quota] 新建模式配额: tenant={}, mode={}, enabled={}",
                    tenantId, mode.getCode(), enabled);
        }
        return toItem(tenantId, mode);
    }

    private TenantModeQuotaItemResponse toItem(Long tenantId, InteractionMode mode) {
        TenantModeQuota quota = quotaDomainService.loadOrDefault(tenantId, mode.getCode());
        return TenantModeQuotaItemResponse.builder()
                .modeCode(mode.getCode())
                .modeDesc(mode.getDesc())
                .enabled(quota.isEnabled())
                .dailyLlmLimit(quota.getDailyLlmLimit())
                .dailyUploadBytes(quota.getDailyUploadBytes())
                .llmUsedToday(quotaDomainService.currentLlmUsed(tenantId, mode.getCode()))
                .build();
    }

    private void ensureTenantExists(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(400, "租户标识不能为空");
        }
        tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new BusinessException(404, "租户不存在: " + tenantId));
    }

    private InteractionMode parseMode(String modeCode) {
        try {
            return InteractionMode.fromCode(modeCode);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, e.getMessage());
        }
    }
}
