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
