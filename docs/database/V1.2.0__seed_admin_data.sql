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
