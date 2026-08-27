import { Button, Input, Select, Space, Typography } from 'antd';
import { SendOutlined, StopOutlined } from '@ant-design/icons';
import { useState } from 'react';
import { CHAT_CONFIG } from '@/config/constants';
import type { InteractionMode, KnowledgeBase } from '@/types/api';

interface Props {
  disabled?: boolean;
  canKnowledge: boolean;
  mode: InteractionMode;
  knowledgeId?: string;
  knowledgeBases: KnowledgeBase[];
  onModeChange: (mode: InteractionMode) => void;
  onKnowledgeChange: (id?: string) => void;
  onSend: (content: string) => void;
  onStop?: () => void;
  initialValue?: string;
}

export function Composer({
  disabled,
  canKnowledge,
  mode,
  knowledgeId,
  knowledgeBases,
  onModeChange,
  onKnowledgeChange,
  onSend,
  onStop,
  initialValue = '',
}: Props) {
  const [value, setValue] = useState(initialValue);

  const submit = () => {
    const text = value.trim();
    if (!text || disabled) {
      return;
    }
    onSend(text);
    setValue('');
  };

  return (
    <div className="composer">
      <Space wrap className="composer-toolbar">
        {canKnowledge ? (
          <Select<InteractionMode>
            value={mode}
            style={{ width: 160 }}
            onChange={onModeChange}
            options={[
              { value: 'CONVERSATION', label: '智能对话' },
              { value: 'KNOWLEDGE_SEARCH', label: '知识检索' },
            ]}
          />
        ) : null}
        {canKnowledge && mode === 'KNOWLEDGE_SEARCH' ? (
          <Select
            allowClear
            placeholder="全部已启用知识库"
            style={{ minWidth: 200 }}
            value={knowledgeId}
            onChange={(id) => onKnowledgeChange(id)}
            options={knowledgeBases.map((kb) => ({
              value: kb.knowledgeId,
              label: kb.name,
            }))}
          />
        ) : null}
      </Space>
      <Input.TextArea
        value={value}
        disabled={disabled}
        maxLength={CHAT_CONFIG.maxMessageLength}
        autoSize={{ minRows: 2, maxRows: 6 }}
        placeholder={disabled ? '正在生成回复...' : '输入问题，Enter 发送，Shift+Enter 换行'}
        onChange={(e) => setValue(e.target.value)}
        onPressEnter={(e) => {
          if (e.shiftKey || e.nativeEvent.isComposing) {
            return;
          }
          e.preventDefault();
          submit();
        }}
      />
      <div className="composer-actions">
        <Typography.Text type="secondary">
          {value.length}/{CHAT_CONFIG.maxMessageLength}
        </Typography.Text>
        {disabled ? (
          <Button danger icon={<StopOutlined />} onClick={onStop}>
            停止
          </Button>
        ) : (
          <Button type="primary" icon={<SendOutlined />} disabled={!value.trim()} onClick={submit}>
            发送
          </Button>
        )}
      </div>
    </div>
  );
}
