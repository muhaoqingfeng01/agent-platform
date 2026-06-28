# Agent Platform API 接口文档

> 自动生成于 2026-06-25 | 21 个 Controller · 113 个接口端点  
> Base URL: `http://localhost:8080`  
> 鉴权方式: Sa-Token Bearer Token（Header: `Authorization: Bearer <token>`）

---

## 目录

1. [认证管理](#1-认证管理) — 登录/登出/Token刷新/当前用户
2. [租户管理](#2-租户管理) — 租户CRUD/启停
3. [用户管理](#3-用户管理) — 用户注册/CRUD/状态/密码修改
4. [角色管理](#4-角色管理) — 角色CRUD/分配用户/分配权限
5. [权限管理](#5-权限管理) — 权限CRUD/批量导入
6. [对话管理](#6-对话管理) — 会话CRUD/状态流转
7. [消息收发](#7-消息收发) — 发送消息(非流式/SSE流式)/历史/反馈
8. [意图管理](#8-意图管理) — 意图CRUD/识别测试/批量测试
9. [提示词管理](#9-提示词管理) — 模板CRUD/发布回滚/版本历史/渲染/Diff
10. [任务规划与执行](#10-任务规划与执行) — 规划/执行/状态/取消/Handler列表
11. [工具平台](#11-工具平台) — 工具注册/CRUD/测试/版本/回滚/调用日志
12. [知识库管理](#12-知识库管理) — 知识库CRUD/切片配置/精度配置/启停
13. [文档管理](#13-文档管理) — 上传/列表/详情/下载/切片/解析/弃用
14. [文件管理](#14-文件管理) — 文件管理列表/状态汇总
15. [知识检索](#15-知识检索) — 混合检索/命中记录/人工标注
16. [精度监控](#16-精度监控) — 精度评估/Grid Search自动调优
17. [安全围栏](#17-安全围栏) — 敏感词CRUD/安全事件查询
18. [人机协同审批](#18-人机协同审批) — 审批工单/同意/拒绝/统计
19. [评测数据集](#19-评测数据集) — 数据集CRUD/样本管理
20. [评测执行](#20-评测执行) — 离线评估(LLM-as-Judge)
21. [优化工单](#21-优化工单) — BadCase闭环/工单管理/反馈统计

---

## 通用约定

| 项目 | 说明 |
|------|------|
| **请求方式** | 所有接口统一使用 `POST` |
| **Content-Type** | `application/json`（文件上传使用 `multipart/form-data`） |
| **响应格式** | `Result<T>` 统一封装：`{ "code": 200, "message": "success", "data": ... }` |
| **分页参数** | `page`（从0开始）、`size`（默认20） |
| **鉴权** | 除登录/注册等公开接口外，需在 Header 中携带 `Authorization: Bearer <token>` |
| **SSE 流式** | `Accept: text/event-stream`，响应为 `SseEmitter` |

---

## 1. 认证管理

**Tag:** `认证管理` | **Base:** `/api/v1/auth` | **权限:** 公开接口

### POST /api/v1/auth/login
用户登录 — 校验用户名密码，返回 Sa-Token 访问令牌和刷新令牌

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| username | String | ✅ | 用户名 |
| password | String | ✅ | 密码 |
| tenantId | Long | | 租户ID |
| provider | String | | LOCAL / LDAP / SSO |

**响应:** `Result<LoginResponse>`
```json
{ "code": 200, "data": { "token": "...", "refreshToken": "...", "tokenType": "Bearer", "expiresIn": 3600 }}
```
| 状态码 | 说明 |
|:--:|------|
| 200 | 登录成功 |
| 401 | 用户名或密码错误 |

---

### POST /api/v1/auth/refresh
刷新 Token — 使用 RefreshToken 换取新 AccessToken

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| userId | String | ✅ | 用户ID |
| refreshToken | String | ✅ | 刷新令牌 |

| 状态码 | 说明 |
|:--:|------|
| 200 | 刷新成功 |
| 401 | RefreshToken无效或已过期 |

---

### POST /api/v1/auth/logout
用户登出 — 删除当前 AccessToken 和 RefreshToken

无请求参数。响应 `Result<Void>`

---

### POST /api/v1/auth/me
获取当前用户信息（需登录）

无请求参数。响应 `Result<UserInfo>` — `{ userId, username, tenantId }`

---

## 2. 租户管理

**Tag:** `租户管理` | **Base:** `/api/v1/tenants`

### POST /api/v1/tenants/create
创建租户  
**权限:** `tenant:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| tenantId | Long | ✅ | 租户唯一标识（2-64位） |
| name | String | ✅ | 租户名称（2-128位） |
| tier | String | | 套餐等级：STANDARD/PREMIUM/ENTERPRISE（默认STANDARD） |

| 状态码 | 说明 |
|:--:|------|
| 200 | 创建成功 |
| 409 | 租户标识已存在 |

---

### POST /api/v1/tenants/list
租户列表  
**权限:** `tenant:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

---

### POST /api/v1/tenants/get
租户详情  
**权限:** `tenant:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 租户主键ID |

| 状态码 | 说明 |
|:--:|------|
| 404 | 租户不存在 |

---

### POST /api/v1/tenants/update
更新租户  
**权限:** `tenant:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| tenantId | Long | ✅ | 租户ID |
| name | String | ✅ | 租户名称（2-128位） |
| tier | String | | 套餐等级 |
| configJson | String | | 租户配置JSON |

---

### POST /api/v1/tenants/toggle-status
启停租户  
**权限:** `TENANT_ADMIN` 角色

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 租户主键ID |
| status | String | | ACTIVE / SUSPENDED |

---

### POST /api/v1/tenants/delete
删除租户（逻辑删除）  
**权限:** `TENANT_ADMIN` 角色

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 租户主键ID |

---

## 3. 用户管理

**Tag:** `用户管理` | **Base:** `/api/v1/users`

### POST /api/v1/users/register
用户注册（公开接口）— 注册后自动分配默认 VIEWER 角色

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| tenantId | Long | ✅ | 所属租户标识 |
| username | String | ✅ | 用户名（3-64位） |
| password | String | ✅ | 密码（至少8位） |
| email | String | | 邮箱 |
| phone | String | | 手机号 |

---

### POST /api/v1/users/list
用户列表  
**权限:** `user:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

---

### POST /api/v1/users/get
用户详情  
**权限:** `user:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 用户主键ID |

---

### POST /api/v1/users/update
更新用户信息  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 用户主键ID |
| email | String | | 新邮箱 |
| phone | String | | 新手机号 |

---

### POST /api/v1/users/toggle-status
启停用户  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 用户主键ID |
| status | String | | ACTIVE / DISABLED |

---

### POST /api/v1/users/change-password
修改密码 — 当前用户修改自己的密码（需验证旧密码），修改后强制下线

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 用户主键ID |
| oldPassword | String | ✅ | 旧密码 |
| newPassword | String | ✅ | 新密码（至少8位） |

---

## 4. 角色管理

**Tag:** `角色管理` | **Base:** `/api/v1/roles`

### POST /api/v1/roles/create
创建角色  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| tenantId | Long | ✅ | 所属租户标识 |
| roleCode | String | ✅ | 角色编码（2-64位），如 DATA_ANALYST |
| roleName | String | ✅ | 角色名称（2-128位），如 数据分析师 |
| description | String | | 角色描述 |

---

### POST /api/v1/roles/list
角色列表  
**权限:** `user:read`

无请求参数（自动从当前租户上下文获取）

---

### POST /api/v1/roles/update
更新角色  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 角色主键ID |
| roleName | String | | 新角色名称 |
| description | String | | 新角色描述 |

---

### POST /api/v1/roles/delete
删除角色  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 角色主键ID |

---

### POST /api/v1/roles/assign-user
为用户分配角色 — 权限变更后强制用户下线  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| roleId | Long | ✅ | 角色ID |
| userId | String | ✅ | 用户业务ID |

---

### POST /api/v1/roles/users
查看角色下的用户  
**权限:** `user:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 角色主键ID |

响应 `Result<List<String>>` — 用户ID列表

---

### POST /api/v1/roles/assign-permission
为角色分配权限  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| roleId | Long | ✅ | 角色ID |
| permissionId | Long | | 权限主键ID |

---

## 5. 权限管理

**Tag:** `权限管理` | **Base:** `/api/v1/permissions`

### POST /api/v1/permissions/list
分页查询权限列表  
**权限:** `user:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<PageResponse<PermissionResponse>>`

---

### POST /api/v1/permissions/create
创建单个权限  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| permissionCode | String | ✅ | 权限编码（2-128位），如 `report:export` |
| resource | String | ✅ | 资源路径（2-128位） |
| action | String | ✅ | 操作类型：READ/WRITE/DELETE/ADMIN/PUBLISH |
| description | String | | 权限描述 |

---

### POST /api/v1/permissions/delete
级联删除权限 — 事务内依次删除角色-权限关联 → 逻辑删除权限  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 权限主键ID |

---

### POST /api/v1/permissions/import
批量导入权限 — JSON 数组上传，事务保证原子性（全部成功或全部回滚）  
**权限:** `user:write`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| permissions | List\<PermissionCreateCommand\> | ✅ | 待导入的权限列表 |

---

## 6. 对话管理

**Tag:** `对话管理` | **Base:** `/api/v1/conversations`

### POST /api/v1/conversations/create
创建新会话  
**权限:** `conversation:create`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| agentId | String | ✅ | Agent配置ID |
| title | String | | 会话标题 |
| metadata | Map\<String, Object\> | | 自定义元数据 |

---

### POST /api/v1/conversations/list
我的会话列表  
**权限:** `conversation:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<PageResponse<ConversationResponse>>`

---

### POST /api/v1/conversations/get
会话详情  
**权限:** `conversation:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 会话业务ID |

---

### POST /api/v1/conversations/update-title
更新标题  
**权限:** `conversation:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 会话业务ID |
| title | String | ✅ | 新标题 |

---

### POST /api/v1/conversations/transition-status
状态流转  
**权限:** `conversation:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 会话业务ID |
| targetStatus | String | ✅ | 目标状态 |

---

### POST /api/v1/conversations/delete
逻辑删除  
**权限:** `conversation:delete`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 会话业务ID |

---

## 7. 消息收发

**Tag:** `对话管理` | **Base:** `/api/v1/conversations/messages`

### POST /api/v1/conversations/messages/send
发送消息（非流式）  
**权限:** `conversation:send`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| conversationId | String | ✅ | 会话业务ID |
| content | String | ✅ | 消息内容 |

响应 `Result<MessageResponse>`

---

### POST /api/v1/conversations/messages/stream
发送消息（SSE 流式响应）  
**权限:** `conversation:send`  
**Produces:** `text/event-stream`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| conversationId | String | ✅ | 会话业务ID |
| content | String | ✅ | 消息内容 |

响应: `SseEmitter`（超时300s），流式返回 LLM 生成的每个 token

---

### POST /api/v1/conversations/messages/list
历史消息列表  
**权限:** `conversation:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| id | String | ✅ | | 会话业务ID |
| page | int | | 0 | 页码 |
| size | int | | 50 | 每页数量 |

响应 `Result<PageResponse<MessageResponse>>`

---

### POST /api/v1/conversations/messages/before
加载更早的消息（基于游标）  
**权限:** `conversation:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 会话业务ID |
| before | String | ✅ | 游标（消息ID，加载此消息之前的） |

响应 `Result<List<MessageResponse>>`

---

### POST /api/v1/conversations/messages/feedback
消息反馈  
**权限:** `conversation:feedback`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| conversationId | String | ✅ | 会话业务ID |
| msgId | String | ✅ | 消息ID |
| feedback | String | | 反馈内容 |

---

## 8. 意图管理

**Tag:** `意图管理` | **Base:** `/api/v1/intents`

### POST /api/v1/intents/create
创建意图  
**权限:** `intent:create`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| name | String | ✅ | 意图名称 |
| category | String | | 意图分类 |
| patterns | List\<String\> | | 匹配模式 |
| examples | List\<String\> | | 示例 |
| llmPrompt | String | | LLM 识别提示词 |
| requiredParams | List\<Map\<String,Object\>\> | | 必需参数 |
| riskLevel | String | | 风险等级 |

---

### POST /api/v1/intents/list
意图列表  
**权限:** `intent:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<PageResponse<IntentResponse>>`

---

### POST /api/v1/intents/get
意图详情  
**权限:** `intent:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 意图业务ID |

---

### POST /api/v1/intents/update
编辑意图  
**权限:** `intent:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 意图业务ID |
| intentName | String | | 新名称 |
| patterns | List\<String\> | | 新模式 |
| examples | List\<String\> | | 新示例 |
| llmPrompt | String | | 新提示词 |
| requiredParams | List\<Map\<String,Object\>\> | | 新参数 |
| riskLevel | String | | 新风险等级 |

---

### POST /api/v1/intents/toggle-status
启停意图  
**权限:** `intent:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 意图业务ID |
| status | String | ✅ | 目标状态 |

---

### POST /api/v1/intents/test
测试意图识别  
**权限:** `intent:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 意图业务ID |
| input | String | ✅ | 测试输入文本 |

响应 `Result<IntentTestResponse>`

---

### POST /api/v1/intents/batch-test
批量测试  
**权限:** `intent:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| items | List\<BatchTestItem\> | | 测试项列表，每项包含 `input` 和 `expectedIntentCode` |

响应 `Result<BatchTestResponse>`

---

### POST /api/v1/intents/delete
删除意图  
**权限:** `intent:delete`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 意图业务ID |

---

## 9. 提示词管理

**Tag:** `提示词管理` | **Base:** `/api/v1/prompts`

### POST /api/v1/prompts/create
创建提示词模板（草稿状态）  
**权限:** `prompt:create`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| name | String | ✅ | 模板名称（最长256） |
| description | String | | 描述 |
| templateText | String | ✅ | 模板正文（支持 `{{variable}}` 占位符） |
| variables | List\<VariableDef\> | | 变量定义 |
| abTestConfig | String | | A/B测试配置 |

---

### POST /api/v1/prompts/list
提示词模板列表（分页）  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |
| status | String | | | 过滤状态：DRAFT/PUBLISHED/ARCHIVED |

响应 `Result<PageResponse<PromptResponse>>`

---

### POST /api/v1/prompts/get
模板详情  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |

---

### POST /api/v1/prompts/update
编辑模板（仅 DRAFT 状态可编辑）  
**权限:** `prompt:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |
| name | String | | 新名称 |
| description | String | | 新描述 |
| templateText | String | | 新模板正文 |
| variables | List\<VariableDef\> | | 新变量定义 |

---

### POST /api/v1/prompts/delete
软删除模板  
**权限:** `prompt:delete`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |

---

### POST /api/v1/prompts/preview
变量填充预览渲染  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |
| variables | Map\<String, Object\> | | 变量键值对 |

响应 `Result<String>` — 渲染后的文本

---

### POST /api/v1/prompts/publish
发布当前草稿（版本号+1）  
**权限:** `prompt:publish`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |

---

### POST /api/v1/prompts/rollback
回滚到指定版本  
**权限:** `prompt:publish`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |
| version | int | ✅ | 目标版本号 |

---

### POST /api/v1/prompts/archive
归档模板  
**权限:** `prompt:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |

---

### POST /api/v1/prompts/versions/list
查看版本历史列表  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |

响应 `Result<List<VersionResponse>>`

---

### POST /api/v1/prompts/versions/detail
查看指定版本内容  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |
| version | int | ✅ | 版本号 |

---

### POST /api/v1/prompts/diff
两个版本的差异对比  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |
| v1 | int | ✅ | 版本1 |
| v2 | int | ✅ | 版本2 |

响应 `Result<DiffResponse>`

---

### POST /api/v1/prompts/render
运行时渲染（仅已发布模板，供编排引擎调用）  
**权限:** `prompt:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 模板业务ID |
| variables | Map\<String, Object\> | | 变量键值对 |

响应 `Result<String>` — 渲染后的最终提示词

---

## 10. 任务规划与执行

**Tag:** `任务规划与执行` | **Base:** `/api/v1/tasks`

### POST /api/v1/tasks/plan
规划任务 — 调用 LLM 将用户意图拆解为 DAG 步骤序列  
**权限:** `task:create`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| userIntent | String | ✅ | 用户意图描述 |
| conversationId | String | | 会话ID |
| agentId | String | ✅ | Agent配置ID |

响应 `Result<Map<String, Object>>` — DAG 执行计划

---

### POST /api/v1/tasks/execute
执行任务 — 根据已规划的 DAG 启动并行执行  
**权限:** `task:execute`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| executionId | String | ✅ | 执行计划ID |
| async | boolean | | 默认true（异步执行） |

响应 `Result<Map<String, String>>`

---

### POST /api/v1/tasks/status
查询执行状态 — 获取任务执行的当前进度和各步骤状态  
**权限:** `task:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| executionId | String | ✅ | 执行ID |

响应 `Result<ExecutionStatusResponse>` — 包含各步骤状态、进度

---

### POST /api/v1/tasks/plan/get
查询执行计划 — 获取已规划的 DAG 结构  
**权限:** `task:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| executionId | String | ✅ | 执行ID |

响应 `Result<Map<String, Object>>` — DAG 节点和边

---

### POST /api/v1/tasks/cancel
取消执行 — 取消正在执行或等待中的任务  
**权限:** `task:execute`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| executionId | String | ✅ | 执行ID |

响应 `Result<Map<String, String>>`

---

### POST /api/v1/tasks/handlers
可用动作列表 — 获取所有已注册的 ActionHandler 及其参数 Schema  
**权限:** `task:read`

无请求参数。响应 `Result<List<Map<String, Object>>>`

---

## 11. 工具平台

**Tag:** `工具平台` | **Base:** `/api/v1/tools`

### POST /api/v1/tools/create
注册新工具  
**权限:** `tool:create`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| name | String | ✅ | 工具名称 |
| description | String | | 功能描述（供LLM理解） |
| toolType | String | ✅ | 工具类型：MCP/HTTP/BUILTIN/CUSTOM |
| inputSchema | String | | 输入参数 JSON Schema |
| outputSchema | String | | 输出格式 JSON Schema |
| endpoint | String | | MCP SSE端点或HTTP URL |
| authType | String | | 认证类型：API_KEY/BEARER/BASIC/NONE |
| apiKey | String | | API密钥（authType=API_KEY 时填） |
| token | String | | Bearer Token（authType=BEARER 时填） |
| requireApproval | boolean | | 高风险操作需审批（默认false） |

---

### POST /api/v1/tools/list
工具列表（按类型筛选）  
**权限:** `tool:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |
| type | String | | | 筛选类型：MCP/HTTP/BUILTIN/CUSTOM |

响应 `Result<PageResponse<ToolResponse>>`

---

### POST /api/v1/tools/invocations
调用日志列表  
**权限:** `tool:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| toolId | String | | | 筛选工具ID |
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<PageResponse<ToolInvocationLogResponse>>`

---

### POST /api/v1/tools/get
工具详情  
**权限:** `tool:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |

---

### POST /api/v1/tools/update
编辑工具配置  
**权限:** `tool:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |
| name | String | | 新名称 |
| description | String | | 新描述 |
| toolType | String | | 新类型 |
| inputSchema | Map\<String,Object\> | | 新输入Schema |
| outputSchema | Map\<String,Object\> | | 新输出Schema |
| endpoint | String | | 新端点 |
| authType | String | | 新认证类型 |
| apiKey | String | | 新API密钥 |
| token | String | | 新Token |
| requireApproval | boolean | | 是否需要审批 |

---

### POST /api/v1/tools/toggle-status
启停工具  
**权限:** `tool:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |
| status | String | ✅ | ACTIVE / DISABLED |

---

### POST /api/v1/tools/test
测试工具调用 — 支持 MCP 和 HTTP 类型  
**权限:** `tool:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |
| params | Map\<String, Object\> | | 测试参数 |

响应 `Result<ToolTestResponse>` — `{ success, result, durationMs, invocationId, errorMessage }`

---

### POST /api/v1/tools/versions/list
版本历史列表  
**权限:** `tool:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |

响应 `Result<List<VersionResponse>>`

---

### POST /api/v1/tools/versions/detail
版本详情  
**权限:** `tool:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |
| version | int | ✅ | 版本号 |

---

### POST /api/v1/tools/rollback
回滚到指定版本  
**权限:** `tool:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | String | ✅ | 工具业务ID |
| version | int | ✅ | 目标版本号 |

---

## 12. 知识库管理

**Tag:** `知识库管理` | **Base:** `/api/v1/knowledge-bases`

### POST /api/v1/knowledge-bases/create
创建知识库  
**权限:** `kb:create`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| name | String | ✅ | 知识库名称 |
| description | String | | 描述 |
| embeddingModel | String | | Embedding 模型名 |

---

### POST /api/v1/knowledge-bases/list
知识库列表  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 默认值 |
|------|------|:--:|:--:|
| page | int | | 0 |
| size | int | | 20 |

响应 `Result<PageResponse<KnowledgeBaseDTO>>`

---

### POST /api/v1/knowledge-bases/get
知识库详情  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

---

### POST /api/v1/knowledge-bases/update
更新知识库名称/描述  
**权限:** `kb:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |
| name | String | | 新名称 |
| description | String | | 新描述 |

---

### POST /api/v1/knowledge-bases/update-chunk-config
设置知识库默认切片策略  
**权限:** `kb:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |
| defaultChunkStrategy | String | | 默认切片策略 |
| chunkConfigJson | String | | 切片配置JSON |

---

### POST /api/v1/knowledge-bases/set-precision-config
设置知识库检索精度参数  
**权限:** `kb:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |
| indexType | String | | 索引类型 |
| indexParams | Map\<String,Object\> | | 索引参数 |
| searchStrategy | String | | 检索策略 |
| searchParams | Map\<String,Object\> | | 检索参数 |
| multiStageParams | Map\<String,Object\> | | 多阶段参数 |
| monitoringParams | Map\<String,Object\> | | 监控参数 |

---

### POST /api/v1/knowledge-bases/precision-config/resolved
查询当前生效的完整精度配置（含继承和覆盖）  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

响应 `Result<PrecisionConfigDTO>`

---

### POST /api/v1/knowledge-bases/enable
启用知识库  
**权限:** `kb:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

---

### POST /api/v1/knowledge-bases/disable
弃用知识库  
**权限:** `kb:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

---

### POST /api/v1/knowledge-bases/delete
级联删除知识库（先弃用后删除）  
**权限:** `kb:delete`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

---

### POST /api/v1/knowledge-bases/stats
知识库文档统计  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

响应 `Result<Map<String, Long>>`

---

## 13. 文档管理

**Tag:** `文档管理` | **Base:** `/api/v1`

### POST /api/v1/knowledge-bases/documents/upload
上传文档（multipart/form-data，自动存 MinIO）  
**权限:** `doc:upload`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| metadata (part) | DocumentUploadRequest | ✅ | 元数据 |
| file (part) | MultipartFile | ✅ | 文件 |

**metadata 字段:**
| 字段 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |
| chunkStrategy | String | | 切片策略 |
| chunkSize | Integer | | 切片大小 |
| chunkOverlap | Integer | | 重叠大小 |
| chunkConfig | String | | 切片配置JSON |

---

### POST /api/v1/documents/list
文档列表（按知识库）  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| knowledgeId | String | | | 筛选知识库 |
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<PageResponse<DocumentDTO>>`

---

### POST /api/v1/documents/get
文档详情  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |

---

### POST /api/v1/documents/status
查询文档处理状态  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |

响应 `Result<String>` — 状态值

---

### POST /api/v1/documents/download
下载原始文档（MinIO 流式代理）  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |

响应: 文件流（直接写入 `HttpServletResponse`）

---

### POST /api/v1/documents/chunks
查询文档切片列表  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| documentId | String | ✅ | | 文档业务ID |
| offset | int | | 0 | 偏移量 |
| limit | int | | 50 | 数量限制 |

响应 `Result<Map<String, Object>>`

---

### POST /api/v1/documents/precision-override
设置文档级精度参数覆盖  
**权限:** `doc:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |
| searchParamsOverrideJson | String | | 检索参数覆盖 |
| multiStageOverrideJson | String | | 多阶段参数覆盖 |

---

### POST /api/v1/documents/delete
删除文档（含 MinIO + Milvus + MySQL，需 KB 创建者权限）  
**权限:** `doc:delete`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |

---

### POST /api/v1/documents/parse
手动触发文档异步解析（PENDING_PARSE/FAILED → 异步管线）  
**权限:** `doc:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |

---

### POST /api/v1/documents/batch-parse
批量触发文档解析  
**权限:** `doc:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |
| documentIds | List\<String\> | | 指定文档ID列表（空则全部） |

响应 `Result<Map<String, Object>>`

---

### POST /api/v1/documents/deprecate
弃用文档（删除 Milvus 向量，保留 MinIO 文件和元数据）  
**权限:** `doc:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| documentId | String | ✅ | 文档业务ID |

---

## 14. 文件管理

**Tag:** `文件管理` | **Base:** `/api/v1/knowledge-bases`

### POST /api/v1/knowledge-bases/files/list
文件管理列表（含状态标签、操作权限）  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| knowledgeId | String | ✅ | | 知识库业务ID |
| page | int | | 0 | 页码 |
| size | int | | 50 | 每页数量 |

响应 `Result<Map<String, Object>>`

---

### POST /api/v1/knowledge-bases/files/summary
文件状态汇总  
**权限:** `doc:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| knowledgeId | String | ✅ | 知识库业务ID |

响应 `Result<Map<String, Long>>` — 各状态文件计数

---

## 15. 知识检索

**Tag:** `知识检索` | **Base:** `/api/v1/knowledge`

### POST /api/v1/knowledge/search
混合检索（向量 + 关键词 + RRF 融合）  
**权限:** `kb:search`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| query | String | | 查询文本 |
| knowledgeId | String | | 知识库ID |
| searchConfig | Map\<String,Object\> | | 检索配置 |

响应 `Result<SearchResultDTO>` — 含 `hits` 和 `documents`

---

### POST /api/v1/knowledge/hits/list
命中记录列表  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| conversationId | String | | | 按会话筛选 |
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<List<HitRecordDTO>>`

---

### POST /api/v1/knowledge/hits/feedback
人工标注（EXCELLENT / NEEDS_FIX / SUPPLEMENT）  
**权限:** `kb:update`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 命中记录ID |
| feedback | String | | 标注类型 |
| note | String | | 备注 |

---

### POST /api/v1/knowledge/precision-strategies
查询可用检索策略预设列表  
**权限:** `kb:read`

无请求参数。响应 `Result<List<Map<String, Object>>>`

---

## 16. 精度监控

**Tag:** `精度监控` | **Base:** `/api/v1/knowledge-bases/precision`

### POST /api/v1/knowledge-bases/precision/evaluate
运行精度评估  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| kbId | String | ✅ | 知识库ID |
| datasetId | String | ✅ | 评测数据集ID |

响应 `Result<PrecisionReport>`

---

### POST /api/v1/knowledge-bases/precision/optimize
Grid Search 自动调优  
**权限:** `kb:read`

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| kbId | String | ✅ | 知识库ID |
| datasetId | String | ✅ | 评测数据集ID |

响应 `Result<OptimizationResult>`

---

## 17. 安全围栏

**Tag:** `安全围栏` | **Base:** `/api/v1/security` | **权限:** 公开接口

### POST /api/v1/security/sensitive-words/create
创建敏感词规则

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| word | String | ✅ | 敏感词或正则表达式 |
| matchType | String | ✅ | 匹配方式：EXACT/REGEX/SEMANTIC |
| category | String | ✅ | 分类：INJECTION/JAILBREAK/PII/CUSTOM |
| severity | String | ✅ | 严重程度：LOW/MEDIUM/HIGH/BLOCK |
| action | String | ✅ | 动作：LOG/WARN/BLOCK |

---

### POST /api/v1/security/sensitive-words/update
更新敏感词规则

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 规则ID |
| word | String | | 新敏感词 |
| matchType | String | | 新匹配方式 |
| category | String | | 新分类 |
| severity | String | | 新严重程度 |
| action | String | | 新动作 |

---

### POST /api/v1/security/sensitive-words/list
敏感词规则列表

| 参数 | 类型 | 必填 | 默认值 |
|------|------|:--:|:--:|
| page | int | | 0 |
| size | int | | 20 |

---

### POST /api/v1/security/sensitive-words/get
敏感词规则详情

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 规则ID |

---

### POST /api/v1/security/sensitive-words/toggle-status
启用/禁用敏感词规则

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 规则ID |

---

### POST /api/v1/security/sensitive-words/delete
删除敏感词规则

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| id | Long | ✅ | 规则ID |

---

### POST /api/v1/security/events/list
安全事件列表

| 参数 | 类型 | 必填 | 默认值 |
|------|------|:--:|:--:|
| page | int | | 0 |
| size | int | | 20 |

响应 `Result<List<SecurityEventResponse>>`

---

### POST /api/v1/security/events/by-conversation
按会话查询安全事件

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| conversationId | String | ✅ | | 会话ID |
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

---

## 18. 人机协同审批

**Tag:** `人机协同审批` | **Base:** `/api/v1/approvals` | **权限:** 公开接口

### POST /api/v1/approvals/list
审批列表（我的待审批/我已审批/我发起的）

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| filter | String | | my-pending | my-pending / my-resolved / my-requested |
| approverId | String | | | 审批人ID |
| requesterId | String | | | 发起人ID |
| status | String | | PENDING | PENDING/APPROVED/REJECTED/TIMEOUT |
| page | int | | 0 | 页码 |
| size | int | | 20 | 每页数量 |

响应 `Result<List<ApprovalWorkflowResponse>>`

---

### POST /api/v1/approvals/get
审批详情

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| approvalId | String | ✅ | 审批业务ID |

---

### POST /api/v1/approvals/approve
同意审批

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| approvalId | String | ✅ | 审批业务ID |
| comment | String | | 审批意见 |

---

### POST /api/v1/approvals/reject
拒绝审批

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| approvalId | String | ✅ | 审批业务ID |
| reason | String | ✅ | 拒绝原因 |

---

### POST /api/v1/approvals/stats
审批统计

无请求参数。响应 `Result<Map<String, Object>>` — `{ pending, approved, rejected, timeout, total }`

---

## 19. 评测数据集

**Tag:** `评测数据集` | **Base:** `/api/v1/evaluation/datasets` | **权限:** 公开接口

### POST /api/v1/evaluation/datasets/create
创建评测数据集

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| name | String | ✅ | 数据集名称 |
| description | String | | 描述 |
| source | String | | 来源（默认MANUAL） |

---

### POST /api/v1/evaluation/datasets/list
数据集列表

| 参数 | 类型 | 必填 | 默认值 |
|------|------|:--:|:--:|
| page | int | | 1 |
| size | int | | 20 |

---

### POST /api/v1/evaluation/datasets/get
数据集详情

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| datasetId | String | ✅ | 数据集业务ID |

---

### POST /api/v1/evaluation/datasets/delete
删除数据集

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| datasetId | String | ✅ | 数据集业务ID |

---

### POST /api/v1/evaluation/datasets/items/add
添加样本

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| datasetId | String | ✅ | 数据集业务ID |
| itemRequest | DatasetAddItemCommand | | 样本内容 |

**itemRequest 字段:**
| 字段 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| question | String | ✅ | 问题文本 |
| expectedAnswer | String | | 期望答案 |
| retrievalContext | String | | 检索上下文 |
| metadataJson | String | | 元数据JSON |

---

### POST /api/v1/evaluation/datasets/items/list
样本列表

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| datasetId | String | ✅ | 数据集业务ID |

响应 `Result<List<ItemResponse>>`

---

### POST /api/v1/evaluation/datasets/items/delete
删除样本

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| datasetId | String | ✅ | 数据集业务ID |
| itemId | Long | | 样本ID |

---

## 20. 评测执行

**Tag:** `评测执行` | **Base:** `/api/v1/evaluation` | **权限:** 公开接口

### POST /api/v1/evaluation/run
执行评测（LLM-as-Judge）

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| datasetId | String | ✅ | 数据集业务ID |

响应 `Result<EvaluationRunResponse>`

---

### POST /api/v1/evaluation/get
评测结果详情

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| evaluationId | String | ✅ | 评测执行ID |

---

### POST /api/v1/evaluation/list
评测历史列表

| 参数 | 类型 | 必填 | 默认值 |
|------|------|:--:|:--:|
| page | int | | 1 |
| size | int | | 20 |

响应 `Result<List<EvaluationRunResponse>>`

---

## 21. 优化工单

**Tag:** `优化工单` | **Base:** `/api/v1/optimization-tickets` | **权限:** 公开接口

### POST /api/v1/optimization-tickets/list
工单列表

| 参数 | 类型 | 必填 | 默认值 |
|------|------|:--:|:--:|
| page | int | | 1 |
| size | int | | 20 |

响应 `Result<List<OptimizationTicketResponse>>`

---

### POST /api/v1/optimization-tickets/get
工单详情

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| ticketId | String | ✅ | 工单业务ID |

---

### POST /api/v1/optimization-tickets/assign
指派处理人

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| ticketId | String | ✅ | 工单业务ID |
| assignee | String | | 处理人标识 |

---

### POST /api/v1/optimization-tickets/update-status
更新工单状态

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| ticketId | String | ✅ | 工单业务ID |
| status | String | ✅ | 新状态 |

---

### POST /api/v1/optimization-tickets/resolve
提交解决方案

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| ticketId | String | ✅ | 工单业务ID |
| resolution | String | | 解决方案描述 |
| resolutionType | String | | 方案类型 |

---

### POST /api/v1/optimization-tickets/feedback/stats
反馈统计面板

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:--:|:--:|------|
| days | int | | 7 | 统计天数 |

响应 `Result<FeedbackStatsResponse>`

---

## 权限码速查

| 权限码 | 适用模块 | 说明 |
|--------|----------|------|
| `tenant:write` | 租户管理 | 创建/更新租户 |
| `tenant:read` | 租户管理 | 查看租户列表/详情 |
| `user:read` | 用户/角色/权限 | 查看 |
| `user:write` | 用户/角色/权限 | 创建/更新/删除 |
| `conversation:create` | 对话管理 | 创建会话 |
| `conversation:read` | 对话/消息 | 查看会话/消息 |
| `conversation:send` | 消息收发 | 发送消息 |
| `conversation:update` | 对话管理 | 更新标题/状态 |
| `conversation:delete` | 对话管理 | 删除会话 |
| `conversation:feedback` | 消息收发 | 消息反馈 |
| `intent:create` | 意图管理 | 创建意图 |
| `intent:read` | 意图管理 | 查看/测试意图 |
| `intent:update` | 意图管理 | 编辑/启停意图 |
| `intent:delete` | 意图管理 | 删除意图 |
| `prompt:create` | 提示词管理 | 创建模板 |
| `prompt:read` | 提示词管理 | 查看/预览/渲染 |
| `prompt:update` | 提示词管理 | 编辑/归档 |
| `prompt:delete` | 提示词管理 | 删除模板 |
| `prompt:publish` | 提示词管理 | 发布/回滚 |
| `task:create` | 任务规划 | 规划任务 |
| `task:execute` | 任务执行 | 执行/取消任务 |
| `task:read` | 任务执行 | 查看状态/计划/Handler |
| `tool:create` | 工具平台 | 注册工具 |
| `tool:read` | 工具平台 | 查看/测试/版本 |
| `tool:update` | 工具平台 | 编辑/启停/回滚 |
| `kb:create` | 知识库管理 | 创建知识库 |
| `kb:read` | 知识库/检索/精度 | 查看/检索/评估 |
| `kb:update` | 知识库/检索 | 更新/配置/标注 |
| `kb:delete` | 知识库管理 | 删除知识库 |
| `kb:search` | 知识检索 | 执行检索 |
| `doc:upload` | 文档管理 | 上传文档 |
| `doc:read` | 文档/文件管理 | 查看/下载/切片 |
| `doc:update` | 文档管理 | 更新/解析/弃用 |
| `doc:delete` | 文档管理 | 删除文档 |

**角色码:** `TENANT_ADMIN` — 启停/删除租户

---

## 统计概览

| 分类 | Controller 数 | 接口数 |
|------|:--:|:--:|
| 认证 | 1 | 4 |
| 租户/用户/角色/权限 | 4 | 23 |
| 对话/消息 | 2 | 11 |
| 意图/提示词/任务 | 3 | 28 |
| 工具平台 | 1 | 10 |
| 知识库/文档/文件 | 4 | 26 |
| 检索/精度 | 2 | 6 |
| 安全/审批 | 2 | 13 |
| 评测/优化 | 3 | 16 |
| **合计** | **21** | **113** |
