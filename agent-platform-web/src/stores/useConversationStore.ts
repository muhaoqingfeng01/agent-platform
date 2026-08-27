import { create } from 'zustand';
import { asText, streamChat } from '@/features/sse/streamChat';
import {
  createConversation,
  deleteConversation,
  listConversations,
  updateConversationTitle,
} from '@/services/conversation';
import { listKnowledgeBases } from '@/services/knowledge';
import { listMessages } from '@/services/message';
import type {
  ChatMessage,
  Conversation,
  InteractionMode,
  KnowledgeBase,
  KnowledgeReference,
} from '@/types/api';

interface ConversationState {
  conversations: Conversation[];
  currentId: string | null;
  messagesByConv: Record<string, ChatMessage[]>;
  knowledgeBases: KnowledgeBase[];
  mode: InteractionMode;
  knowledgeId?: string;
  streaming: boolean;
  streamError: string | null;
  loadingList: boolean;
  loadingMessages: boolean;
  loadConversations: () => Promise<void>;
  loadKnowledgeBases: () => Promise<void>;
  createNew: () => Promise<string>;
  select: (id: string) => Promise<void>;
  remove: (id: string) => Promise<void>;
  setMode: (mode: InteractionMode) => void;
  setKnowledgeId: (id?: string) => void;
  send: (content: string) => Promise<void>;
  stop: () => void;
  reconnect: () => void;
}

function localId(prefix: string) {
  return `${prefix}_${Date.now()}_${Math.random().toString(16).slice(2)}`;
}

function titleFromContent(content: string): string {
  const compact = content.replace(/\s+/g, ' ').trim();
  return compact.length > 24 ? `${compact.slice(0, 24)}…` : compact || '新对话';
}

let activeAbort: AbortController | null = null;

function isAbortError(error: unknown): boolean {
  return (
    (error instanceof DOMException && error.name === 'AbortError') ||
    (error instanceof Error && error.name === 'AbortError')
  );
}

