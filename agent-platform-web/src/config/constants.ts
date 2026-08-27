export const API_PREFIX = '/api/v1';

export const STORAGE_KEYS = {
  token: 'ap.token',
  refreshToken: 'ap.refreshToken',
  userId: 'ap.userId',
} as const;

export const PERMISSIONS = {
  kbRead: 'kb:read',
  conversationCreate: 'conversation:create',
  conversationRead: 'conversation:read',
  conversationSend: 'conversation:send',
} as const;

export const CHAT_CONFIG = {
  maxMessageLength: 8000,
  pageSize: 50,
  streamTimeoutMs: 300_000,
};

/** 开发环境走 Vite 代理；生产可设 VITE_API_BASE_URL */
export function getApiBaseUrl(): string {
  const fromEnv = import.meta.env.VITE_API_BASE_URL;
  if (fromEnv && fromEnv.length > 0) {
    return fromEnv.replace(/\/$/, '');
  }
  return '';
}
