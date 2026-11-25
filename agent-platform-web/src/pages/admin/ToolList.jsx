import { useEffect, useState } from 'react'
import {
  Button,
  Form,
  Input,
  Modal,
  Select,
  Space,
  Switch,
  Table,
  Tag,
  message,
} from 'antd'
import { api } from '../../api.js'
import { hasAnyPermission, hasPermission } from '../../auth.js'

const TOOL_TYPES = [
  { value: 'HTTP', label: 'HTTP' },
  { value: 'MCP', label: 'MCP' },
  { value: 'BUILTIN', label: 'BUILTIN' },
  { value: 'CUSTOM', label: 'CUSTOM' },
]

const AUTH_TYPES = [
  { value: 'NONE', label: 'NONE' },
  { value: 'API_KEY', label: 'API_KEY' },
  { value: 'BEARER', label: 'BEARER' },
]

function formatTime(ms) {
  if (!ms) return '-'
  const d = new Date(ms)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('zh-CN', { hour12: false })
}

function schemaToText(schema) {
  if (!schema) return ''
  if (typeof schema === 'string') {
    try {
      return JSON.stringify(JSON.parse(schema), null, 2)
    } catch {
      return schema
    }
  }
  try {
    return JSON.stringify(schema, null, 2)
  } catch {
    return ''
  }
}

function parseJsonObject(text, label) {
  const raw = (text || '').trim()
  if (!raw) return null
  try {
    const parsed = JSON.parse(raw)
    if (parsed === null || typeof parsed !== 'object' || Array.isArray(parsed)) {
      throw new Error(`${label} 须为 JSON 对象`)
    }
    return parsed
  } catch (e) {
    if (e.message.includes(label)) throw e
    throw new Error(`${label} 不是合法 JSON`)
  }
}

function hasConfiguredSecret(authConfig, field) {
  const v = authConfig?.[field]
  return Boolean(v && String(v).trim())
}

