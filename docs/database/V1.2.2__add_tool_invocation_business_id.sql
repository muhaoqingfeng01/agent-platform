-- V1.2.2: Add invocation_id business ID to t_tool_invocation_log
-- Aligns with V1.2.1 pattern (intent_id, memory_id) for consistent API paths

-- Add invocation_id column
ALTER TABLE t_tool_invocation_log
    ADD COLUMN invocation_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '调用记录唯一标识' AFTER id;

-- Populate invocation_id for existing rows using id
UPDATE t_tool_invocation_log SET invocation_id = CONCAT('tlinv_', id) WHERE invocation_id = '';

-- Add unique index for invocation_id
CREATE UNIQUE INDEX idx_invocation_invocation_id ON t_tool_invocation_log(invocation_id);
