import ReactMarkdown from 'react-markdown'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { Button, Space } from 'antd'
import FeedbackBar from '../FeedbackBar.jsx'
import EventBlocks from './EventBlocks.jsx'

export default function MessageItem({
  msg,
  conversationId,
  onFeedback,
  onRegenerate,
  regenerating,
}) {
  const isUser = (msg.role || '').toUpperCase() === 'USER'
  return (
    <div style={{ marginBottom: 16, display: 'flex', flexDirection: 'column', gap: 6 }}>
      <div style={{ fontSize: 12, color: 'var(--ap-text-muted)', alignSelf: isUser ? 'flex-end' : 'flex-start' }}>
        {isUser ? '你' : '助手'}
        {msg.stopped ? ' · 已停止' : ''}
      </div>
      {!isUser && <EventBlocks events={msg.events} />}
      <div className={isUser ? 'msg-user' : 'msg-assistant'}>
        {isUser ? (
          msg.content
        ) : (
          <ReactMarkdown
            components={{
              code({ inline, className, children, ...props }) {
                const match = /language-(\w+)/.exec(className || '')
                if (!inline && match) {
                  return (
                    <SyntaxHighlighter style={oneDark} language={match[1]} PreTag="div" {...props}>
                      {String(children).replace(/\n$/, '')}
                    </SyntaxHighlighter>
                  )
                }
                return (
                  <code className={className} {...props}>
                    {children}
                  </code>
                )
              },
            }}
          >
            {msg.content || (msg.streaming ? '…' : '')}
          </ReactMarkdown>
        )}
      </div>
      {!isUser && msg.messageId && conversationId && (
        <Space size={8} wrap>
          <FeedbackBar
            messageId={msg.messageId}
            conversationId={conversationId}
            onSubmit={onFeedback}
          />
          <Button size="small" type="link" disabled={regenerating} onClick={() => onRegenerate?.(msg)}>
            重生
          </Button>
        </Space>
      )}
    </div>
  )
}
