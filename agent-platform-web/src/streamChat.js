import { getToken } from './api.js'
import { parseSseBlock, splitSseChunks } from './parseSse.js'

async function readSse(res, onEvent, signal) {
  if (!res.ok) {
    let msg = '流式接口不可用'
    try {
      const json = await res.json()
      msg = json.message || msg
    } catch {
      // ignore
    }
    throw new Error(msg)
  }
  if (!res.body) {
    throw new Error('流式接口不可用')
  }
  const reader = res.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''
  try {
    while (true) {
      if (signal?.aborted) {
        await reader.cancel()
        break
      }
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const { blocks, rest } = splitSseChunks(buffer)
      buffer = rest
      for (const block of blocks) {
        onEvent(parseSseBlock(block))
      }
    }
  } catch (e) {
    if (signal?.aborted || e?.name === 'AbortError') {
      return
    }
    throw e
  }
  if (buffer.trim() && !signal?.aborted) {
    onEvent(parseSseBlock(buffer))
  }
}

function authHeaders() {
  const headers = {
    'Content-Type': 'application/json',
    Accept: 'text/event-stream',
  }
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`
  return headers
}

/**
 * @returns {{ promise: Promise<void>, abort: () => void, getStreamId: () => string|null }}
 */
export function streamChat({ conversationId, content, mode, knowledgeId, onEvent }) {
  const controller = new AbortController()
  let streamId = null
  const wrapped = (ev) => {
    if (ev.event === 'stream_started' && ev.data?.streamId) {
      streamId = ev.data.streamId
    }
    onEvent?.(ev)
  }
  const promise = (async () => {
    const res = await fetch('/api/v1/conversations/messages/stream', {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ conversationId, content, mode, knowledgeId }),
      signal: controller.signal,
    })
    await readSse(res, wrapped, controller.signal)
  })()
  return {
    promise,
    abort: () => controller.abort(),
    getStreamId: () => streamId,
  }
}

export function regenerateChat({ conversationId, sourceMessageId, mode, knowledgeId, onEvent }) {
  const controller = new AbortController()
  let streamId = null
  const wrapped = (ev) => {
    if (ev.event === 'stream_started' && ev.data?.streamId) {
      streamId = ev.data.streamId
    }
    onEvent?.(ev)
  }
  const promise = (async () => {
    const res = await fetch('/api/v1/conversations/messages/regenerate', {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ conversationId, sourceMessageId, mode, knowledgeId }),
      signal: controller.signal,
    })
    await readSse(res, wrapped, controller.signal)
  })()
  return {
    promise,
    abort: () => controller.abort(),
    getStreamId: () => streamId,
  }
}
