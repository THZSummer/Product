import apiClient from './http'

export const authApi = {
  /**
   * 登录
   */
  login: async (username: string, password: string) => {
    const response = await apiClient.post('/auth/login', { username, password })
    return response.data
  },

  /**
   * 登出
   */
  logout: () => {
    localStorage.removeItem('access_token')
    localStorage.removeItem('user_id')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser: () => {
    return {
      userId: localStorage.getItem('user_id'),
      username: localStorage.getItem('username'),
      role: localStorage.getItem('role'),
    }
  },

  /**
   * 检查是否已登录
   */
  isAuthenticated: () => {
    return !!localStorage.getItem('access_token')
  },
}
