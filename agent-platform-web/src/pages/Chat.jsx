import { message } from 'antd'
import { useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ChatShell from '../components/chat/ChatShell.jsx'
import { api, getToken } from '../api.js'
import { regenerateChat, streamChat } from '../streamChat.js'
import { useAuthStore, useThemeStore } from '../store.js'

let localSeq = 0
function nextLocalId() {
  localSeq += 1
  return `local-${Date.now()}-${localSeq}`
}

export default function Chat() {
  const logout = useAuthStore((s) => s.logout)
  const navigate = useNavigate()
  const theme = useThemeStore((s) => s.theme)
  const toggleTheme = useThemeStore((s) => s.toggleTheme)
  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  const [conversations, setConversations] = useState([])
  const [currentId, setCurrentId] = useState()
  const [messages, setMessages] = useState([])
  const [cards, setCards] = useState([])
  const [input, setInput] = useState('')
  const [mode, setMode] = useState('CONVERSATION')
  const [knowledgeId, setKnowledgeId] = useState()
  const [knowledgeOptions, setKnowledgeOptions] = useState([])
  const [sending, setSending] = useState(false)
  const [drawer, setDrawer] = useState(false)
  const [narrow, setNarrow] = useState(() => window.innerWidth < 800)
  const [follow, setFollow] = useState(true)

  const streamRef = useRef(null)
  const streamIdRef = useRef(null)

  const reloadConversations = async () => {
    const page = await api.listConversations()
    setConversations(page?.records || page?.list || [])
  }

  const openConversation = async (id) => {
    setCurrentId(id)
    const page = await api.listMessages(id)
    setMessages(
      (page?.records || []).map((m) => ({
        ...m,
        localId: m.messageId || nextLocalId(),
        events: [],
      })),
    )
    const pending = await api.pendingApprovals(id)
    setCards(pending?.records || [])
  }

  useEffect(() => {
    reloadConversations().catch((e) => message.error(e.message))
    api
      .listKnowledgeBases()
      .then((page) => {
        const records = page?.records || []
        setKnowledgeOptions(
          records.map((kb) => ({
            value: kb.knowledgeId,
            label: kb.name || kb.knowledgeId,
          })),
        )
      })
      .catch(() => {
        /* 无权限时静默 */
      })
  }, [])

  useEffect(() => {
    const onResize = () => setNarrow(window.innerWidth < 800)
    window.addEventListener('resize', onResize)
    return () => window.removeEventListener('resize', onResize)
  }, [])

  useEffect(() => {
    const token = getToken()
    if (!token) return undefined
    const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
    const ws = new WebSocket(`${protocol}://${location.host}/ws/conversation?token=${encodeURIComponent(token)}`)
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

  const appendAssistantEvent = (type, data) => {
    setMessages((prev) => {
      const copy = [...prev]
      const last = { ...copy[copy.length - 1] }
      last.events = [...(last.events || []), { type, data }]
      copy[copy.length - 1] = last
      return copy
    })
  }

  const handleStreamEvent = ({ event, data }) => {
    if (event === 'stream_started') {
      streamIdRef.current = data?.streamId || null
      return
    }
    if (event === 'token') {
      const text = typeof data === 'string' ? data : data?.token || ''
      setMessages((prev) => {
        const copy = [...prev]
        const last = { ...copy[copy.length - 1] }
        last.content = (last.content || '') + text
        last.streaming = true
        copy[copy.length - 1] = last
        return copy
      })
      return
    }
    if (event === 'done') {
      setMessages((prev) => {
        const copy = [...prev]
        const last = { ...copy[copy.length - 1] }
        last.messageId = data?.messageId
        last.streaming = false
        copy[copy.length - 1] = last
        return copy
      })
      return
    }
    if (
      event === 'thinking' ||
      event === 'intent' ||
      event === 'references' ||
      event === 'task_plan' ||
      event === 'task_step' ||
      event === 'tool_call' ||
      event === 'tool_result' ||
      event === 'analysis_table'
    ) {
      appendAssistantEvent(event, data)
      return
    }
    if (event === 'approval_required' || event === 'approval_list') {
      if (currentId) {
        api.pendingApprovals(currentId).then((p) => setCards(p?.records || []))
      }
      return
    }
    if (event === 'error') {
      message.error(data?.message || '流式失败')
    }
  }

  const stopStreaming = async () => {
    const handle = streamRef.current
    const streamId = streamIdRef.current || handle?.getStreamId?.()
    handle?.abort?.()
    if (currentId && streamId) {
      try {
        await api.cancelStream(currentId, streamId)
      } catch {
        // 客户端 abort 已足够
      }
    }
    setMessages((prev) => {
      const copy = [...prev]
      if (!copy.length) return copy
      const last = { ...copy[copy.length - 1] }
      if ((last.role || '').toUpperCase() === 'ASSISTANT') {
        last.stopped = true
        last.streaming = false
        copy[copy.length - 1] = last
      }
      return copy
    })
    setSending(false)
    streamRef.current = null
    streamIdRef.current = null
  }

  const createConv = async () => {
    const created = await api.createConversation('新对话')
    await reloadConversations()
    await openConversation(created.conversationId)
  }

  const send = async () => {
    if (!currentId || !input.trim() || sending) return
    const userText = input.trim()
    setInput('')
    setFollow(true)
    setMessages((prev) => [
      ...prev,
      { role: 'USER', content: userText, localId: nextLocalId() },
      { role: 'ASSISTANT', content: '', messageId: null, events: [], streaming: true, localId: nextLocalId() },
    ])
    setSending(true)
    const handle = streamChat({
      conversationId: currentId,
      content: userText,
      mode,
      knowledgeId,
      onEvent: handleStreamEvent,
    })
    streamRef.current = handle
    try {
      await handle.promise
    } catch (e) {
      if (e?.name !== 'AbortError') message.error(e.message)
    } finally {
      setSending(false)
      streamRef.current = null
      streamIdRef.current = null
    }
  }

  const regenerate = async (msg) => {
    if (!currentId || !msg?.messageId || sending) return
    setFollow(true)
    setMessages((prev) => [
      ...prev,
      { role: 'ASSISTANT', content: '', messageId: null, events: [], streaming: true, localId: nextLocalId() },
    ])
    setSending(true)
    const handle = regenerateChat({
      conversationId: currentId,
      sourceMessageId: msg.messageId,
      mode,
      knowledgeId,
      onEvent: handleStreamEvent,
    })
    streamRef.current = handle
    try {
      await handle.promise
    } catch (e) {
      if (e?.name !== 'AbortError') message.error(e.message)
    } finally {
      setSending(false)
      streamRef.current = null
      streamIdRef.current = null
    }
  }

  const kbOptions = useMemo(() => knowledgeOptions, [knowledgeOptions])

  return (
    <ChatShell
      narrow={narrow}
      drawer={drawer}
      setDrawer={setDrawer}
      conversations={conversations}
      currentId={currentId}
      onSelectConversation={(id) => openConversation(id).catch((e) => message.error(e.message))}
      onCreateConversation={() => createConv().catch((e) => message.error(e.message))}
      onRenameConversation={async (id, title) => {
        await api.updateTitle(id, title)
        await reloadConversations()
        message.success('已重命名')
      }}
      onDeleteConversation={async (id) => {
        await api.deleteConversation(id)
        if (currentId === id) {
          setCurrentId(undefined)
          setMessages([])
          setCards([])
        }
        await reloadConversations()
        message.success('已删除')
      }}
      mode={mode}
      onModeChange={setMode}
      knowledgeId={knowledgeId}
      onKnowledgeChange={setKnowledgeId}
      knowledgeOptions={kbOptions}
      theme={theme}
      onToggleTheme={toggleTheme}
      onLogout={handleLogout}
      cards={cards}
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
      messages={messages}
      onFeedback={async (payload) => {
        await api.feedback(payload)
        message.success('反馈已提交')
      }}
      onRegenerate={regenerate}
      regenerating={sending}
      follow={follow}
      input={input}
      onInputChange={setInput}
      onSend={() => send().catch((e) => message.error(e.message))}
      onStop={() => stopStreaming()}
      sending={sending}
    />
  )
}
