import { create } from 'zustand'
import { getStoredUser, getToken, setStoredUser, setToken } from './api.js'

const THEME_KEY = 'agent.theme'

function readTheme() {
  return localStorage.getItem(THEME_KEY) === 'light' ? 'light' : 'dark'
}

export const useAuthStore = create((set) => ({
  token: getToken(),
  user: getStoredUser(),
  setSession: (login) => {
    setToken(login.token)
    setStoredUser(login)
    set({ token: login.token, user: login })
  },
  logout: () => {
    setToken(null)
    setStoredUser(null)
    set({ token: null, user: null })
  },
}))

export const useThemeStore = create((set, get) => ({
  theme: readTheme(),
  toggleTheme: () => {
    const next = get().theme === 'dark' ? 'light' : 'dark'
    localStorage.setItem(THEME_KEY, next)
    document.documentElement.setAttribute('data-theme', next)
    set({ theme: next })
  },
  applyDomTheme: () => {
    document.documentElement.setAttribute('data-theme', get().theme)
  },
}))
