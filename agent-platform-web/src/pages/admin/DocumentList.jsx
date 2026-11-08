import { useEffect, useState } from 'react'
import { Button, Space, Table, Tag, Upload, message } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../../api.js'
import { hasPermission } from '../../auth.js'

export default function DocumentList() {
  const { knowledgeId } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [rows, setRows] = useState([])
  const [kbName, setKbName] = useState(knowledgeId)

  const load = async () => {
    setLoading(true)
    try {
      const [docs, kb] = await Promise.all([
        api.listDocuments(knowledgeId, 0, 100),
        api.getKnowledgeBase(knowledgeId).catch(() => null),
      ])
      setRows(docs?.records || docs?.list || [])
      if (kb?.name) setKbName(kb.name)
    } catch (e) {
      message.error(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [knowledgeId])

  const columns = [
    { title: '文件名', dataIndex: 'filename', ellipsis: true },
    { title: '类型', dataIndex: 'fileType', width: 80 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (v) => <Tag>{v || '-'}</Tag>,
    },
    { title: '切片数', dataIndex: 'chunkCount', width: 80 },
    {
      title: '操作',
      width: 220,
      render: (_, r) => (
        <Space wrap>
          {hasPermission('doc:update') && (
            <Button
              size="small"
              onClick={async () => {
                try {
                  await api.parseDocument(r.documentId)
                  message.success('已触发解析')
                  load()
                } catch (e) {
                  message.error(e.message)
                }
              }}
            >
              解析
            </Button>
          )}
          {hasPermission('doc:delete') && (
            <Button
              size="small"
              danger
              onClick={async () => {
                try {
                  await api.deleteDocument(r.documentId)
                  message.success('已删除')
                  load()
                } catch (e) {
                  message.error(e.message)
                }
              }}
            >
              删除
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Space style={{ marginBottom: 12 }} wrap>
        <Button onClick={() => navigate('/admin/knowledge')}>返回知识库</Button>
        <h3 style={{ margin: 0, color: 'var(--ap-text)' }}>文档 · {kbName}</h3>
        {hasPermission('doc:upload') && (
          <Upload
            showUploadList={false}
            customRequest={async ({ file, onSuccess, onError }) => {
              try {
                await api.uploadDocument(knowledgeId, file)
                message.success('上传成功')
                onSuccess?.('ok')
                load()
              } catch (e) {
                message.error(e.message)
                onError?.(e)
              }
            }}
          >
            <Button type="primary">上传文档</Button>
          </Upload>
        )}
        <Button onClick={load}>刷新</Button>
      </Space>
      <Table rowKey="documentId" loading={loading} columns={columns} dataSource={rows} pagination={false} />
    </div>
  )
}
