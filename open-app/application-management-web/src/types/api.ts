/**
 * 通用 API 响应类型
 * 所有后端 API 响应的统一格式
 */
export interface ApiResponse<T = unknown> {
  /** 响应码，0 表示成功 */
  code: number
  /** 响应消息 */
  message: string
  /** 响应数据 */
  data: T
}

/**
 * 分页数据结构
 */
export interface PaginationData<T = unknown> {
  /** 数据列表 */
  items: T[]
  /** 总条数 */
  total: number
  /** 当前页码 */
  page: number
  /** 每页条数 */
  pageSize: number
  /** 总页数 */
  totalPages: number
}

/**
 * 应用相关类型导入
 */
import type { Application } from './application'

/**
 * 应用列表 API 响应
 */
export interface ApplicationListResponse extends ApiResponse<PaginationData<Application>> {}

/**
 * 应用详情 API 响应
 */
export interface ApplicationDetailResponse extends ApiResponse<Application> {}

/**
 * 创建应用 API 响应
 */
export interface CreateApplicationResponse extends ApiResponse<Application> {}

/**
 * 更新应用 API 响应
 */
export interface UpdateApplicationResponse extends ApiResponse<Application> {}

/**
 * 删除应用 API 响应
 */
export interface DeleteApplicationResponse extends ApiResponse<null> {}

/**
 * 错误响应
 */
export interface ErrorResponse {
  /** 错误码 */
  code: number
  /** 错误消息 */
  message: string
  /** 错误详情 */
  details?: string
}

/**
 * API 错误状态码
 */
export enum ApiErrorCode {
  /** 成功 */
  SUCCESS = 0,
  /** 参数错误 */
  BAD_REQUEST = 400,
  /** 未授权 */
  UNAUTHORIZED = 401,
  /** 禁止访问 */
  FORBIDDEN = 403,
  /** 资源不存在 */
  NOT_FOUND = 404,
  /** 资源冲突（如名称重复） */
  CONFLICT = 409,
  /** 服务器内部错误 */
  INTERNAL_ERROR = 500,
}
