package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.KnowledgeBaseApplicationService;
import com.example.agent.application.knowledge.PrecisionConfigApplicationService;
import com.example.agent.application.knowledge.dto.KnowledgeBaseDTO;
import com.example.agent.application.knowledge.dto.PrecisionConfigDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.result.Result;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeBaseCreateRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeBaseListRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeBaseGetRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeBaseUpdateRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeBaseUpdateChunkConfigRequest;
import com.example.agent.interfaces.dto.request.knowledge.KnowledgeBaseSetPrecisionConfigRequest;
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

    @PostMapping("/create")
    @SaCheckPermission("kb:create")
    @Operation(summary = "创建知识库")
    public Result<KnowledgeBaseDTO> create(@Valid @RequestBody KnowledgeBaseCreateRequest request) {
        return Result.ok(kbService.create(request.getName(), request.getDescription(), request.getEmbeddingModel()));
    }

    @PostMapping("/list")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库列表")
    public Result<PageResponse<KnowledgeBaseDTO>> list(@RequestBody KnowledgeBaseListRequest request) {
        return Result.ok(kbService.list(request.getPage(), request.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库详情")
    public Result<KnowledgeBaseDTO> getById(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return Result.ok(kbService.getByKnowledgeId(request.getKnowledgeId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("kb:update")
    @Operation(summary = "更新知识库名称/描述")
    public Result<Void> update(@Valid @RequestBody KnowledgeBaseUpdateRequest request) {
        kbService.update(request.getKnowledgeId(), request.getName(), request.getDescription());
        return Result.ok();
    }

    @PostMapping("/update-chunk-config")
    @SaCheckPermission("kb:update")
    @Operation(summary = "设置知识库默认切片策略")
    public Result<Void> updateChunkConfig(@Valid @RequestBody KnowledgeBaseUpdateChunkConfigRequest request) {
        kbService.updateChunkStrategy(request.getKnowledgeId(),
                com.example.agent.domain.knowledge.valueobject.ChunkStrategy.fromCode(request.getDefaultChunkStrategy()),
                request.getChunkConfigJson());
        return Result.ok();
    }

    @PostMapping("/set-precision-config")
    @SaCheckPermission("kb:update")
    @Operation(summary = "设置知识库检索精度参数")
    public Result<Void> setPrecisionConfig(@Valid @RequestBody KnowledgeBaseSetPrecisionConfigRequest request) {
        precisionService.setPrecisionConfig(request.getKnowledgeId(), toDTO(request));
        return Result.ok();
    }

    @PostMapping("/precision-config/resolved")
    @SaCheckPermission("kb:read")
    @Operation(summary = "查询知识库当前生效的完整精度配置")
    public Result<PrecisionConfigDTO> getResolvedConfig(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return Result.ok(precisionService.getResolvedConfig(request.getKnowledgeId()));
    }

    @PostMapping("/enable")
    @SaCheckPermission("kb:update")
    @Operation(summary = "启用知识库")
    public Result<Void> enable(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        kbService.enable(request.getKnowledgeId());
        return Result.ok();
    }

    @PostMapping("/disable")
    @SaCheckPermission("kb:update")
    @Operation(summary = "弃用知识库")
    public Result<Void> disable(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        kbService.disable(request.getKnowledgeId());
        return Result.ok();
    }

    @PostMapping("/delete")
    @SaCheckPermission("kb:delete")
    @Operation(summary = "级联删除知识库（先弃用后删除）")
    public Result<Void> delete(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        kbService.deleteWithCascade(request.getKnowledgeId());
        return Result.ok();
    }

    @PostMapping("/stats")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库文档统计")
    public Result<Map<String, Long>> getStats(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return Result.ok(kbService.getStats(request.getKnowledgeId()));
    }

    private PrecisionConfigDTO toDTO(KnowledgeBaseSetPrecisionConfigRequest req) {
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
