package com.example.agent.application.tenant;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.interaction.valueobject.InteractionMode;
import com.example.agent.domain.tenant.Tenant;
import com.example.agent.domain.tenant.TenantModeQuota;
import com.example.agent.domain.tenant.TenantModeQuotaRepository;
import com.example.agent.domain.tenant.TenantRepository;
import com.example.agent.domain.tenant.service.TenantQuotaDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantModeQuotaApplicationServiceTest {

    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private TenantModeQuotaRepository quotaRepository;
    @Mock
    private TenantQuotaDomainService quotaDomainService;

    private TenantModeQuotaApplicationService service;

    @BeforeEach
    void setUp() {
        service = new TenantModeQuotaApplicationService(
                tenantRepository, quotaRepository, quotaDomainService);
    }

    @Test
    void list_unknownTenant_404() {
        when(tenantRepository.findByTenantId(9L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> service.list(9L));
        assertEquals(404, ex.getCode());
    }

    @Test
    void upsert_insertsWhenMissing() {
        when(tenantRepository.findByTenantId(1L)).thenReturn(Optional.of(Tenant.builder().tenantId(1L).build()));
        when(quotaRepository.findByTenantIdAndModeCodeIncludingDeleted(1L, "KNOWLEDGE_SEARCH"))
                .thenReturn(Optional.empty());
        when(quotaDomainService.loadOrDefault(1L, "KNOWLEDGE_SEARCH"))
                .thenReturn(TenantModeQuota.defaultEnabled(1L, "KNOWLEDGE_SEARCH"));
        when(quotaDomainService.currentLlmUsed(1L, "KNOWLEDGE_SEARCH")).thenReturn(0L);

        TenantModeQuotaUpsertCommand cmd = new TenantModeQuotaUpsertCommand();
        cmd.setTenantId(1L);
        cmd.setModeCode("KNOWLEDGE_SEARCH");
        cmd.setEnabled(false);
        cmd.setDailyLlmLimit(20);

        TenantModeQuotaItemResponse item = service.upsert(cmd);
        assertEquals("KNOWLEDGE_SEARCH", item.getModeCode());
        ArgumentCaptor<TenantModeQuota> captor = ArgumentCaptor.forClass(TenantModeQuota.class);
        verify(quotaRepository).save(captor.capture());
        verify(quotaRepository, never()).update(any());
        assertFalse(captor.getValue().isEnabled());
        assertEquals(20, captor.getValue().getDailyLlmLimit());
    }

    @Test
    void upsert_invalidMode_400() {
        when(tenantRepository.findByTenantId(1L)).thenReturn(Optional.of(Tenant.builder().tenantId(1L).build()));
        TenantModeQuotaUpsertCommand cmd = new TenantModeQuotaUpsertCommand();
        cmd.setTenantId(1L);
        cmd.setModeCode("NOT_A_MODE");
        cmd.setEnabled(true);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.upsert(cmd));
        assertEquals(400, ex.getCode());
    }

    @Test
    void upsert_restoresDeletedRow() {
        when(tenantRepository.findByTenantId(1L)).thenReturn(Optional.of(Tenant.builder().tenantId(1L).build()));
        TenantModeQuota deleted = TenantModeQuota.builder()
                .id(8L).tenantId(1L).modeCode("ANALYSIS").enabled(false).deleted(true).build();
        when(quotaRepository.findByTenantIdAndModeCodeIncludingDeleted(1L, "ANALYSIS"))
                .thenReturn(Optional.of(deleted));
        when(quotaDomainService.loadOrDefault(anyLong(), anyString()))
                .thenReturn(TenantModeQuota.defaultEnabled(1L, "ANALYSIS"));
        when(quotaDomainService.currentLlmUsed(anyLong(), anyString())).thenReturn(0L);

        TenantModeQuotaUpsertCommand cmd = new TenantModeQuotaUpsertCommand();
        cmd.setTenantId(1L);
        cmd.setModeCode("analysis");
        cmd.setEnabled(true);

        service.upsert(cmd);
        ArgumentCaptor<TenantModeQuota> captor = ArgumentCaptor.forClass(TenantModeQuota.class);
        verify(quotaRepository).update(captor.capture());
        assertTrue(captor.getValue().isEnabled());
        assertFalse(Boolean.TRUE.equals(captor.getValue().getDeleted()));
        assertEquals(InteractionMode.ANALYSIS.getCode(), captor.getValue().getModeCode());
    }
}
