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
