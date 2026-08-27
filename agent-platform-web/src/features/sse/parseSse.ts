/**
 * 解析 fetch ReadableStream 中的 SSE 帧。
 * 后端使用 Spring SseEmitter：event: token / thinking / references / done / error / ping
 */

export interface SseFrame {
  id?: string;
  event: string;
  data: unknown;
  raw: string;
}

export function parseSseBlock(block: string): SseFrame | null {
  const trimmed = block.replace(/^\uFEFF/, '').trim();
  if (!trimmed) {
    return null;
  }

  let event = 'message';
  let id: string | undefined;
  const dataLines: string[] = [];

  for (const rawLine of trimmed.split(/\r?\n/)) {
    const line = rawLine.trimEnd();
    if (!line || line.startsWith(':')) {
      continue;
    }
    if (line.startsWith('event:')) {
      event = line.slice(6).trim();
      continue;
    }
    if (line.startsWith('id:')) {
      id = line.slice(3).trim();
      continue;
    }
    if (line.startsWith('data:')) {
      let payload = line.slice(5);
      if (payload.startsWith(' ')) {
        payload = payload.slice(1);
      }
      dataLines.push(payload);
    }
  }

  const raw = dataLines.join('\n');
  let data: unknown = raw;
  if (raw.length > 0) {
    try {
      data = JSON.parse(raw);
    } catch {
      data = raw;
    }
  }

  if (typeof data === 'object' && data !== null && 'event' in data && event === 'message') {
    const obj = data as { event?: string; data?: unknown };
    if (typeof obj.event === 'string') {
      event = obj.event;
      data = obj.data ?? data;
    }
  }

  return { id, event, data, raw };
}

export function splitSseChunks(buffer: string): { frames: SseFrame[]; rest: string } {
  const normalized = buffer.replace(/\r\n/g, '\n');
  const parts = normalized.split('\n\n');
  const rest = parts.pop() ?? '';
  const frames: SseFrame[] = [];
  for (const part of parts) {
    const frame = parseSseBlock(part);
    if (frame) {
      frames.push(frame);
    }
  }
  return { frames, rest };
}

export async function consumeSseStream(
  stream: ReadableStream<Uint8Array>,
  onEvent: (frame: SseFrame) => void,
  signal?: AbortSignal,
): Promise<void> {
  const reader = stream.getReader();
  const decoder = new TextDecoder('utf-8');
  let buffer = '';

  try {
    while (true) {
      if (signal?.aborted) {
        await reader.cancel();
        break;
      }
      const { done, value } = await reader.read();
      if (done) {
        break;
      }
      buffer += decoder.decode(value, { stream: true });
      const split = splitSseChunks(buffer);
      buffer = split.rest;
      split.frames.forEach(onEvent);
    }
    buffer += decoder.decode();
    if (buffer.trim()) {
      const last = parseSseBlock(buffer);
      if (last) {
        onEvent(last);
      }
    }
  } finally {
    reader.releaseLock();
  }
}
