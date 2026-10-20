package com.example.agent.domain.tenant.service;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tenant.QuotaCounterPort;
import com.example.agent.domain.tenant.TenantModeQuota;
import com.example.agent.domain.tenant.TenantModeQuotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 租户配额守卫测试 — 模式关闭 403、超限 429、无配置默认开通.
 */
@ExtendWith(MockitoExtension.class)
class TenantQuotaDomainServiceTest {

    @Mock
    private TenantModeQuotaRepository quotaRepository;
    @Mock
    private QuotaCounterPort quotaCounterPort;

    private TenantQuotaDomainService service;

    @BeforeEach
    void setUp() {
        service = new TenantQuotaDomainService(quotaRepository, quotaCounterPort);
    }

    @Test
    void assertModeEnabled_missingRow_allows() {
        when(quotaRepository.findByTenantIdAndModeCode(1L, "KNOWLEDGE_SEARCH"))
                .thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.assertModeEnabled(1L, "KNOWLEDGE_SEARCH"));
        assertTrue(service.isModeEnabled(1L, "KNOWLEDGE_SEARCH"));
    }

    @Test
    void assertModeEnabled_disabled_throws403() {
        when(quotaRepository.findByTenantIdAndModeCode(1L, "KNOWLEDGE_SEARCH"))
                .thenReturn(Optional.of(quota(false, 100, null)));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assertModeEnabled(1L, "KNOWLEDGE_SEARCH"));
        assertEquals(403, ex.getCode());
        assertTrue(ex.getMessage().contains("KNOWLEDGE_SEARCH"));
        assertFalse(service.isModeEnabled(1L, "KNOWLEDGE_SEARCH"));
    }

    @Test
    void assertLlmQuota_nullLimit_skipsCounter() {
        when(quotaRepository.findByTenantIdAndModeCode(1L, "CONVERSATION"))
                .thenReturn(Optional.of(quota(true, null, null)));
        assertDoesNotThrow(() -> service.assertLlmQuota(1L, "CONVERSATION"));
        verify(quotaCounterPort, never()).current(anyString());
    }

    @Test
    void assertLlmQuota_reachedLimit_throws429() {
        when(quotaRepository.findByTenantIdAndModeCode(1L, "CONVERSATION"))
                .thenReturn(Optional.of(quota(true, 10, null)));
        when(quotaCounterPort.current(anyString())).thenReturn(10L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assertLlmQuota(1L, "CONVERSATION"));
        assertEquals(429, ex.getCode());
    }

    @Test
    void assertLlmQuota_zeroLimit_throws429() {
        when(quotaRepository.findByTenantIdAndModeCode(1L, "ANALYSIS"))
                .thenReturn(Optional.of(quota(true, 0, null)));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assertLlmQuota(1L, "ANALYSIS"));
        assertEquals(429, ex.getCode());
    }

    @Test
    void assertUploadQuota_usesMinLimit() {
        when(quotaRepository.findByTenantId(1L)).thenReturn(List.of(
                quota(true, null, 1000L),
                quota(true, null, 200L)));
        when(quotaCounterPort.current(anyString())).thenReturn(150L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assertUploadQuota(1L, 60));
        assertEquals(429, ex.getCode());
    }

    @Test
    void assertUploadQuota_noLimit_allows() {
        when(quotaRepository.findByTenantId(1L)).thenReturn(List.of());
        assertDoesNotThrow(() -> service.assertUploadQuota(1L, 999_999));
        verify(quotaCounterPort, never()).current(anyString());
    }

    private TenantModeQuota quota(boolean enabled, Integer llm, Long upload) {
        return TenantModeQuota.builder()
                .tenantId(1L)
                .modeCode("KNOWLEDGE_SEARCH")
                .enabled(enabled)
                .dailyLlmLimit(llm)
                .dailyUploadBytes(upload)
                .deleted(false)
                .build();
    }
}
