import { create } from 'zustand'
import { getToken, setToken } from './api.js'

export const useAuthStore = create((set) => ({
  token: getToken(),
  user: null,
  setSession: (login) => {
    setToken(login.token)
    set({ token: login.token, user: login })
  },
  logout: () => {
    setToken(null)
    set({ token: null, user: null })
  },
}))
