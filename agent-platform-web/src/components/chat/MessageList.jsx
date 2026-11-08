import { useEffect, useRef } from 'react'
import MessageItem from './MessageItem.jsx'

export default function MessageList({
  messages,
  conversationId,
  onFeedback,
  onRegenerate,
  regenerating,
  follow,
}) {
  const endRef = useRef(null)
  const wrapRef = useRef(null)

  useEffect(() => {
    if (!follow) return
    endRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' })
  }, [messages, follow])

  return (
    <div ref={wrapRef} style={{ flex: 1, overflow: 'auto', padding: '8px 0 16px' }}>
      <div className="chat-column">
        {messages.length === 0 && (
          <div style={{ color: 'var(--ap-text-muted)', textAlign: 'center', padding: '48px 12px' }}>
            开始一段新对话，或从左侧选择历史会话
          </div>
        )}
        {messages.map((msg, idx) => (
          <MessageItem
            key={msg.localId || msg.messageId || idx}
            msg={msg}
            conversationId={conversationId}
            onFeedback={onFeedback}
            onRegenerate={onRegenerate}
            regenerating={regenerating}
          />
        ))}
        <div ref={endRef} />
      </div>
    </div>
  )
}
