import apiClient from './http'
import type {
  Application,
  ListParams,
  CreateAppData,
  UpdateAppData,
  ApplicationListResponse,
  ApplicationDetailResponse,
  CreateApplicationResponse,
  UpdateApplicationResponse,
  DeleteApplicationResponse,
  ApiResponse,
} from '@/types'

/**
 * 应用管理 API 服务
 * 提供对应用 CRUD 操作的封装接口
 * 
 * 注意：currentUserId 参数由 http.ts 请求拦截器自动从 JWT Token 中解析并添加
 */
export const applicationApi = {
  /**
   * 获取应用列表
   * @param params 列表查询参数
   */
  async getList(params: ListParams): Promise<ApplicationListResponse> {
    const response = await apiClient.get('/applications', { params })
    return response.data as ApplicationListResponse
  },

  /**
   * 获取应用详情
   * @param id 应用 ID
   */
  async getDetail(id: string): Promise<ApplicationDetailResponse> {
    const response = await apiClient.get(`/applications/${id}`)
    return response.data as ApplicationDetailResponse
  },

  /**
   * 创建应用
   * @param data 创建应用的数据
   */
  async create(data: CreateAppData): Promise<CreateApplicationResponse> {
    const response = await apiClient.post('/applications', data)
    return response.data as CreateApplicationResponse
  },

  /**
   * 更新应用
   * @param id 应用 ID
   * @param data 更新应用的数据
   */
  async update(id: string, data: UpdateAppData): Promise<UpdateApplicationResponse> {
    const response = await apiClient.put(`/applications/${id}`, data)
    return response.data as UpdateApplicationResponse
  },

  /**
   * 删除应用（软删除）
   * @param id 应用 ID
   */
  async delete(id: string): Promise<DeleteApplicationResponse> {
    const response = await apiClient.delete(`/applications/${id}`)
    return response.data as DeleteApplicationResponse
  },

  /**
   * 恢复应用（管理员权限）
   * @param id 应用 ID
   */
  async restore(id: string): Promise<ApiResponse<null>> {
    const response = await apiClient.post(`/applications/${id}/restore`)
    return response.data as ApiResponse<null>
  },

  /**
   * 变更应用状态（管理员权限）
   * @param id 应用 ID
   * @param status 目标状态
   * @param reason 变更原因（可选）
   */
  async changeStatus(id: string, status: string, reason?: string): Promise<ApiResponse<null>> {
    const response = await apiClient.patch(`/applications/${id}/status`, {
      status,
      reason,
    })
    return response.data as ApiResponse<null>
  },
}

export default applicationApi