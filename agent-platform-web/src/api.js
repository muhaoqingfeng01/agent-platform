const TOKEN_KEY = 'agent.token'
const USER_KEY = 'agent.user'

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  if (token) sessionStorage.setItem(TOKEN_KEY, token)
  else sessionStorage.removeItem(TOKEN_KEY)
}

export function getStoredUser() {
  try {
    const raw = sessionStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function setStoredUser(user) {
  if (user) sessionStorage.setItem(USER_KEY, JSON.stringify(user))
  else sessionStorage.removeItem(USER_KEY)
}

async function request(path, body, method = 'POST') {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`
  const res = await fetch(path, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  const json = await res.json()
  if (json.code !== 200) {
    throw new Error(json.message || '请求失败')
  }
  return json.data
}

async function uploadMultipart(path, formData) {
  const headers = {}
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`
  const res = await fetch(path, { method: 'POST', headers, body: formData })
  const json = await res.json()
  if (json.code !== 200) {
    throw new Error(json.message || '上传失败')
  }
  return json.data
}

export const api = {
  login: (username, password) => request('/api/v1/auth/login', { username, password }),
  createConversation: (title) => request('/api/v1/conversations/create', { title }),
  listConversations: () => request('/api/v1/conversations/list', { page: 0, size: 50 }),
  updateTitle: (id, title) => request('/api/v1/conversations/update-title', { id, title }),
  deleteConversation: (id) => request('/api/v1/conversations/delete', { id }),
  listMessages: (id) => request('/api/v1/conversations/messages/list', { id, page: 0, size: 50 }),
  feedback: (payload) =>
    request('/api/v1/conversations/messages/feedback', {
      conversationId: payload.conversationId,
      msgId: payload.msgId || payload.messageId,
      feedback: payload.feedback,
      reason: payload.reason,
    }),
  cancelStream: (conversationId, streamId) =>
    request('/api/v1/conversations/messages/cancel', { conversationId, streamId }),
  pendingApprovals: (conversationId) => request('/api/v1/approvals/pending', { conversationId }),
  approve: (approvalId, comment) => request('/api/v1/approvals/approve', { approvalId, comment }),
  reject: (approvalId, reason) => request('/api/v1/approvals/reject', { approvalId, reason }),
  listKnowledgeBases: (page = 0, size = 100) =>
    request('/api/v1/knowledge-bases/list', { page, size }),
  createKnowledgeBase: (payload) => request('/api/v1/knowledge-bases/create', payload),
  getKnowledgeBase: (knowledgeId) => request('/api/v1/knowledge-bases/get', { knowledgeId }),
  enableKnowledgeBase: (knowledgeId) => request('/api/v1/knowledge-bases/enable', { knowledgeId }),
  disableKnowledgeBase: (knowledgeId) => request('/api/v1/knowledge-bases/disable', { knowledgeId }),
  deleteKnowledgeBase: (knowledgeId) => request('/api/v1/knowledge-bases/delete', { knowledgeId }),
  listDocuments: (knowledgeId, page = 0, size = 50) =>
    request('/api/v1/documents/list', { knowledgeId, page, size }),
  parseDocument: (documentId) => request('/api/v1/documents/parse', { documentId }),
  deleteDocument: (documentId) => request('/api/v1/documents/delete', { documentId }),
  getDocumentStatus: (documentId) => request('/api/v1/documents/status', { documentId }),
  uploadDocument: (knowledgeId, file, meta = {}) => {
    const form = new FormData()
    const metadata = {
      knowledgeId,
      chunkStrategy: meta.chunkStrategy,
      chunkSize: meta.chunkSize,
      chunkOverlap: meta.chunkOverlap,
      chunkConfig: meta.chunkConfig,
    }
    form.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }))
    form.append('file', file)
    return uploadMultipart('/api/v1/knowledge-bases/documents/upload', form)
  },
  listIntents: (page = 0, size = 50) => request('/api/v1/intents/list', { page, size }),
  createIntent: (payload) => request('/api/v1/intents/create', payload),
  updateIntent: (payload) => request('/api/v1/intents/update', payload),
  toggleIntentStatus: (id, status) => request('/api/v1/intents/toggle-status', { id, status }),
  deleteIntent: (id) => request('/api/v1/intents/delete', { id }),
  testIntent: (id, input) => request('/api/v1/intents/test', { id, input }),
  listUsers: (page = 0, size = 50) => request('/api/v1/users/list', { page, size }),
  registerUser: (payload) => request('/api/v1/users/register', payload),
  toggleUserStatus: (id, status) => request('/api/v1/users/toggle-status', { id, status }),
  changePassword: (payload) => request('/api/v1/users/change-password', payload),
  listRoles: () => request('/api/v1/roles/list', {}),
  createRole: (payload) => request('/api/v1/roles/create', payload),
  updateRole: (payload) => request('/api/v1/roles/update', payload),
  deleteRole: (id) => request('/api/v1/roles/delete', { id }),
  assignRoleToUser: (roleId, userId) => request('/api/v1/roles/assign-user', { roleId, userId }),
  assignPermissionToRole: (roleId, permissionId) =>
    request('/api/v1/roles/assign-permission', { roleId, permissionId }),
  listPermissions: (page = 0, size = 200) => request('/api/v1/permissions/list', { page, size }),
  listPrompts: (page = 0, size = 100) => request('/api/v1/prompts/list', { page, size }),
  getPrompt: (id) => request('/api/v1/prompts/get', { id }),
  createPrompt: (payload) => request('/api/v1/prompts/create', payload),
  updatePrompt: (payload) => request('/api/v1/prompts/update', payload),
  generatePrompt: (payload) => request('/api/v1/prompts/generate', payload),
  deletePrompt: (id) => request('/api/v1/prompts/delete', { id }),
  publishPrompt: (id) => request('/api/v1/prompts/publish', { id }),
  archivePrompt: (id) => request('/api/v1/prompts/archive', { id }),
  listTools: (page = 0, size = 100, type) =>
    request('/api/v1/tools/list', { page, size, type }),
  getTool: (id) => request('/api/v1/tools/get', { id }),
  createTool: (payload) => request('/api/v1/tools/create', payload),
  updateTool: (payload) => request('/api/v1/tools/update', payload),
  toggleToolStatus: (id, status) => request('/api/v1/tools/toggle-status', { id, status }),
  testTool: (id, params) => request('/api/v1/tools/test', { id, params }),
  generateToolDescription: (payload) => request('/api/v1/tools/generate-description', payload),
  planTask: (payload) => request('/api/v1/tasks/plan', payload),
  getTaskPlan: (executionId) => request('/api/v1/tasks/plan/get', { executionId }),
  executeTask: (executionId, async = true) =>
    request('/api/v1/tasks/execute', { executionId, async }),
  taskStatus: (executionId) => request('/api/v1/tasks/status', { executionId }),
  cancelTask: (executionId) => request('/api/v1/tasks/cancel', { executionId }),
  listTaskHandlers: () => request('/api/v1/tasks/handlers', {}),
}
