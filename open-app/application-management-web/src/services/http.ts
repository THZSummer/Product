import axios from 'axios'

// 创建基础 axios 实例
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器 - 添加认证token等
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
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
    // TODO: 处理全局错误提示
    console.error('API Error:', error)
    return Promise.reject(error)
  },
)

export default apiClient