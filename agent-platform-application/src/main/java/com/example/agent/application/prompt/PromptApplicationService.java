package com.example.agent.application.prompt;

import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.ResourceNotFoundException;
import com.example.agent.common.util.IdGenerator;
import com.example.agent.domain.prompt.entity.PromptTemplate;
import com.example.agent.domain.prompt.entity.PromptTemplateVersion;
import com.example.agent.domain.prompt.repository.PromptTemplateRepository;
import com.example.agent.domain.prompt.repository.PromptTemplateVersionRepository;
import com.example.agent.domain.prompt.service.PromptDomainService;
import com.example.agent.domain.prompt.valueobject.PromptStatus;
import com.example.agent.domain.prompt.valueobject.VariableDef;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.infrastructure.persistence.cache.CachedPromptRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 提示词管理应用服务 — Facade 模式.
 * <p>
 * 作为提示词管理的统一入口，协调:
 * <ul>
 *   <li>{@link PromptDomainService} — 领域规则与不变量</li>
 *   <li>{@link PromptRenderService} — 模板渲染（Strategy + Chain of Responsibility）</li>
 *   <li>{@link PromptTemplateRepository} — 持久化</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptApplicationService {

    private final PromptTemplateRepository templateRepository;
    private final PromptTemplateVersionRepository versionRepository;
    private final PromptDomainService domainService;
    private final PromptRenderService renderService;
    private final CachedPromptRepository cachedPromptRepository;

    // ==================== CRUD ====================

    /**
     * 创建提示词模板（草稿状态）— Factory Method 模式.
     */
    @Transactional
    public PromptResponse createPrompt(CreatePromptRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        String promptId = IdGenerator.generate("prompt");

        List<VariableDef> variables = request.getVariables() != null
                ? request.getVariables() : new ArrayList<>();

        PromptTemplate template = PromptTemplate.builder()
                .tenantId(tenantId)
                .promptId(promptId)
                .name(request.getName())
                .description(request.getDescription())
                .templateText(request.getTemplateText())
                .variables(variables)
                .version(0)  // 尚未发布
                .status(PromptStatus.DRAFT)
                .abTestConfig(request.getAbTestConfig())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        // 领域校验
        domainService.validateNewTemplate(template);
        domainService.validateVariables(template.getTemplateText(), template.getVariables());

        templateRepository.save(template);
        log.info("[Prompt] 创建草稿: promptId={}, name={}, tenantId={}", promptId, request.getName(), tenantId);

        return PromptResponse.from(template);
    }

    /**
     * 编辑草稿 — 仅 DRAFT 状态可编辑.
     */
    @Transactional
    public PromptResponse updatePrompt(String promptId, UpdatePromptRequest request) {
        PromptTemplate template = getTemplateWithAccess(promptId);

        if (template.getStatus() != PromptStatus.DRAFT) {
            throw new BusinessException(400, "仅草稿状态可编辑，当前状态: " + template.getStatus().getLabel());
        }

        String name = request.getName() != null ? request.getName() : template.getName();
        String desc = request.getDescription() != null ? request.getDescription() : template.getDescription();
        String text = request.getTemplateText() != null ? request.getTemplateText() : template.getTemplateText();
        List<VariableDef> vars = request.getVariables() != null ? request.getVariables() : template.getVariables();

        // 领域校验
        domainService.validateVariables(text, vars);

        templateRepository.updateContent(promptId, name, desc, text, toVariablesJson(vars));
        template.setName(name);
        template.setDescription(desc);
        template.setTemplateText(text);
        template.setVariables(vars);
        template.setUpdatedAt(LocalDateTime.now());

        log.info("[Prompt] 编辑草稿: promptId={}", promptId);
        return PromptResponse.from(template);
    }

    /**
     * 查询模板详情.
     */
    public PromptResponse getPrompt(String promptId) {
        PromptTemplate template = getTemplateWithAccess(promptId);
        return PromptResponse.from(template);
    }

    /**
     * 分页查询模板列表.
     */
    public PageResponse<PromptResponse> listPrompts(int page, int size, String statusFilter) {
        String tenantId = TenantContext.getCurrentTenantId();
        int offset = page * size;

        List<PromptTemplate> templates;
        long total;

        if (statusFilter != null && !statusFilter.isBlank()) {
            PromptStatus status = PromptStatus.valueOf(statusFilter.toUpperCase());
            templates = templateRepository.findByTenantIdAndStatus(tenantId, status, offset, size);
            // 简化：按状态过滤时的总数（生产环境可优化为专用 count 方法）
            total = templateRepository.countByTenantId(tenantId);
        } else {
            templates = templateRepository.findByTenantId(tenantId, offset, size);
            total = templateRepository.countByTenantId(tenantId);
        }

        List<PromptResponse> records = templates.stream()
                .map(PromptResponse::from)
                .toList();

        return PageResponse.of(records, total, page, size);
    }

    /**
     * 软删除模板.
     */
    @Transactional
    public void deletePrompt(String promptId) {
        PromptTemplate template = getTemplateWithAccess(promptId); // 校验存在 + 租户隔离
        templateRepository.softDelete(promptId);

        // 删除后清除缓存
        cachedPromptRepository.evictCache(template.getTenantId(), template.getName());

        log.info("[Prompt] 软删除: promptId={}", promptId);
    }

    // ==================== 发布与回滚 ====================

    /**
     * 发布模板 — Template Method 模式.
     * <p>
     * 委托给 {@link PromptDomainService#publish} 执行核心业务逻辑.
     */
    @Transactional
    public PromptResponse publishPrompt(String promptId) {
        PromptTemplate template = getTemplateWithAccess(promptId);
        String publisher = TenantContext.getCurrentUserId();

        domainService.publish(template, publisher);

        // 发布后更新 Redis 缓存
        cachedPromptRepository.updateLatestCache(template);

        log.info("[Prompt] 发布成功: promptId={}, version={}", promptId, template.getVersion());
        return PromptResponse.from(template);
    }

    /**
     * 回滚到指定版本 — Memento 模式.
     */
    @Transactional
    public PromptResponse rollbackPrompt(String promptId, int targetVersion) {
        PromptTemplate template = getTemplateWithAccess(promptId);
        String operator = TenantContext.getCurrentUserId();

        domainService.rollback(template, targetVersion, operator);

        // 回滚后更新 Redis 缓存
        cachedPromptRepository.updateLatestCache(template);

        log.info("[Prompt] 回滚成功: promptId={}, targetVersion={}, newVersion={}",
                promptId, targetVersion, template.getVersion());
        return PromptResponse.from(template);
    }

    /**
     * 归档模板.
     */
    @Transactional
    public void archivePrompt(String promptId) {
        PromptTemplate template = getTemplateWithAccess(promptId);
        domainService.archive(template);

        // 归档后清除缓存
        cachedPromptRepository.evictCache(template.getTenantId(), template.getName());
    }

    // ==================== 版本历史 ====================

    /**
     * 查看版本历史列表.
     */
    public List<VersionResponse> getVersionHistory(String promptId) {
        getTemplateWithAccess(promptId); // 校验存在 + 租户
        return versionRepository.findByPromptId(promptId).stream()
                .map(VersionResponse::from)
                .toList();
    }

    /**
     * 查看指定版本详情.
     */
    public VersionResponse getVersionDetail(String promptId, int version) {
        getTemplateWithAccess(promptId); // 校验存在 + 租户
        PromptTemplateVersion v = versionRepository.findByPromptIdAndVersion(promptId, version)
                .orElseThrow(() -> new ResourceNotFoundException("版本", promptId + " v" + version));
        return VersionResponse.from(v);
    }

    /**
     * 两个版本的差异对比（简单文本 diff）.
     */
    public DiffResponse diffVersions(String promptId, int v1, int v2) {
        getTemplateWithAccess(promptId); // 校验存在 + 租户

        PromptTemplateVersion ver1 = versionRepository.findByPromptIdAndVersion(promptId, v1)
                .orElseThrow(() -> new ResourceNotFoundException("版本", promptId + " v" + v1));
        PromptTemplateVersion ver2 = versionRepository.findByPromptIdAndVersion(promptId, v2)
                .orElseThrow(() -> new ResourceNotFoundException("版本", promptId + " v" + v2));

        return DiffResponse.builder()
                .promptId(promptId)
                .version1(v1)
                .version2(v2)
                .templateText1(ver1.getTemplateText())
                .templateText2(ver2.getTemplateText())
                .variables1(ver1.getVariables() != null ? ver1.getVariables() : Collections.emptyList())
                .variables2(ver2.getVariables() != null ? ver2.getVariables() : Collections.emptyList())
                .changeLog1(ver1.getChangeLog())
                .changeLog2(ver2.getChangeLog())
                .build();
    }

    // ==================== 预览渲染 ====================

    /**
     * 预览渲染 — 用指定变量值填充模板.
     */
    public String previewRender(String promptId, Map<String, Object> variables) {
        PromptTemplate template = getTemplateWithAccess(promptId);
        return renderService.render(template.getTemplateText(), variables);
    }

    /**
     * 运行时渲染（仅已发布模板）— 用于 Agent 编排引擎调用.
     *
     * @param promptId 模板业务 ID
     * @param context  运行时上下文
     * @return 渲染后的最终提示词
     */
    public String runtimeRender(String promptId, Map<String, Object> context) {
        PromptTemplate template = getTemplateWithAccess(promptId);

        if (!template.isPublished()) {
            throw new BusinessException(400, "仅已发布的模板可用于运行时渲染，当前状态: "
                    + template.getStatus().getLabel());
        }

        return renderService.renderWithValidation(
                template.getTemplateText(), context, template.getVariables());
    }

    // ==================== 私有方法 ====================

    private PromptTemplate getTemplateWithAccess(String promptId) {
        PromptTemplate template = templateRepository.findByPromptId(promptId)
                .orElseThrow(() -> new ResourceNotFoundException("提示词模板", promptId));
        domainService.assertTenantAccess(template, TenantContext.getCurrentTenantId());
        return template;
    }

    private String toVariablesJson(List<VariableDef> variables) {
        if (variables == null || variables.isEmpty()) return "[]";
        // 使用简单的 Jackson 序列化 (委托给 infrastructure 层的 ObjectMapper)
        // 此处直接使用 org.springframework 内置序列化
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            return om.writeValueAsString(variables);
        } catch (Exception e) {
            log.warn("[Prompt] 变量序列化失败", e);
            return "[]";
        }
    }

    // ==================== DTOs ====================

    @Data
    public static class CreatePromptRequest {
        private String name;
        private String description;
        private String templateText;
        private List<VariableDef> variables;
        private String abTestConfig;
    }

    @Data
    public static class UpdatePromptRequest {
        private String name;
        private String description;
        private String templateText;
        private List<VariableDef> variables;
    }

    @Data
    @Builder
    public static class PromptResponse {
        private Long id;
        private String promptId;
        private String tenantId;
        private String name;
        private String description;
        private String templateText;
        private List<VariableDef> variables;
        private Integer version;
        private String status;
        private String statusLabel;
        private String abTestConfig;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PromptResponse from(PromptTemplate template) {
            return PromptResponse.builder()
                    .id(template.getId())
                    .promptId(template.getPromptId())
                    .tenantId(template.getTenantId())
                    .name(template.getName())
                    .description(template.getDescription())
                    .templateText(template.getTemplateText())
                    .variables(template.getVariables())
                    .version(template.getVersion())
                    .status(template.getStatus().name())
                    .statusLabel(template.getStatus().getLabel())
                    .abTestConfig(template.getAbTestConfig())
                    .createdAt(template.getCreatedAt())
                    .updatedAt(template.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class VersionResponse {
        private Long id;
        private String promptId;
        private Integer version;
        private String templateText;
        private List<VariableDef> variables;
        private String changeLog;
        private String publisher;
        private LocalDateTime publishedAt;

        public static VersionResponse from(PromptTemplateVersion v) {
            return VersionResponse.builder()
                    .id(v.getId())
                    .promptId(v.getPromptId())
                    .version(v.getVersion())
                    .templateText(v.getTemplateText())
                    .variables(v.getVariables())
                    .changeLog(v.getChangeLog())
                    .publisher(v.getPublisher())
                    .publishedAt(v.getPublishedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class DiffResponse {
        private String promptId;
        private int version1;
        private int version2;
        private String templateText1;
        private String templateText2;
        private List<VariableDef> variables1;
        private List<VariableDef> variables2;
        private String changeLog1;
        private String changeLog2;
    }
}
