import { useEffect, useMemo, useRef } from 'react';
import { VariableSizeList, type ListChildComponentProps } from 'react-window';
import { MessageBubble } from '@/pages/Chat/MessageBubble';
import type { ChatMessage } from '@/types/api';

interface Props {
  messages: ChatMessage[];
  height: number;
}

function estimateHeight(message: ChatMessage): number {
  const contentLen = message.content?.length ?? 0;
  const lines = Math.max(1, Math.ceil(contentLen / 42));
  const extra =
    (message.thinking ? 28 : 0) +
    (message.references?.length ? 36 : 0) +
    (message.error ? 48 : 0);
  return Math.min(720, 72 + lines * 22 + extra);
}

export function MessageList({ messages, height }: Props) {
  const listRef = useRef<VariableSizeList>(null);
  const sizeMap = useMemo(() => messages.map(estimateHeight), [messages]);

  useEffect(() => {
    listRef.current?.resetAfterIndex(0, true);
    if (messages.length > 0) {
      listRef.current?.scrollToItem(messages.length - 1, 'end');
    }
  }, [messages, messages[messages.length - 1]?.content, messages.length]);

  if (height <= 0) {
    return null;
  }

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
