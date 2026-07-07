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
