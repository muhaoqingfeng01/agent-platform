import axios, { type AxiosError } from 'axios';
import { getApiBaseUrl, STORAGE_KEYS } from '@/config/constants';
import type { ApiResult } from '@/types/api';

export class ApiError extends Error {
  code: number;

  constructor(code: number, message: string) {
    super(message);
    this.code = code;
    this.name = 'ApiError';
  }
}

export const apiClient = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: 30_000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = sessionStorage.getItem(STORAGE_KEYS.token);
  if (token) {
    config.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult<unknown> | undefined;
    if (body && typeof body.code === 'number' && body.code !== 200) {
      if (body.code === 401) {
        clearSessionAndRedirect();
      }
      return Promise.reject(new ApiError(body.code, body.message || '请求失败'));
    }
    return response;
  },
  (error: AxiosError<ApiResult<unknown>>) => {
    const status = error.response?.status;
    const body = error.response?.data;
    const code = typeof body?.code === 'number' ? body.code : (status ?? 500);
    let message = body?.message;
    if (!message) {
      if (!error.response || status === 502 || status === 503 || status === 504) {
        message = '无法连接后端，请确认服务已在 8080 启动';
      } else if (status === 500) {
        message = '后端暂不可用，请稍后重试';
      } else {
        message = error.message || '网络错误';
      }
    }
    if (code === 401 || status === 401) {
      clearSessionAndRedirect();
    }
    return Promise.reject(new ApiError(code, message));
  },
);

export function unwrapData<T>(result: ApiResult<T> | undefined): T {
  if (!result) {
    throw new ApiError(500, '空响应');
  }
  if (result.code !== 200) {
    throw new ApiError(result.code, result.message || '请求失败');
  }
  return result.data;
}

function clearSessionAndRedirect() {
  sessionStorage.removeItem(STORAGE_KEYS.token);
  sessionStorage.removeItem(STORAGE_KEYS.refreshToken);
  sessionStorage.removeItem(STORAGE_KEYS.userId);
  if (window.location.pathname !== '/login') {
    window.location.href = '/login';
  }
}
