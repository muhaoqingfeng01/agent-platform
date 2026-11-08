import { useEffect, useState } from 'react'
import { Button, Form, Input, Modal, Select, Space, Table, message } from 'antd'
import { api } from '../../api.js'
import { hasPermission } from '../../auth.js'
import { useAuthStore } from '../../store.js'

export default function RoleList() {
  const tenantId = useAuthStore((s) => s.user?.tenantId)
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [users, setUsers] = useState([])
  const [perms, setPerms] = useState([])
  const [open, setOpen] = useState(false)
  const [assignUserOpen, setAssignUserOpen] = useState(false)
  const [assignPermOpen, setAssignPermOpen] = useState(false)
  const [currentRole, setCurrentRole] = useState(null)
  const [form] = Form.useForm()
  const [assignUserForm] = Form.useForm()
  const [assignPermForm] = Form.useForm()

  const load = async () => {
    setLoading(true)
    try {
      const data = await api.listRoles()
      setRows(data?.records || [])
    } catch (e) {
      message.error(e.message)
    } finally {
      setLoading(false)
    }
  }

  const loadOptions = async () => {
    try {
      const [u, p] = await Promise.all([
        api.listUsers(0, 200),
        api.listPermissions(0, 500),
      ])
      setUsers(Array.isArray(u) ? u : u?.records || [])
      setPerms(p?.records || [])
    } catch (e) {
      message.error(e.message)
    }
  }

  useEffect(() => {
    load()
    loadOptions()
  }, [])

  const columns = [
    { title: '编码', dataIndex: 'roleCode' },
    { title: '名称', dataIndex: 'roleName' },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '操作',
      width: 320,
      render: (_, r) => (
        <Space wrap>
          {hasPermission('user:write') && (
            <>
              <Button
                size="small"
                onClick={() => {
                  setCurrentRole(r)
                  assignUserForm.resetFields()
                  setAssignUserOpen(true)
                }}
              >
                绑用户
              </Button>
              <Button
                size="small"
                onClick={() => {
                  setCurrentRole(r)
                  assignPermForm.resetFields()
                  setAssignPermOpen(true)
                }}
              >
                绑权限
              </Button>
              <Button
                size="small"
                danger
                onClick={() => {
                  Modal.confirm({
                    title: '确认删除角色？',
                    onOk: async () => {
                      await api.deleteRole(r.id)
                      message.success('已删除')
                      load()
                    },
                  })
                }}
              >
                删除
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 12 }}>
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>角色</h3>
        {hasPermission('user:write') && (
          <Button type="primary" onClick={() => setOpen(true)}>
            新建角色
          </Button>
        )}
        <Button onClick={load}>刷新</Button>
      </Space>
      <Table rowKey="id" loading={loading} columns={columns} dataSource={rows} pagination={false} />

      <Modal title="新建角色" open={open} onCancel={() => setOpen(false)} onOk={() => form.submit()} destroyOnClose>
        <Form
          form={form}
          layout="vertical"
          initialValues={{ tenantId }}
          onFinish={async (values) => {
            try {
              await api.createRole({ ...values, tenantId: values.tenantId || tenantId })
              message.success('已创建')
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
          <Form.Item name="roleCode" label="角色编码" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="roleName" label="角色名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`绑定用户 · ${currentRole?.roleName || ''}`}
        open={assignUserOpen}
        onCancel={() => setAssignUserOpen(false)}
        onOk={() => assignUserForm.submit()}
        destroyOnClose
      >
        <Form
          form={assignUserForm}
          layout="vertical"
          onFinish={async (values) => {
            try {
              await api.assignRoleToUser(currentRole.id, values.userId)
              message.success('已绑定（用户将被踢下线）')
              setAssignUserOpen(false)
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item name="userId" label="用户" rules={[{ required: true }]}>
            <Select
              showSearch
              optionFilterProp="label"
              options={users.map((u) => ({
                value: u.userId,
                label: `${u.username} (${u.userId})`,
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`绑定权限 · ${currentRole?.roleName || ''}`}
        open={assignPermOpen}
        onCancel={() => setAssignPermOpen(false)}
        onOk={() => assignPermForm.submit()}
        destroyOnClose
      >
        <Form
          form={assignPermForm}
          layout="vertical"
          onFinish={async (values) => {
            try {
              await api.assignPermissionToRole(currentRole.id, values.permissionId)
              message.success('已绑定权限')
              setAssignPermOpen(false)
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item name="permissionId" label="权限" rules={[{ required: true }]}>
            <Select
              showSearch
              optionFilterProp="label"
              options={perms.map((p) => ({
                value: p.id,
                label: `${p.permissionCode} — ${p.description || ''}`,
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
