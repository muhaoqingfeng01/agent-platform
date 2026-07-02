package com.example.agent.interfaces.rest;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.agent.application.knowledge.KnowledgeBaseApplicationService;
import com.example.agent.application.knowledge.PrecisionConfigApplicationService;
import com.example.agent.application.knowledge.dto.KnowledgeBaseDTO;
import com.example.agent.application.knowledge.dto.KnowledgeBaseStatsResponse;
import com.example.agent.application.knowledge.dto.PrecisionConfigDTO;
import com.example.agent.common.dto.PageResponse;
import com.example.agent.common.helper.ResultRespHelper;
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
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.create", request, (req) ->
                kbService.create(req.getName(), req.getDescription(), req.getEmbeddingModel()));
    }

    @PostMapping("/list")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库列表")
    public Result<PageResponse<KnowledgeBaseDTO>> list(@RequestBody KnowledgeBaseListRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.list", request, (req) ->
                kbService.list(req.getPage(), req.getSize()));
    }

    @PostMapping("/get")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库详情")
    public Result<KnowledgeBaseDTO> getById(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.getById", request, (req) ->
                kbService.getByKnowledgeId(req.getKnowledgeId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("kb:update")
    @Operation(summary = "更新知识库名称/描述")
    public Result<Void> update(@Valid @RequestBody KnowledgeBaseUpdateRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.update", request, (req) -> {
            kbService.update(req.getKnowledgeId(), req.getName(), req.getDescription());
            return null;
        });
    }

    @PostMapping("/update-chunk-config")
    @SaCheckPermission("kb:update")
    @Operation(summary = "设置知识库默认切片策略")
    public Result<Void> updateChunkConfig(@Valid @RequestBody KnowledgeBaseUpdateChunkConfigRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.updateChunkConfig", request, (req) -> {
            kbService.updateChunkStrategy(req.getKnowledgeId(),
                    com.example.agent.domain.knowledge.valueobject.ChunkStrategy.fromCode(req.getDefaultChunkStrategy()),
                    req.getChunkConfigJson());
            return null;
        });
    }

    @PostMapping("/set-precision-config")
    @SaCheckPermission("kb:update")
    @Operation(summary = "设置知识库检索精度参数")
    public Result<Void> setPrecisionConfig(@Valid @RequestBody KnowledgeBaseSetPrecisionConfigRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.setPrecisionConfig", request, (req) -> {
            precisionService.setPrecisionConfig(req.getKnowledgeId(), toDTO(req));
            return null;
        });
    }

    @PostMapping("/precision-config/resolved")
    @SaCheckPermission("kb:read")
    @Operation(summary = "查询知识库当前生效的完整精度配置")
    public Result<PrecisionConfigDTO> getResolvedConfig(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.getResolvedConfig", request, (req) ->
                precisionService.getResolvedConfig(req.getKnowledgeId()));
    }

    @PostMapping("/enable")
    @SaCheckPermission("kb:update")
    @Operation(summary = "启用知识库")
    public Result<Void> enable(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.enable", request, (req) -> {
            kbService.enable(req.getKnowledgeId());
            return null;
        });
    }

    @PostMapping("/disable")
    @SaCheckPermission("kb:update")
    @Operation(summary = "弃用知识库")
    public Result<Void> disable(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.disable", request, (req) -> {
            kbService.disable(req.getKnowledgeId());
            return null;
        });
    }

    @PostMapping("/delete")
    @SaCheckPermission("kb:delete")
    @Operation(summary = "级联删除知识库（先弃用后删除）")
    public Result<Void> delete(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.delete", request, (req) -> {
            kbService.deleteWithCascade(req.getKnowledgeId());
            return null;
        });
    }

    @PostMapping("/stats")
    @SaCheckPermission("kb:read")
    @Operation(summary = "知识库文档统计")
    public Result<KnowledgeBaseStatsResponse> getStats(@Valid @RequestBody KnowledgeBaseGetRequest request) {
        return ResultRespHelper.responseInvoke("KnowledgeBaseController.getStats", request, (req) ->
                kbService.getStats(req.getKnowledgeId()));
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
