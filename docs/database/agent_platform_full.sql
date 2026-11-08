-- ============================================================================
-- Agent Platform — 全量数据库脚本（唯一维护入口）
-- ============================================================================
-- 库名建议: agent_platform_dev
-- 字符集: utf8mb4
--
-- 【使用方式】
--   1. 新环境: 创建库后一次性执行本文件
--      CREATE DATABASE IF NOT EXISTS agent_platform_dev
--        DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--      USE agent_platform_dev;
--      SOURCE agent_platform_full.sql;   -- 或在客户端导入本文件
--   2. 已有环境升级: 只执行文末「变更追加区」中尚未执行过的段落
--
-- 【维护约定 — 强制】
--   - 后续所有数据库变更一律追加到本文件末尾，禁止改写历史段落
--   - 每段追加必须包含: 时间、版本号/标识、作用说明
--   - 同时在下方「变更记录」表增加一行
--   - 历史分文件 V1.x.x__*.sql 仅作归档；新变更以本文件为准
-- ============================================================================
-- 变更记录
-- ----------------------------------------------------------------------------
-- | 时间                | 版本/标识 | 作用 |
-- |---------------------|-----------|------|
-- | 2026-06-11          | V1.0.0    | 基线表：租户/用户/角色权限/会话消息/知识库/工具/提示词/评测 |
-- | 2026-06-11          | V1.1.0    | 扩展表：意图/记忆/提示词版本/任务/文档切片/安全/审批/评测优化 |
-- | 2026-06-12          | V1.2.0    | 种子数据：默认租户、admin 账号、超级管理员角色与通配权限 |
-- | 2026-06-14          | V1.2.1-a  | 业务 ID：t_intent.intent_id、t_long_term_memory.memory_id |
-- | 2026-06-14          | V1.2.1-b  | 权限种子：前端菜单与后端 Sa-Token 权限码补齐 |
-- | 2026-06-17          | V1.2.2    | 工具调用日志业务 ID：t_tool_invocation_log.invocation_id |
-- | 2026-06-15          | V1.3.0    | RAG：知识库/文档切片策略与检索精度控制字段 |
-- | 2026-06-16          | V1.4.0    | KB 文件管理：created_by、状态迁移、chunk 软删 |
-- | 2026-06-18          | V1.5.0    | 工具版本化：version 字段 + t_tool_registry_version |
-- | 2026-07-03          | V1.6.0    | 通用异步任务中心：t_async_task |
-- | 2026-07-03          | V1.7.0    | P7 多模式交互权限种子 |
-- | 2026-08-01          | V1.8.0    | 租户交互模式开关与日配额：t_tenant_mode_quota |
-- | 2026-11-08          | FULL-INIT | 汇总生成全量脚本（合并上述历史迁移） |
-- | 2026-11-08          | V1.9.0    | 演示 HTTP 工具种子（echo/time/weather/calc）供对话工具调用联调 |
-- ----------------------------------------------------------------------------
-- 以下为按版本顺序合并的 DDL/DML（幂等语句尽量保留 IF NOT EXISTS / ON DUPLICATE KEY）
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ############################################################################
-- [追加段] 时间: 2026-06-11  |  版本: V1.0.0  |  作用: 基线 Schema（13 张核心表）
-- 来源文件: docs\database\V1.0.0__baseline_schema.sql
-- ############################################################################

-- ============================================================
-- Flyway Baseline Migration
-- Agent Platform — Initial Schema
-- Version: V1.0.0
-- ============================================================