export default function ToolList() {
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [typeFilter, setTypeFilter] = useState(undefined)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [testOpen, setTestOpen] = useState(false)
  const [testTarget, setTestTarget] = useState(null)
  const [testResult, setTestResult] = useState(null)
  const [testing, setTesting] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [generatedText, setGeneratedText] = useState('')
  const [form] = Form.useForm()
  const [testForm] = Form.useForm()
  const toolTypeWatch = Form.useWatch('toolType', form)
  const authTypeWatch = Form.useWatch('authType', form)
  const descriptionWatch = Form.useWatch('description', form)
  const canGenerate = hasAnyPermission(['tool:create', 'tool:update'])
  const descriptionReady = Boolean(descriptionWatch && String(descriptionWatch).trim())

  const resetGenerateState = () => {
    setGeneratedText('')
    setGenerating(false)
  }

  const load = async (type = typeFilter) => {
    setLoading(true)
    try {
      const page = await api.listTools(0, 100, type || undefined)
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
    resetGenerateState()
    form.setFieldsValue({
      toolType: 'HTTP',
      authType: 'NONE',
      requireApproval: false,
      inputSchemaText: '{\n  "type": "object",\n  "properties": {}\n}',
      outputSchemaText: '',
    })
    setOpen(true)
  }

  const openEdit = async (row) => {
    try {
      const detail = await api.getTool(row.toolId)
      setEditing(detail)
      resetGenerateState()
      const auth = detail.authConfig || {}
      const authType =
        typeof auth.authType === 'string'
          ? auth.authType
          : auth.authType?.code || auth.authType?.name || 'NONE'
      form.setFieldsValue({
        name: detail.name,
        description: detail.description,
        toolType: detail.toolType,
        endpoint: detail.endpoint,
        authType,
        apiKey: '',
        token: '',
        requireApproval: Boolean(detail.requireApproval),
        inputSchemaText: schemaToText(detail.schema?.inputSchema),
        outputSchemaText: schemaToText(detail.schema?.outputSchema),
      })
      setOpen(true)
    } catch (e) {
      message.error(e.message)
    }
  }

  const handleGenerateDescription = async () => {
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
      const res = await api.generateToolDescription({
        description,
        name: form.getFieldValue('name') || undefined,
        toolType: form.getFieldValue('toolType') || undefined,
      })
      const text = res?.description || ''
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

  const fillGeneratedDescription = () => {
    if (!generatedText) return
    const current = String(form.getFieldValue('description') || '')
    const apply = () => {
      form.setFieldsValue({ description: generatedText })
      message.success('已填充到描述')
    }
    if (current.trim() && current.trim() !== generatedText.trim()) {
      Modal.confirm({
        title: '覆盖已有描述？',
        content: '当前描述已有内容，确认后将被生成结果替换。',
        onOk: apply,
      })
      return
    }
    apply()
  }

  const buildPayload = (values, isCreate) => {
    const needsEndpoint = values.toolType === 'HTTP' || values.toolType === 'MCP'
    if (needsEndpoint && !(values.endpoint || '').trim()) {
      throw new Error(`${values.toolType} 类型必须填写 endpoint`)
    }

    const inputObj = parseJsonObject(values.inputSchemaText, 'inputSchema')
    const outputObj = parseJsonObject(values.outputSchemaText, 'outputSchema')

    const base = {
      name: values.name,
      description: values.description,
      toolType: values.toolType,
      endpoint: values.endpoint || undefined,
      authType: values.authType || 'NONE',
      requireApproval: Boolean(values.requireApproval),
    }

    if (values.apiKey && String(values.apiKey).trim()) {
      base.apiKey = String(values.apiKey).trim()
    }
    if (values.token && String(values.token).trim()) {
      base.token = String(values.token).trim()
    }

    if (isCreate) {
      return {
        ...base,
        inputSchema: inputObj ? JSON.stringify(inputObj) : undefined,
        outputSchema: outputObj ? JSON.stringify(outputObj) : undefined,
      }
    }

    return {
      ...base,
      id: editing.toolId,
      inputSchema: inputObj || undefined,
      outputSchema: outputObj || undefined,
    }
  }

  const columns = [
    { title: '名称', dataIndex: 'name' },
    {
      title: '类型',
      dataIndex: 'toolType',
      width: 110,
      render: (v, r) => r.toolTypeLabel || v,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (v, r) => (
        <Tag color={v === 'ACTIVE' ? 'green' : 'default'}>{r.statusLabel || v}</Tag>
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
          {hasPermission('tool:update') && (
            <Button size="small" onClick={() => openEdit(r)}>
              编辑
            </Button>
          )}
          {hasPermission('tool:read') && (
            <Button
              size="small"
              onClick={() => {
                setTestTarget(r)
                setTestResult(null)
                testForm.resetFields()
                testForm.setFieldsValue({ paramsText: '{\n  \n}' })
                setTestOpen(true)
              }}
            >
              测试
            </Button>
          )}
          {hasPermission('tool:update') && (
            <Button
              size="small"
              onClick={async () => {
                try {
                  const next = r.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
                  await api.toggleToolStatus(r.toolId, next)
                  message.success(next === 'ACTIVE' ? '已启用' : '已禁用')
                  load()
                } catch (e) {
                  message.error(e.message)
                }
              }}
            >
              {r.status === 'ACTIVE' ? '停用' : '启用'}
            </Button>
          )}
        </Space>
      ),
    },
  ]

  const showHttpAuth = toolTypeWatch === 'HTTP' || toolTypeWatch === 'MCP'
  const showApiKey = showHttpAuth && authTypeWatch === 'API_KEY'
  const showToken = showHttpAuth && authTypeWatch === 'BEARER'
  const secretConfigured = editing
    ? {
        apiKey: hasConfiguredSecret(editing.authConfig, 'apiKey'),
        token: hasConfiguredSecret(editing.authConfig, 'token'),
      }
    : { apiKey: false, token: false }

  return (
    <div>
      <Space style={{ marginBottom: 12 }} wrap>
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>工具</h3>
        {hasPermission('tool:create') && (
          <Button type="primary" onClick={openCreate}>
            新建
          </Button>
        )}
        <Select
          allowClear
          placeholder="按类型筛选"
          style={{ width: 140 }}
          options={TOOL_TYPES}
          value={typeFilter}
          onChange={(v) => {
            setTypeFilter(v)
            load(v)
          }}
        />
        <Button onClick={() => load()}>刷新</Button>
      </Space>
      <Table rowKey="toolId" loading={loading} columns={columns} dataSource={rows} pagination={false} />

      <Modal
        title={editing ? '编辑工具' : '注册工具'}
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
              const payload = buildPayload(values, !editing)
              if (editing) {
                await api.updateTool(payload)
              } else {
                await api.createTool(payload)
              }
              message.success('已保存')
              setOpen(false)
              load()
            } catch (e) {
              message.error(e.message)
            }
          }}
        >
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请填写名称' }]}>
            <Input maxLength={128} />
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
                    onClick={handleGenerateDescription}
                  >
                    AI 生成
                  </Button>
                )}
              </Space>
            }
            extra="先写草稿用途，点 AI 生成后可一键填充并继续修改"
          >
            <Input.TextArea rows={3} maxLength={2000} showCount placeholder="供 LLM 理解工具用途，可先写草稿再润色" />
          </Form.Item>
          {generatedText && (
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, color: 'var(--ap-text-secondary, #666)' }}>生成结果（只读预览）</div>
              <Input.TextArea value={generatedText} rows={4} readOnly />
              <Button style={{ marginTop: 8 }} onClick={fillGeneratedDescription}>
                填充到描述
              </Button>
            </div>
          )}
          <Form.Item name="toolType" label="类型" rules={[{ required: true }]}>
            <Select options={TOOL_TYPES} />
          </Form.Item>
          {(toolTypeWatch === 'HTTP' || toolTypeWatch === 'MCP') && (
            <Form.Item
              name="endpoint"
              label={toolTypeWatch === 'MCP' ? 'MCP SSE 端点' : 'HTTP URL'}
              rules={[{ required: true, message: '请填写 endpoint' }]}
            >
              <Input
                placeholder={
                  toolTypeWatch === 'MCP'
                    ? 'https://mcp.example.com/sse'
                    : 'http://127.0.0.1:8081/api/v1/demo-tools/echo'
                }
              />
            </Form.Item>
          )}
          {toolTypeWatch !== 'HTTP' && toolTypeWatch !== 'MCP' && (
            <Form.Item name="endpoint" label="Endpoint（可选）">
              <Input />
            </Form.Item>
          )}
          {showHttpAuth && (
            <Form.Item name="authType" label="认证类型" initialValue="NONE">
              <Select options={AUTH_TYPES} />
            </Form.Item>
          )}
          {showApiKey && (
            <Form.Item
              name="apiKey"
              label="API Key"
              extra={secretConfigured.apiKey ? '已配置密钥，留空则不修改' : undefined}
            >
              <Input.Password placeholder={secretConfigured.apiKey ? '已配置（留空不修改）' : ''} />
            </Form.Item>
          )}
          {showToken && (
            <Form.Item
              name="token"
              label="Bearer Token"
              extra={secretConfigured.token ? '已配置 Token，留空则不修改' : undefined}
            >
              <Input.Password placeholder={secretConfigured.token ? '已配置（留空不修改）' : ''} />
            </Form.Item>
          )}
          <Form.Item
            name="inputSchemaText"
            label="inputSchema（JSON）"
            rules={[{ required: true, message: '请填写 inputSchema' }]}
          >
            <Input.TextArea rows={6} placeholder='{"type":"object","properties":{...}}' />
          </Form.Item>
          <Form.Item name="outputSchemaText" label="outputSchema（JSON，可选）">
            <Input.TextArea rows={4} />
          </Form.Item>
          <Form.Item name="requireApproval" label="高风险需审批" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`测试 · ${testTarget?.name || ''}`}
        open={testOpen}
        onCancel={() => setTestOpen(false)}
        onOk={() => testForm.submit()}
        okText="执行测试"
        confirmLoading={testing}
        width={640}
        destroyOnClose
      >
        <Form
          form={testForm}
          layout="vertical"
          onFinish={async (values) => {
            setTesting(true)
            setTestResult(null)
            try {
              const params = parseJsonObject(values.paramsText, 'params') || {}
              const res = await api.testTool(testTarget.toolId, params)
              setTestResult(res)
              if (res?.success) message.success(`调用成功，耗时 ${res.durationMs ?? '-'} ms`)
              else if (res?.pendingApproval) message.warning('已提交审批，等待通过后再执行')
              else message.error(res?.errorMessage || '调用失败')
            } catch (e) {
              message.error(e.message)
            } finally {
              setTesting(false)
            }
          }}
        >
          <Form.Item
            name="paramsText"
            label="调用参数（JSON 对象）"
            rules={[{ required: true, message: '请填写参数 JSON' }]}
          >
            <Input.TextArea rows={6} placeholder='{"message":"hello"}' />
          </Form.Item>
        </Form>
        {testResult && (
          <div>
            <div style={{ marginBottom: 8, color: 'var(--ap-text-secondary, #666)' }}>
              耗时：{testResult.durationMs ?? '-'} ms
              {testResult.invocationId ? ` · invocationId=${testResult.invocationId}` : ''}
            </div>
            <pre
              style={{
                background: 'var(--ap-bg)',
                padding: 12,
                borderRadius: 8,
                overflow: 'auto',
                maxHeight: 280,
              }}
            >
              {JSON.stringify(testResult, null, 2)}
            </pre>
          </div>
        )}
      </Modal>
    </div>
  )
}
