-- ============================================================
-- Agent Platform — 租户模式开关与日配额
-- Version: V1.8.0
-- ============================================================
-- 一期「模式市场」= 官方 InteractionMode 目录 + 租户开关，不做第三方 jar 热加载。
-- 日配额按 UTC 自然日切分（yyyyMMdd）；未配置行视为 enabled=1 且限额 NULL（不限制）。
-- ============================================================

CREATE TABLE IF NOT EXISTS t_tenant_mode_quota (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL COMMENT '租户唯一标识（对齐 t_tenant.tenant_id）',
    mode_code           VARCHAR(64)  NOT NULL COMMENT '交互模式编码: CONVERSATION/KNOWLEDGE_SEARCH/TASK_EXECUTION/ANALYSIS/APPROVAL',
    enabled             TINYINT      NOT NULL DEFAULT 1 COMMENT '是否开通该模式: 1=开 0=关',
    daily_llm_limit     INT          NULL COMMENT '每日 LLM 调用上限；NULL=不限制；0=禁止调用',
    daily_upload_bytes  BIGINT       NULL COMMENT '每日上传字节上限（租户级取各模式最小值）；NULL=不限制；0=禁止上传',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=正常, 1=已删除',
    UNIQUE KEY uk_tenant_mode (tenant_id, mode_code),
    INDEX idx_tenant_enabled (tenant_id, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户交互模式开关与日配额';
