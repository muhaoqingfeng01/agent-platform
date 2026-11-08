import { Button, Card, Form, Input, Typography, message } from 'antd'
import { api } from '../api.js'
import { useAuthStore } from '../store.js'

export default function Login() {
  const setSession = useAuthStore((s) => s.setSession)

  const onFinish = async (values) => {
    try {
      const data = await api.login(values.username, values.password)
      setSession(data)
    } catch (e) {
      message.error(e.message)
    }
  }

  return (
    <div style={{ minHeight: '100vh', display: 'grid', placeItems: 'center' }}>
      <Card title="Agent Platform 登录" style={{ width: 360 }}>
        <Typography.Paragraph type="secondary">
          使用后台账号登录，进入对话、审批与反馈。
        </Typography.Paragraph>
        <Form layout="vertical" onFinish={onFinish} initialValues={{ username: 'admin' }}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>
            登录
          </Button>
        </Form>
      </Card>
    </div>
  )
}
