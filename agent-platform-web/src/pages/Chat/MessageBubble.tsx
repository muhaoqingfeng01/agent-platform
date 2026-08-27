import { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Alert, Button, Tag, Typography, message as toast } from 'antd';
import { CopyOutlined } from '@ant-design/icons';
import type { ChatMessage } from '@/types/api';

interface Props {
  message: ChatMessage;
}

export function MessageBubble({ message }: Props) {
  const isUser = message.role === 'USER';
  const [copied, setCopied] = useState(false);

  const copy = async () => {
    if (!message.content) {
      return;
    }
    try {
      await navigator.clipboard.writeText(message.content);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    } catch {
      toast.error('复制失败');
    }
  };

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
          <div className={`bubble-md ${message.streaming ? 'stream-caret' : ''}`}>
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
        {message.content && !message.streaming ? (
          <div className="bubble-actions">
            <Button type="text" size="small" icon={<CopyOutlined />} onClick={() => void copy()}>
              {copied ? '已复制' : '复制'}
            </Button>
          </div>
        ) : null}
      </div>
    </div>
  );
}
