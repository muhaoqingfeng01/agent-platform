import { API_PREFIX, getApiBaseUrl, STORAGE_KEYS } from '@/config/constants';
import { consumeSseStream, type SseFrame } from '@/features/sse/parseSse';
import type { InteractionMode } from '@/types/api';

export interface StreamChatPayload {
  conversationId: string;
  content: string;
  mode?: InteractionMode;
  knowledgeId?: string;
}

export async function streamChat(
  payload: StreamChatPayload,
  onEvent: (frame: SseFrame) => void,
  signal?: AbortSignal,
): Promise<void> {
  const token = sessionStorage.getItem(STORAGE_KEYS.token);
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'text/event-stream',
  };
  if (token) {
    headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
  }

  const response = await fetch(`${getApiBaseUrl()}${API_PREFIX}/conversations/messages/stream`, {
    method: 'POST',
    headers,
    body: JSON.stringify(payload),
    signal,
  });

  const contentType = response.headers.get('content-type') || '';
  if (!response.ok || !contentType.includes('text/event-stream')) {
    let message = `流式请求失败 (${response.status})`;
    try {
      const json = (await response.json()) as { message?: string; code?: number };
      message = json.message || message;
      if (json.code === 401 || response.status === 401) {
        sessionStorage.removeItem(STORAGE_KEYS.token);
        window.location.href = '/login';
      }
    } catch {
      /* 非 JSON */
    }
    throw new Error(message);
  }

  if (!response.body) {
    throw new Error('浏览器未返回可读流');
  }

  await consumeSseStream(response.body, onEvent, signal);
}

export function asText(data: unknown): string {
  if (data == null) {
    return '';
  }
  if (typeof data === 'string') {
    return data;
  }
  if (typeof data === 'object' && data !== null && 'message' in data) {
    const msg = (data as { message?: unknown }).message;
    if (typeof msg === 'string') {
      return msg;
    }
  }
  return JSON.stringify(data);
}
