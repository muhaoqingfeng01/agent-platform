import { Button, Drawer, Input, Layout, List, Select, Space, Typography, message } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import ApprovalCard from '../components/ApprovalCard.jsx'
import FeedbackBar from '../components/FeedbackBar.jsx'
import { api, getToken } from '../api.js'
import { streamChat } from '../streamChat.js'
import { useAuthStore } from '../store.js'

const { Sider, Content, Header } = Layout

export default function Chat() {
  const logout = useAuthStore((s) => s.logout)
  const [conversations, setConversations] = useState([])
  const [currentId, setCurrentId] = useState()
  const [messages, setMessages] = useState([])
  const [cards, setCards] = useState([])
  const [input, setInput] = useState('')
  const [mode, setMode] = useState('CONVERSATION')
  const [knowledgeId, setKnowledgeId] = useState()
  const [sending, setSending] = useState(false)
  const [drawer, setDrawer] = useState(false)
  const narrow = useMemo(() => window.innerWidth < 800, [])

  const reloadConversations = async () => {
    const page = await api.listConversations()
    setConversations(page?.records || page?.list || [])
  }

  const openConversation = async (id) => {
    setCurrentId(id)
    const page = await api.listMessages(id)
    setMessages(page?.records || [])
    const pending = await api.pendingApprovals(id)
    setCards(pending?.records || [])
  }

  useEffect(() => {
    reloadConversations().catch((e) => message.error(e.message))
  }, [])

  useEffect(() => {
    const token = getToken()
    if (!token) return undefined
    const ws = new WebSocket(`ws://${location.host}/ws/conversation?token=${encodeURIComponent(token)}`)
    ws.onmessage = (ev) => {
      try {
        const payload = JSON.parse(ev.data)
        if (payload.type === 'approval_card') {
          setCards((prev) => {
            const next = payload.payload || payload
            const id = next.approvalId
            return [...prev.filter((c) => c.approvalId !== id), next]
          })
        }
        if (payload.type === 'approval_result') {
          const id = payload.payload?.approvalId
          setCards((prev) => prev.map((c) => (c.approvalId === id ? { ...c, ...payload.payload } : c)))
        }
      } catch {
        // ignore
      }
    }
    return () => ws.close()
  }, [])

  const createConv = async () => {
    const created = await api.createConversation('新对话')
    await reloadConversations()
    await openConversation(created.conversationId)
  }

  const send = async () => {
    if (!currentId || !input.trim()) return
    const userText = input.trim()
    setInput('')
    setMessages((prev) => [...prev, { role: 'USER', content: userText }])
    const assistant = { role: 'ASSISTANT', content: '', messageId: null }
    setMessages((prev) => [...prev, assistant])
    setSending(true)
    try {
      await streamChat({
        conversationId: currentId,
        content: userText,
        mode,
        knowledgeId,
        onEvent: ({ event, data }) => {
          if (event === 'token') {
            const text = typeof data === 'string' ? data : data?.token || ''
            setMessages((prev) => {
              const copy = [...prev]
              const last = { ...copy[copy.length - 1] }
              last.content = (last.content || '') + text
              copy[copy.length - 1] = last
              return copy
            })
          }
          if (event === 'done') {
            setMessages((prev) => {
              const copy = [...prev]
              const last = { ...copy[copy.length - 1] }
              last.messageId = data?.messageId
              copy[copy.length - 1] = last
              return copy
            })
          }
          if (event === 'approval_required' || event === 'approval_list') {
            api.pendingApprovals(currentId).then((p) => setCards(p?.records || []))
          }
          if (event === 'error') {
            message.error(data?.message || '流式失败')
          }
        },
      })
    } catch (e) {
      message.error(e.message)
    } finally {
      setSending(false)
    }
  }

  const list = (
    <List
      header={<Button block onClick={createConv}>新建会话</Button>}
      dataSource={conversations}
      renderItem={(item) => (
        <List.Item onClick={() => openConversation(item.conversationId)} style={{ cursor: 'pointer' }}>
          {item.title || item.conversationId}
        </List.Item>
      )}
    />
  )

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography.Text style={{ color: '#fff' }}>Agent Platform 聊天</Typography.Text>
        <Space>
          {narrow && <Button onClick={() => setDrawer(true)}>会话</Button>}
          <Button onClick={logout}>退出</Button>
        </Space>
      </Header>
      <Layout>
        {!narrow && <Sider width={260} theme="light">{list}</Sider>}
        <Content style={{ padding: 16 }}>
          <Space style={{ marginBottom: 12 }}>
            <Select
              value={mode}
              style={{ width: 180 }}
              onChange={setMode}
              options={[
                { value: 'CONVERSATION', label: '智能对话' },
                { value: 'KNOWLEDGE_SEARCH', label: '知识检索' },
                { value: 'TASK_EXECUTION', label: '任务执行' },
                { value: 'ANALYSIS', label: '分析推理' },
                { value: 'APPROVAL', label: '安全审批' },
              ]}
            />
            {mode === 'KNOWLEDGE_SEARCH' && (
              <Input
                placeholder="知识库 ID（可选）"
                value={knowledgeId}
                onChange={(e) => setKnowledgeId(e.target.value)}
                style={{ width: 200 }}
              />
            )}
          </Space>
          {cards.map((card) => (
            <ApprovalCard
              key={card.approvalId}
              card={card}
              disabled={sending}
              onApprove={async (id) => {
                await api.approve(id, '同意')
                message.success('已同意')
                const pending = await api.pendingApprovals(currentId)
                setCards(pending?.records || [])
              }}
              onReject={async (id) => {
                await api.reject(id, '拒绝')
                message.success('已拒绝')
                const pending = await api.pendingApprovals(currentId)
                setCards(pending?.records || [])
              }}
            />
          ))}
          <div style={{ minHeight: 360, marginBottom: 12 }}>
            {messages.map((msg, idx) => (
              <div key={idx} style={{ marginBottom: 12 }}>
                <Typography.Text strong>{msg.role === 'USER' ? '我' : '助手'}</Typography.Text>
                <Typography.Paragraph style={{ whiteSpace: 'pre-wrap' }}>{msg.content}</Typography.Paragraph>
                {msg.role === 'ASSISTANT' && msg.messageId && currentId && (
                  <FeedbackBar
                    messageId={msg.messageId}
                    conversationId={currentId}
                    onSubmit={async (payload) => {
                      await api.feedback(payload)
                      message.success('反馈已提交')
                    }}
                  />
                )}
              </div>
            ))}
          </div>
          <Space.Compact style={{ width: '100%' }}>
            <Input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onPressEnter={send}
              placeholder="输入消息"
              disabled={!currentId || sending}
            />
            <Button type="primary" onClick={send} loading={sending} disabled={!currentId}>
              发送
            </Button>
          </Space.Compact>
        </Content>
      </Layout>
      <Drawer open={drawer} onClose={() => setDrawer(false)}>{list}</Drawer>
    </Layout>
  )
}
