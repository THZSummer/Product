/**
 * MSW Browser Worker
 * 用于开发环境下的 API Mock
 */
import { setupWorker } from 'msw/browser'
import { handlers } from './handlers'

/**
 * 浏览器 worker 实例
 * 在 main.tsx 中按需启用
 */
export const worker = setupWorker(...handlers)
