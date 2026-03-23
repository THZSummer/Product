import React from 'react'
import { Tag } from 'antd'
import type { AppStatus } from '@/types'
import { AppStatusLabels } from '@/types'

// 定义状态对应的颜色
const statusColors: Record<AppStatus, string> = {
  draft: 'orange',      // 草稿 - 橙色
  active: 'green',      // 已启用 - 绿色
  disabled: 'red',      // 已禁用 - 红色
  deleted: 'gray',      // 已删除 - 灰色
}

export interface AppStatusBadgeProps {
  /** 应用状态 */
  status: AppStatus
  /** 是否为小尺寸 */
  size?: 'small' | 'default'
  /** 额外的类名 */
  className?: string
}

/**
 * 应用状态徽章组件
 * 根据应用状态显示不同的颜色和文字
 */
const AppStatusBadge: React.FC<AppStatusBadgeProps> = ({ 
  status, 
  size = 'default',
  className = '' 
}) => {
  const statusText = AppStatusLabels[status]
  const color = statusColors[status]
  
  return (
    <Tag 
      color={color} 
      className={className}
      style={{ 
        fontSize: size === 'small' ? '12px' : '14px',
        margin: 0,
        cursor: 'default'
      }}
    >
      {statusText}
    </Tag>
  )
}

export default AppStatusBadge