-- Tenant table (multi-tenant isolation)
CREATE TABLE IF NOT EXISTS t_tenant (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT  NOT NULL UNIQUE COMMENT '租户唯一标识',
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
    tenant_id     BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id   BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id     BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id     BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
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
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
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


-- ############################################################################
-- [追加段] 时间: 2026-06-11  |  版本: V1.1.0  |  作用: 扩展 Schema（意图/记忆/任务/文档/安全/审批/评测等）
-- 来源文件: docs\database\V1.1.0__extended_schema.sql
-- ############################################################################

-- ============================================================
-- Flyway Migration
-- Agent Platform — 基于《数据库设计文档》P0-P5 扩展表
-- Version: V1.1.0
-- Description: 新增 15 张表（意图、记忆、提示词版本、任务执行、文档、
--               切片、命中记录、工具日志、敏感词、安全事件、审计日志、
--               审批工单、评测数据集、样本、优化工单）
-- ============================================================

-- ============================================================
-- T3: 意图识别与对话管理
-- ============================================================

-- 意图定义表（规则优先 + LLM 兜底策略）
CREATE TABLE IF NOT EXISTS t_intent (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
    intent_code     VARCHAR(64)  NOT NULL COMMENT '意图编码（如 ORDER_QUERY、LEAVE_APPLY）',
    intent_name     VARCHAR(128) NOT NULL COMMENT '意图名称',
    category        VARCHAR(64)  NOT NULL DEFAULT 'FAQ' COMMENT '意图分类: FAQ, TASK, CHITCHAT, MULTI_STEP',
    patterns        JSON         COMMENT '正则/关键词匹配规则列表',
    examples        JSON         COMMENT '示例问法（用于 LLM few-shot 分类）',
    llm_prompt      TEXT         COMMENT 'LLM 兜底分类专用提示词',
    required_params JSON         COMMENT '必要参数 JSON Schema',
    risk_level      VARCHAR(32)  NOT NULL DEFAULT 'LOW' COMMENT '风险等级: LOW, MEDIUM, HIGH, CRITICAL',
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, DISABLED',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    UNIQUE INDEX idx_intent_tenant_code (tenant_id, intent_code),
    INDEX idx_intent_category (category),
    INDEX idx_intent_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意图定义表';

-- 长期记忆表（用户画像 & 重要事实）
CREATE TABLE IF NOT EXISTS t_long_term_memory (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
    user_id         VARCHAR(64)  NOT NULL COMMENT '关联用户',
    memory_type     VARCHAR(64)  NOT NULL COMMENT '记忆类型: FACT, PREFERENCE, CONTEXT, SUMMARY',
    memory_key      VARCHAR(256) NOT NULL COMMENT '记忆键（如 user_role、preferred_language）',
    memory_value    TEXT         COMMENT '记忆值',
    confidence      DECIMAL(3,2) DEFAULT 1.00 COMMENT '置信度 0.00-1.00',
    source          VARCHAR(256) COMMENT '来源（conversation_id 或 manual）',
    expire_at       DATETIME     COMMENT '过期时间（NULL=永久）',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_memory_user (tenant_id, user_id),
    INDEX idx_memory_type (memory_type),
    INDEX idx_memory_expire (expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='长期记忆表';


-- ============================================================
-- T4: 提示词版本历史
-- ============================================================

CREATE TABLE IF NOT EXISTS t_prompt_template_version (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    prompt_id       VARCHAR(64)  NOT NULL COMMENT '关联提示词模板',
    version         INT          NOT NULL COMMENT '版本号',
    template_text   TEXT         NOT NULL COMMENT '该版本模板全文（含占位符）',
    variables       JSON         COMMENT '该版本变量定义',
    change_log      VARCHAR(512) COMMENT '变更说明',
    publisher       VARCHAR(64)  COMMENT '发布人',
    published_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_prompt_version (prompt_id, version),
    INDEX idx_prompt_pub_time (prompt_id, published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词模板版本历史表';


-- ============================================================
-- T5: 任务规划与执行引擎
-- ============================================================

-- 任务执行记录表（每次 DAG 复杂任务）
CREATE TABLE IF NOT EXISTS t_task_execution (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT  NOT NULL COMMENT '所属租户',
    execution_id      VARCHAR(64)  NOT NULL UNIQUE COMMENT '执行唯一标识',
    conversation_id   VARCHAR(64)  COMMENT '关联会话',
    agent_id          VARCHAR(64)  NOT NULL COMMENT '关联 Agent',
    plan_json         JSON         NOT NULL COMMENT 'DAG 任务计划 [{"id":"1","action":"xxx","dep":[]}]',
    status            VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, RUNNING, COMPLETED, FAILED, CANCELLED',
    total_steps       INT          NOT NULL DEFAULT 0 COMMENT '总步骤数',
    completed_steps   INT          NOT NULL DEFAULT 0 COMMENT '已完成步骤数',
    failed_step_id    VARCHAR(32)  COMMENT '失败步骤 ID',
    error_message     TEXT         COMMENT '错误信息',
    started_at        DATETIME     COMMENT '开始时间',
    finished_at       DATETIME     COMMENT '完成时间',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_task_conv (conversation_id),
    INDEX idx_task_agent (agent_id),
    INDEX idx_task_status (status),
    INDEX idx_task_tenant_time (tenant_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行记录表';

-- 任务步骤执行详情表（每个 DAG 节点追踪 + 重试）
CREATE TABLE IF NOT EXISTS t_task_step_execution (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    execution_id    VARCHAR(64)  NOT NULL COMMENT '关联执行记录',
    step_id         VARCHAR(32)  NOT NULL COMMENT '步骤 ID（对应 plan_json 中的 id）',
    action          VARCHAR(128) NOT NULL COMMENT '动作类型（如 retrieve_order、send_email）',
    handler_class   VARCHAR(256) COMMENT 'ActionHandler 实现类全限定名',
    input_json      JSON         COMMENT '输入参数',
    output_json     JSON         COMMENT '输出结果',
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, RUNNING, SUCCESS, FAILED, SKIPPED',
    retry_count     INT          NOT NULL DEFAULT 0 COMMENT '已重试次数',
    max_retries     INT          NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    duration_ms     BIGINT       COMMENT '执行耗时（毫秒）',
    error_message   TEXT         COMMENT '错误信息',
    started_at      DATETIME     COMMENT '开始时间',
    finished_at     DATETIME     COMMENT '完成时间',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_step_exec (execution_id),
    INDEX idx_step_status (status),
    UNIQUE INDEX idx_step_exec_step (execution_id, step_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务步骤执行详情表';


-- ============================================================
-- T6: RAG 知识库引擎
-- ============================================================

-- 文档表（上传 → MinIO → Tika 解析 → 切分）
CREATE TABLE IF NOT EXISTS t_document (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT   NOT NULL COMMENT '所属租户',
    knowledge_id    VARCHAR(64)   NOT NULL COMMENT '所属知识库',
    document_id     VARCHAR(64)   NOT NULL UNIQUE COMMENT '文档唯一标识',
    filename        VARCHAR(512)  NOT NULL COMMENT '原始文件名',
    file_type       VARCHAR(64)   NOT NULL COMMENT '文件类型: PDF, DOCX, TXT, MD, HTML, CSV',
    file_size       BIGINT        NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
    minio_path      VARCHAR(1024) COMMENT 'MinIO 存储路径',
    content_hash    VARCHAR(128)  COMMENT '文件内容 SHA256（用于去重）',
    chunk_count     INT           NOT NULL DEFAULT 0 COMMENT '切分块数',
    status          VARCHAR(32)   NOT NULL DEFAULT 'UPLOADING' COMMENT 'UPLOADING, PARSING, CHUNKING, EMBEDDING, READY, FAILED',
    error_message   TEXT          COMMENT '失败原因',
    uploaded_by     VARCHAR(64)   COMMENT '上传者 user_id',
    uploaded_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT       NOT NULL DEFAULT 0,
    INDEX idx_doc_kb (knowledge_id),
    INDEX idx_doc_tenant (tenant_id),
    INDEX idx_doc_status (status),
    INDEX idx_doc_hash (content_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档表';

-- 文档切片表（MySQL 存元数据 + Milvus 存向量）
CREATE TABLE IF NOT EXISTS t_document_chunk (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id     VARCHAR(64)   NOT NULL COMMENT '所属文档',
    knowledge_id    VARCHAR(64)   NOT NULL COMMENT '所属知识库（冗余加速查询）',
    chunk_index     INT           NOT NULL COMMENT '切片序号（从 0 开始）',
    content         MEDIUMTEXT    NOT NULL COMMENT '切片文本内容',
    token_count     INT           COMMENT 'Token 数量',
    content_hash    VARCHAR(128)  COMMENT '内容哈希（增量更新去重）',
    milvus_id       VARCHAR(128)  COMMENT 'Milvus 向量 ID（关联向量存储）',
    metadata_json   JSON          COMMENT '源数据（页码、章节标题、行号等）',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chunk_doc (document_id),
    INDEX idx_chunk_kb (knowledge_id),
    UNIQUE INDEX idx_chunk_milvus (milvus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档切片表';

-- 知识命中记录表（检索追溯 + 人工标注）
CREATE TABLE IF NOT EXISTS t_knowledge_hit_record (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT   NOT NULL COMMENT '所属租户',
    conversation_id   VARCHAR(64)   NOT NULL COMMENT '关联会话',
    message_id        VARCHAR(64)   COMMENT '关联消息',
    chunk_id          BIGINT        COMMENT '命中的切片 ID（关联 document_chunk.id）',
    query_text        TEXT          COMMENT '用户查询文本',
    relevance_score   DECIMAL(5,4)  COMMENT '检索相关性分数',
    rank_position     INT           COMMENT '融合排序后位置',
    used_in_prompt    TINYINT       NOT NULL DEFAULT 0 COMMENT '是否实际注入提示词: 0=否, 1=是',
    human_feedback    VARCHAR(32)   COMMENT '人工标注: EXCELLENT, NEEDS_FIX, SUPPLEMENT',
    feedback_note     TEXT          COMMENT '人工标注备注',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_hit_conv (conversation_id),
    INDEX idx_hit_chunk (chunk_id),
    INDEX idx_hit_tenant_time (tenant_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识命中记录表';


-- ============================================================
-- T7: MCP 工具调用日志
-- ============================================================

CREATE TABLE IF NOT EXISTS t_tool_invocation_log (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT  NOT NULL COMMENT '所属租户',
    tool_id           VARCHAR(64)  NOT NULL COMMENT '关联工具',
    conversation_id   VARCHAR(64)  COMMENT '关联会话',
    message_id        VARCHAR(64)  COMMENT '关联消息',
    execution_id      VARCHAR(64)  COMMENT '关联任务执行（T5）',
    input_json        JSON         COMMENT '输入参数',
    output_json       JSON         COMMENT '输出结果',
    status            VARCHAR(32)  NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS, FAILED, TIMEOUT, REJECTED',
    duration_ms       BIGINT       COMMENT '调用耗时（毫秒）',
    error_message     TEXT         COMMENT '错误信息',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tool_log_tool (tool_id),
    INDEX idx_tool_log_conv (conversation_id),
    INDEX idx_tool_log_tenant_time (tenant_id, created_at),
    INDEX idx_tool_log_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具调用日志表';


-- ============================================================
-- T10: 安全围栏
-- ============================================================

-- 敏感词库表（输入过滤规则）
CREATE TABLE IF NOT EXISTS t_sensitive_word (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT  COMMENT '所属租户（NULL=全局规则）',
    word            VARCHAR(256) NOT NULL COMMENT '敏感词或正则表达式',
    match_type      VARCHAR(32)  NOT NULL DEFAULT 'EXACT' COMMENT '匹配方式: EXACT, REGEX, SEMANTIC',
    category        VARCHAR(64)  NOT NULL DEFAULT 'CUSTOM' COMMENT '分类: INJECTION, JAILBREAK, PII, CUSTOM',
    severity        VARCHAR(32)  NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度: LOW, MEDIUM, HIGH, BLOCK',
    action          VARCHAR(32)  NOT NULL DEFAULT 'LOG' COMMENT '动作: LOG, WARN, BLOCK',
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, DISABLED',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sw_tenant (tenant_id),
    INDEX idx_sw_category (category),
    INDEX idx_sw_severity (severity, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词库表';

-- 安全事件表（拦截追溯 & 合规审计）
CREATE TABLE IF NOT EXISTS t_security_event (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT  COMMENT '所属租户',
    event_type          VARCHAR(64)  NOT NULL COMMENT '事件类型: INPUT_FILTER, OUTPUT_DESENSITIZE, INJECTION_DETECTED',
    rule_id             BIGINT       COMMENT '触发的规则 ID（关联 sensitive_word.id）',
    conversation_id     VARCHAR(64)  COMMENT '关联会话',
    message_id          VARCHAR(64)  COMMENT '关联消息',
    original_content    TEXT         COMMENT '原始内容',
    processed_content   TEXT         COMMENT '处理后内容（脱敏后或空）',
    matched_pattern     VARCHAR(512) COMMENT '匹配到的模式',
    action_taken        VARCHAR(32)  NOT NULL COMMENT '采取动作: LOG, WARN, BLOCK',
    operator            VARCHAR(64)  DEFAULT 'SYSTEM' COMMENT '操作人',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_se_event_type (event_type),
    INDEX idx_se_tenant_time (tenant_id, created_at),
    INDEX idx_se_conv (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全事件表';


-- ============================================================
-- T9/T10: 综合审计日志
-- ============================================================

-- ⚠️ 注意：大规模部署建议将数据写入 Elasticsearch，MySQL 仅保留近期热数据（30天）
CREATE TABLE IF NOT EXISTS t_audit_log (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT  NOT NULL COMMENT '所属租户',
    trace_id          VARCHAR(64)  NOT NULL COMMENT '全链路追踪 ID（串联 T9）',
    conversation_id   VARCHAR(64)  COMMENT '关联会话',
    actor_type        VARCHAR(32)  NOT NULL COMMENT '操作者类型: USER, ASSISTANT, TOOL, SYSTEM',
    actor_id          VARCHAR(64)  COMMENT '操作者标识',
    action            VARCHAR(128) NOT NULL COMMENT '动作类型（如 LLM_CALL、TOOL_INVOKE、RAG_RETRIEVE）',
    resource_type     VARCHAR(64)  COMMENT '资源类型',
    resource_id       VARCHAR(128) COMMENT '资源标识',
    request_json      JSON         COMMENT '请求内容',
    response_json     JSON         COMMENT '响应内容',
    duration_ms       BIGINT       COMMENT '耗时（毫秒）',
    status            VARCHAR(32)  NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS, FAILED, ERROR',
    ip_address        VARCHAR(64)  COMMENT '客户端 IP',
    user_agent        VARCHAR(512) COMMENT 'User-Agent',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_trace (trace_id),
    INDEX idx_audit_tenant_time (tenant_id, created_at),
    INDEX idx_audit_conv (conversation_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='综合审计日志表';


-- ============================================================
-- T11: 人机协同审批
-- ============================================================

CREATE TABLE IF NOT EXISTS t_approval_workflow (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT  NOT NULL COMMENT '所属租户',
    approval_id         VARCHAR(64)  NOT NULL UNIQUE COMMENT '审批唯一标识',
    tool_id             VARCHAR(64)  NOT NULL COMMENT '关联工具',
    conversation_id     VARCHAR(64)  COMMENT '关联会话',
    execution_id        VARCHAR(64)  COMMENT '关联任务执行',
    requester_id        VARCHAR(64)  NOT NULL COMMENT '请求人 user_id',
    approver_id         VARCHAR(64)  COMMENT '审批人 user_id（NULL=待分配）',
    title               VARCHAR(256) NOT NULL COMMENT '审批标题',
    operation_detail    JSON         NOT NULL COMMENT '操作内容详情（工具名、参数、风险等级）',
    status              VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, TIMEOUT, CANCELLED',
    approve_comment     TEXT         COMMENT '审批意见',
    timeout_at          DATETIME     NOT NULL COMMENT '超时时间（默认创建后 5 分钟）',
    approved_at         DATETIME     COMMENT '审批完成时间',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_approval_status (status),
    INDEX idx_approval_requester (requester_id),
    INDEX idx_approval_approver (approver_id),
    INDEX idx_approval_timeout (status, timeout_at),
    INDEX idx_approval_tenant_time (tenant_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批工单表';


-- ============================================================
-- T12: 效果评估与持续优化
-- ============================================================

-- 评测数据集表
CREATE TABLE IF NOT EXISTS t_evaluation_dataset (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT  NOT NULL COMMENT '所属租户',
    dataset_id      VARCHAR(64)  NOT NULL UNIQUE COMMENT '数据集唯一标识',
    name            VARCHAR(256) NOT NULL COMMENT '数据集名称',
    description     TEXT         COMMENT '描述',
    item_count      INT          NOT NULL DEFAULT 0 COMMENT '样本数量',
    source          VARCHAR(32)  NOT NULL DEFAULT 'MANUAL' COMMENT '来源: MANUAL, PROD_SAMPLE, SYNTHETIC',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    INDEX idx_ds_tenant (tenant_id),
    INDEX idx_ds_source (source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评测数据集表';

-- 评测样本表（Q&A 对）
CREATE TABLE IF NOT EXISTS t_evaluation_dataset_item (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    dataset_id          VARCHAR(64)  NOT NULL COMMENT '所属数据集',
    question            TEXT         NOT NULL COMMENT '测试问题',
    expected_answer     TEXT         COMMENT '标准答案（参考答案）',
    retrieval_context   JSON         COMMENT '期望检索到的上下文片段',
    metadata_json       JSON         COMMENT '扩展字段（来源会话 ID、难度等级等）',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dsi_dataset (dataset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评测样本表';

-- 优化工单表（BadCase 闭环）
CREATE TABLE IF NOT EXISTS t_optimization_ticket (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT  NOT NULL COMMENT '所属租户',
    ticket_id         VARCHAR(64)  NOT NULL UNIQUE COMMENT '工单唯一标识',
    conversation_id   VARCHAR(64)  NOT NULL COMMENT '关联点踩会话',
    message_id        VARCHAR(64)  NOT NULL COMMENT '关联点踩消息',
    issue_type        VARCHAR(64)  NOT NULL COMMENT '问题类型: HALLUCINATION, IRRELEVANT, INCOMPLETE, WRONG, OTHER',
    severity          VARCHAR(32)  NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度: LOW, MEDIUM, HIGH, CRITICAL',
    description       TEXT         COMMENT '问题描述（自动提取 + 人工补充）',
    assignee          VARCHAR(64)  COMMENT '指派处理人 user_id',
    status            VARCHAR(32)  NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN, ANALYZING, IN_PROGRESS, RESOLVED, CLOSED',
    resolution        TEXT         COMMENT '解决方案',
    resolution_type   VARCHAR(64)  COMMENT '解决类型: KNOWLEDGE_FIX, PROMPT_ADJUST, MODEL_FINETUNE, OTHER',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ticket_status (status),
    INDEX idx_ticket_assignee (assignee),
    INDEX idx_ticket_issue_type (issue_type),
    INDEX idx_ticket_tenant_time (tenant_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优化工单表';


-- ############################################################################
-- [追加段] 时间: 2026-06-12  |  版本: V1.2.0  |  作用: 管理员种子数据（admin / Mhqf@123456）
-- 来源文件: docs\database\V1.2.0__seed_admin_data.sql
-- ############################################################################

-- ============================================================
-- Flyway Seed Data Migration
-- Agent Platform — 初始化租户、管理员用户、角色、权限
-- Version: V1.2.0
-- ============================================================

-- 1. 创建租户
INSERT INTO t_tenant (tenant_id, name, status, tier) VALUES
(1000001, '默认租户', 'ACTIVE', 'ENTERPRISE')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 2. 创建管理员角色
INSERT INTO t_role (tenant_id, role_code, role_name, description) VALUES
(1000001, 'SUPER_ADMIN', '超级管理员', '拥有系统所有权限')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- 3. 创建通配符权限（所有资源 + 所有操作）
INSERT INTO t_permission (permission_code, resource, action, description) VALUES
('*', '*', '*', '超级管理员通配符权限 — 拥有所有资源和操作的访问权')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 4. 创建管理员用户
--    密码: Mhqf@123456 (bcrypt hashed)
INSERT INTO t_user (tenant_id, user_id, username, password_hash, email, status) VALUES
(1000001, 'user-admin-001', 'admin',
 '$2b$10$02hPEJnRXRiYYDVzy75Kk.vq3n/fPPdhbVoSSRYX1xfqaIyDN5rkq',
 'admin@agent-platform.local', 'ACTIVE')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 5. 用户 → 角色 关联
INSERT INTO t_user_role (user_id, role_id)
SELECT 'user-admin-001', r.id
FROM t_role r
WHERE r.tenant_id = 1000001 AND r.role_code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 6. 角色 → 权限 关联
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r, t_permission p
WHERE r.tenant_id = 1000001 AND r.role_code = 'SUPER_ADMIN'
  AND p.permission_code = '*'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);


-- ############################################################################
-- [追加段] 时间: 2026-06-14  |  版本: V1.2.1-a  |  作用: 意图与长期记忆业务 ID 字段
-- 来源文件: docs\database\V1.2.1__add_business_ids.sql
-- ############################################################################

-- V1.2.1: Add business IDs and fix missing columns for T3 implementation
-- Consistent with V1.0.0 pattern (conversation_id, message_id)

-- Add intent_id to t_intent (for consistent API paths like /api/v1/intents/{intentId})
ALTER TABLE t_intent
    ADD COLUMN intent_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '意图唯一标识' AFTER id;

-- Populate intent_id for existing rows using id
UPDATE t_intent SET intent_id = CONCAT('int_', id) WHERE intent_id = '';

-- Add unique index for intent_id
CREATE UNIQUE INDEX idx_intent_intent_id ON t_intent(intent_id);

-- Add memory_id to t_long_term_memory (for consistent API paths)
ALTER TABLE t_long_term_memory
    ADD COLUMN memory_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '记忆唯一标识' AFTER id;

-- Populate memory_id for existing rows using id
UPDATE t_long_term_memory SET memory_id = CONCAT('mem_', id) WHERE memory_id = '';

-- Add unique index for memory_id
CREATE UNIQUE INDEX idx_memory_memory_id ON t_long_term_memory(memory_id);


-- ############################################################################
-- [追加段] 时间: 2026-06-14  |  版本: V1.2.1-b  |  作用: 完整权限码种子数据
-- 来源文件: docs\database\V1.2.1__seed_permissions.sql
-- ############################################################################

-- ============================================================
-- Flyway Seed Data Migration
-- Agent Platform — 补充完整权限配置
-- Version: V1.2.1
-- ============================================================
-- 权限编码格式: resource:action
-- 覆盖所有前端路由/菜单 + 后端 @SaCheckPermission 注解
-- 使用 ON DUPLICATE KEY UPDATE 保证幂等，可重复执行
-- ============================================================

-- ==================== 用户管理 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('user:read',  'user', 'READ',  '查看用户列表与详情', NOW(), 0),
('user:write', 'user', 'WRITE', '创建/编辑用户、分配角色权限', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 租户管理 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('tenant:read',  'tenant', 'READ',  '查看租户列表与详情', NOW(), 0),
('tenant:write', 'tenant', 'WRITE', '创建/编辑租户配置', NOW(), 0),
('tenant:admin', 'tenant', 'ADMIN', '租户全量管理（含启停/删除）', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 会话 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('conversation:read',     'conversation', 'READ',   '查看会话列表与历史消息', NOW(), 0),
('conversation:send',     'conversation', 'WRITE',  '发送消息 / SSE 流式对话', NOW(), 0),
('conversation:create',   'conversation', 'WRITE',  '创建新会话', NOW(), 0),
('conversation:update',   'conversation', 'WRITE',  '更新会话标题/元数据', NOW(), 0),
('conversation:delete',   'conversation', 'DELETE', '删除会话', NOW(), 0),
('conversation:feedback', 'conversation', 'WRITE',  '提交消息反馈（赞/踩）', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 知识库 (kb) — 后端实际使用的权限码 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('kb:read',   'kb', 'READ',   '查看知识库列表与文档', NOW(), 0),
('kb:create', 'kb', 'WRITE',  '创建知识库', NOW(), 0),
('kb:update', 'kb', 'WRITE',  '更新知识库配置', NOW(), 0),
('kb:delete', 'kb', 'DELETE', '删除知识库', NOW(), 0),
('kb:search', 'kb', 'READ',   '知识库检索/命中记录', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 知识库 (knowledge) — 前端路由/菜单使用的权限码 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('knowledge:read',   'knowledge', 'READ',   '查看知识库（knowledge 前缀，等价 kb:read）', NOW(), 0),
('knowledge:create', 'knowledge', 'WRITE',  '创建知识库（knowledge 前缀，等价 kb:create）', NOW(), 0),
('knowledge:update', 'knowledge', 'WRITE',  '更新知识库（knowledge 前缀，等价 kb:update）', NOW(), 0),
('knowledge:delete', 'knowledge', 'DELETE', '删除知识库（knowledge 前缀，等价 kb:delete）', NOW(), 0),
('knowledge:search', 'knowledge', 'READ',   '知识库检索（knowledge 前缀，等价 kb:search）', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 文档管理 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('doc:read',   'doc', 'READ',   '查看/下载文档', NOW(), 0),
('doc:upload', 'doc', 'WRITE',  '上传文档（解析+入库）', NOW(), 0),
('doc:update', 'doc', 'WRITE',  '更新文档元数据/标签', NOW(), 0),
('doc:delete', 'doc', 'DELETE', '删除文档及关联分块', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 工具平台 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('tool:read',    'tool', 'READ',  '查看工具列表与详情', NOW(), 0),
('tool:create',  'tool', 'WRITE', '注册新工具', NOW(), 0),
('tool:update',  'tool', 'WRITE', '编辑工具定义/版本', NOW(), 0),
('tool:execute', 'tool', 'WRITE', '执行工具调用', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 任务 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('task:read',    'task', 'READ',  '查看任务列表与执行状态', NOW(), 0),
('task:create',  'task', 'WRITE', '创建任务计划', NOW(), 0),
('task:execute', 'task', 'WRITE', '执行/重试/终止任务', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 提示词 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('prompt:read',    'prompt', 'READ',    '查看提示词模板/版本列表', NOW(), 0),
('prompt:write',   'prompt', 'WRITE',   '编辑提示词（通用写权限）', NOW(), 0),
('prompt:create',  'prompt', 'WRITE',   '创建新提示词模板', NOW(), 0),
('prompt:update',  'prompt', 'WRITE',   '更新提示词模板 / 变基版本', NOW(), 0),
('prompt:delete',  'prompt', 'DELETE',  '删除提示词模板', NOW(), 0),
('prompt:publish', 'prompt', 'PUBLISH', '发布/启用提示词版本', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 审批 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('approval:read',    'approval', 'READ',  '查看审批列表与详情', NOW(), 0),
('approval:approve', 'approval', 'WRITE', '同意/驳回审批', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 安全 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('security:read',  'security', 'READ',  '查看敏感词规则/安全事件', NOW(), 0),
('security:write', 'security', 'WRITE', '管理敏感词规则/安全策略', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 意图管理 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('intent:read',   'intent', 'READ',   '查看意图列表与详情', NOW(), 0),
('intent:create', 'intent', 'WRITE',  '创建意图规则', NOW(), 0),
('intent:update', 'intent', 'WRITE',  '编辑意图规则', NOW(), 0),
('intent:delete', 'intent', 'DELETE', '删除意图规则', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 效果评估 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('evaluation:read',  'evaluation', 'READ',  '查看数据集/评测结果', NOW(), 0),
('evaluation:write', 'evaluation', 'WRITE', '创建数据集/执行评测', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 优化闭环 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('optimization:read',  'optimization', 'READ',  '查看优化工单列表与详情', NOW(), 0),
('optimization:write', 'optimization', 'WRITE', '创建/处理优化工单', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 可观测性 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('observability:read', 'observability', 'READ', '查看链路追踪/指标大盘（前端路由 + 菜单）', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== IM ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('im:admin', 'im', 'ADMIN', '管理 IM 渠道配置（前端路由 + 菜单）', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== SUPER_ADMIN 角色关联所有权限 ====================
-- 将新建的权限全部授予 SUPER_ADMIN 角色（幂等：ON DUPLICATE KEY UPDATE）
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
CROSS JOIN t_permission p
WHERE r.role_code = 'SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM t_role_permission rp2
    WHERE rp2.role_id = r.id AND rp2.permission_id = p.id
  );


-- ############################################################################
-- [追加段] 时间: 2026-06-17  |  版本: V1.2.2  |  作用: 工具调用日志 invocation_id
-- 来源文件: docs\database\V1.2.2__add_tool_invocation_business_id.sql
-- ############################################################################

-- V1.2.2: Add invocation_id business ID to t_tool_invocation_log
-- Aligns with V1.2.1 pattern (intent_id, memory_id) for consistent API paths

-- Add invocation_id column
ALTER TABLE t_tool_invocation_log
    ADD COLUMN invocation_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '调用记录唯一标识' AFTER id;

-- Populate invocation_id for existing rows using id
UPDATE t_tool_invocation_log SET invocation_id = CONCAT('tlinv_', id) WHERE invocation_id = '';

-- Add unique index for invocation_id
CREATE UNIQUE INDEX idx_invocation_invocation_id ON t_tool_invocation_log(invocation_id);


-- ############################################################################
-- [追加段] 时间: 2026-06-15  |  版本: V1.3.0  |  作用: RAG 切片策略与精度控制字段
-- 来源文件: docs\database\V1.3.0__add_chunk_strategy_and_precision_fields.sql
-- ############################################################################

-- ============================================================
-- Flyway Migration
-- Agent Platform — T6 RAG 知识库引擎：多策略切片 + 精度控制
-- Version: V1.3.0
-- Description: 知识库表 / 文档表新增切片策略字段 + 检索精度控制字段
-- ============================================================

-- ============================================================
-- t_knowledge_base 扩展：切片策略 + 检索精度控制
-- ============================================================
ALTER TABLE t_knowledge_base
    ADD COLUMN default_chunk_strategy VARCHAR(32) DEFAULT 'paragraph_sliding_window'
        COMMENT '默认切片策略: paragraph_sliding_window|fixed_size|markdown_header_aware|sentence_level|recursive_char_split|semantic',
    ADD COLUMN chunk_config_json JSON DEFAULT NULL
        COMMENT '知识库级切片策略自定义参数（覆盖默认值）',
    ADD COLUMN index_type VARCHAR(32) DEFAULT 'IVF_FLAT'
        COMMENT '向量索引类型: IVF_FLAT|IVF_SQ8|IVF_PQ|HNSW|DISKANN',
    ADD COLUMN index_params_json JSON DEFAULT NULL
        COMMENT '索引构建参数: {nlist, M, efConstruction, nbits, m, ...} — NULL=使用系统默认',
    ADD COLUMN search_strategy VARCHAR(32) DEFAULT 'balanced'
        COMMENT '检索策略: precise|balanced|fast|recall|turbo',
    ADD COLUMN search_params_json JSON DEFAULT NULL
        COMMENT '检索参数覆盖: {nprobe, topK, similarity_threshold, consistency_level, ...} — NULL=使用策略默认',
    ADD COLUMN multi_stage_params_json JSON DEFAULT NULL
        COMMENT '多阶段检索参数: {enable_reranker, rrf_k, vector_weight, ...} — NULL=使用系统默认',
    ADD COLUMN monitoring_params_json JSON DEFAULT NULL
        COMMENT '精度监控参数: {enable_auto_tuning, recall_target, ...} — NULL=使用系统默认';

-- ============================================================
-- t_document 扩展：切片策略覆盖 + 检索精度覆盖
-- ============================================================
ALTER TABLE t_document
    ADD COLUMN chunk_strategy VARCHAR(32) DEFAULT NULL
        COMMENT '文档级切片策略（NULL=使用知识库默认值）',
    ADD COLUMN chunk_config_json JSON DEFAULT NULL
        COMMENT '文档级切片自定义参数（NULL=使用策略默认值）',
    ADD COLUMN search_strategy_override VARCHAR(32) DEFAULT NULL
        COMMENT '文档级策略覆盖 — NULL=使用知识库策略',
    ADD COLUMN search_params_override_json JSON DEFAULT NULL
        COMMENT '文档级检索参数覆盖 — NULL=使用知识库参数',
    ADD COLUMN multi_stage_override_json JSON DEFAULT NULL
        COMMENT '文档级多阶段参数覆盖 — NULL=使用知识库参数';

-- ============================================================
-- 索引
-- ============================================================
ALTER TABLE t_knowledge_base ADD INDEX idx_kb_chunk_strategy (default_chunk_strategy);
ALTER TABLE t_knowledge_base ADD INDEX idx_kb_search_strategy (search_strategy);
ALTER TABLE t_document ADD INDEX idx_doc_strategy (chunk_strategy);
ALTER TABLE t_document ADD INDEX idx_doc_search_strategy (search_strategy_override);


-- ############################################################################
-- [追加段] 时间: 2026-06-16  |  版本: V1.4.0  |  作用: KB 文件管理升级（created_by/状态/软删）
-- 来源文件: docs\database\V1.4.0__kb_file_management_upgrade.sql
-- ############################################################################

-- ============================================================
-- Flyway Migration
-- Agent Platform — T6 知识库文件管理系统升级
-- Version: V1.4.0
-- Description: KB 创建者追踪 + 状态重命名 + 文档状态迁移 + chunk 软删
-- ============================================================

-- ============================================================
-- 1. t_knowledge_base: 新增 created_by 字段 + status 值迁移
-- ============================================================

-- 1.1 新增 created_by 字段（先 NULL，回填后再 NOT NULL）
ALTER TABLE t_knowledge_base
    ADD COLUMN created_by VARCHAR(64) DEFAULT NULL
        COMMENT '创建者 user_id（权限控制：仅创建者可删除该 KB 内文件）';

-- 1.2 回填已有数据：无创建者的旧 KB 归为 'system'
UPDATE t_knowledge_base SET created_by = 'system' WHERE created_by IS NULL;

-- 1.3 设置为 NOT NULL
ALTER TABLE t_knowledge_base
    MODIFY COLUMN created_by VARCHAR(64) NOT NULL
        COMMENT '创建者 user_id';

-- 1.4 status 值迁移：ACTIVE → ENABLED
UPDATE t_knowledge_base SET status = 'ENABLED' WHERE status = 'ACTIVE';

-- 1.5 新增索引：按租户+创建者查询
ALTER TABLE t_knowledge_base ADD INDEX idx_kb_creator (tenant_id, created_by);


-- ============================================================
-- 2. t_document: status 值迁移 + 索引优化
-- ============================================================

-- 2.1 旧状态 → 新状态映射
-- UPLOADING → PENDING_PARSE（上传完成，等待手动触发解析）
UPDATE t_document SET status = 'PENDING_PARSE' WHERE status = 'UPLOADING';

-- READY → PARSED（已解析，向量可用）
UPDATE t_document SET status = 'PARSED' WHERE status = 'READY';

-- 注意：PARSING / CHUNKING / EMBEDDING / FAILED 保持不变

-- 2.2 新增联合索引：按知识库+状态查询（文件管理列表过滤）
ALTER TABLE t_document ADD INDEX idx_doc_kb_status (knowledge_id, status);


-- ============================================================
-- 3. t_document_chunk: 新增 deleted 软删标记
-- ============================================================

-- 3.1 新增软删字段（弃用文档时标记而非物理删除，保留审计链）
ALTER TABLE t_document_chunk
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0
        COMMENT '逻辑删除: 0=正常, 1=已删除（弃用或删除文档时标记）';

-- 3.2 索引：全文检索需要排除已删除的 chunk
ALTER TABLE t_document_chunk ADD INDEX idx_chunk_deleted (deleted);


-- ============================================================
-- 4. t_knowledge_hit_record: 级联删除支持
-- ============================================================

-- 4.1 新增索引：按知识库删除命中记录（KB 级联删除时使用）
ALTER TABLE t_knowledge_hit_record ADD INDEX idx_hit_knowledge (chunk_id);


-- ============================================================
-- 变更摘要
-- ============================================================
-- t_knowledge_base:  +1 列 (created_by), +1 索引 (idx_kb_creator), status 值迁移
-- t_document:        +1 索引 (idx_doc_kb_status), status 值迁移
-- t_document_chunk:  +1 列 (deleted), +1 索引 (idx_chunk_deleted)
-- t_knowledge_hit_record: +1 索引 (idx_hit_knowledge)
--
-- Milvus Collection Schema 变更（不在 SQL 中，需手动或代码处理）:
--   kb_{tenantId} collection 新增 knowledge_id VARCHAR(128) 标量字段
-- ============================================================


-- ############################################################################
-- [追加段] 时间: 2026-06-18  |  版本: V1.5.0  |  作用: 工具版本化
-- 来源文件: docs\database\V1.5.0__tool_versioning.sql
-- ############################################################################

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


-- ############################################################################
-- [追加段] 时间: 2026-07-03  |  版本: V1.6.0  |  作用: 异步任务中心
-- 来源文件: docs\database\V1.6.0__async_task_center.sql
-- ############################################################################

-- =========================================================
-- V1.6.0: 通用异步任务中心
-- 所有异步耗时操作（文档解析、报表导出、数据导入等）统一管理
-- =========================================================

CREATE TABLE IF NOT EXISTS t_async_task (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id       VARCHAR(64) NOT NULL UNIQUE        COMMENT '业务主键',
    task_type     VARCHAR(64) NOT NULL               COMMENT '任务类型: DOCUMENT_PARSE / REPORT_EXPORT / ...',
    biz_id        VARCHAR(128) NOT NULL              COMMENT '业务主体ID（如documentId），防重+查询',
    tenant_id     BIGINT NOT NULL                    COMMENT '租户ID',
    status        VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED | RUNNING | COMPLETED | FAILED | TIMEOUT',
    payload_json  JSON                               COMMENT '业务参数JSON，透传给TaskHandler',
    result_json   JSON                               COMMENT '执行结果JSON',
    timeout_at    DATETIME NOT NULL                  COMMENT '超时截止时间',
    retry_count   INT DEFAULT 0                      COMMENT '已重试次数',
    max_retries   INT DEFAULT 3                      COMMENT '最大重试次数',
    error_message TEXT                               COMMENT '错误信息',
    started_at    DATETIME                           COMMENT '开始执行时间',
    finished_at   DATETIME                           COMMENT '完成时间',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT DEFAULT 0                  COMMENT '逻辑删除',
    INDEX idx_type_status_timeout (task_type, status, timeout_at),
    INDEX idx_biz_id (biz_id),
    INDEX idx_task_type_biz (task_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用异步任务表';


-- ############################################################################
-- [追加段] 时间: 2026-07-03  |  版本: V1.7.0  |  作用: 多模式交互权限
-- 来源文件: docs\database\V1.7.0__add_interaction_permissions.sql
-- ############################################################################

-- ============================================================
-- Agent Platform — P7 多模式交互权限种子数据
-- Version: V1.7.0
-- ============================================================
-- 新增交互端点权限码，并授予所有已有角色
-- 使用 ON DUPLICATE KEY UPDATE 保证幂等，可重复执行
-- ============================================================

-- ==================== 多模式交互 ====================
INSERT INTO t_permission (permission_code, resource, action, description, created_at, deleted) VALUES
('interaction:execute', 'interaction', 'WRITE', '执行统一交互（同步/流式）', NOW(), 0),
('interaction:read',    'interaction', 'READ',  '查询可用交互模式列表', NOW(), 0)
ON DUPLICATE KEY UPDATE description = VALUES(description), action = VALUES(action);

-- ==================== 授予所有已有角色 ====================
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
CROSS JOIN t_permission p
WHERE p.permission_code IN ('interaction:execute', 'interaction:read')
  AND NOT EXISTS (
    SELECT 1 FROM t_role_permission rp2
    WHERE rp2.role_id = r.id AND rp2.permission_id = p.id
  );


-- ############################################################################
-- [追加段] 时间: 2026-08-01  |  版本: V1.8.0  |  作用: 租户模式配额
-- 来源文件: docs\database\V1.8.0__tenant_mode_quota.sql
-- ############################################################################

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


-- ############################################################################
-- [变更追加区] 新的 DDL/DML 请追加在本行之后
-- 模板:
-- ############################################################################
-- [追加段] 时间: YYYY-MM-DD HH:MM  |  版本: V1.x.x  |  作用: 一句话说明
-- ############################################################################
-- <SQL 语句>
--
-- 同时更新文件头部「变更记录」表增加一行
-- ############################################################################


-- ############################################################################
-- [追加段] 时间: 2026-11-08 22:30  |  版本: V1.9.0  |  作用: 演示 HTTP 工具种子数据
-- ############################################################################
-- 依赖: 应用内 DemoHttpToolController（白名单 /api/v1/demo-tools/**）
-- 默认端口 8081；若改端口请同步改 endpoint
-- 幂等: 按 tool_id UNIQUE 覆盖更新
-- ############################################################################

INSERT INTO t_tool_registry (
    tenant_id, tool_id, name, description, tool_type, schema_json, endpoint,
    auth_config, require_approval, status, version, created_at, updated_at, deleted
) VALUES
(1000001, 'tool_demo_echo', '回声工具',
 '将用户输入的文本原样返回，用于验证对话内工具调用链路是否通畅。',
 'HTTP',
 JSON_OBJECT(
   'inputSchema', '{"method":"POST","type":"object","properties":{"message":{"type":"string","description":"要回显的文本"}},"required":["message"]}',
   'outputSchema', '{"type":"object","properties":{"echo":{"type":"string"},"length":{"type":"integer"},"ok":{"type":"boolean"}}}'
 ),
 'http://127.0.0.1:8081/api/v1/demo-tools/echo',
 NULL, 0, 'ACTIVE', 1, NOW(), NOW(), 0),

(1000001, 'tool_demo_time', '当前时间',
 '按指定时区返回当前日期时间与星期，默认 Asia/Shanghai。用户问几点、今天星期几时可调用。',
 'HTTP',
 JSON_OBJECT(
   'inputSchema', '{"method":"POST","type":"object","properties":{"timezone":{"type":"string","description":"IANA 时区，如 Asia/Shanghai、UTC"}},"required":[]}',
   'outputSchema', '{"type":"object","properties":{"timezone":{"type":"string"},"datetime":{"type":"string"},"dayOfWeek":{"type":"string"},"ok":{"type":"boolean"}}}'
 ),
 'http://127.0.0.1:8081/api/v1/demo-tools/current-time',
 NULL, 0, 'ACTIVE', 1, NOW(), NOW(), 0),

(1000001, 'tool_demo_weather', '城市天气查询',
 '查询指定城市的模拟天气（演示数据，非真实气象）。用户问某地天气时可调用。',
 'HTTP',
 JSON_OBJECT(
   'inputSchema', '{"method":"POST","type":"object","properties":{"city":{"type":"string","description":"城市名称，如上海、北京"}},"required":["city"]}',
   'outputSchema', '{"type":"object","properties":{"city":{"type":"string"},"condition":{"type":"string"},"temperatureC":{"type":"integer"},"summary":{"type":"string"},"ok":{"type":"boolean"}}}'
 ),
 'http://127.0.0.1:8081/api/v1/demo-tools/city-weather',
 NULL, 0, 'ACTIVE', 1, NOW(), NOW(), 0),

(1000001, 'tool_demo_calc', '简易计算器',
 '执行加减乘除。op 取 add/sub/mul/div，参数 a、b 为数字。',
 'HTTP',
 JSON_OBJECT(
   'inputSchema', '{"method":"POST","type":"object","properties":{"op":{"type":"string","description":"运算: add/sub/mul/div"},"a":{"type":"number","description":"左操作数"},"b":{"type":"number","description":"右操作数"}},"required":["op","a","b"]}',
   'outputSchema', '{"type":"object","properties":{"op":{"type":"string"},"a":{"type":"number"},"b":{"type":"number"},"result":{"type":"number"},"ok":{"type":"boolean"}}}'
 ),
 'http://127.0.0.1:8081/api/v1/demo-tools/calculator',
 NULL, 0, 'ACTIVE', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    tool_type = VALUES(tool_type),
    schema_json = VALUES(schema_json),
    endpoint = VALUES(endpoint),
    require_approval = VALUES(require_approval),
    status = VALUES(status),
    updated_at = NOW(),
    deleted = 0;

SET FOREIGN_KEY_CHECKS = 1;

-- ======================== END OF agent_platform_full.sql ========================
