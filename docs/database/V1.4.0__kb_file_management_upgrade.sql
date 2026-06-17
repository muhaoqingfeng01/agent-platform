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
