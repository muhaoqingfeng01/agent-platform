import { useEffect, useState } from 'react'
import { Button, Form, Input, Modal, Space, Table, Tag, message } from 'antd'
import { api } from '../../api.js'
import { hasPermission } from '../../auth.js'
import { useAuthStore } from '../../store.js'

export default function UserList() {
  const tenantId = useAuthStore((s) => s.user?.tenantId)
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const load = async () => {
    setLoading(true)
    try {
      const data = await api.listUsers(0, 100)
      setRows(Array.isArray(data) ? data : data?.records || [])
    } catch (e) {
      message.error(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const columns = [
    { title: '用户名', dataIndex: 'username' },
    { title: 'userId', dataIndex: 'userId', ellipsis: true },
    { title: '邮箱', dataIndex: 'email' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (v) => <Tag color={v === 'ACTIVE' || v === 'ENABLED' ? 'green' : 'default'}>{v}</Tag>,
    },
    {
      title: '操作',
      width: 160,
      render: (_, r) =>
        hasPermission('user:write') ? (
          <Button
            size="small"
            onClick={async () => {
              try {
                const next =
                  r.status === 'ACTIVE' || r.status === 'ENABLED' ? 'DISABLED' : 'ACTIVE'
                await api.toggleUserStatus(r.id, next)
                message.success('状态已更新')
                load()
              } catch (e) {
                message.error(e.message)
              }
            }}
          >
            {r.status === 'ACTIVE' || r.status === 'ENABLED' ? '停用' : '启用'}
          </Button>
        ) : null,
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 12 }}>
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>用户</h3>
        {hasPermission('user:write') && (
          <Button type="primary" onClick={() => setOpen(true)}>
            注册用户
          </Button>
        )}
        <Button onClick={load}>刷新</Button>
      </Space>
      <Table rowKey="id" loading={loading} columns={columns} dataSource={rows} pagination={false} />

      <Modal title="注册用户" open={open} onCancel={() => setOpen(false)} onOk={() => form.submit()} destroyOnClose>
        <Form
          form={form}
          layout="vertical"
          initialValues={{ tenantId }}
          onFinish={async (values) => {
            try {
              await api.registerUser({
                ...values,
                tenantId: values.tenantId || tenantId,
              })
              message.success('已注册')
              setOpen(false)
              form.resetFields()
              load()
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item name="tenantId" label="租户 ID" rules={[{ required: true }]}>
            <Input disabled={tenantId != null} />
          </Form.Item>
          <Form.Item name="username" label="用户名" rules={[{ required: true, min: 3 }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, min: 8 }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="手机">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
