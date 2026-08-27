import { Button, Form, Input, InputNumber, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/useAuthStore';
import { ApiError } from '@/services/apiClient';

interface LoginForm {
  username: string;
  password: string;
  tenantId?: number;
}

export function LoginPage() {
  const login = useAuthStore((s) => s.login);
  const loading = useAuthStore((s) => s.loading);
  const navigate = useNavigate();

  const onFinish = async (values: LoginForm) => {
    try {
      await login(values.username.trim(), values.password, values.tenantId);
      message.success('登录成功');
      navigate('/chat', { replace: true });
    } catch (error) {
      const text = error instanceof ApiError ? error.message : '登录失败';
      message.error(text);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <Typography.Title level={3} style={{ marginTop: 0 }}>
          Agent Platform
        </Typography.Title>
        <Typography.Paragraph type="secondary">企业智能体 Web 聊天</Typography.Paragraph>
        <Form<LoginForm> layout="vertical" onFinish={onFinish} autoComplete="off">
          <Form.Item name="username" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input placeholder="admin" size="large" />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password placeholder="请输入密码" size="large" />
          </Form.Item>
          <Form.Item name="tenantId" label="租户 ID（可选）">
            <InputNumber style={{ width: '100%' }} placeholder="多租户时填写" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block size="large" loading={loading}>
            登录
          </Button>
        </Form>
      </div>
    </div>
  );
}
