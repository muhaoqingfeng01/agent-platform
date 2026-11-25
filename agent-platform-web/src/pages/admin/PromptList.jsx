import { useEffect, useState } from 'react'
import { Button, Form, Input, Modal, Space, Table, Tag, Tooltip, message } from 'antd'
import { api } from '../../api.js'
import { hasAnyPermission, hasPermission } from '../../auth.js'

const RUNTIME_HINTS = {
  'chat.system': '对话系统提示。发布后，新会话会使用此模板。',
  'rag.system': '知识库检索系统提示。发布后，新会话会使用此模板。',
}

const STATUS_COLOR = {
  DRAFT: 'default',
  PUBLISHED: 'green',
  ARCHIVED: 'orange',
}

function formatTime(ms) {
  if (!ms) return '-'
  const d = new Date(ms)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('zh-CN', { hour12: false })
}

export default function PromptList() {
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [generating, setGenerating] = useState(false)
  const [generatedText, setGeneratedText] = useState('')
  const [form] = Form.useForm()
  const descriptionWatch = Form.useWatch('description', form)
  const canGenerate = hasAnyPermission(['prompt:create', 'prompt:update'])
  const descriptionReady = Boolean(descriptionWatch && String(descriptionWatch).trim())

  const load = async () => {
    setLoading(true)
    try {
      const page = await api.listPrompts(0, 100)
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

  const resetGenerateState = () => {
    setGeneratedText('')
    setGenerating(false)
  }

  const openCreate = () => {
    setEditing(null)
    form.resetFields()
    resetGenerateState()
    setOpen(true)
  }

  const openEdit = (row) => {
    setEditing(row)
    form.setFieldsValue({
      name: row.name,
      description: row.description,
      templateText: row.templateText,
    })
    resetGenerateState()
    setOpen(true)
  }

  const handleGenerate = async () => {
    const description = String(form.getFieldValue('description') || '').trim()
    if (!description) {
      message.error('请先填写描述')
      return
    }
    if (description.length > 2000) {
      message.error('描述最长 2000 个字符')
      return
    }
    setGenerating(true)
    try {
      const res = await api.generatePrompt({
        description,
        name: form.getFieldValue('name') || undefined,
      })
      const text = res?.templateText || ''
      if (!text) {
        message.error('模型返回空内容')
        return
      }
      setGeneratedText(text)
    } catch (e) {
      message.error(e.message)
    } finally {
      setGenerating(false)
    }
  }

  const fillToBody = () => {
    if (!generatedText) return
    const current = String(form.getFieldValue('templateText') || '')
    const apply = () => {
      form.setFieldsValue({ templateText: generatedText })
      message.success('已填充到正文')
    }
    if (current.trim()) {
      Modal.confirm({
        title: '覆盖已有正文？',
        content: '当前正文已有内容，确认后将被生成结果替换。',
        onOk: apply,
      })
      return
    }
    apply()
  }

  const columns = [
    {
      title: '名称',
      dataIndex: 'name',
      render: (name) => {
        const hint = RUNTIME_HINTS[name]
        if (!hint) return name
        return <Tooltip title={hint}>{name}</Tooltip>
      },
    },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 110,
      render: (status, row) => (
        <Tag color={STATUS_COLOR[status] || 'default'}>{row.statusLabel || status}</Tag>
      ),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      width: 180,
      render: (v) => formatTime(v),
    },
    {
      title: '操作',
      width: 280,
      render: (_, r) => (
        <Space wrap>
          {hasPermission('prompt:update') && r.status === 'DRAFT' && (
            <Button size="small" onClick={() => openEdit(r)}>
              编辑
            </Button>
          )}
          {hasPermission('prompt:publish') && r.status === 'DRAFT' && (
            <Button
              size="small"
              type="primary"
              onClick={async () => {
                try {
                  await api.publishPrompt(r.promptId)
                  message.success('已发布')
                  load()
                } catch (e) {
                  message.error(e.message)
                }
              }}
            >
              发布
            </Button>
          )}
          {hasPermission('prompt:update') && r.status !== 'ARCHIVED' && (
            <Button
              size="small"
              onClick={() => {
                Modal.confirm({
                  title: '确认归档该模板？',
                  content: '归档后对话不再选用此模板。',
                  onOk: async () => {
                    try {
                      await api.archivePrompt(r.promptId)
                      message.success('已归档')
                      load()
                    } catch (e) {
                      message.error(e.message)
                      throw e
                    }
                  },
                })
              }}
            >
              归档
            </Button>
          )}
          {hasPermission('prompt:delete') && (
            <Button
              size="small"
              danger
              onClick={() => {
                Modal.confirm({
                  title: '确认删除该模板？',
                  onOk: async () => {
                    try {
                      await api.deletePrompt(r.promptId)
                      message.success('已删除')
                      load()
                    } catch (e) {
                      message.error(e.message)
                      throw e
                    }
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
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>提示词</h3>
        {hasPermission('prompt:create') && (
          <Button type="primary" onClick={openCreate}>
            新建
          </Button>
        )}
        <Button onClick={load}>刷新</Button>
      </Space>
      <Table rowKey="promptId" loading={loading} columns={columns} dataSource={rows} pagination={false} />

      <Modal
        title={editing ? '编辑提示词' : '新建提示词'}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        width={720}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            try {
              if (editing) {
                await api.updatePrompt({
                  id: editing.promptId,
                  name: values.name,
                  description: values.description,
                  templateText: values.templateText,
                })
              } else {
                await api.createPrompt({
                  name: values.name,
                  description: values.description,
                  templateText: values.templateText,
                })
              }
              message.success('已保存')
              setOpen(false)
              load()
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item
            name="name"
            label="名称"
            extra="对话与检索分别使用已发布的 chat.system、rag.system。其他名称发布后不会被新会话选用。"
            rules={[{ required: true, message: '请填写名称' }]}
          >
            <Input maxLength={256} />
          </Form.Item>
          <Form.Item
            name="description"
            label={
              <Space>
                <span>描述</span>
                {canGenerate && (
                  <Button
                    size="small"
                    type="link"
                    style={{ padding: 0, height: 'auto' }}
                    disabled={!descriptionReady || generating}
                    loading={generating}
                    onClick={handleGenerate}
                  >
                    AI 生成
                  </Button>
                )}
              </Space>
            }
          >
            <Input.TextArea rows={3} maxLength={2000} showCount placeholder="写清模板用途，便于 AI 生成正文" />
          </Form.Item>
          {generatedText && (
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, color: 'var(--ap-text-secondary, #666)' }}>生成结果（只读预览）</div>
              <Input.TextArea value={generatedText} rows={6} readOnly />
              <Button style={{ marginTop: 8 }} onClick={fillToBody}>
                填充到正文
              </Button>
            </div>
          )}
          <Form.Item
            name="templateText"
            label="正文"
            rules={[{ required: true, message: '请填写正文' }]}
          >
            <Input.TextArea rows={10} placeholder="支持 {{variable}} 占位符" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
