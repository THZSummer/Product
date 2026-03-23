/**
 * 格式化工具函数
 */

/**
 * 格式化时间显示
 * @param date - 日期字符串或Date对象
 * @param format - 格式化模板，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的时间字符串
 */
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)

export const formatTime = (date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string => {
  return dayjs(date).format(format)
}

/**
 * 格式化日期显示（仅日期部分）
 * @param date - 日期字符串或Date对象
 * @returns 格式化后的日期字符串，如 '2023-01-01'
 */
export const formatDate = (date: string | Date): string => {
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 根据时间戳计算相对时间  
 * @param date - 日期字符串或Date对象
 * @returns 相対时间描述，如 '2小时前'、'3天前' 等
 */
export const fromNow = (date: string | Date): string => {
  return dayjs(date).fromNow()
}

/**
 * 验证URL格式
 * @param url - 需要验证的URL字符串
 * @returns 是否为有效的URL格式
 */
export const isValidUrl = (url: string): boolean => {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

/**
 * 验证应用名称格式
 * @param name - 应用名称
 * @returns 是否为有效格式
 * @description 应用名称只能包含中文、字母、数字、下划线和连字符，且长度不超过64个字符
 */
export const isValidAppName = (name: string): boolean => {
  if (!name || typeof name !== 'string') {
    return false
  }
  
  // 检查长度
  if (name.length > 64) {
    return false
  }
  
  // 检查字符格式
  const pattern = /^[a-zA-Z0-9\u4e00-\u9fa5_-]+$/
  return pattern.test(name)
}

/**
 * 格式化文件大小
 * @param size - 字节数
 * @returns 格式化后的文件大小字符串，如 '1.2 KB'、'3.4 MB'
 */
export const formatFileSize = (size: number): string => {
  if (size < 1024) {
    return `${size} B`
  }
  
  if (size < 1024 * 1024) {
    return `${(size / 1024).toFixed(1)} KB`
  }
  
  if (size < 1024 * 1024 * 1024) {
    return `${(size / (1024 * 1024)).toFixed(1)} MB`
  }
  
  return `${(size / (1024 * 1024 * 1024)).toFixed(1)} GB`
}