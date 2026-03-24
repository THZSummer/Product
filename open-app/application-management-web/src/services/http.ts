import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'

// 创建基础 axios 实例
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * 从 JWT Token 中解析 payload
 * @param token JWT Token
 * @returns 解析后的 payload 对象
 */
function parseJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const base64Url = token.split('.')[1]
    if (!base64Url) return null
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join(''),
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

/**
 * 从 JWT Token 中获取当前用户 ID
 * @param token JWT Token
 * @returns 用户 ID
 */
function getCurrentUserIdFromToken(token: string): string | null {
  const payload = parseJwtPayload(token)
  // 优先使用 sub 字段（标准 JWT 用户标识），其次使用 userId 或 currentUserId
  return (payload?.sub || payload?.userId || payload?.currentUserId) as string | null
}

// 请求拦截器 - 添加认证 token 和 currentUserId
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = localStorage.getItem('access_token')
    if (token) {
      // 添加 Authorization header
      if (config.headers) {
        config.headers.Authorization = `Bearer ${token}`
      }
      
      // 从 JWT Token 中解析 currentUserId 并添加到请求参数
      const currentUserId = getCurrentUserIdFromToken(token)
      if (currentUserId && config.params) {
        config.params.currentUserId = currentUserId
      } else if (currentUserId) {
        config.params = { currentUserId }
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器 - 统一处理错误
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 401 未授权，跳转到登录页
    if (error.response?.status === 401) {
      localStorage.removeItem('access_token')
      localStorage.removeItem('user_id')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      window.location.href = '/login'
    } else {
      console.error('API Error:', error)
    }
    return Promise.reject(error)
  },
)

export default apiClient