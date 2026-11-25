import { useEffect, useRef, useState } from 'react'
import { Alert, Button, Form, Input, Space, Table, Tag, Typography, message } from 'antd'
import { api } from '../../api.js'
import { hasPermission } from '../../auth.js'

const TERMINAL = new Set(['COMPLETED', 'FAILED', 'CANCELLED'])
const PAUSED = 'WAITING_APPROVAL'

const STATUS_COLOR = {
  PENDING: 'default',
  RUNNING: 'processing',
  COMPLETED: 'green',
  SUCCESS: 'green',
  FAILED: 'red',
  CANCELLED: 'orange',
  WAITING_APPROVAL: 'gold',
}

function statusTag(status) {
  if (!status) return '-'
  return <Tag color={STATUS_COLOR[status] || 'default'}>{status}</Tag>
}

function formatDep(dep) {
  if (!dep) return '-'
  if (Array.isArray(dep)) return dep.length ? dep.join(', ') : '-'
  return String(dep)
}

function formatSchema(schema) {
  if (schema == null || schema === '') return '-'
  if (typeof schema === 'string') return schema
  try {
    return JSON.stringify(schema)
  } catch {
    return '-'
  }
}

export default function TaskPlanPage() {
  const [form] = Form.useForm()
  const [handlers, setHandlers] = useState([])
  const [handlersLoading, setHandlersLoading] = useState(false)
  const [planning, setPlanning] = useState(false)
  const [executing, setExecuting] = useState(false)
  const [cancelling, setCancelling] = useState(false)
  const [executionId, setExecutionId] = useState('')
  const [planMeta, setPlanMeta] = useState(null)
  const [nodes, setNodes] = useState([])
  const [status, setStatus] = useState(null)
  const [polling, setPolling] = useState(false)
  const timerRef = useRef(null)

  const canCreate = hasPermission('task:create')
  const canExecute = hasPermission('task:execute')
  const canRead = hasPermission('task:read')

  const stopPoll = () => {
    if (timerRef.current) {
      clearInterval(timerRef.current)
      timerRef.current = null
    }
    setPolling(false)
  }

  const applyStatus = (st) => {
    setStatus(st)
    const code = st?.status
    if (TERMINAL.has(code) || code === PAUSED) stopPoll()
  }

  const pullStatus = async (id) => {
    const st = await api.taskStatus(id)
    applyStatus(st)
    return st
  }

  const startPoll = (id) => {
    stopPoll()
    setPolling(true)
    timerRef.current = setInterval(async () => {
      try {
        const st = await api.taskStatus(id)
        setStatus(st)
        if (TERMINAL.has(st?.status) || st?.status === PAUSED) {
          if (timerRef.current) {
            clearInterval(timerRef.current)
            timerRef.current = null
          }
          setPolling(false)
        }
      } catch (e) {
        if (timerRef.current) {
          clearInterval(timerRef.current)
          timerRef.current = null
        }
        setPolling(false)
        message.error(e.message || '查询状态失败，已停止轮询')
      }
    }, 2000)
  }

  const loadHandlers = async () => {
    if (!canRead) return
    setHandlersLoading(true)
    try {
      const data = await api.listTaskHandlers()
      setHandlers(data?.records || [])
    } catch (e) {
      message.error(e.message)
    } finally {
      setHandlersLoading(false)
    }
  }

  useEffect(() => {
    loadHandlers()
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current)
        timerRef.current = null
      }
    }
  }, [])

  const onPlan = async () => {
    const values = await form.validateFields()
    stopPoll()
    setPlanning(true)
    setStatus(null)
    try {
      const planned = await api.planTask({
        agentId: values.agentId.trim(),
        userIntent: values.userIntent.trim(),
      })
      const id = planned?.executionId
      if (!id) throw new Error('规划未返回 executionId')
      setExecutionId(id)
      setPlanMeta(planned)
      if (canRead) {
        const detail = await api.getTaskPlan(id)
        setNodes(detail?.nodes || [])
      } else {
        setNodes([])
      }
      message.success(planned?.message || '规划完成')
    } catch (e) {
      message.error(e.message)
    } finally {
      setPlanning(false)
    }
  }

  const onExecute = async () => {
    if (!executionId) return
    setExecuting(true)
    try {
      const res = await api.executeTask(executionId, true)
      message.success(res?.message || '已提交执行')
      if (canRead) {
        const st = await pullStatus(executionId)
        const code = st?.status
        if (code && !TERMINAL.has(code) && code !== PAUSED) startPoll(executionId)
      }
    } catch (e) {
      message.error(e.message)
    } finally {
      setExecuting(false)
    }
  }

  const onCancel = async () => {
    if (!executionId) return
    setCancelling(true)
    try {
      const res = await api.cancelTask(executionId)
      message.success(res?.message || '已取消')
      stopPoll()
      if (canRead) await pullStatus(executionId)
    } catch (e) {
      message.error(e.message)
    } finally {
      setCancelling(false)
    }
  }

  const execStatus = status?.status
  const settled = TERMINAL.has(execStatus) || execStatus === PAUSED
  const running = execStatus === 'RUNNING' || execStatus === 'PENDING' || polling

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Typography.Title level={4} style={{ margin: 0 }}>
        任务规划
      </Typography.Title>
      <Alert
        type="warning"
        showIcon
        message="当前仅注册三个示例 Handler：retrieve_orders、calculate_sum、send_email。生产级动作（C6）尚未完成，规划失败或结果不符合预期时先核对目标是否落在这三项上。"
      />

      <Form form={form} layout="inline" initialValues={{ agentId: 'default' }}>
        <Form.Item name="agentId" label="agentId" rules={[{ required: true, message: '填写 agentId' }]}>
          <Input style={{ width: 180 }} />
        </Form.Item>
        <Form.Item
          name="userIntent"
          label="目标"
          rules={[{ required: true, whitespace: true, message: '填写目标描述' }]}
        >
          <Input.TextArea autoSize={{ minRows: 1, maxRows: 4 }} style={{ width: 420 }} />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" disabled={!canCreate} loading={planning} onClick={onPlan}>
              规划
            </Button>
            <Button
              disabled={!canExecute || !executionId || running || settled}
              loading={executing}
              onClick={onExecute}
            >
              执行
            </Button>
            <Button danger disabled={!canExecute || !executionId || !running} loading={cancelling} onClick={onCancel}>
              取消
            </Button>
          </Space>
        </Form.Item>
      </Form>

      {executionId && (
        <Space wrap>
          <Typography.Text>executionId：{executionId}</Typography.Text>
          {planMeta?.totalSteps != null && <Typography.Text>步骤 {planMeta.totalSteps}</Typography.Text>}
          {planMeta?.levels != null && <Typography.Text>分层 {planMeta.levels}</Typography.Text>}
          {execStatus && (
            <span>
              状态 {statusTag(execStatus)}
              {polling ? ' 轮询中' : ''}
            </span>
          )}
          {status?.completedSteps != null && status?.totalSteps != null && (
            <Typography.Text>
              进度 {status.completedSteps}/{status.totalSteps}
            </Typography.Text>
          )}
        </Space>
      )}

      {execStatus === 'FAILED' && (
        <Alert type="error" showIcon message={status?.errorMessage || '执行失败'} />
      )}
      {execStatus === 'CANCELLED' && <Alert type="warning" showIcon message="任务已取消" />}
      {execStatus === PAUSED && (
        <Alert
          type="info"
          showIcon
          message="执行停在等待审批。本页不提供恢复，审批通过后需在对话侧继续。"
        />
      )}

      <Typography.Title level={5} style={{ margin: 0 }}>
        规划步骤
      </Typography.Title>
      <Table
        size="small"
        rowKey={(r) => r.id || r.action}
        pagination={false}
        dataSource={nodes}
        locale={{ emptyText: canRead ? '尚未规划' : '需要 task:read 才能查看计划' }}
        columns={[
          { title: '步骤', dataIndex: 'id', width: 120 },
          { title: '动作', dataIndex: 'action', width: 160 },
          { title: '说明', dataIndex: 'description', ellipsis: true },
          { title: '依赖', dataIndex: 'dep', width: 160, render: formatDep },
        ]}
      />

      <Typography.Title level={5} style={{ margin: 0 }}>
        执行步骤
      </Typography.Title>
      <Table
        size="small"
        rowKey={(r) => r.stepId || r.action}
        pagination={false}
        dataSource={status?.steps || []}
        locale={{ emptyText: '尚未执行' }}
        columns={[
          { title: '步骤', dataIndex: 'stepId', width: 120 },
          { title: '动作', dataIndex: 'action', width: 160 },
          { title: '状态', dataIndex: 'status', width: 140, render: statusTag },
          { title: '重试', dataIndex: 'retryCount', width: 80 },
          {
            title: '耗时(ms)',
            dataIndex: 'durationMs',
            width: 100,
            render: (v) => (v == null ? '-' : v),
          },
          { title: '错误', dataIndex: 'errorMessage', ellipsis: true, render: (v) => v || '-' },
        ]}
      />

      <Space>
        <Typography.Title level={5} style={{ margin: 0 }}>
          可用 Handler
        </Typography.Title>
        <Button size="small" disabled={!canRead} loading={handlersLoading} onClick={loadHandlers}>
          刷新
        </Button>
      </Space>
      <Table
        size="small"
        rowKey="action"
        loading={handlersLoading}
        pagination={false}
        dataSource={handlers}
        locale={{ emptyText: canRead ? '暂无 Handler' : '需要 task:read' }}
        columns={[
          { title: '动作', dataIndex: 'action', width: 160 },
          { title: '说明', dataIndex: 'description', ellipsis: true },
          {
            title: '高风险',
            dataIndex: 'highRisk',
            width: 90,
            render: (v) => (v ? <Tag color="red">是</Tag> : <Tag>否</Tag>),
          },
          { title: '重试', dataIndex: 'maxRetries', width: 80 },
          { title: '超时(ms)', dataIndex: 'timeoutMs', width: 100 },
          { title: '参数', dataIndex: 'paramsSchema', ellipsis: true, render: formatSchema },
        ]}
      />
    </Space>
  )
}
