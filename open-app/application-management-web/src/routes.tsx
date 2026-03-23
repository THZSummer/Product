import { Routes, Route, Navigate } from 'react-router-dom'
import AppLayout from '@layouts/AppLayout'
import ApplicationList from '@pages/ApplicationList'
import ApplicationCreate from '@pages/ApplicationCreate'
import ApplicationDetail from '@pages/ApplicationDetail'
import ApplicationEdit from '@pages/ApplicationEdit'

/**
 * 路由配置
 * 所有应用管理相关路由都在 /apps 路径下
 */
const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/apps" element={<AppLayout />}>
        {/* 重定向：根路径跳转到列表页 */}
        <Route index element={<ApplicationList />} />

        {/* 创建应用页 */}
        <Route path="create" element={<ApplicationCreate />} />

        {/* 应用详情页 */}
        <Route path=":appId" element={<ApplicationDetail />} />

        {/* 编辑应用页 */}
        <Route path=":appId/edit" element={<ApplicationEdit />} />
      </Route>

      {/* 默认重定向到应用列表 */}
      <Route path="*" element={<Navigate to="/apps" replace />} />
    </Routes>
  )
}

export default AppRoutes
