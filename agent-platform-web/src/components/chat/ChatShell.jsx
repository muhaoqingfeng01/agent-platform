import { Drawer, Layout } from 'antd'
import ChatHeader from './ChatHeader.jsx'
import ConversationSidebar from './ConversationSidebar.jsx'
import MessageList from './MessageList.jsx'
import Composer from './Composer.jsx'
import ApprovalCard from '../ApprovalCard.jsx'

const { Sider, Content } = Layout

export default function ChatShell({
  narrow,
  drawer,
  setDrawer,
  conversations,
  currentId,
  onSelectConversation,
  onCreateConversation,
  onRenameConversation,
  onDeleteConversation,
  mode,
  onModeChange,
  knowledgeId,
  onKnowledgeChange,
  knowledgeOptions,
  theme,
  onToggleTheme,
  onLogout,
  cards,
  onApprove,
  onReject,
  messages,
  onFeedback,
  onRegenerate,
  regenerating,
  follow,
  input,
  onInputChange,
  onSend,
  onStop,
  sending,
}) {
  const sidebar = (
    <ConversationSidebar
      conversations={conversations}
      currentId={currentId}
      onSelect={(id) => {
        onSelectConversation(id)
        setDrawer?.(false)
      }}
      onCreate={onCreateConversation}
      onRename={onRenameConversation}
      onDelete={onDeleteConversation}
    />
  )

  return (
    <Layout className="app-shell" style={{ minHeight: '100%' }}>
      <ChatHeader
        mode={mode}
        onModeChange={onModeChange}
        knowledgeId={knowledgeId}
        onKnowledgeChange={onKnowledgeChange}
        knowledgeOptions={knowledgeOptions}
        theme={theme}
        onToggleTheme={onToggleTheme}
        onOpenSidebar={() => setDrawer(true)}
        narrow={narrow}
        onLogout={onLogout}
      />
      <Layout style={{ background: 'transparent' }}>
        {!narrow && (
          <Sider
            width={260}
            theme="dark"
            style={{
              background: 'var(--ap-bg-elevated)',
              borderRight: '1px solid var(--ap-border)',
            }}
          >
            {sidebar}
          </Sider>
        )}
        <Content style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
          <div className="chat-column" style={{ paddingTop: 12 }}>
            {cards.map((card) => (
              <ApprovalCard
                key={card.approvalId}
                card={card}
                disabled={sending}
                onApprove={onApprove}
                onReject={onReject}
              />
            ))}
          </div>
          <MessageList
            messages={messages}
            conversationId={currentId}
            onFeedback={onFeedback}
            onRegenerate={onRegenerate}
            regenerating={regenerating}
            follow={follow}
          />
          <Composer
            value={input}
            onChange={onInputChange}
            onSend={onSend}
            onStop={onStop}
            sending={sending}
            disabled={!currentId}
          />
        </Content>
      </Layout>
      <Drawer
        open={drawer}
        onClose={() => setDrawer(false)}
        placement="left"
        width={280}
        styles={{ body: { padding: 0, background: 'var(--ap-bg-elevated)' } }}
      >
        {sidebar}
      </Drawer>
    </Layout>
  )
}
