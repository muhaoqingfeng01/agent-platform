import { Button, Empty, List, Popconfirm, Typography } from 'antd';
import { DeleteOutlined, MessageOutlined, PlusOutlined } from '@ant-design/icons';
import type { Conversation } from '@/types/api';

interface Props {
  items: Conversation[];
  currentId: string | null;
  loading?: boolean;
  onSelect: (id: string) => void;
  onCreate: () => void;
  onDelete: (id: string) => void;
}

export function ConversationList({ items, currentId, loading, onSelect, onCreate, onDelete }: Props) {
  return (
    <div className="conv-list">
      <div className="conv-list-header">
        <Typography.Text strong>会话</Typography.Text>
        <Button type="primary" size="small" icon={<PlusOutlined />} onClick={onCreate}>
          新建
        </Button>
      </div>
      {items.length === 0 && !loading ? (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无会话" />
      ) : (
        <List
          loading={loading}
          dataSource={items}
          renderItem={(item) => (
            <List.Item
              className={item.conversationId === currentId ? 'conv-item active' : 'conv-item'}
              onClick={() => onSelect(item.conversationId)}
              actions={[
                <Popconfirm
                  key="del"
                  title="删除该会话？"
                  onConfirm={(e) => {
                    e?.stopPropagation();
                    onDelete(item.conversationId);
                  }}
                >
                  <Button
                    type="text"
                    size="small"
                    danger
                    icon={<DeleteOutlined />}
                    onClick={(e) => e.stopPropagation()}
                  />
                </Popconfirm>,
              ]}
            >
              <List.Item.Meta
                avatar={<MessageOutlined />}
                title={item.title || '未命名对话'}
                description={item.status}
              />
            </List.Item>
          )}
        />
      )}
    </div>
  );
}
