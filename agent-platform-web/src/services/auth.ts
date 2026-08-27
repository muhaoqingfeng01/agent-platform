import { API_PREFIX } from '@/config/constants';
import { apiClient, unwrapData } from '@/services/apiClient';
import type { ApiResult, LoginResponse, UserInfo } from '@/types/api';

export async function loginApi(payload: {
  username: string;
  password: string;
  tenantId?: number;
}): Promise<LoginResponse> {
  const res = await apiClient.post<ApiResult<LoginResponse>>(`${API_PREFIX}/auth/login`, payload);
  return unwrapData(res.data);
}

export async function logoutApi(): Promise<void> {
  await apiClient.post(`${API_PREFIX}/auth/logout`);
}

export async function meApi(): Promise<UserInfo> {
  const res = await apiClient.post<ApiResult<UserInfo>>(`${API_PREFIX}/auth/me`);
  return unwrapData(res.data);
}

export async function refreshApi(userId: string, refreshToken: string): Promise<LoginResponse> {
  const res = await apiClient.post<ApiResult<LoginResponse>>(`${API_PREFIX}/auth/refresh`, {
    userId,
    refreshToken,
  });
  return unwrapData(res.data);
}
