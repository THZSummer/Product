/**
 * Mock 数据
 * 用于开发和测试的模拟数据
 */
import { Application, AppStatus, AppType } from '../types/application'

/**
 * 生成 Mock 应用列表
 */
export const generateMockApps = (): Application[] => {
  const apps: Application[] = [
    {
      id: 'app_mock_001',
      name: '测试应用 A',
      description: '这是一个测试应用',
      type: AppType.SELF_BUILD,
      status: AppStatus.ACTIVE,
      iconUrl: 'https://example.com/icon-a.png',
      callbackUrl: 'https://example.com/callback-a',
      ownerId: 'user_001',
      ownerType: 'user',
      createdAt: '2026-03-20T10:00:00Z',
      updatedAt: '2026-03-23T15:30:00Z',
    },
    {
      id: 'app_mock_002',
      name: '第三方应用 B',
      description: '第三方集成应用',
      type: AppType.THIRD_PARTY,
      status: AppStatus.DRAFT,
      iconUrl: 'https://example.com/icon-b.png',
      callbackUrl: 'https://example.com/callback-b',
      ownerId: 'user_001',
      ownerType: 'user',
      createdAt: '2026-03-19T09:00:00Z',
      updatedAt: '2026-03-22T14:00:00Z',
    },
    {
      id: 'app_mock_003',
      name: '个人应用 C',
      description: '个人开发者应用',
      type: AppType.PERSONAL,
      status: AppStatus.DISABLED,
      ownerId: 'user_002',
      ownerType: 'user',
      createdAt: '2026-03-18T08:00:00Z',
      updatedAt: '2026-03-21T10:00:00Z',
    },
  ]
  return apps
}

/**
 * 默认 Mock 数据
 */
export const mockApps = generateMockApps()
