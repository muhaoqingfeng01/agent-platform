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
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id       VARCHAR(64)   NOT NULL COMMENT '所属租户',
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
    tenant_id         VARCHAR(64)   NOT NULL COMMENT '所属租户',
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
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id       VARCHAR(64)  COMMENT '所属租户（NULL=全局规则）',
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
    tenant_id           VARCHAR(64)  COMMENT '所属租户',
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
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id           VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '所属租户',
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
