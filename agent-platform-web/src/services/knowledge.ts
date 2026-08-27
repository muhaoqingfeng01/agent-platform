import { API_PREFIX } from '@/config/constants';
import { apiClient, unwrapData } from '@/services/apiClient';
import type { ApiResult, KnowledgeBase, PageResponse } from '@/types/api';

export async function listKnowledgeBases(page = 0, size = 50): Promise<PageResponse<KnowledgeBase>> {
  const res = await apiClient.post<ApiResult<PageResponse<KnowledgeBase>>>(
    `${API_PREFIX}/knowledge-bases/list`,
    { page, size },
  );
  return unwrapData(res.data);
}
