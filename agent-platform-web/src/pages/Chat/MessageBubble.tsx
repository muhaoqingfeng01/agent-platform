import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Alert, Tag, Typography } from 'antd';
import type { ChatMessage } from '@/types/api';

interface Props {
  message: ChatMessage;
}

export function MessageBubble({ message }: Props) {
  const isUser = message.role === 'USER';
  return (
    <div className={isUser ? 'bubble-row user' : 'bubble-row assistant'}>
      <div className={isUser ? 'bubble user' : 'bubble assistant'}>
        {!isUser && message.thinking ? (
          <Typography.Text type="secondary" className="thinking">
            {message.thinking}
          </Typography.Text>
        ) : null}
        {!isUser && message.references && message.references.length > 0 ? (
          <div className="ref-tags">
            {message.references.map((ref) => (
              <Tag key={`${ref.documentId}-${ref.filename}`}>{ref.filename || ref.documentId}</Tag>
            ))}
          </div>
        ) : null}
        {isUser ? (
          <div className="bubble-text">{message.content}</div>
        ) : (
          <div className="bubble-md">
            {message.content ? (
              <ReactMarkdown remarkPlugins={[remarkGfm]}>{message.content}</ReactMarkdown>
            ) : (
              <Typography.Text type="secondary">
                {message.streaming ? '正在生成...' : '（空回复）'}
              </Typography.Text>
            )}
          </div>
        )}
        {message.error ? <Alert type="error" showIcon message={message.error} /> : null}
      </div>
    </div>
  );
}
