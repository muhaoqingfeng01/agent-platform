import { Layout, Menu, Button, Space, Typography } from 'antd'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore, useThemeStore } from '../../store.js'
import { canAccessAdmin, hasAnyPermission } from '../../auth.js'

const { Header, Sider, Content } = Layout

function buildMenuItems() {
  const items = []
  if (hasAnyPermission(['kb:read', 'kb:create', 'doc:read'])) {
    items.push({ key: '/admin/knowledge', label: <Link to="/admin/knowledge">知识库</Link> })
  }
  if (hasAnyPermission(['intent:read', 'intent:create'])) {
    items.push({ key: '/admin/intents', label: <Link to="/admin/intents">意图</Link> })
  }
  if (hasAnyPermission(['user:read', 'user:write'])) {
    items.push({ key: '/admin/users', label: <Link to="/admin/users">用户</Link> })
    items.push({ key: '/admin/roles', label: <Link to="/admin/roles">角色</Link> })
  }
  return items
}

export default function AdminLayout() {
  const location = useLocation()
  const navigate = useNavigate()
  const logout = useAuthStore((s) => s.logout)
  const theme = useThemeStore((s) => s.theme)
  const toggleTheme = useThemeStore((s) => s.toggleTheme)
  const items = buildMenuItems()

  const selected = items
    .map((i) => i.key)
    .find((k) => location.pathname === k || location.pathname.startsWith(`${k}/`))

  if (!canAccessAdmin()) {
    return (
      <div style={{ padding: 48, textAlign: 'center' }}>
        <Typography.Title level={4}>无管理后台权限</Typography.Title>
        <Button type="primary" onClick={() => navigate('/chat')}>
          返回聊天
        </Button>
      </div>
    )
  }

  return (
    <Layout style={{ minHeight: '100%' }}>
      <Sider
        theme="light"
        width={200}
        style={{ background: 'var(--ap-bg-elevated)', borderRight: '1px solid var(--ap-border)' }}
      >
        <div style={{ padding: '16px 16px 8px', color: 'var(--ap-text)', fontWeight: 600 }}>管理后台</div>
        <Menu mode="inline" selectedKeys={selected ? [selected] : []} items={items} style={{ border: 0 }} />
      </Sider>
      <Layout>
        <Header
          style={{
            background: 'var(--ap-bg-elevated)',
            borderBottom: '1px solid var(--ap-border)',
            padding: '0 16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'flex-end',
          }}
        >
          <Space>
            <Button size="small" onClick={() => navigate('/chat')}>
              返回聊天
            </Button>
            <Button size="small" onClick={toggleTheme}>
              {theme === 'dark' ? '浅色' : '深色'}
            </Button>
            <Button
              size="small"
              onClick={() => {
                logout()
                navigate('/login')
              }}
            >
              退出
            </Button>
          </Space>
        </Header>
        <Content style={{ padding: 16, background: 'var(--ap-bg)' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
