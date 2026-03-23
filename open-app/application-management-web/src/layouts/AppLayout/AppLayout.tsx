import React from 'react'
import { Layout } from 'antd'
import { Outlet } from 'react-router-dom'
import './AppLayout.less'

const { Header, Content } = Layout

/**
 * 应用管理布局组件
 * 提供统一的 Header 和 Content 结构
 */
const AppLayout: React.FC = () => {
  return (
    <Layout className="app-layout">
      <Header className="app-header">
        <div className="app-header-content">
          <h1 className="app-title">应用管理</h1>
        </div>
      </Header>
      <Content className="app-content">
        <Outlet />
      </Content>
    </Layout>
  )
}

export default AppLayout
