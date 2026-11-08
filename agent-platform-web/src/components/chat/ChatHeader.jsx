import { Button, Select, Space, Typography } from 'antd'
import { useNavigate } from 'react-router-dom'
import { canAccessAdmin } from '../../auth.js'

const MODE_OPTIONS = [
  { value: 'CONVERSATION', label: '智能对话' },
  { value: 'KNOWLEDGE_SEARCH', label: '知识检索' },
  { value: 'TASK_EXECUTION', label: '任务执行' },
  { value: 'ANALYSIS', label: '分析推理' },
  { value: 'APPROVAL', label: '安全审批' },
]

export default function ChatHeader({
  mode,
  onModeChange,
  knowledgeId,
  onKnowledgeChange,
  knowledgeOptions,
  theme,
  onToggleTheme,
  onOpenSidebar,
  narrow,
  onLogout,
}) {
  const navigate = useNavigate()
  const showAdmin = canAccessAdmin()
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 12,
        padding: '12px 16px',
        borderBottom: '1px solid var(--ap-border)',
        background: 'var(--ap-bg-elevated)',
      }}
    >
      <Space wrap>
        {narrow && (
          <Button onClick={onOpenSidebar} size="small">
            会话
          </Button>
        )}
        <Typography.Text strong style={{ color: 'var(--ap-text)' }}>
          Agent Platform
        </Typography.Text>
        <Select value={mode} onChange={onModeChange} options={MODE_OPTIONS} style={{ width: 140 }} size="small" />
        {mode === 'KNOWLEDGE_SEARCH' && (
          <Select
            allowClear
            placeholder="选择知识库"
            value={knowledgeId}
            onChange={onKnowledgeChange}
            options={knowledgeOptions}
            style={{ width: 200 }}
            size="small"
          />
        )}
      </Space>
      <Space>
        {showAdmin && (
          <Button size="small" type="primary" ghost onClick={() => navigate('/admin')}>
            管理后台
          </Button>
        )}
        <Button size="small" onClick={onToggleTheme}>
          {theme === 'dark' ? '浅色' : '深色'}
        </Button>
        <Button size="small" onClick={onLogout}>
          退出
        </Button>
      </Space>
    </div>
  )
}
