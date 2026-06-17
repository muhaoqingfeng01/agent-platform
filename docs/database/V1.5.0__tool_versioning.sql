-- ============================================================
-- V1.5.0: 工具版本化 — 保留历史版本，支持回滚
-- ============================================================

-- 1. 给 t_tool_registry 表添加版本号字段
ALTER TABLE t_tool_registry
    ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 1 COMMENT '当前版本号' AFTER status;

-- 2. 创建版本历史表
CREATE TABLE t_tool_registry_version (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT  NOT NULL,
    tool_id         VARCHAR(64)  NOT NULL COMMENT '关联 t_tool_registry.tool_id',
    version         INT          NOT NULL DEFAULT 1,
    tool_name       VARCHAR(128) NOT NULL,
    tool_type       VARCHAR(20)  NOT NULL COMMENT 'MCP/HTTP/BUILTIN/CUSTOM',
    endpoint_url    VARCHAR(512) COMMENT '连接端点',
    input_schema    JSON COMMENT '输入 JSON Schema',
    output_schema   JSON COMMENT '输出 JSON Schema',
    auth_config     JSON COMMENT '认证配置',
    require_approval TINYINT(1)  DEFAULT 0 COMMENT '是否需要审批',
    description     VARCHAR(512) COMMENT '功能描述',
    change_reason   VARCHAR(256) COMMENT '变更原因',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tool_version (tenant_id, tool_id, version DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具注册版本历史';
