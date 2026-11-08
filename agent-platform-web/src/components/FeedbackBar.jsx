import { Button, Input, Space } from 'antd'
import { useState } from 'react'

export default function FeedbackBar({ messageId, conversationId, onSubmit }) {
  const [reason, setReason] = useState('')
  return (
    <Space size={4} style={{ marginTop: 4 }}>
      <Button
        size="small"
        onClick={() => onSubmit({ msgId: messageId, messageId, conversationId, feedback: 'LIKE' })}
      >
        赞
      </Button>
      <Button
        size="small"
        onClick={() =>
          onSubmit({ msgId: messageId, messageId, conversationId, feedback: 'DISLIKE', reason })
        }
      >
        踩
      </Button>
      <Input
        size="small"
        placeholder="点踩原因（可选）"
        value={reason}
        onChange={(e) => setReason(e.target.value)}
        style={{ width: 160 }}
      />
    </Space>
  )
}
