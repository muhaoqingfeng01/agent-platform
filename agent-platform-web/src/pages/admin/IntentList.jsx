import { useEffect, useState } from 'react'
import { Button, Form, Input, Modal, Select, Space, Table, Tag, message } from 'antd'
import { api } from '../../api.js'
import { hasPermission } from '../../auth.js'

function splitLines(text) {
  return (text || '')
    .split('\n')
    .map((s) => s.trim())
    .filter(Boolean)
}

export default function IntentList() {
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [testOpen, setTestOpen] = useState(false)
  const [testTarget, setTestTarget] = useState(null)
  const [testResult, setTestResult] = useState(null)
  const [form] = Form.useForm()
  const [testForm] = Form.useForm()

  const load = async () => {
    setLoading(true)
    try {
      const page = await api.listIntents(0, 100)
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

  const openCreate = () => {
    setEditing(null)
    form.resetFields()
    setOpen(true)
  }

  const openEdit = (row) => {
    setEditing(row)
    form.setFieldsValue({
      name: row.intentName,
      category: row.category,
      riskLevel: row.riskLevel || 'LOW',
      patternsText: (row.patterns || []).join('\n'),
      examplesText: (row.examples || []).join('\n'),
      llmPrompt: row.llmPrompt,
    })
    setOpen(true)
  }

  const columns = [
    { title: '名称', dataIndex: 'intentName' },
    { title: '编码', dataIndex: 'intentCode', ellipsis: true },
    { title: '分类', dataIndex: 'category', width: 100 },
    { title: '风险', dataIndex: 'riskLevel', width: 90 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
      render: (v) => <Tag color={v === 'ACTIVE' ? 'green' : 'default'}>{v}</Tag>,
    },
    {
      title: '操作',
      width: 320,
      render: (_, r) => (
        <Space wrap>
          {hasPermission('intent:update') && (
            <Button size="small" onClick={() => openEdit(r)}>
              编辑
            </Button>
          )}
          {hasPermission('intent:read') && (
            <Button
              size="small"
              onClick={() => {
                setTestTarget(r)
                setTestResult(null)
                testForm.resetFields()
                setTestOpen(true)
              }}
            >
              试识别
            </Button>
          )}
          {hasPermission('intent:update') && (
            <Button
              size="small"
              onClick={async () => {
                try {
                  const next = r.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
                  await api.toggleIntentStatus(r.intentId, next)
                  message.success('状态已更新')
                  load()
                } catch (e) {
                  message.error(e.message)
                }
              }}
            >
              {r.status === 'ACTIVE' ? '停用' : '启用'}
            </Button>
          )}
          {hasPermission('intent:delete') && (
            <Button
              size="small"
              danger
              onClick={() => {
                Modal.confirm({
                  title: '确认删除意图？',
                  onOk: async () => {
                    await api.deleteIntent(r.intentId)
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
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>意图</h3>
        {hasPermission('intent:create') && (
          <Button type="primary" onClick={openCreate}>
            新建
          </Button>
        )}
        <Button onClick={load}>刷新</Button>
      </Space>
      <Table rowKey="intentId" loading={loading} columns={columns} dataSource={rows} pagination={false} />

      <Modal
        title={editing ? '编辑意图' : '新建意图'}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        width={640}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            try {
              const patterns = splitLines(values.patternsText)
              const examples = splitLines(values.examplesText)
              if (editing) {
                await api.updateIntent({
                  id: editing.intentId,
                  intentName: values.name,
                  patterns,
                  examples,
                  llmPrompt: values.llmPrompt,
                  riskLevel: values.riskLevel,
                })
              } else {
                await api.createIntent({
                  name: values.name,
                  category: values.category,
                  patterns,
                  examples,
                  llmPrompt: values.llmPrompt,
                  riskLevel: values.riskLevel,
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
          <Form.Item name="name" label="名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          {!editing && (
            <Form.Item name="category" label="分类" initialValue="FAQ">
              <Input />
            </Form.Item>
          )}
          <Form.Item name="riskLevel" label="风险等级" initialValue="LOW">
            <Select
              options={[
                { value: 'LOW', label: 'LOW' },
                { value: 'MEDIUM', label: 'MEDIUM' },
                { value: 'HIGH', label: 'HIGH' },
              ]}
            />
          </Form.Item>
          <Form.Item name="patternsText" label="匹配模式（每行一条）">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="examplesText" label="示例（每行一条）">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="llmPrompt" label="LLM 提示">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`试识别 · ${testTarget?.intentName || ''}`}
        open={testOpen}
        onCancel={() => setTestOpen(false)}
        onOk={() => testForm.submit()}
        okText="测试"
        destroyOnClose
      >
        <Form
          form={testForm}
          layout="vertical"
          onFinish={async (values) => {
            try {
              const res = await api.testIntent(testTarget.intentId, values.input)
              setTestResult(res)
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item name="input" label="输入文本" rules={[{ required: true }]}>
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
        {testResult && (
          <pre style={{ background: 'var(--ap-bg)', padding: 12, borderRadius: 8, overflow: 'auto' }}>
            {JSON.stringify(testResult, null, 2)}
          </pre>
        )}
      </Modal>
    </div>
  )
}
