import { API_PREFIX } from '@/config/constants';
import { apiClient, unwrapData } from '@/services/apiClient';
import type { ApiResult, Conversation, PageResponse } from '@/types/api';

export async function listConversations(page = 0, size = 50): Promise<PageResponse<Conversation>> {
  const res = await apiClient.post<ApiResult<PageResponse<Conversation>>>(
    `${API_PREFIX}/conversations/list`,
    { page, size },
  );
  return unwrapData(res.data);
}

export async function createConversation(title?: string, agentId?: string): Promise<Conversation> {
  const res = await apiClient.post<ApiResult<Conversation>>(`${API_PREFIX}/conversations/create`, {
    title: title || '新对话',
    agentId,
  });
  return unwrapData(res.data);
}

export async function getConversation(id: string): Promise<Conversation> {
  const res = await apiClient.post<ApiResult<Conversation>>(`${API_PREFIX}/conversations/get`, { id });
  return unwrapData(res.data);
}

export async function updateConversationTitle(id: string, title: string): Promise<void> {
  await apiClient.post(`${API_PREFIX}/conversations/update-title`, { id, title });
}

export async function deleteConversation(id: string): Promise<void> {
  await apiClient.post(`${API_PREFIX}/conversations/delete`, { id });
}
