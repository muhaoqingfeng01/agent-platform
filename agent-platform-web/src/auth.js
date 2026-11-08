import { useAuthStore } from './store.js'

/** 是否拥有任一权限码 */
export function hasAnyPermission(codes = []) {
  const perms = useAuthStore.getState().user?.permissions || []
  if (!codes.length) return true
  return codes.some((c) => perms.includes(c))
}

export function hasPermission(code) {
  return hasAnyPermission([code])
}

/** 是否可进入管理后台 */
export function canAccessAdmin() {
  return hasAnyPermission([
    'kb:read',
    'kb:create',
    'doc:read',
    'doc:upload',
    'intent:read',
    'intent:create',
    'user:read',
    'user:write',
  ])
}
