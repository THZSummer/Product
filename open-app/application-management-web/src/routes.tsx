import { Routes, Route, Navigate } from 'react-router-dom'
import AppLayout from '@layouts/AppLayout'
import ApplicationList from '@pages/ApplicationList'
import ApplicationCreate from '@pages/ApplicationCreate'
import ApplicationDetail from '@pages/ApplicationDetail'
import ApplicationEdit from '@pages/ApplicationEdit'
import LoginPage from '@pages/Login'
import { authApi } from './services/authApi'

// 登录守卫组件
const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isAuthenticated = authApi.isAuthenticated()
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

/**
 * 路由配置
 * 所有应用管理相关路由都在 /apps 路径下
 */
const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* 登录页 */}
      <Route path="/login" element={<LoginPage />} />

      {/* 需要认证的应用管理路由 */}
      <Route
        path="/apps"
        element={
          <PrivateRoute>
            <AppLayout />
          </PrivateRoute>
        }
      >
        <Route index element={<ApplicationList />} />
        <Route path="create" element={<ApplicationCreate />} />
        <Route path=":appId" element={<ApplicationDetail />} />
        <Route path=":appId/edit" element={<ApplicationEdit />} />
      </Route>

      {/* 默认重定向到应用列表 */}
      <Route path="*" element={<Navigate to="/apps" replace />} />
    </Routes>
  )
}

export default AppRoutes
