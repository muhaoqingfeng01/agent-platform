/** 后端统一响应 Result<T> */
export interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
  timestamp?: number;
}

export interface PageResponse<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  tokenType?: string;
  expiresIn?: number;
  userId?: string;
  username?: string;
  tenantId?: number;
  roles?: string[];
  permissions?: string[];
}

export interface UserInfo {
  userId: string;
  username: string;
  tenantId?: number;
  roles?: string[];
  permissions?: string[];
}

export interface Conversation {
  conversationId: string;
  agentId?: string;
  userId?: string;
  title: string;
  status: string;
  messageCount?: number;
  totalTokens?: number;
  createdAt?: number;
}

export interface ChatMessage {
  messageId: string;
  conversationId: string;
  role: 'USER' | 'ASSISTANT' | 'SYSTEM' | 'TOOL' | string;
  content: string;
  tokenCount?: number;
  feedback?: string | null;
  createdAt?: number;
  thinking?: string;
  references?: KnowledgeReference[];
  streaming?: boolean;
  error?: string;
}

export interface KnowledgeBase {
  knowledgeId: string;
  name: string;
  description?: string;
  status?: string;
  statusLabel?: string;
}

export interface KnowledgeReference {
  documentId?: string;
  filename?: string;
  fileType?: string;
  fileSize?: number;
  downloadUrl?: string;
  previewUrl?: string;
  uploadedAt?: number;
}

export type InteractionMode = 'CONVERSATION' | 'KNOWLEDGE_SEARCH';
