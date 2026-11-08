import { Button, Card, Space, Tag, Typography } from 'antd'

const { Text, Paragraph } = Typography

export default function ApprovalCard({ card, onApprove, onReject, disabled }) {
  const expired = Boolean(card.expired) || (card.remainingSeconds ?? 0) <= 0
  const pending = (card.status || 'PENDING') === 'PENDING' && !expired
  return (
    <Card
      size="small"
      style={{ borderColor: '#cf1322', marginBottom: 8 }}
      title={card.title || '需要审批'}
      extra={<Tag color="red">{card.statusLabel || card.status || 'PENDING'}</Tag>}
    >
      <Paragraph style={{ marginBottom: 8 }}>
        {card.operationDetail || '高风险操作待确认'}
      </Paragraph>
      <Text type="secondary">
        剩余 {Math.max(0, card.remainingSeconds ?? 0)} 秒
      </Text>
      <div style={{ marginTop: 8 }}>
        <Space>
          <Button
            type="primary"
            disabled={!pending || disabled}
            onClick={() => onApprove(card.approvalId)}
          >
            同意
          </Button>
          <Button
            danger
            disabled={!pending || disabled}
            onClick={() => onReject(card.approvalId)}
          >
            拒绝
          </Button>
        </Space>
      </div>
    </Card>
  )
}
