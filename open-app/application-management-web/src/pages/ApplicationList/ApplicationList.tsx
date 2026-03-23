import React from 'react'
import { Button, Row, Col } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { AppTable } from '@/components'
import './ApplicationList.less'

/**
 * 应用列表页组件
 * 展示应用列表、支持搜索、筛选、分页
 */
const ApplicationList: React.FC = () => {
  const navigate = useNavigate()

  const handleCreate = () => {
    navigate('/apps/create')
  }

  return (
    <div className="application-list">
      <div className="list-header">
        <Row justify="space-between" align="middle">
          <Col>
            <h2>应用列表</h2>
          </Col>
          <Col>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新建应用
            </Button>
          </Col>
        </Row>
      </div>
      <div className="list-content">
        <AppTable />
      </div>
    </div>
  )
}

export default ApplicationList
