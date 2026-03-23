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
} from '@/types'

/**
 * 应用管理 API 服务
 * 提供对应用 CRUD 操作的封装接口
 */
export const applicationApi = {
  /**
   * 获取应用列表
   * @param params 列表查询参数
   */
  async getList(params: ListParams): Promise<ApplicationListResponse> {
    const response = await apiClient.get('/applications', { params })
    return response.data
  },

  /**
   * 获取应用详情
   * @param id 应用ID
   */
  async getDetail(id: string): Promise<ApplicationDetailResponse> {
    const response = await apiClient.get(`/applications/${id}`)
    return response.data
  },

  /**
   * 创建应用
   * @param data 创建应用的数据
   */
  async create(data: CreateAppData): Promise<CreateApplicationResponse> {
    const response = await apiClient.post('/applications', data)
    return response.data
  },

  /**
   * 更新应用
   * @param id 应用ID
   * @param data 更新应用的数据
   */
  async update(id: string, data: UpdateAppData): Promise<UpdateApplicationResponse> {
    const response = await apiClient.put(`/applications/${id}`, data)
    return response.data
  },

  /**
   * 删除应用（软删除）
   * @param id 应用ID
   */
  async delete(id: string): Promise<DeleteApplicationResponse> {
    const response = await apiClient.delete(`/applications/${id}`)
    return response.data
  },
}

export default applicationApi