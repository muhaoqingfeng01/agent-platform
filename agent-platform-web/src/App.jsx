import { Navigate, Route, Routes } from 'react-router-dom'
import Chat from './pages/Chat.jsx'
import Login from './pages/Login.jsx'
import AdminLayout from './components/admin/AdminLayout.jsx'
import KnowledgeList from './pages/admin/KnowledgeList.jsx'
import DocumentList from './pages/admin/DocumentList.jsx'
import IntentList from './pages/admin/IntentList.jsx'
import UserList from './pages/admin/UserList.jsx'
import RoleList from './pages/admin/RoleList.jsx'
import { useAuthStore } from './store.js'
import { canAccessAdmin } from './auth.js'

function RequireAuth({ children }) {
  const token = useAuthStore((s) => s.token)
  if (!token) return <Navigate to="/login" replace />
  return children
}

function RequireAdmin({ children }) {
  if (!canAccessAdmin()) return <Navigate to="/chat" replace />
  return children
}

export default function App() {
  const token = useAuthStore((s) => s.token)
  return (
    <Routes>
      <Route path="/login" element={token ? <Navigate to="/chat" replace /> : <Login />} />
      <Route
        path="/chat"
        element={
          <RequireAuth>
            <Chat />
          </RequireAuth>
        }
      />
      <Route
        path="/admin"
        element={
          <RequireAuth>
            <RequireAdmin>
              <AdminLayout />
            </RequireAdmin>
          </RequireAuth>
        }
      >
        <Route index element={<Navigate to="knowledge" replace />} />
        <Route path="knowledge" element={<KnowledgeList />} />
        <Route path="knowledge/:knowledgeId/docs" element={<DocumentList />} />
        <Route path="intents" element={<IntentList />} />
        <Route path="users" element={<UserList />} />
        <Route path="roles" element={<RoleList />} />
      </Route>
      <Route path="*" element={<Navigate to={token ? '/chat' : '/login'} replace />} />
    </Routes>
  )
}
