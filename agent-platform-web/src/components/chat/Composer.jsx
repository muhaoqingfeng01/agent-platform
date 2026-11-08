import { Button, Input } from 'antd'

export default function Composer({ value, onChange, onSend, onStop, sending, disabled }) {
  return (
    <div className="chat-column" style={{ paddingBottom: 16 }}>
      <div className="composer-box">
        <Input.TextArea
          value={value}
          onChange={(e) => onChange(e.target.value)}
          onPressEnter={(e) => {
            if (!e.shiftKey) {
              e.preventDefault()
              if (!sending) onSend?.()
            }
          }}
          placeholder="输入消息，Enter 发送，Shift+Enter 换行"
          autoSize={{ minRows: 2, maxRows: 6 }}
          disabled={disabled}
          variant="borderless"
          style={{ color: 'var(--ap-text)', background: 'transparent' }}
        />
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8, marginTop: 8 }}>
          {sending ? (
            <Button danger onClick={onStop}>
              停止
            </Button>
          ) : (
            <Button type="primary" onClick={onSend} disabled={disabled || !value?.trim()}>
              发送
            </Button>
          )}
        </div>
      </div>
    </div>
  )
}
