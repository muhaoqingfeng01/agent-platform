package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.KnowledgeBaseApplicationService;
import com.example.agent.application.knowledge.PrecisionConfigApplicationService;
import com.example.agent.application.knowledge.dto.KnowledgeBaseDTO;
import com.example.agent.application.knowledge.dto.PrecisionConfigDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.knowledge.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 知识库管理 Controller.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge-bases")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "知识库 CRUD + 切片/检索配置")
public class KnowledgeBaseController {

    private final KnowledgeBaseApplicationService kbService;
    private final PrecisionConfigApplicationService precisionService;

    @PostMapping
    @SaCheckPermission("kb:create")
    @Operation(summary = "创建知识库")
    public Result<KnowledgeBaseDTO> create(@Valid @RequestBody CreateKnowledgeBaseRequest request) {
        return Result.ok(kbService.create(request.getName(), request.getDescription(), request.getEmbeddingModel()));
    }

    @GetMapping
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库列表")
    public Result<PageResponse<KnowledgeBaseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(kbService.list(page, size));
    }

    @GetMapping("/{knowledgeId}")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库详情")
    public Result<KnowledgeBaseDTO> getById(@PathVariable String knowledgeId) {
        return Result.ok(kbService.getByKnowledgeId(knowledgeId));
    }

    @PutMapping("/{knowledgeId}")
    @SaCheckPermission("kb:update")
    @Operation(summary = "更新知识库名称/描述")
    public Result<Void> update(@PathVariable String knowledgeId,
                               @Valid @RequestBody UpdateKnowledgeBaseRequest request) {
        kbService.update(knowledgeId, request.getName(), request.getDescription());
        return Result.ok();
    }

    @PutMapping("/{knowledgeId}/chunk-config")
    @SaCheckPermission("kb:update")
    @Operation(summary = "设置知识库默认切片策略")
    public Result<Void> updateChunkConfig(@PathVariable String knowledgeId,
                                          @Valid @RequestBody UpdateChunkConfigRequest request) {
        kbService.updateChunkStrategy(knowledgeId,
                com.example.agent.domain.knowledge.valueobject.ChunkStrategy.fromCode(request.getDefaultChunkStrategy()),
                request.getChunkConfigJson());
        return Result.ok();
    }

    @PutMapping("/{knowledgeId}/precision-config")
    @SaCheckPermission("kb:update")
    @Operation(summary = "设置知识库检索精度参数")
    public Result<Void> setPrecisionConfig(@PathVariable String knowledgeId,
                                           @Valid @RequestBody SetPrecisionConfigRequest request) {
        precisionService.setPrecisionConfig(knowledgeId, toDTO(request));
        return Result.ok();
    }

    @GetMapping("/{knowledgeId}/precision-config/resolved")
    @SaCheckPermission("kb:read")
    @Operation(summary = "查询知识库当前生效的完整精度配置")
    public Result<PrecisionConfigDTO> getResolvedConfig(@PathVariable String knowledgeId) {
        return Result.ok(precisionService.getResolvedConfig(knowledgeId));
    }

    @PutMapping("/{knowledgeId}/enable")
    @SaCheckPermission("kb:update")
    @Operation(summary = "启用知识库")
    public Result<Void> enable(@PathVariable String knowledgeId) {
        kbService.enable(knowledgeId);
        return Result.ok();
    }

    @PutMapping("/{knowledgeId}/disable")
    @SaCheckPermission("kb:update")
    @Operation(summary = "弃用知识库")
    public Result<Void> disable(@PathVariable String knowledgeId) {
        kbService.disable(knowledgeId);
        return Result.ok();
    }

    @DeleteMapping("/{knowledgeId}")
    @SaCheckPermission("kb:delete")
    @Operation(summary = "级联删除知识库（先弃用后删除）")
    public Result<Void> delete(@PathVariable String knowledgeId) {
        kbService.deleteWithCascade(knowledgeId);
        return Result.ok();
    }

    @GetMapping("/{knowledgeId}/stats")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库文档统计")
    public Result<Map<String, Long>> getStats(@PathVariable String knowledgeId) {
        return Result.ok(kbService.getStats(knowledgeId));
    }

    private PrecisionConfigDTO toDTO(SetPrecisionConfigRequest req) {
        return PrecisionConfigDTO.builder()
                .indexType(req.getIndexType())
                .indexParams(req.getIndexParams())
                .searchStrategy(req.getSearchStrategy())
                .searchParams(req.getSearchParams())
                .multiStageParams(req.getMultiStageParams())
                .monitoringParams(req.getMonitoringParams())
                .build();
    }
}
