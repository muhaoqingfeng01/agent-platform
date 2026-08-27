import { useEffect, useRef, useState } from 'react';
import { Button, Drawer, Layout, Space, Typography, message } from 'antd';
import { LogoutOutlined, MenuOutlined } from '@ant-design/icons';
import { ConversationList } from '@/pages/Chat/ConversationList';
import { MessageList } from '@/pages/Chat/MessageList';
import { Composer } from '@/pages/Chat/Composer';
import { PERMISSIONS } from '@/config/constants';
import { useAuthStore } from '@/stores/useAuthStore';
import { useConversationStore } from '@/stores/useConversationStore';

const { Sider, Content, Header } = Layout;

export function ChatPage() {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);
  const hasPermission = useAuthStore((s) => s.hasPermission);
  const canKnowledge = hasPermission(PERMISSIONS.kbRead);

  const {
    conversations,
    currentId,
    messagesByConv,
    knowledgeBases,
    mode,
    knowledgeId,
    streaming,
    streamError,
    loadingList,
    loadConversations,
    loadKnowledgeBases,
    createNew,
    select,
    remove,
    setMode,
    setKnowledgeId,
    send,
    stop,
    reconnect,
  } = useConversationStore();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [listHeight, setListHeight] = useState(480);
  const [composerSeed, setComposerSeed] = useState(0);
  const [composerPreset, setComposerPreset] = useState('');
  const paneRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    void loadConversations().catch((err) => message.error(err.message || '加载会话失败'));
    if (canKnowledge) {
      void loadKnowledgeBases();
    }
  }, [canKnowledge, loadConversations, loadKnowledgeBases]);

  useEffect(() => {
    const el = paneRef.current;
    if (!el) {
      return;
    }
    const observer = new ResizeObserver(() => {
      setListHeight(el.clientHeight);
    });
    observer.observe(el);
    setListHeight(el.clientHeight);
    return () => observer.disconnect();
  }, []);

  const messages = currentId ? messagesByConv[currentId] ?? [] : [];

  const onCreate = async () => {
    try {
      await createNew();
      setMobileOpen(false);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '创建会话失败');
    }
  };

  const onSelect = async (id: string) => {
    try {
      await select(id);
      setMobileOpen(false);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '加载消息失败');
    }
  };

  const sider = (
    <ConversationList
      items={conversations}
      currentId={currentId}
      loading={loadingList}
      onSelect={(id) => void onSelect(id)}
      onCreate={() => void onCreate()}
      onDelete={(id) => void remove(id).catch((err) => message.error(err.message))}
    />
  );

  return (
    <Layout className="chat-layout">
      <Sider width={280} className="chat-sider desktop-only" theme="light">
        {sider}
      </Sider>
      <Layout>
        <Header className="chat-header">
          <Space>
            <Button className="mobile-only" icon={<MenuOutlined />} onClick={() => setMobileOpen(true)} />
            <Typography.Title level={4} style={{ margin: 0, color: '#fff' }}>
              {conversations.find((c) => c.conversationId === currentId)?.title || '智能对话'}
            </Typography.Title>
          </Space>
          <Space>
            <Typography.Text style={{ color: '#fff' }}>{user?.username}</Typography.Text>
            <Button
              icon={<LogoutOutlined />}
              onClick={() => void logout().then(() => (window.location.href = '/login'))}
            >
              退出
            </Button>
          </Space>
        </Header>
        <Content className="chat-content">
          {streamError ? (
            <div className="stream-banner">
              <span>{streamError}</span>
              <Button size="small" onClick={reconnect}>
                重连
              </Button>
            </div>
          ) : null}
          <div className="message-pane" ref={paneRef}>
            {!currentId ? (
              <div className="page-center muted">新建或选择一个会话开始聊天</div>
            ) : (
              <MessageList
                messages={messages}
                height={listHeight}
                onSuggest={(text) => {
                  setComposerPreset(text);
                  setComposerSeed((n) => n + 1);
                }}
              />
            )}
          </div>
          <Composer
            key={composerSeed}
            initialValue={composerPreset}
            disabled={streaming}
            canKnowledge={canKnowledge}
            mode={mode}
            knowledgeId={knowledgeId}
            knowledgeBases={knowledgeBases}
            onModeChange={setMode}
            onKnowledgeChange={setKnowledgeId}
            onStop={stop}
            onSend={(content) => void send(content).catch((err) => message.error(err.message))}
          />
        </Content>
      </Layout>
      <Drawer title="会话" placement="left" open={mobileOpen} onClose={() => setMobileOpen(false)} width={280}>
        {sider}
      </Drawer>
    </Layout>
  );
}
