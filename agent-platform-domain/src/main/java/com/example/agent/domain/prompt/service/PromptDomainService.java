package com.example.agent.domain.prompt.service;

import com.example.agent.domain.prompt.entity.PromptTemplate;
import com.example.agent.domain.prompt.entity.PromptTemplateVersion;
import com.example.agent.domain.prompt.repository.PromptTemplateRepository;
import com.example.agent.domain.prompt.repository.PromptTemplateVersionRepository;
import com.example.agent.domain.prompt.valueobject.PromptStatus;
import com.example.agent.domain.prompt.valueobject.VariableDef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词领域服务 — 封装跨实体业务规则与不变量.
 * <p>
 * 负责:
 * <ul>
 *   <li>模板创建校验（名称唯一性、变量合法性）</li>
 *   <li>发布流程协调（版本快照 + 状态流转）</li>
 *   <li>回滚流程协调（Memento 模式恢复历史状态）</li>
 *   <li>模板文本变量提取与校验</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromptDomainService {

    private final PromptTemplateRepository templateRepository;
    private final PromptTemplateVersionRepository versionRepository;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    // ==================== 新建校验 ====================

    /**
     * 校验新建模板的业务不变量.
     */
    public void validateNewTemplate(PromptTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("模板不能为空");
        }
        if (template.getName() == null || template.getName().isBlank()) {
            throw new IllegalArgumentException("模板名称不能为空");
        }
        if (template.getTemplateText() == null || template.getTemplateText().isBlank()) {
            throw new IllegalArgumentException("模板文本不能为空");
        }
        // 检查同一租户下名称唯一性
        templateRepository.findByTenantIdAndName(template.getTenantId(), template.getName())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "模板名称已存在: " + template.getName());
                });
    }

    /**
     * 提取模板文本中的变量名列表并校验变量定义一致性.
     */
    public List<String> extractVariableNames(String templateText) {
        return VARIABLE_PATTERN.matcher(templateText).results()
                .map(m -> m.group(1))
                .distinct()
                .toList();
    }

    /**
     * 校验变量定义与模板文本中的变量是否一致.
     */
    public void validateVariables(String templateText, List<VariableDef> variableDefs) {
        List<String> textVars = extractVariableNames(templateText);
        List<String> defNames = variableDefs != null
                ? variableDefs.stream().map(VariableDef::getName).toList()
                : List.of();

        // 模板中引用但未定义的变量 → 警告
        List<String> undefined = textVars.stream()
                .filter(v -> !defNames.contains(v))
                .toList();
        if (!undefined.isEmpty()) {
            log.warn("模板中引用了未定义的变量: {}", undefined);
        }

        // 定义了但模板中未使用的变量 → 警告
        List<String> unused = defNames.stream()
                .filter(v -> !textVars.contains(v))
                .toList();
        if (!unused.isEmpty()) {
            log.warn("定义了但模板中未使用的变量: {}", unused);
        }
    }

    // ==================== 发布流程 ====================

    /**
     * 发布模板 — 创建版本快照 + 状态流转.
     * <p>
     * 流程: 校验状态 → 版本号+1 → 保存历史快照 → 更新主表.
     *
     * @param template 当前模板（必须是 DRAFT）
     * @param publisher 发布人
     * @return 更新后的模板
     */
    public PromptTemplate publish(PromptTemplate template, String publisher) {
        if (!template.isPublishable()) {
            throw new IllegalStateException(
                    "只有草稿状态可发布，当前状态: " + template.getStatus().getLabel());
        }

        int newVersion = template.getCurrentOrZero() + 1;

        // 1. 创建版本快照 (Memento)
        PromptTemplateVersion snapshot = PromptTemplateVersion.builder()
                .promptId(template.getPromptId())
                .version(newVersion)
                .templateText(template.getTemplateText())
                .variables(template.getVariables())
                .changeLog("发布 v" + newVersion)
                .publisher(publisher)
                .publishedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        versionRepository.save(snapshot);

        // 2. 更新主表状态
        template.setVersion(newVersion);
        template.setStatus(PromptStatus.PUBLISHED);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.publish(template.getPromptId(), newVersion,
                template.getTemplateText(), toVariablesJson(template.getVariables()));

        log.info("[Prompt] 发布成功: promptId={}, version={}, publisher={}",
                template.getPromptId(), newVersion, publisher);
        return template;
    }

    // ==================== 回滚流程 ====================

    /**
     * 回滚到指定历史版本 — Memento 模式恢复.
     * <p>
     * 流程: 读取目标版本快照 → 版本号+1 → 创建新快照（标记为回滚） → 更新主表.
     * 回滚本质上是"用历史版本内容创建新版本"，保留完整审计链。
     *
     * @param template 当前模板
     * @param targetVersion 目标版本号
     * @param operator 操作人
     * @return 更新后的模板
     */
    public PromptTemplate rollback(PromptTemplate template, int targetVersion, String operator) {
        // 1. 读取目标版本快照
        PromptTemplateVersion target = versionRepository
                .findByPromptIdAndVersion(template.getPromptId(), targetVersion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "版本不存在: " + template.getPromptId() + " v" + targetVersion));

        int newVersion = template.getCurrentOrZero() + 1;

        // 2. 创建回滚快照
        PromptTemplateVersion rollbackSnapshot = PromptTemplateVersion.builder()
                .promptId(template.getPromptId())
                .version(newVersion)
                .templateText(target.getTemplateText())
                .variables(target.getVariables())
                .changeLog("回滚至 v" + targetVersion)
                .publisher(operator)
                .publishedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        versionRepository.save(rollbackSnapshot);

        // 3. 更新主表
        template.setTemplateText(target.getTemplateText());
        template.setVariables(target.getVariables());
        template.setVersion(newVersion);
        template.setStatus(PromptStatus.PUBLISHED);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.publish(template.getPromptId(), newVersion,
                template.getTemplateText(), toVariablesJson(template.getVariables()));

        log.info("[Prompt] 回滚成功: promptId={}, {} → v{} (based on v{}), operator={}",
                template.getPromptId(), template.getVersion(), newVersion, targetVersion, operator);
        return template;
    }

    // ==================== 状态流转校验 ====================

    /**
     * 归档模板 — ACTIVE/PUBLISHED → ARCHIVED（不可逆）.
     */
    public void archive(PromptTemplate template) {
        if (template.getStatus() == PromptStatus.ARCHIVED) {
            throw new IllegalStateException("已归档的模板不可再次归档");
        }
        template.setStatus(PromptStatus.ARCHIVED);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.updateStatus(template.getPromptId(), PromptStatus.ARCHIVED);
        log.info("[Prompt] 归档: promptId={}", template.getPromptId());
    }

    /**
     * 校验租户隔离.
     */
    public void assertTenantAccess(PromptTemplate template, String currentTenantId) {
        if (!template.getTenantId().equals(currentTenantId)) {
            throw new SecurityException("无权访问其他租户的提示词模板");
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 渲染后校验：检查是否有未填充的必填变量.
     */
    public void validateUnfilledVariables(String rendered) {
        Matcher m = VARIABLE_PATTERN.matcher(rendered);
        if (m.find()) {
            throw new IllegalStateException("存在未填充的变量: " + m.group(1));
        }
    }

    private String toVariablesJson(List<VariableDef> variables) {
        if (variables == null || variables.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < variables.size(); i++) {
            VariableDef v = variables.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format(
                    "{\"name\":\"%s\",\"type\":\"%s\",\"description\":\"%s\",\"defaultValue\":%s,\"required\":%s}",
                    escape(v.getName()),
                    escape(v.getType()),
                    escape(v.getDescription()),
                    v.getDefaultValue() != null ? "\"" + escape(v.getDefaultValue()) + "\"" : "null",
                    v.isRequired()));
        }
        sb.append("]");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
