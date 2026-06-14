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
