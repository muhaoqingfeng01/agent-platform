import { Button, Dropdown, Input, Modal, Space, Typography, message } from 'antd'
import { useState } from 'react'

export default function ConversationSidebar({
  conversations,
  currentId,
  onSelect,
  onCreate,
  onRename,
  onDelete,
}) {
  const [renameOpen, setRenameOpen] = useState(false)
  const [renameId, setRenameId] = useState()
  const [title, setTitle] = useState('')

  const openRename = (item) => {
    setRenameId(item.conversationId)
    setTitle(item.title || '')
    setRenameOpen(true)
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', padding: 12, gap: 10 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography.Text strong style={{ color: 'var(--ap-text)' }}>
          Agent
        </Typography.Text>
      </div>
      <Button type="primary" block onClick={onCreate}>
        + 新对话
      </Button>
      <div style={{ flex: 1, overflow: 'auto', display: 'flex', flexDirection: 'column', gap: 4 }}>
        {conversations.map((item) => {
          const active = item.conversationId === currentId
          const menu = {
            items: [
              { key: 'rename', label: '重命名' },
              { key: 'delete', label: '删除', danger: true },
            ],
            onClick: ({ key, domEvent }) => {
              domEvent.stopPropagation()
              if (key === 'rename') openRename(item)
              if (key === 'delete') {
                Modal.confirm({
                  title: '删除会话？',
                  content: '删除后可从后台逻辑删除，前端列表将刷新。',
                  onOk: () => onDelete?.(item.conversationId),
                })
              }
            },
          }
          return (
            <Dropdown key={item.conversationId} menu={menu} trigger={['contextMenu']}>
              <div
                className={`sidebar-item${active ? ' active' : ''}`}
                onClick={() => onSelect?.(item.conversationId)}
              >
                <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                  <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {item.title || item.conversationId}
                  </span>
                  <Button
                    type="text"
                    size="small"
                    style={{ color: 'var(--ap-text-muted)' }}
                    onClick={(e) => {
                      e.stopPropagation()
                      openRename(item)
                    }}
                  >
                    …
                  </Button>
                </Space>
              </div>
            </Dropdown>
          )
        })}
      </div>

      <Modal
        title="重命名会话"
        open={renameOpen}
        onCancel={() => setRenameOpen(false)}
        onOk={async () => {
          if (!title.trim()) {
            message.warning('标题不能为空')
            return
          }
          await onRename?.(renameId, title.trim())
          setRenameOpen(false)
        }}
      >
        <Input value={title} onChange={(e) => setTitle(e.target.value)} maxLength={64} />
      </Modal>
    </div>
  )
}
