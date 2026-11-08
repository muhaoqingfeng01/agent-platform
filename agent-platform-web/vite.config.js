import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

/**
 * 后端地址优先读环境变量，避免端口写死在多处。
 * - VITE_BACKEND_URL  例: http://localhost:8081
 * - VITE_DEV_PORT     前端开发端口，默认 5173
 */
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  // 默认走 127.0.0.1，规避 Windows 上 localhost 双栈导致代理 ECONNREFUSED
  const backendUrl = (env.VITE_BACKEND_URL || 'http://127.0.0.1:8081').replace(/\/$/, '')
  const wsUrl = backendUrl.replace(/^http/i, 'ws')
  const devPort = Number(env.VITE_DEV_PORT || 5173)

  return {
    plugins: [react()],
    server: {
      port: devPort,
      strictPort: true,
      proxy: {
        '/api': {
          target: backendUrl,
          changeOrigin: true,
        },
        '/ws': {
          target: wsUrl,
          ws: true,
          changeOrigin: true,
        },
      },
    },
    test: {
      environment: 'node',
    },
  }
})
