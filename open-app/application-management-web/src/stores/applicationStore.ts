import { create } from 'zustand'
import type { Application, ListParams } from '@/types'
import { applicationApi } from '@/services'

// 定义状态类型
interface ApplicationState {
  // 应用列表数据
  applications: Application[]
  total: number
  page: number
  pageSize: number
  
  // 筛选条件
  filters: {
    keyword?: string
    status?: Application['status']
    type?: Application['type']
  }
  
  // 当前选中的应用
  currentApplication?: Application
  
  // 加载状态
  loading: {
    list: boolean
    detail: boolean
    create: boolean
    update: boolean
    delete: boolean
  }
  
  // 错误信息
  error?: string
  
  // Action 方法 - 获取应用列表
  fetchApplications: (params: ListParams) => Promise<void>
  
  // Action 方法 - 获取应用详情
  fetchApplicationDetail: (id: string) => Promise<void>
  
  // Action 方法 - 创建应用
  createApplication: (data: Parameters<typeof applicationApi.create>[0]) => Promise<Application | null>
  
  // Action 方法 - 更新应用
  updateApplication: (id: string, data: Parameters<typeof applicationApi.update>[1]) => Promise<Application | null>
  
  // Action 方法 - 删除应用
  deleteApplication: (id: string) => Promise<boolean>
  
  // Action 方法 - 设置筛选条件
  setFilters: (filters: Partial<ApplicationState['filters']>) => void
  
  // Action 方法 - 设置当前应用
  setCurrentApplication: (app?: Application) => void
  
  // Action 方法 - 设置分页
  setPage: (page: number, pageSize: number) => void
  
  // Action 方法 - 清除错误
  clearError: () => void
}

export const useApplicationStore = create<ApplicationState>((set, get) => ({
  // 初始状态
  applications: [],
  total: 0,
  page: 1,
  pageSize: 10,
  filters: {},
  loading: {
    list: false,
    detail: false,
    create: false,
    update: false,
    delete: false,
  },
  
  // Action 实现
  fetchApplications: async (params: ListParams) => {
    set(state => ({
      ...state,
      loading: { ...state.loading, list: true },
      error: undefined,
    }))
    
    try {
      const response = await applicationApi.getList(params)
      
      set(state => ({
        ...state,
        applications: response.data.items,
        total: response.data.total,
        loading: { ...state.loading, list: false },
      }))
    } catch (error) {
      set(state => ({
        ...state,
        loading: { ...state.loading, list: false },
        error: (error as Error).message || '获取应用列表失败',
      }))
    }
  },
  
  fetchApplicationDetail: async (id: string) => {
    set(state => ({
      ...state,
      loading: { ...state.loading, detail: true },
      error: undefined,
    }))
    
    try {
      const response = await applicationApi.getDetail(id)
      
      set(state => ({
        ...state,
        currentApplication: response.data,
        loading: { ...state.loading, detail: false },
      }))
    } catch (error) {
      set(state => ({
        ...state,
        loading: { ...state.loading, detail: false },
        error: (error as Error).message || '获取应用详情失败',
      }))
    }
  },
  
  createApplication: async (data) => {
    set(state => ({
      ...state,
      loading: { ...state.loading, create: true },
      error: undefined,
    }))
    
    try {
      const response = await applicationApi.create(data)
      const newApp = response.data
      
      // 创建成功后，将新应用添加到状态中
      set(state => ({
        ...state,
        applications: [newApp, ...state.applications],
        total: state.total + 1,
        loading: { ...state.loading, create: false },
      }))
      
      return newApp
    } catch (error) {
      set(state => ({
        ...state,
        loading: { ...state.loading, create: false },
        error: (error as Error).message || '创建应用失败',
      }))
      return null
    }
  },
  
  updateApplication: async (id, data) => {
    set(state => ({
      ...state,
      loading: { ...state.loading, update: true },
      error: undefined,
    }))
    
    try {
      const response = await applicationApi.update(id, data)
      const updatedApp = response.data
      
      // 更新成功后，更新状态中的应用信息
      set(state => ({
        ...state,
        applications: state.applications.map(app =>
          app.id === id ? updatedApp : app
        ),
        currentApplication: state.currentApplication?.id === id ? updatedApp : state.currentApplication,
        loading: { ...state.loading, update: false },
      }))
      
      return updatedApp
    } catch (error) {
      set(state => ({
        ...state,
        loading: { ...state.loading, update: false },
        error: (error as Error).message || '更新应用失败',
      }))
      return null
    }
  },
  
  deleteApplication: async (id: string) => {
    set(state => ({
      ...state,
      loading: { ...state.loading, delete: true },
      error: undefined,
    }))
    
    try {
      await applicationApi.delete(id)
      
      // 删除成功后，从状态中移除应用
      set(state => ({
        ...state,
        applications: state.applications.filter(app => app.id !== id),
        total: state.total - 1,
        loading: { ...state.loading, delete: false },
      }))
      
      // 如果删除的是当前查看的应用，则清空当前应用
      if (get().currentApplication?.id === id) {
        set(state => ({ ...state, currentApplication: undefined }))
      }
      
      return true
    } catch (error) {
      set(state => ({
        ...state,
        loading: { ...state.loading, delete: false },
        error: (error as Error).message || '删除应用失败',
      }))
      return false
    }
  },
  
  setFilters: (filters) => {
    set(state => ({
      ...state,
      filters: { ...state.filters, ...filters },
      page: 1, // 筛选条件变化时跳回第一页
    }))
  },
  
  setCurrentApplication: (app) => {
    set(state => ({ ...state, currentApplication: app }))
  },
  
  setPage: (page, pageSize) => {
    set(state => ({
      ...state,
      page,
      pageSize,
    }))
  },
  
  clearError: () => {
    set(state => ({ ...state, error: undefined }))
  },
}))