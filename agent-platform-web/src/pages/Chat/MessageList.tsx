import { useEffect, useMemo, useRef } from 'react';
import { Button, Empty, Typography } from 'antd';
import { VariableSizeList, type ListChildComponentProps } from 'react-window';
import { MessageBubble } from '@/pages/Chat/MessageBubble';
import type { ChatMessage } from '@/types/api';

interface Props {
  messages: ChatMessage[];
  height: number;
  onSuggest?: (text: string) => void;
}

const SUGGESTIONS = ['这个平台能做什么？', '帮我用简洁的语言介绍知识库检索', '总结一下刚才的对话要点'];
const VIRTUAL_THRESHOLD = 80;

function estimateHeight(message: ChatMessage): number {
  const contentLen = message.content?.length ?? 0;
  const lines = Math.max(1, Math.ceil(contentLen / 42));
  const extra =
    (message.thinking ? 28 : 0) +
    (message.references?.length ? 36 : 0) +
    (message.error ? 48 : 0);
  return Math.min(720, 72 + lines * 22 + extra);
}

function EmptyChat({ onSuggest }: { onSuggest?: (text: string) => void }) {
  return (
    <div className="empty-chat">
      <Empty description="开始一段新对话" />
      <div className="suggest-row">
        {SUGGESTIONS.map((text) => (
          <Button key={text} onClick={() => onSuggest?.(text)}>
            {text}
          </Button>
        ))}
      </div>
      <Typography.Text type="secondary">Enter 发送，Shift+Enter 换行</Typography.Text>
    </div>
  );
}

export function MessageList({ messages, height, onSuggest }: Props) {
  if (messages.length === 0) {
    return <EmptyChat onSuggest={onSuggest} />;
  }

  if (messages.length > VIRTUAL_THRESHOLD && height > 0) {
    return <VirtualMessageList messages={messages} height={height} />;
  }

  return <NativeMessageList messages={messages} />;
}

function NativeMessageList({ messages }: { messages: ChatMessage[] }) {
  const scroller = useRef<HTMLDivElement>(null);
  const stickToBottom = useRef(true);

  useEffect(() => {
    const el = scroller.current;
    if (!el || !stickToBottom.current) {
      return;
    }
    el.scrollTop = el.scrollHeight;
  }, [messages, messages[messages.length - 1]?.content, messages[messages.length - 1]?.thinking]);

  return (
    <div
      className="message-scroller"
      ref={scroller}
      onScroll={() => {
        const el = scroller.current;
        if (!el) {
          return;
        }
        stickToBottom.current = el.scrollHeight - el.scrollTop - el.clientHeight < 96;
      }}
    >
      {messages.map((item) => (
        <MessageBubble key={item.messageId} message={item} />
      ))}
    </div>
  );
}

function VirtualMessageList({ messages, height }: { messages: ChatMessage[]; height: number }) {
  const listRef = useRef<VariableSizeList>(null);
  const sizeMap = useMemo(() => messages.map(estimateHeight), [messages]);

  useEffect(() => {
    listRef.current?.resetAfterIndex(0, true);
    if (messages.length > 0) {
      listRef.current?.scrollToItem(messages.length - 1, 'end');
    }
  }, [messages, messages[messages.length - 1]?.content, messages.length]);

  const Row = ({ index, style }: ListChildComponentProps) => (
    <div style={{ ...style, padding: '0 12px' }}>
      <MessageBubble message={messages[index]} />
    </div>
  );

  return (
    <VariableSizeList
      ref={listRef}
      height={height}
      width="100%"
      itemCount={messages.length}
      itemSize={(index) => sizeMap[index] ?? 96}
      itemKey={(index) => messages[index]?.messageId ?? String(index)}
    >
      {Row}
    </VariableSizeList>
  );
}
