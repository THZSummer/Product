import React, { useEffect } from 'react'
import { 
  Card, 
  Descriptions, 
  Button, 
  Space, 
  Tag, 
  Typography,
  Skeleton,
  Divider, 
  Popconfirm,
  message 
} from 'antd'
import { 
  EditOutlined, 
  DeleteOutlined, 
  ArrowLeftOutlined,
  CopyOutlined 
} from '@ant-design/icons'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useApplicationStore } from '@/stores'
import { AppStatusBadge } from '@/components/AppStatusBadge'
import { AppTypeTag } from '@/components/AppTypeTag'
import { AppStatus, AppType } from '@/types'
import dayjs from 'dayjs'
import './ApplicationDetail.less'

const { Title, Text } = Typography

/**
 * 应用详情页组件
 * 展示应用的详细信息，支持编辑和删除
 */
const ApplicationDetail: React.FC = () => {
  const { appId } = useParams<{ appId: string }>()
  const navigate = useNavigate()
  
  // 从 store 获取数据和操作方法
  const { 
    currentApplication, 
    loading,
    fetchApplicationDetail,
    deleteApplication
  } = useApplicationStore()

  useEffect(() => {
    if (appId) {
      fetchApplicationDetail(appId)
    }
  }, [appId])

  // 返回列表页
  const handleBack = () => {
    navigate('/apps')
  }

  // 编辑应用
  const handleEdit = () => {
    if (currentApplication) {
      navigate(`/apps/${currentApplication.id}/edit`)
    }
  }

  // 删除应用
  const handleDelete = async () => {
    if (!currentApplication) return
    
    const success = await deleteApplication(currentApplication.id)
    if (success) {
      message.success('应用删除成功')
      navigate('/apps')
    } else {
      message.error('应用删除失败，请重试')
    }
  }

  // 检查是否为草稿状态
  const isDraft = currentApplication?.status === AppStatus.DRAFT

  // 定义默认值  
  const getDefaultAppType = (): AppType => AppType.SELF_BUILD
  const getDefaultAppStatus = (): AppStatus => AppStatus.DRAFT

  // 副标题区域
  const extra = (
    <Space>
      <Button 
        icon={<ArrowLeftOutlined />} 
        onClick={handleBack}
      >
        返回
      </Button>
      <Link to={`/apps/${currentApplication?.id}/edit`} style={{ pointerEvents: isDraft ? 'auto' : 'none' }}>
        <Button 
          type="primary" 
          icon={<EditOutlined />} 
          disabled={!isDraft}
        >
          {isDraft ? '编辑' : '草稿才能编辑'}
        </Button>
      </Link>
      <Popconfirm
        title="确定要删除这个应用吗？"
        description="删除后应用将被移动到回收站，30天后自动清除。"
        onConfirm={handleDelete}
        okText="确定"
        cancelText="取消"
      >
        <Button 
          danger 
          icon={<DeleteOutlined />} 
          disabled={!isDraft}
        >
          删除
        </Button>
      </Popconfirm>
    </Space>
  )

  return (
    <div className="application-detail">
      <Skeleton loading={loading.detail} active>
        <Card
          title={
            <Space>
              <img 
                src={currentApplication?.iconUrl || '/default-app-icon.png'} 
                alt="应用图标" 
                style={{ width: 32, height: 32, borderRadius: 8 }} 
              />
              <div>
                <Title level={4} style={{ marginBottom: 4 }}>
                  {currentApplication?.name}
                </Title>
                <Space>
                  <AppTypeTag type={currentApplication?.type || getDefaultAppType()} />
                  <AppStatusBadge status={currentApplication?.status || getDefaultAppStatus()} />
                </Space>
              </div>
            </Space>
          }
          extra={extra}
          style={{ marginBottom: 16 }}
        >
          <Descriptions column={2} bordered>
            <Descriptions.Item label="应用ID">
              <Space size="small">
                <Text code>{currentApplication?.id}</Text>
                <Button 
                  size="small" 
                  icon={<CopyOutlined />}
                  onClick={() => {
                    navigator.clipboard.writeText(currentApplication?.id || '')
                    message.success('应用ID已复制到剪贴板')
                  }}
                  title="复制应用ID"
                />
              </Space>
            </Descriptions.Item>
            
            <Descriptions.Item label="应用类型">
              <AppTypeTag type={currentApplication?.type || getDefaultAppType()} />
            </Descriptions.Item>
            
            <Descriptions.Item label="应用状态">
              <AppStatusBadge status={currentApplication?.status || getDefaultAppStatus()} />
            </Descriptions.Item>
            
            <Descriptions.Item label="所有者ID">
              <Text code>{currentApplication?.ownerId}</Text>
            </Descriptions.Item>
            
            <Descriptions.Item label="描述" span={2}>
              <Text>{currentApplication?.description || '-'}</Text>
            </Descriptions.Item>
            
            <Descriptions.Item label="图标URL" span={2}>
              {currentApplication?.iconUrl ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <img 
                    src={currentApplication.iconUrl} 
                    alt="应用图标" 
                    style={{ width: 32, height: 32, borderRadius: 4, border: '1px solid #ddd' }} 
                  />
                  <Text copyable={{ text: currentApplication.iconUrl }}>
                    {currentApplication.iconUrl}
                  </Text>
                </div>
              ) : (
                <Text>-</Text>
              )}
            </Descriptions.Item>
            
            <Descriptions.Item label="回调URL" span={2}>
              {currentApplication?.callbackUrl ? (
                <Text copyable={{ text: currentApplication.callbackUrl }}>
                  <a href={currentApplication.callbackUrl} target="_blank" rel="noopener noreferrer">
                    {currentApplication.callbackUrl}
                  </a>
                </Text>
              ) : (
                <Text type="secondary">-</Text>
              )}
            </Descriptions.Item>
            
            <Descriptions.Item label="创建时间" span={1}>
              {currentApplication?.createdAt 
                ? dayjs(currentApplication.createdAt).format('YYYY-MM-DD HH:mm:ss') 
                : '-'
              }
            </Descriptions.Item>
            
            <Descriptions.Item label="更新时间" span={1}>
              {currentApplication?.updatedAt 
                ? dayjs(currentApplication.updatedAt).format('YYYY-MM-DD HH:mm:ss') 
                : '-'
              }
            </Descriptions.Item>
          </Descriptions>
          
          <Divider />
          
          <div>
            <Title level={5}>应用配置示例</Title>
            <div style={{ padding: '16px', background: '#f6ffed', border: '1px solid #b7eb8f', borderRadius: 4 }}>
              <Text strong>AppKey/AppSecret:</Text>
              <pre style={{ 
                margin: '8px 0', 
                padding: '12px', 
                background: '#fff', 
                border: '1px solid #eee', 
                borderRadius: 4,
                fontFamily: 'monospace',
                fontSize: '12px',
                overflowX: 'auto'
              }}>
{`{
  "appKey": "${currentApplication?.id || 'app_placeholder'}",
  "appSecret": "secret_value_for_${currentApplication?.name?.replace(/\s+/g, '_') || 'your_app_name'}"
}`}
              </pre>
              <Text type="secondary" style={{ display: 'block' }}>
                这些是应用集成所需的基本认证信息
              </Text>
            </div>
          </div>
        </Card>
      </Skeleton>
    </div>
  )
}

export default ApplicationDetail