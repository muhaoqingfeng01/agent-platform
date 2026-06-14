-- ============================================================
-- Flyway Baseline Migration
-- Agent Platform — Initial Schema
-- Version: V1.0.0
-- ============================================================

-- Tenant table (multi-tenant isolation)
CREATE TABLE IF NOT EXISTS t_tenant (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   VARCHAR(64)  NOT NULL UNIQUE COMMENT '租户唯一标识',
    name        VARCHAR(128) NOT NULL COMMENT '租户名称',
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, SUSPENDED, DELETED',
    tier        VARCHAR(32)  NOT NULL DEFAULT 'STANDARD' COMMENT '套餐: STANDARD, PREMIUM, ENTERPRISE',
    config_json TEXT         COMMENT '租户配置JSON',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=正常, 1=已删除',
    INDEX idx_tenant_status (status),
    INDEX idx_tenant_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- User table
CREATE TABLE IF NOT EXISTS t_user (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     VARCHAR(64)  NOT NULL COMMENT '所属租户',
    user_id       VARCHAR(64)  NOT NULL UNIQUE COMMENT '用户唯一标识',
    username      VARCHAR(64)  NOT NULL COMMENT '用户名',
    password_hash VARCHAR(256) NOT NULL COMMENT '密码哈希',
    email         VARCHAR(128) COMMENT '邮箱',
    phone         VARCHAR(32)  COMMENT '手机号',
    status        VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    UNIQUE INDEX idx_user_tenant (tenant_id, username),
    INDEX idx_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- Role table (RBAC)
CREATE TABLE IF NOT EXISTS t_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   VARCHAR(64)  NOT NULL COMMENT '所属租户',
    role_code   VARCHAR(64)  NOT NULL COMMENT '角色编码',
    role_name   VARCHAR(128) NOT NULL COMMENT '角色名称',
    description VARCHAR(512) COMMENT '角色描述',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    UNIQUE INDEX idx_role_tenant_code (tenant_id, role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- Permission table
CREATE TABLE IF NOT EXISTS t_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(128) NOT NULL UNIQUE COMMENT '权限编码',
    resource      VARCHAR(128) NOT NULL COMMENT '资源路径',
    action        VARCHAR(64)  NOT NULL COMMENT '操作: READ, WRITE, DELETE, ADMIN',
    description   VARCHAR(512) COMMENT '权限描述',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- User-Role mapping
CREATE TABLE IF NOT EXISTS t_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    role_id BIGINT      NOT NULL,
    UNIQUE INDEX idx_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- Role-Permission mapping
CREATE TABLE IF NOT EXISTS t_role_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id       BIGINT      NOT NULL,
    permission_id BIGINT      NOT NULL,
    UNIQUE INDEX idx_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- Agent configuration table
CREATE TABLE IF NOT EXISTS t_agent_config (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     VARCHAR(64)  NOT NULL COMMENT '所属租户',
    agent_id      VARCHAR(64)  NOT NULL UNIQUE COMMENT 'Agent唯一标识',
    name          VARCHAR(128) NOT NULL COMMENT 'Agent名称',
    description   TEXT         COMMENT 'Agent描述',
    system_prompt TEXT         COMMENT '系统提示词',
    model_config  JSON         COMMENT '模型配置JSON',
    tool_ids      JSON         COMMENT '关联工具ID列表',
    knowledge_ids JSON         COMMENT '关联知识库ID列表',
    status        VARCHAR(32)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT, ACTIVE, PAUSED, ARCHIVED',
    version       INT          NOT NULL DEFAULT 1 COMMENT '版本号',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    INDEX idx_agent_tenant (tenant_id),
    INDEX idx_agent_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent配置表';

-- Conversation table
CREATE TABLE IF NOT EXISTS t_conversation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
    conversation_id VARCHAR(64)  NOT NULL UNIQUE COMMENT '会话唯一标识',
    agent_id        VARCHAR(64)  NOT NULL COMMENT '关联Agent',
    user_id         VARCHAR(64)  NOT NULL COMMENT '发起用户',
    title           VARCHAR(256) COMMENT '会话标题',
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, CLOSED, ARCHIVED',
    message_count   INT          NOT NULL DEFAULT 0 COMMENT '消息数量',
    total_tokens    BIGINT       NOT NULL DEFAULT 0 COMMENT '累计Token消耗',
    metadata_json   JSON         COMMENT '会话元数据',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    INDEX idx_conv_tenant (tenant_id),
    INDEX idx_conv_agent (agent_id),
    INDEX idx_conv_user (user_id),
    INDEX idx_conv_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';

-- Message table
CREATE TABLE IF NOT EXISTS t_message (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id VARCHAR(64)  NOT NULL COMMENT '所属会话',
    message_id      VARCHAR(64)  NOT NULL UNIQUE COMMENT '消息唯一标识',
    role            VARCHAR(32)  NOT NULL COMMENT '角色: USER, ASSISTANT, SYSTEM, TOOL',
    content         MEDIUMTEXT   NOT NULL COMMENT '消息内容',
    token_count     INT          COMMENT 'Token数量',
    metadata_json   JSON         COMMENT '消息元数据(含tool_calls等)',
    feedback        VARCHAR(32)  COMMENT '反馈: LIKE, DISLIKE, NULL',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_msg_conv (conversation_id),
    INDEX idx_msg_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- Knowledge base table
CREATE TABLE IF NOT EXISTS t_knowledge_base (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
    knowledge_id    VARCHAR(64)  NOT NULL UNIQUE COMMENT '知识库唯一标识',
    name            VARCHAR(256) NOT NULL COMMENT '知识库名称',
    description     TEXT         COMMENT '描述',
    embedding_model VARCHAR(128) COMMENT '嵌入模型',
    chunk_size      INT          NOT NULL DEFAULT 500 COMMENT '分块大小',
    chunk_overlap   INT          NOT NULL DEFAULT 50 COMMENT '分块重叠',
    document_count  INT          NOT NULL DEFAULT 0 COMMENT '文档数量',
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    INDEX idx_kb_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';

-- Tool registry table
CREATE TABLE IF NOT EXISTS t_tool_registry (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     VARCHAR(64)  NOT NULL COMMENT '所属租户',
    tool_id       VARCHAR(64)  NOT NULL UNIQUE COMMENT '工具唯一标识',
    name          VARCHAR(128) NOT NULL COMMENT '工具名称',
    description   TEXT         COMMENT '工具描述',
    tool_type     VARCHAR(64)  NOT NULL COMMENT '类型: MCP, HTTP, BUILTIN, CUSTOM',
    schema_json   JSON         NOT NULL COMMENT '工具Input/Output Schema',
    endpoint      VARCHAR(512) COMMENT 'MCP/HTTP端点',
    auth_config   JSON         COMMENT '认证配置',
    require_approval TINYINT   NOT NULL DEFAULT 0 COMMENT '是否需要审批',
    status        VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    INDEX idx_tool_tenant (tenant_id),
    INDEX idx_tool_type (tool_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具注册表';

-- Prompt template table
CREATE TABLE IF NOT EXISTS t_prompt_template (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
    prompt_id       VARCHAR(64)  NOT NULL COMMENT 'Prompt唯一标识',
    name            VARCHAR(256) NOT NULL COMMENT '模板名称',
    description     TEXT         COMMENT '描述',
    template_text   TEXT         NOT NULL COMMENT '模板文本(支持变量占位)',
    variables       JSON         COMMENT '变量定义',
    version         INT          NOT NULL DEFAULT 1 COMMENT '当前版本号',
    status          VARCHAR(32)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT, PUBLISHED, ARCHIVED',
    ab_test_config  JSON         COMMENT 'A/B测试配置',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    INDEX idx_prompt_tenant (tenant_id),
    INDEX idx_prompt_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词模板表';

-- Evaluation run table
CREATE TABLE IF NOT EXISTS t_evaluation_run (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
    evaluation_id   VARCHAR(64)  NOT NULL UNIQUE COMMENT '评测唯一标识',
    agent_id        VARCHAR(64)  NOT NULL COMMENT '被评测Agent',
    dataset_id      VARCHAR(64)  NOT NULL COMMENT '使用数据集',
    status          VARCHAR(32)  NOT NULL DEFAULT 'RUNNING' COMMENT '状态',
    overall_score   DECIMAL(5,2) COMMENT '综合评分',
    metrics_json    JSON         COMMENT '详细指标',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at     DATETIME     COMMENT '完成时间',
    INDEX idx_eval_agent (agent_id),
    INDEX idx_eval_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评测记录表';
