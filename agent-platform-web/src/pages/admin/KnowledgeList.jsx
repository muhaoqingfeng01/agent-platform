import { useEffect, useState } from 'react'
import { Button, Form, Input, Modal, Space, Table, Tag, message } from 'antd'
import { useNavigate } from 'react-router-dom'
import { api } from '../../api.js'
import { hasPermission } from '../../auth.js'

export default function KnowledgeList() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const load = async () => {
    setLoading(true)
    try {
      const page = await api.listKnowledgeBases(0, 100)
      setRows(page?.records || page?.list || [])
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
    { title: '名称', dataIndex: 'name' },
    { title: 'ID', dataIndex: 'knowledgeId', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      render: (v, r) => <Tag>{r.statusLabel || v || '-'}</Tag>,
    },
    { title: '文档数', dataIndex: 'documentCount', width: 90 },
    {
      title: '操作',
      width: 280,
      render: (_, r) => (
        <Space wrap>
          <Button size="small" onClick={() => navigate(`/admin/knowledge/${r.knowledgeId}/docs`)}>
            文档
          </Button>
          {hasPermission('kb:update') && (
            <Button
              size="small"
              onClick={async () => {
                try {
                  if ((r.status || '').toUpperCase() === 'DISABLED') {
                    await api.enableKnowledgeBase(r.knowledgeId)
                  } else {
                    await api.disableKnowledgeBase(r.knowledgeId)
                  }
                  message.success('已更新状态')
                  load()
                } catch (e) {
                  message.error(e.message)
                }
              }}
            >
              {(r.status || '').toUpperCase() === 'DISABLED' ? '启用' : '停用'}
            </Button>
          )}
          {hasPermission('kb:delete') && (
            <Button
              size="small"
              danger
              onClick={() => {
                Modal.confirm({
                  title: '确认删除知识库？',
                  content: r.name,
                  onOk: async () => {
                    await api.deleteKnowledgeBase(r.knowledgeId)
                    message.success('已删除')
                    load()
                  },
                })
              }}
            >
              删除
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 12 }}>
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>知识库</h3>
        {hasPermission('kb:create') && (
          <Button type="primary" onClick={() => setOpen(true)}>
            新建
          </Button>
        )}
        <Button onClick={load}>刷新</Button>
      </Space>
      <Table rowKey="knowledgeId" loading={loading} columns={columns} dataSource={rows} pagination={false} />
      <Modal
        title="新建知识库"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            try {
              await api.createKnowledgeBase(values)
              message.success('已创建')
              setOpen(false)
              form.resetFields()
              load()
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item name="name" label="名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="embeddingModel" label="Embedding 模型">
            <Input placeholder="可选，默认走平台配置" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
