import { API_PREFIX } from '@/config/constants';
import { apiClient, unwrapData } from '@/services/apiClient';
import type { ApiResult, ChatMessage, PageResponse } from '@/types/api';

export async function listMessages(
  conversationId: string,
  page = 0,
  size = 50,
): Promise<PageResponse<ChatMessage>> {
  const res = await apiClient.post<ApiResult<PageResponse<ChatMessage>>>(
    `${API_PREFIX}/conversations/messages/list`,
    { id: conversationId, page, size },
  );
  return unwrapData(res.data);
}

export async function loadMessagesBefore(
  conversationId: string,
  before: string,
): Promise<ChatMessage[]> {
  const res = await apiClient.post<ApiResult<{ records: ChatMessage[] }>>(
    `${API_PREFIX}/conversations/messages/before`,
    { id: conversationId, before },
  );
  const data = unwrapData(res.data);
  return data?.records ?? [];
}
