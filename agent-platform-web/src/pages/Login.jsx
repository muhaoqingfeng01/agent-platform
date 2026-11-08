import { Button, Form, Input, Typography, message } from 'antd'
import { useNavigate } from 'react-router-dom'
import { api } from '../api.js'
import { useAuthStore } from '../store.js'

export default function Login() {
  const setSession = useAuthStore((s) => s.setSession)
  const navigate = useNavigate()

  const onFinish = async (values) => {
    try {
      const data = await api.login(values.username, values.password)
      setSession(data)
      navigate('/chat', { replace: true })
    } catch (e) {
      message.error(e.message)
    }
  }

  return (
    <div className="login-hero">
      <div className="login-card">
        <Typography.Title level={3} style={{ marginTop: 0, color: 'var(--ap-text)' }}>
          Agent Platform
        </Typography.Title>
        <Typography.Paragraph style={{ color: 'var(--ap-text-muted)' }}>
          企业级 AI Agent · 对话、知识检索与人机协同
        </Typography.Paragraph>
        <Form layout="vertical" onFinish={onFinish} initialValues={{ username: 'admin' }}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input size="large" />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true }]}>
            <Input.Password size="large" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block size="large">
            登录
          </Button>
        </Form>
      </div>
    </div>
  )
}
