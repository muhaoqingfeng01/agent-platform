/**
 * 解析 Spring SseEmitter 文本：兼容 `event:` 行与 data JSON 内 event 字段。
 */
export function parseSseBlock(block) {
  const lines = block.split(/\r?\n/)
  let event = 'message'
  const dataLines = []
  for (const line of lines) {
    if (line.startsWith('event:')) {
      event = line.slice(6).trim()
    } else if (line.startsWith('data:')) {
      dataLines.push(line.slice(5).trimStart())
    }
  }
  const dataText = dataLines.join('\n')
  let data = dataText
  try {
    data = dataText ? JSON.parse(dataText) : null
    if (data && typeof data === 'object' && data.event) {
      event = data.event
    }
  } catch {
    // 保持原文
  }
  return { event, data }
}

export function splitSseChunks(buffer) {
  const parts = buffer.split(/\r?\n\r?\n/)
  const rest = parts.pop() ?? ''
  return { blocks: parts.filter((p) => p.trim()), rest }
}
