import React from 'react'
import { Card, Space, Typography, Tag, Divider } from 'antd'
import { Link } from 'react-router-dom'
import { AppStatusBadge } from '@/components/AppStatusBadge'
import { AppTypeTag } from '@/components/AppTypeTag'
import type { Application } from '@/types'
import dayjs from 'dayjs'

const { Title, Text } = Typography

export interface AppCardProps {
  /** 应用信息 */
  application: Application
  /** 额外类名 */
  className?: string
  /** 点击卡片回调函数 */
  onClick?: () => void
}

/**
 * 应用卡片组件
 * 以卡片形式展示应用的基本信息
 */
const AppCard: React.FC<AppCardProps> = ({ 
  application,
  className = '',
  onClick 
}) => {
  return (
    <Card
      className={className}
      hoverable
      onClick={onClick}
      style={{ cursor: onClick ? 'pointer' : 'default' }}
      cover={
        application.iconUrl ? (
          <div 
            style={{ 
              height: 120, 
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              background: '#fafafa',
              borderBottom: '1px solid #f0f0f0'
            }}
          >
            <img
              src={application.iconUrl}
              alt={`${application.name} 图标`}
              style={{ 
                maxWidth: '80%',
                maxHeight: 80,
                objectFit: 'contain'
              }}
            />
          </div>
        ) : undefined
      }
    >
      <Card.Meta
        title={
          <Link to={`/apps/${application.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
            <Title level={4} style={{ margin: 0, fontSize: '16px' }}>
              {application.name}
            </Title>
          </Link>
        }
        description={
          <div style={{ marginTop: 8 }}>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
              <AppTypeTag type={application.type} size="small" />
              <AppStatusBadge status={application.status} size="small" />
            </div>
            {application.description && (
              <Text 
                style={{ marginTop: 8, display: 'block' }} 
                ellipsis={{ tooltip: application.description }}
              >
                {application.description}
              </Text>
            )}
          </div>
        }
      />
      
      <Divider style={{ margin: '12px 0' }} />
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Space size="small">
          <Tag color="default" style={{ margin: 0 }}>
            ID: {application.id.slice(0, 8)}...
          </Tag>
        </Space>
        <Space size="small">
          <Text type="secondary" style={{ fontSize: '12px' }}>
            {dayjs(application.createdAt).format('YYYY-MM-DD')}
          </Text>
        </Space>
      </div>
      
      {application.callbackUrl && (
        <div style={{ marginTop: 8 }}>
          <Text type="secondary" style={{ fontSize: '12px' }}>
            <span style={{ fontWeight: 'bold' }}>回调URL:</span>{' '}
            <Text copyable={{ text: application.callbackUrl }}>
              <Text ellipsis={{ tooltip: application.callbackUrl }}>
                {application.callbackUrl}
              </Text>
            </Text>
          </Text>
        </div>
      )}
    </Card>
  )
}

export default AppCard