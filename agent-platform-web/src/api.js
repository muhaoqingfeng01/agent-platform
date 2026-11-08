const TOKEN_KEY = 'agent.token'

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  if (token) sessionStorage.setItem(TOKEN_KEY, token)
  else sessionStorage.removeItem(TOKEN_KEY)
}

async function request(path, body) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`
  const res = await fetch(path, {
    method: 'POST',
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  const json = await res.json()
  if (json.code !== 200) {
    throw new Error(json.message || '请求失败')
  }
  return json.data
}

export const api = {
  login: (username, password) => request('/api/v1/auth/login', { username, password }),
  createConversation: (title) => request('/api/v1/conversations/create', { title }),
  listConversations: () => request('/api/v1/conversations/list', { page: 0, size: 50 }),
  listMessages: (id) => request('/api/v1/conversations/messages/list', { id, page: 0, size: 50 }),
  feedback: (payload) => request('/api/v1/conversations/messages/feedback', payload),
  pendingApprovals: (conversationId) => request('/api/v1/approvals/pending', { conversationId }),
  approve: (approvalId, comment) => request('/api/v1/approvals/approve', { approvalId, comment }),
  reject: (approvalId, reason) => request('/api/v1/approvals/reject', { approvalId, reason }),
}
