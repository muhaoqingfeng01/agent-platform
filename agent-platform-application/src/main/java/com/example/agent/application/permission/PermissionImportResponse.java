package com.example.agent.application.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 权限批量导入响应 DTO.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限批量导入结果")
public class PermissionImportResponse {

    @Schema(description = "已导入的权限列表")
    private List<PermissionResponse> records;
}
