import { getToken } from './api.js'
import { parseSseBlock, splitSseChunks } from './parseSse.js'

export async function streamChat({ conversationId, content, mode, knowledgeId, onEvent }) {
  const headers = {
    'Content-Type': 'application/json',
    Accept: 'text/event-stream',
  }
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`

  const res = await fetch('/api/v1/conversations/messages/stream', {
    method: 'POST',
    headers,
    body: JSON.stringify({ conversationId, content, mode, knowledgeId }),
  })
  if (!res.ok || !res.body) {
    throw new Error('流式接口不可用')
  }

  const reader = res.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    const { blocks, rest } = splitSseChunks(buffer)
    buffer = rest
    for (const block of blocks) {
      onEvent(parseSseBlock(block))
    }
  }
  if (buffer.trim()) {
    onEvent(parseSseBlock(buffer))
  }
}