export const useConversationStore = create<ConversationState>((set, get) => ({
  conversations: [],
  currentId: null,
  messagesByConv: {},
  knowledgeBases: [],
  mode: 'CONVERSATION',
  knowledgeId: undefined,
  streaming: false,
  streamError: null,
  loadingList: false,
  loadingMessages: false,

  loadConversations: async () => {
    set({ loadingList: true });
    try {
      const page = await listConversations(0, 50);
      set({ conversations: page.records ?? [], loadingList: false });
    } catch (error) {
      set({ loadingList: false });
      throw error;
    }
  },

  loadKnowledgeBases: async () => {
    try {
      const page = await listKnowledgeBases(0, 50);
      set({ knowledgeBases: page.records ?? [] });
    } catch {
      set({ knowledgeBases: [] });
    }
  },

  createNew: async () => {
    const created = await createConversation('新对话');
    set((state) => ({
      conversations: [created, ...state.conversations],
      currentId: created.conversationId,
      messagesByConv: { ...state.messagesByConv, [created.conversationId]: [] },
      streamError: null,
    }));
    return created.conversationId;
  },

  select: async (id: string) => {
    set({ currentId: id, loadingMessages: true, streamError: null });
    try {
      const page = await listMessages(id, 0, 50);
      const records = [...(page.records ?? [])].sort(
        (a, b) => (a.createdAt ?? 0) - (b.createdAt ?? 0),
      );
      set((state) => ({
        loadingMessages: false,
        messagesByConv: { ...state.messagesByConv, [id]: records },
      }));
    } catch (error) {
      set({ loadingMessages: false });
      throw error;
    }
  },

  remove: async (id: string) => {
    await deleteConversation(id);
    set((state) => {
      const next = { ...state.messagesByConv };
      delete next[id];
      const conversations = state.conversations.filter((item) => item.conversationId !== id);
      const currentId = state.currentId === id ? conversations[0]?.conversationId ?? null : state.currentId;
      return { conversations, messagesByConv: next, currentId };
    });
  },

  setMode: (mode) => set({ mode }),
  setKnowledgeId: (knowledgeId) => set({ knowledgeId }),

  send: async (content: string) => {
    const state = get();
    if (state.streaming) {
      return;
    }
    let conversationId = state.currentId;
    if (!conversationId) {
      conversationId = await get().createNew();
    }

    const existing = get().messagesByConv[conversationId] ?? [];
    const conv = get().conversations.find((item) => item.conversationId === conversationId);
    const shouldRename =
      existing.filter((item) => item.role === 'USER').length === 0 &&
      (!conv?.title || conv.title === '新对话');

    const userMsg: ChatMessage = {
      messageId: localId('local_user'),
      conversationId,
      role: 'USER',
      content,
      createdAt: Date.now(),
    };
    const assistantMsg: ChatMessage = {
      messageId: localId('local_assistant'),
      conversationId,
      role: 'ASSISTANT',
      content: '',
      thinking: '',
      references: [],
      streaming: true,
      createdAt: Date.now(),
    };

    set((prev) => ({
      streaming: true,
      streamError: null,
      currentId: conversationId,
      messagesByConv: {
        ...prev.messagesByConv,
        [conversationId!]: [...(prev.messagesByConv[conversationId!] ?? []), userMsg, assistantMsg],
      },
    }));

    if (shouldRename) {
      const title = titleFromContent(content);
      void updateConversationTitle(conversationId, title).catch(() => undefined);
      set((prev) => ({
        conversations: prev.conversations.map((item) =>
          item.conversationId === conversationId ? { ...item, title } : item,
        ),
      }));
    }

    const patchAssistant = (updater: (msg: ChatMessage) => ChatMessage) => {
      set((prev) => {
        const list = [...(prev.messagesByConv[conversationId!] ?? [])];
        const index = list.findIndex((item) => item.messageId === assistantMsg.messageId);
        if (index >= 0) {
          list[index] = updater(list[index]);
        }
        return {
          messagesByConv: { ...prev.messagesByConv, [conversationId!]: list },
        };
      });
    };

    try {
      activeAbort?.abort();
      activeAbort = new AbortController();
      await streamChat(
        {
          conversationId,
          content,
          mode: state.mode,
          knowledgeId: state.mode === 'KNOWLEDGE_SEARCH' ? state.knowledgeId : undefined,
        },
        (frame) => {
          if (frame.event === 'ping') {
            return;
          }
          if (frame.event === 'thinking') {
            patchAssistant((msg) => ({ ...msg, thinking: asText(frame.data) }));
            return;
          }
          if (frame.event === 'token') {
            const token = asText(frame.data);
            patchAssistant((msg) => ({ ...msg, content: `${msg.content}${token}` }));
            return;
          }
          if (frame.event === 'references') {
            const refs = Array.isArray(frame.data)
              ? (frame.data as KnowledgeReference[])
              : [];
            patchAssistant((msg) => ({ ...msg, references: refs }));
            return;
          }
          if (frame.event === 'done') {
            const data = (frame.data || {}) as { messageId?: string };
            patchAssistant((msg) => ({
              ...msg,
              streaming: false,
              messageId: data.messageId || msg.messageId,
            }));
            return;
          }
          if (frame.event === 'error') {
            const message = asText(frame.data) || '流式输出失败';
            patchAssistant((msg) => ({ ...msg, streaming: false, error: message }));
            set({ streamError: message });
          }
        },
        activeAbort.signal,
      );
      activeAbort = null;
      patchAssistant((msg) => ({ ...msg, streaming: false }));
      set({ streaming: false });
    } catch (error) {
      activeAbort = null;
      if (isAbortError(error)) {
        patchAssistant((msg) => ({
          ...msg,
          streaming: false,
          content: msg.content || '（已停止生成）',
        }));
        set({ streaming: false, streamError: null });
        return;
      }
      const message = error instanceof Error ? error.message : '连接中断';
      patchAssistant((msg) => ({ ...msg, streaming: false, error: message }));
      set({ streaming: false, streamError: message });
    }
  },

  stop: () => {
    activeAbort?.abort();
    activeAbort = null;
    set((prev) => ({ streaming: false, streamError: prev.streamError }));
  },

  reconnect: () => {
    set({ streamError: null, streaming: false });
  },
}));
