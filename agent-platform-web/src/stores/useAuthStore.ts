import { create } from 'zustand';
import { STORAGE_KEYS } from '@/config/constants';
import { loginApi, logoutApi, meApi } from '@/services/auth';
import type { UserInfo } from '@/types/api';

interface AuthState {
  token: string | null;
  refreshToken: string | null;
  user: UserInfo | null;
  loading: boolean;
  hasPermission: (code: string) => boolean;
  hydrate: () => Promise<void>;
  login: (username: string, password: string, tenantId?: number) => Promise<void>;
  logout: () => Promise<void>;
}

function persistSession(token: string, refreshToken?: string, userId?: string) {
  sessionStorage.setItem(STORAGE_KEYS.token, token);
  if (refreshToken) {
    sessionStorage.setItem(STORAGE_KEYS.refreshToken, refreshToken);
  }
  if (userId) {
    sessionStorage.setItem(STORAGE_KEYS.userId, userId);
  }
}

function clearSession() {
  sessionStorage.removeItem(STORAGE_KEYS.token);
  sessionStorage.removeItem(STORAGE_KEYS.refreshToken);
  sessionStorage.removeItem(STORAGE_KEYS.userId);
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: sessionStorage.getItem(STORAGE_KEYS.token),
  refreshToken: sessionStorage.getItem(STORAGE_KEYS.refreshToken),
  user: null,
  loading: false,

  hasPermission: (code: string) => {
    const permissions = get().user?.permissions ?? [];
    return permissions.includes(code);
  },

  hydrate: async () => {
    const token = sessionStorage.getItem(STORAGE_KEYS.token);
    if (!token) {
      set({ token: null, user: null });
      return;
    }
    set({ loading: true, token });
    try {
      const user = await meApi();
      set({ user, loading: false });
    } catch {
      clearSession();
      set({ token: null, refreshToken: null, user: null, loading: false });
    }
  },

  login: async (username, password, tenantId) => {
    set({ loading: true });
    try {
      const result = await loginApi({ username, password, tenantId });
      persistSession(result.token, result.refreshToken, result.userId);
      const user: UserInfo = {
        userId: result.userId || '',
        username: result.username || username,
        tenantId: result.tenantId,
        roles: result.roles ?? [],
        permissions: result.permissions ?? [],
      };
      set({
        token: result.token,
        refreshToken: result.refreshToken,
        user,
        loading: false,
      });
      if (!user.permissions?.length) {
        try {
          const me = await meApi();
          set({ user: me });
        } catch {
          /* 登录已成功，权限稍后再拉 */
        }
      }
    } catch (error) {
      set({ loading: false });
      throw error;
    }
  },

  logout: async () => {
    try {
      await logoutApi();
    } catch {
      /* 本地仍清理 */
    }
    clearSession();
    set({ token: null, refreshToken: null, user: null });
  },
}));
