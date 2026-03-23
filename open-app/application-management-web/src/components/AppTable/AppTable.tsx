import React, { useState, useEffect } from 'react'
import { Table, Space, Button, Input, Typography, Tag, Popconfirm, notification } from 'antd'
import { SearchOutlined, EditOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons'
import { Link, useNavigate } from 'react-router-dom'
import { useApplicationStore } from '@/stores'
import { AppStatusBadge } from '@/components/AppStatusBadge'
import { AppTypeTag } from '@/components/AppTypeTag'
import type { Application, AppStatus, AppType } from '@/types'
import dayjs from 'dayjs'

// Type assertion to work around onFilter type mismatch issue
type ColumnFilterItem = {
  text: string;
  value: any;
}

const { Text } = Typography

export interface AppTableProps {
  /** 是否显示操作列 */
  showActions?: boolean
  /** 是否可选中行 */
  selectable?: boolean
  /** 选中状态改变回调 */
  onSelectionChange?: (selectedRowKeys: React.Key[]) => void
}

/**
 * 应用列表表格组件
 * 展示应用的基本信息，支持搜索、筛选等功能
 */
const AppTable: React.FC<AppTableProps> = ({ 
  showActions = true, 
  selectable = false,
  onSelectionChange 
}) => {
  const navigate = useNavigate()
  
  // 从 store 获取状态和方法
  const { 
    applications, 
    total, 
    page, 
    pageSize, 
    filters,
    loading,
    fetchApplications,
    deleteApplication,
    setFilters,
    setPage
  } = useApplicationStore()
  
  // 本地状态
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [searchKeyword, setSearchKeyword] = useState(filters.keyword || '')

  // 选中行发生变化
  const onSelectChange = (newSelectedRowKeys: React.Key[]) => {
    setSelectedRowKeys(newSelectedRowKeys)
    if (onSelectionChange) {
      onSelectionChange(newSelectedRowKeys)
    }
  }

  // 表格行选择配置
  const rowSelection = selectable ? {
    selectedRowKeys,
    onChange: onSelectChange,
  } : undefined

  // 表格列定义
  const columns = [
    {
      title: '应用名称',
      dataIndex: 'name',
      key: 'name',
      sorter: true,
      width: 200,
      render: (name: string, record: Application) => (
        <Link to={`/apps/${record.id}`} style={{ textDecoration: 'none' }}>
          <Space>
            {record.iconUrl ? (
              <img 
                src={record.iconUrl} 
                alt="应用图标" 
                style={{ width: 24, height: 24, borderRadius: '4px' }} 
              />
            ) : (
              <div style={{ width: 24, height: 24, borderRadius: '4px', backgroundColor: '#f0f0f0' }}></div>
            )}
            <Text strong ellipsis={{ tooltip: name }}>{name}</Text>
          </Space>
        </Link>
      ),
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120,
      render: (type: AppType) => <AppTypeTag type={type} size="small" />,
      filters: [
        { text: '自建应用', value: 'self_build' },
        { text: '第三方应用', value: 'third_party' },
        { text: '个人应用', value: 'personal' },
      ] as ColumnFilterItem[],
      onFilter: (value: unknown, record: Application) => {
        return record.type === value
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: AppStatus) => <AppStatusBadge status={status} size="small" />,
      filters: [
        { text: '草稿', value: 'draft' },
        { text: '已启用', value: 'active' },
        { text: '已禁用', value: 'disabled' },
        { text: '已删除', value: 'deleted' },
      ] as ColumnFilterItem[],
      onFilter: (value: unknown, record: Application) => {
        return record.status === value
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      render: (description?: string) => (
        <Text ellipsis={{ tooltip: description }} style={{ maxWidth: 200 }}>
          {description || '-'}
        </Text>
      ),
    },
    {
      title: '回调URL',
      dataIndex: 'callbackUrl',
      key: 'callbackUrl',
      render: (url?: string) => (
        <Text copyable={!!url} ellipsis={{ tooltip: url }}>
          {url || '-'}
        </Text>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      sorter: true,
      render: (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      sorter: true,
      render: (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm:ss'),
    },
    ...(showActions 
      ? [{
          title: '操作',
          key: 'action',
          width: 150,
          fixed: 'right' as const,
          render: (_: any, record: Application) => (
            <Space size="middle">
              <Button 
                type="link" 
                size="small" 
                icon={<EyeOutlined />}
                onClick={() => navigate(`/apps/${record.id}`)}
              >
                详情
              </Button>
              <Button 
                type="link" 
                size="small" 
                icon={<EditOutlined />}
                onClick={() => navigate(`/apps/${record.id}/edit`)}
              >
                编辑
              </Button>
              <Popconfirm
                title="确定要删除这个应用吗？"
                description="删除后该应用将被移动到回收站，30天后自动清除。"
                onConfirm={async () => {
                  const success = await deleteApplication(record.id)
                  if (success) {
                    notification.success({
                      message: '删除成功',
                      description: '应用已成功删除',
                    })
                  } else {
                    notification.error({
                      message: '删除失败',
                      description: '应用删除失败，请重试',
                    })
                  }
                }}
                okText="确定"
                cancelText="取消"
              >
                <Button 
                  type="link" 
                  size="small" 
                  danger
                  icon={<DeleteOutlined />}
                >
                  删除
                </Button>
              </Popconfirm>
            </Space>
          ),
        }] 
      : []),
  ]

  // 加载数据
  useEffect(() => {
    fetchApplications({
      page,
      pageSize,
      keyword: filters.keyword,
      status: filters.status,
      type: filters.type,
    })
  }, [page, pageSize, filters])

  // 搜索功能
  const handleSearch = () => {
    setFilters({ keyword: searchKeyword })
  }

  // 重置搜索
  const handleReset = () => {
    setSearchKeyword('')
    setFilters({ keyword: '' })
  }

  // 分页变化
  const handlePaginationChange = (pageNum: number, pageSizeNum: number) => {
    setPage(pageNum, pageSizeNum)
  }

  return (
    <div className="app-table">
      <div className="table-header" style={{ marginBottom: 16 }}>
        <Space style={{ width: '100%', justifyContent: 'space-between' }}>
          <Space>
            <Input
              placeholder="搜索应用名称"
              prefix={<SearchOutlined />}
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onPressEnter={handleSearch}
              style={{ width: 250 }}
            />
            <Button onClick={handleSearch}>搜索</Button>
            <Button onClick={handleReset}>重置</Button>
          </Space>
        </Space>
      </div>

      <Table
        dataSource={applications}
        columns={columns}
        rowKey="id"
        loading={loading.list}
        pagination={{
          current: page,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: handlePaginationChange,
        }}
        scroll={{ x: 'max-content' }}
        rowSelection={rowSelection}
      />
    </div>
  )
}

export default AppTable