import React from 'react'
import { Tag } from 'antd'
import type { AppType } from '@/types'
import { AppTypeLabels } from '@/types'

// 定义类型对应的颜色
const typeColors: Record<AppType, string> = {
  self_build: 'blue',      // 自建应用 - 蓝色
  third_party: 'purple',   // 第三方应用 - 紫色
  personal: 'cyan',        // 个人应用 - 青色
}

export interface AppTypeTagProps {
  /** 应用类型 */
  type: AppType
  /** 是否为小尺寸 */
  size?: 'small' | 'default'
  /** 额外的类名 */
  className?: string
}

/**
 * 应用类型标签组件
 * 根据应用类型显示不同的颜色和文字
 */
const AppTypeTag: React.FC<AppTypeTagProps> = ({ 
  type, 
  size = 'default',
  className = '' 
}) => {
  const typeText = AppTypeLabels[type]
  const color = typeColors[type]
  
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
      {typeText}
    </Tag>
  )
}

export default AppTypeTag