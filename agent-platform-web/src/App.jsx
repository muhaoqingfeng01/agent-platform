import Chat from './pages/Chat.jsx'
import Login from './pages/Login.jsx'
import { useAuthStore } from './store.js'

export default function App() {
  const token = useAuthStore((s) => s.token)
  return token ? <Chat /> : <Login />
}
