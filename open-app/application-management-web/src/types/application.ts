/**
 * 应用状态枚举
 * 定义应用的生命周期状态
 */
export enum AppStatus {
  /** 草稿状态：应用已创建但未发布 */
  DRAFT = 'draft',
  /** 已启用：应用正常运行中 */
  ACTIVE = 'active',
  /** 已禁用：应用被管理员禁用 */
  DISABLED = 'disabled',
  /** 已删除：应用已软删除 */
  DELETED = 'deleted',
}

/**
 * 应用类型枚举
 * 定义应用的所属类型
 */
export enum AppType {
  /** 自建应用：企业自主开发的应用 */
  SELF_BUILD = 'self_build',
  /** 第三方应用：第三方开发的应用 */
  THIRD_PARTY = 'third_party',
  /** 个人应用：个人开发者创建的应用 */
  PERSONAL = 'personal',
}

/**
 * 应用实体接口
 * 对应后端 Application Entity
 */
export interface Application {
  /** 应用唯一标识 */
  id: string
  /** 应用名称 */
  name: string
  /** 应用描述 */
  description?: string
  /** 应用类型 */
  type: AppType
  /** 应用状态 */
  status: AppStatus
  /** 应用图标 URL */
  iconUrl?: string
  /** 回调 URL */
  callbackUrl?: string
  /** 所有者 ID */
  ownerId: string
  /** 所有者类型 */
  ownerType: 'user' | 'organization'
  /** 创建时间 (ISO 8601) */
  createdAt: string
  /** 更新时间 (ISO 8601) */
  updatedAt: string
}

/**
 * 应用列表查询参数
 * 用于 GET /api/v1/applications 请求参数
 */
export interface ListParams {
  /** 页码，从 1 开始 */
  page: number
  /** 每页条数，默认 20 */
  pageSize: number
  /** 按应用名称模糊搜索 */
  keyword?: string
  /** 按状态筛选 */
  status?: AppStatus
  /** 按类型筛选 */
  type?: AppType
  /** 排序字段 */
  sortBy?: 'createdAt' | 'updatedAt'
  /** 排序方向 */
  sortOrder?: 'asc' | 'desc'
}

/**
 * 创建应用请求数据
 * 对应后端 CreateApplicationRequest DTO
 */
export interface CreateAppData {
  /** 应用名称（必填） */
  name: string
  /** 应用描述（可选） */
  description?: string
  /** 应用类型（必填） */
  type: AppType
  /** 应用图标 URL（可选） */
  iconUrl?: string
  /** 回调 URL（可选） */
  callbackUrl?: string
}

/**
 * 更新应用请求数据
 * 对应后端 UpdateApplicationRequest DTO
 * 所有字段均为可选
 */
export interface UpdateAppData {
  /** 应用名称 */
  name?: string
  /** 应用描述 */
  description?: string
  /** 应用图标 URL */
  iconUrl?: string
  /** 回调 URL */
  callbackUrl?: string
}

/**
 * 应用类型中文映射
 */
export const AppTypeLabels: Record<AppType, string> = {
  [AppType.SELF_BUILD]: '自建应用',
  [AppType.THIRD_PARTY]: '第三方应用',
  [AppType.PERSONAL]: '个人应用',
}

/**
 * 应用状态中文映射
 */
export const AppStatusLabels: Record<AppStatus, string> = {
  [AppStatus.DRAFT]: '草稿',
  [AppStatus.ACTIVE]: '已启用',
  [AppStatus.DISABLED]: '已禁用',
  [AppStatus.DELETED]: '已删除',
}
